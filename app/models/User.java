package models;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.joda.time.DateTime;
import org.mindrot.jbcrypt.BCrypt;

import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
import util.DateTimeUtil;

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
		encryptPassword();
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
			encryptPassword();
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
	 * Find a user by id
	 * 
	 * @param userId
	 *            The id of the user to be found
	 * @return A user
	 */
	public static User findById(Long userId) {
		return JPA.em().find(User.class, userId);
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
	 */
	public void encryptPassword() {
		password = BCrypt.hashpw(password, BCrypt.gensalt());
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
