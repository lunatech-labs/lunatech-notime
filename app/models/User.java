package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.mindrot.jbcrypt.BCrypt;

import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
public class User extends Model {
	
	@Id
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
    
    public static Finder<Long, User> find = new Finder<Long, User>(Long.class, User.class);
    
    public static List<User> all() {
    	return find.all();
    }

    public static List<User> allExcept(Long id) {
        List<User> users = all();
        users.remove(read(id));
        return users;
    }

    public static void create(User user) {
    	user.password = BCrypt.hashpw(user.password, BCrypt.gensalt());
        user.save();
    }

    public static User read(Long id) {
        return find.byId(id);
    }
    
    public static void update(Long id, User userToBeUpdated) {
    	User user = read(id);
    	user.username = userToBeUpdated.username;
    	if (!userToBeUpdated.password.equals(user.password))
    		user.password = BCrypt.hashpw(userToBeUpdated.password, BCrypt.gensalt());
    	user.fullname = userToBeUpdated.fullname;
    	user.email = userToBeUpdated.email;
    	user.admin = userToBeUpdated.admin;
    	user.employee = userToBeUpdated.employee;
        user.update();        
    }

    public static void delete(Long id) {
        read(id).delete();
    }
    
    public static boolean hasDuplicity(User userToBeCreated) {
        return !validateDuplicity(userToBeCreated).isEmpty();
    }

    public static String validateDuplicity(User userToBeCreated) {
        for(User existingUser : all()) {
            if(existingUser.username.equals(userToBeCreated.username))
                return "Duplicate username!";
            if(existingUser.email.equals(userToBeCreated.email))
                return "Duplicate email!";
        }
        return new String();
    }

    public static boolean hasDuplicity(Long id, User userToBeUpdated) {
        return !validateDuplicity(id, userToBeUpdated).isEmpty();
    }
    
    public static String validateDuplicity(Long id, User userToBeUpdated) {
        for(User existingUser : allExcept(id)) {
    		if(existingUser.username.equals(userToBeUpdated.username))
    			return "Duplicate username!";
    		if(existingUser.email.equals(userToBeUpdated.email))
    			return "Duplicate email!";
    	}
    	return new String();
    }

}
