package models;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.OneToMany;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;
import org.mindrot.jbcrypt.BCrypt;

import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;

@Entity
public class User {

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

	/**
	 * Encrypts the user's password and inserts this new user. The user will
	 * also be assigned to all default projects.
	 */
	public void save() {
		password = User.encryptPassword(password);
		createdOn = new LocalDate();
		JPA.em().persist(this);
		ProjectAssignment.assignAllDefaultProjectsTo(this);
	}

	/**
	 * Encrypts the user's password if updated and updates this user
	 * 
	 * @param userId
	 *            The id of the user that is going to be updated
	 */
	public void update(Long userId) {
		if (!password.equals(findById(userId).password))
			password = User.encryptPassword(password);
		this.id = userId;
		JPA.em().merge(this);
	}

	/**
	 * Deletes this user
	 */
	public void delete() {
		JPA.em().remove(this);
	}

	/**
	 * Find a user by id
	 * 
	 * @param userId
	 *            The id of the user to be searched for
	 * @return A user
	 */
	public static User findById(Long userId) {
		return JPA.em().find(User.class, userId);
	}

	/**
	 * Find a user by username
	 * 
	 * @param username
	 *            The username of the user to be searched for
	 * @return If 0 or more than 1 (more than one means non-unique value, which
	 *         shouldn't be possible) users are found, null is returned. If one
	 *         is found, this user is returned
	 */
	public static User findByUsername(String username) {
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
	 * Find all users
	 * 
	 * @return A List of user objects
	 */
	public static List<User> findAll() {
		CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
		CriteriaQuery<User> query = cb.createQuery(User.class);
		query.from(User.class);		
		return JPA.em().createQuery(query).getResultList();
	}

	/**
	 * Find all users except one user
	 * 
	 * @param userId
	 *            The id of the user that needs to be filtered
	 * @return A List of user objects
	 */
	public static List<User> findAllExcept(Long userId) {
		List<User> users = findAll();
		users.remove(findById(userId));
		return users;
	}

	/**
	 * All existing users
	 * 
	 * @return A Map with as key the user's id and as value the user's full name
	 */
	public static Map<String, String> options() {
		LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
		for (User u : findAll()) {
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
	public static String encryptPassword(String password) {
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
	public boolean checkPassword(String input) {
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
	public static User authenticate(String username, String password) {
		User user = findByUsername(username);

		if (user == null || !user.checkPassword(password))
			return null;
		else
			return user;
	}

	// VALIDATION METHODS NEED TO BE REPLACED BY ANNOTATIONS OR BE REWRITTEN
	public static boolean hasDuplicity(User userToBeCreated) {
		return !validateDuplicity(userToBeCreated).isEmpty();
	}

	public static String validateDuplicity(User userToBeCreated) {
		for (User existingUser : findAll()) {
			if (existingUser.username
					.equalsIgnoreCase(userToBeCreated.username))
				return "Duplicate username!";
			if (existingUser.email.equalsIgnoreCase(userToBeCreated.email))
				return "Duplicate email!";
		}
		return new String();
	}

	public static boolean hasDuplicity(Long id, User userToBeUpdated) {
		return !validateDuplicity(id, userToBeUpdated).isEmpty();
	}

	public static String validateDuplicity(Long id, User userToBeUpdated) {
		for (User existingUser : findAllExcept(id)) {
			if (existingUser.username
					.equalsIgnoreCase(userToBeUpdated.username))
				return "Duplicate username!";
			if (existingUser.email.equalsIgnoreCase(userToBeUpdated.email))
				return "Duplicate email!";
		}
		return new String();
	}

}
