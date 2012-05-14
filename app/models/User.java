package models;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import beans.UserForm;

import models.security.UserRole;
import models.security.UserRole_;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;
import org.mindrot.jbcrypt.BCrypt;

import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
import be.objectify.deadbolt.models.Permission;
import be.objectify.deadbolt.models.Role;
import be.objectify.deadbolt.models.RoleHolder;

@Entity
@Table(name = "uzer")
public class User implements RoleHolder {

	@Id
	@GeneratedValue
	public Long id;

	@Required
	@Column(unique = true)
	public String username;

	@Required
	public String password;

	@Required
	public String fullname;

	@Required
	@Email
	@Column(unique = true)
	public String email;

	@OneToMany(mappedBy = "user")
	public List<ProjectAssignment> assignments;

	@Type(type = "org.joda.time.contrib.hibernate.PersistentLocalDate")
	public LocalDate createdOn;

	public boolean active;

	@ManyToMany
	public List<UserRole> userRoles;

	public User() {
	}

	public User(UserForm form) {
		id = form.id;
		username = form.username;
		password = form.password;
		fullname = form.fullname;
		email = form.email;
		active = form.active;
		userRoles = form.userRoles;
	}

	/**
	 * Encrypts the user's password and inserts this new user. The user will
	 * also be assigned to all default projects.
	 */
	public void save() {
		password = User.encryptPassword(password);
		createdOn = new LocalDate();
		active = true;
		JPA.em().persist(this);
		ProjectAssignment.assignAllDefaultProjectsTo(this);
	}

	/**
	 * Encrypts the user's password (if updated) and updates this user
	 * 
	 * @param userId
	 *            The id of the user that is going to be updated
	 */
	public void update(Long userId) {
		this.id = userId;

		if (password != null && !password.isEmpty())
			password = User.encryptPassword(password);
		else
			password = User.findById(userId).password;

		// Make it impossible to delete the last admin role
		if (isLastAdminUser()) {
			UserRole adminRole = UserRole.adminRole();
			if (userRoles == null) {
				List<UserRole> roles = new LinkedList<UserRole>();
				roles.add(adminRole);
				userRoles = roles;
			} else {
				if (!userRoles.contains(adminRole)) {
					userRoles.add(adminRole);
				}
			}
		}

		createdOn = User.findById(userId).createdOn;
		JPA.em().merge(this);

		if (!active)
			inactivateAssignments();
	}

	/**
	 * Encrypts and updates the user's password
	 *
	 * @param password
	 *				The unencrypted password
	 */
	public void updatePassword(String password) {
		this.password = password;
		update(id);
	}

	/**
	 * Deletes this user
	 */
	public boolean delete() {
		boolean deletable = isDeletable();
		if (deletable) {
			for (Customer customer : Customer.findAllForCustomerManager(this)) {
				customer.customerManagers.remove(this);
			}
			JPA.em().remove(this);
		}
		return deletable;
	}

	/**
	 * Inactivates this user.
	 */
	public void inactivate() {
		active = false;
		JPA.em().merge(this);
	}

	/**
	 * Inactivates all its assignments.
	 */
	public void inactivateAssignments() {
		for (ProjectAssignment assignment : ProjectAssignment
				.findAllActiveForUser(this)) {
			assignment.inactivate();
		}
	}

	/**
	 * Checks if a user is deletable. A user is deletable when all its
	 * assignments are deletable, he is not a project manager and he is not the
	 * last user with the admin role.
	 * 
	 * @return true if the project is deletable
	 */
	public boolean isDeletable() {
		for (ProjectAssignment assignment : assignments) {
			if (!assignment.isDeletable()) {
				return false;
			}
		}
		for (Project project : Project.findAll()) {
			if (project.projectManager == this) {
				return false;
			}
		}
		if (isLastAdminUser()) {
			return false;
		}
		return true;
	}

	/**
	 * Checks if this user is the last user with an admin role
	 * 
	 * @return true if this is the last user with an admin role
	 */
	private boolean isLastAdminUser() {
		List<User> admins = findAllForRole("admin");
		if (admins.size() <= 1) {
			if (admins.get(0).id == id) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Find a user by id
	 * 
	 * @param userId
	 *            The id of the user to be searched for
	 * @return A user
	 */
	public static User findById(final Long userId) {
		return JPA.em().find(User.class, userId);
	}

	/**
	 * Find a user by his username
	 * 
	 * @param username
	 *            The username of the user to be searched for
	 * @return If 0 or more than 1 (more than one means non-unique value, which
	 *         shouldn't be possible) users are found, null is returned. If one
	 *         is found, this user is returned
	 */
	public static User findByUsername(final String username) {
		try {
			CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
			CriteriaQuery<User> query = cb.createQuery(User.class);
			Root<User> user = query.from(User.class);
			query.where(cb.equal(user.get(User_.username), username));
			return JPA.em().createQuery(query).getSingleResult();
		} catch (NoResultException nre) {
			return null;
		} catch (NonUniqueResultException nure) {
			return null;
		}
	}

	/**
	 * Find a user by his email
	 * 
	 * @param email
	 *            The email of the user to be searched for
	 * @return If 0 or more than 1 (more than one means non-unique value, which
	 *         shouldn't be possible) users are found, null is returned. If one
	 *         is found, this user is returned
	 */
	public static User findByEmail(final String email) {
		try {
			CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
			CriteriaQuery<User> query = cb.createQuery(User.class);
			Root<User> user = query.from(User.class);
			query.where(cb.equal(user.get(User_.email), email));
			return JPA.em().createQuery(query).getSingleResult();
		} catch (NoResultException nre) {
			return null;
		} catch (NonUniqueResultException nure) {
			return null;
		}
	}

	/**
	 * Find all users
	 * 
	 * @return A List of {@link User}s
	 */
	public static List<User> findAll() {
		CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
		CriteriaQuery<User> query = cb.createQuery(User.class);
		query.from(User.class);
		return JPA.em().createQuery(query).getResultList();
	}

	/**
	 * Find all users on a project
	 * 
	 * @param project
	 *            The project which the users must be assigned to
	 * @return A List of {@link User}s
	 */
	public static List<User> findAllForProject(final Project project) {
		CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
		CriteriaQuery<User> query = cb.createQuery(User.class);
		Root<User> user = query.from(User.class);
		Join<User, ProjectAssignment> assignment = user.join(User_.assignments);
		query.where(cb.equal(assignment.get(ProjectAssignment_.project),
				project));
		return JPA.em().createQuery(query).getResultList();
	}

	/**
	 * Find all users which have a certain role
	 * 
	 * @param roleName
	 *            The roleName of the role to which the users must be assigned
	 *            to
	 * @return A List of {@link User}s
	 */
	public static List<User> findAllForRole(final String roleName) {
		CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
		CriteriaQuery<User> query = cb.createQuery(User.class);
		Root<User> user = query.from(User.class);
		Join<User, UserRole> role = user.join(User_.userRoles);
		query.where(cb.equal(role.get(UserRole_.roleName), roleName));
		return JPA.em().createQuery(query).getResultList();
	}

	/**
	 * Find all users except one user
	 * 
	 * @param user
	 *            The user that needs to be filtered
	 * @return A List of user objects
	 */
	public static List<User> findAllExcept(final User user) {
		List<User> users = findAll();
		users.remove(user);
		return users;
	}

	/**
	 * Find all active users
	 * 
	 * @return A List of {@link User}s
	 */
	public static List<User> findAllActive() {
		CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
		CriteriaQuery<User> query = cb.createQuery(User.class);
		Root<User> user = query.from(User.class);
		query.where(cb.isTrue(user.get(User_.active)));
		return JPA.em().createQuery(query).getResultList();
	}

	/**
	 * All existing users
	 * 
	 * @return A Map with as key the user's id and as value the user's full name
	 */
	public static Map<String, String> options() {
		final LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
		for (User u : findAllActive()) {
			options.put(u.id.toString(), u.fullname);
		}
		return options;
	}

	/**
	 * Encrypts password with BCrypt
	 * 
	 * @param password
	 *            The password to be encrypted
	 * @return The encrypted password
	 */
	public static String encryptPassword(final String password) {
		return BCrypt.hashpw(password, BCrypt.gensalt());
	}

	/**
	 * Checks if password are equal
	 * 
	 * @param input
	 *            The non-encrypted password which is checked against the user's
	 *            password
	 * @return True if the passwords are equal, false if not
	 */
	public boolean checkPassword(final String input) {
		return BCrypt.checkpw(input, password);
	}

	/**
	 * Authenticate a user
	 * 
	 * @param username
	 *            The username of the user to be authenticated
	 * @param password
	 *            The non-encrypted password of the user to be authenticated
	 * @return If no user is found or the passwords aren't equal, null is
	 *         returned. Otherwise the user is returned
	 */
	public static User authenticate(final String username, final String password) {
		User user = findByUsername(username);
		if (user == null || !user.checkPassword(password) || !user.active)
			return null;
		else
			return user;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public List<? extends Role> getRoles() {
		return userRoles;
	}

	/**
	 * Permissions are not used.
	 */
	@Override
	public List<? extends Permission> getPermissions() {
		return null;
	}

	/**
	 * Checks if this user is assigned to a role
	 * 
	 * @param role
	 *            The {@link UserRole} to check if assigned to this user
	 * @return True if the role is assigned to this user
	 */
	public boolean containsRole(UserRole role) {
		if (userRoles != null) {
			return userRoles.contains(role);
		}
		return false;
	}

	/**
	 * Assigns a role to this user
	 * 
	 * @param role
	 *            The {@link UserRole} to assign to this user
	 */
	public void assignRole(UserRole role) {
		if (userRoles == null) {
			List<UserRole> roles = new LinkedList<UserRole>();
			roles.add(role);
			userRoles = roles;
		} else {
			userRoles.add(role);
		}
		JPA.em().merge(this);
	}

	/**
	 * Remove a role from this user
	 * 
	 * @param role
	 *            The {@link UserRole} to remove from this user
	 */
	public void removeRole(UserRole role) {
		if (userRoles != null) {
			userRoles.remove(role);
			JPA.em().merge(this);
		}
	}

	/**
	 * Checks if this user is has the admin role
	 * 
	 * @return True if this user has the admin role
	 */
	public boolean hasAdminRole() {
		return containsRole(UserRole.adminRole());
	}

	/**
	 * Checks if this user is has the customer manager role
	 * 
	 * @return True if this user has the customer manager role
	 */
	public boolean hasCustomerManagerRole() {
		return containsRole(UserRole.customerManagerRole());
	}

	/**
	 * Checks if this user is has the project manager role
	 * 
	 * @return True if this user has the project manager role
	 */
	public boolean hasProjectManagerRole() {
		return containsRole(UserRole.projectManagerRole());
	}

	/**
	 * Checks if this user is has only the user role
	 * 
	 * @return True if this user has only the user role
	 */
	public boolean hasOnlyUserRole() {
		return containsRole(UserRole.userRole()) && !hasAdminRole()
				&& !hasCustomerManagerRole() && !hasProjectManagerRole();
	}

}
