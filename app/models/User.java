package models;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.joda.time.DateTime;
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

	public boolean employee;

	public boolean admin;

	@OneToMany(mappedBy = "user")
	public List<ProjectAssignment> assignments;

	/**
	 * Encrypts the user's password and inserts this new user
	 */
	public void save() {
		password = User.encryptPassword(password);
		JPA.em().persist(this);
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
		List<User> users = JPA.em()
				.createQuery("from User where username = :username")
				.setParameter("username", username).getResultList();
		if (users.isEmpty() || users.size() > 1)
			return null;
		else
			return users.get(0);
	}

	/**
	 * Find all users
	 * 
	 * @return A List of user objects
	 */
	public static List<User> findAll() {
		return JPA.em().createQuery("from User").getResultList();
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
	 * All assignments for a user
	 * 
	 * @return A Map with as key the assignment id and as value the project name
	 */
	public static Map<String, String> assignmentsFor(Long userId) {
		LinkedHashMap<String, String> assignments = new LinkedHashMap<String, String>();

		for (ProjectAssignment assignment : findById(userId).assignments) {
			if (ProjectAssignment.isDateInAssignmentRange(new DateTime(),
					assignment.id))
				assignments.put(assignment.id.toString(),
						assignment.project.name.toString());
		}
		return assignments;
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
