package beans;

import java.util.Collections;
import java.util.List;

import models.User;
import models.security.UserRole;

import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.Required;
import play.i18n.Messages;

public class UserForm {
	
	public Long id;

	@Required
	public String username;

	public String password;

	public String repeatedPassword;

	@Required
	public String fullname;

	@Required
	@Email
	public String email;

	public boolean active;

	public List<UserRole> userRoles;

	public UserForm() {
	}

	public UserForm(User user) {
		id = user.id;
		username = user.username;
		fullname = user.fullname;
		email = user.email;
		active = user.active;
		userRoles = user.userRoles;
	}
    
    public String validate() {
		if (hasDuplicateUsername())
			return "Duplicate username!";
		if (hasDuplicateEmail())
			return "Duplicate email!";
		if (!hasEqualPasswords())
			return Messages.get("user.passwordFieldsNotEqual");
		if (passwordRequired() && passwordsAreEmpty())
			return Messages.get("user.anEmptyPasswordField");
		return null;
	}

	public boolean hasDuplicateUsername() {
		List<User> users = Collections.emptyList();
		if (id != null)
			users = User.findAllExcept(User.findByUsername(username));
		else
			users = User.findAll();

		for (User user : users) {
			if (user.username.equalsIgnoreCase(username))
				return true;
		}
		return false;
	}

	public boolean hasDuplicateEmail() {
		List<User> users = Collections.emptyList();
		if (id != null)
			users = User.findAllExcept(User.findByEmail(email));
		else
			users = User.findAll();

		for (User user : users) {
			if (user.email.equalsIgnoreCase(email))
				return true;
		}
		return false;
	}

	public boolean hasEqualPasswords() {
		return password.equals(repeatedPassword);
	}

	public boolean passwordRequired() {
		return id == null;
	}

	public boolean passwordsAreEmpty() {
		return password == null || password.isEmpty() || repeatedPassword == null || repeatedPassword.isEmpty();
	}

	public User toUser() {
		return new User(this);
	}

}
