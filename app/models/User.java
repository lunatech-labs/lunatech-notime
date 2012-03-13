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

import org.joda.time.DateTime;
import org.mindrot.jbcrypt.BCrypt;

import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
import util.DateTimeUtil;
import util.form.validators.Unique;

@Entity
@SequenceGenerator(name = "user_seq", sequenceName = "user_seq")
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
	public Long id;
	
    @Required
	@Column(unique = true)
    public String username;
    
    @Required
    public String password;
    
    @Required
    @Unique
    public String fullname;
    
    @Required
    @Email
    @Column(unique = true)
    public String email;
    
    public boolean employee;

    public boolean admin;
    
    @OneToMany(mappedBy="user")
	public List<ProjectAssignment> assignments;
    
    public static List<User> all() {
    	return JPA.em().createQuery("from User").getResultList();
    }

    public static List<User> allExcept(Long id) {
        List<User> users = all();
        users.remove(read(id));
        return users;
    }

    public static void create(User user) {
    	user.password = BCrypt.hashpw(user.password, BCrypt.gensalt());
        JPA.em().persist(user);
    }

    public static User read(Long id) {
        return JPA.em().find(User.class, id);
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
        JPA.em().merge(user);  
    }

    public static void delete(Long id) {
        JPA.em().remove(User.read(id));
    }
    
    public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        for(User u: all()) {
            options.put(u.id.toString(), u.fullname);
        }
        return options;
    }
    
    public static Map<String,String> assignments(Long userId) {
        List<ProjectAssignment> assignments = read(userId).assignments;
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        
        for(ProjectAssignment assignment: assignments) {
        	if(ProjectAssignment.isDateInAssignmentRange(new DateTime(), assignment.id))
        		options.put(assignment.id.toString(), assignment.project.name.toString());
        }
        return options;
    }
    
    public static boolean hasDuplicity(User userToBeCreated) {
        return !validateDuplicity(userToBeCreated).isEmpty();
    }

    public static String validateDuplicity(User userToBeCreated) {
        for(User existingUser : all()) {
            if(existingUser.username.equalsIgnoreCase(userToBeCreated.username))
                return "Duplicate username!";
            if(existingUser.email.equalsIgnoreCase(userToBeCreated.email))
                return "Duplicate email!";
        }
        return new String();
    }

    public static boolean hasDuplicity(Long id, User userToBeUpdated) {
        return !validateDuplicity(id, userToBeUpdated).isEmpty();
    }
    
    public static String validateDuplicity(Long id, User userToBeUpdated) {
        for(User existingUser : allExcept(id)) {
    		if(existingUser.username.equalsIgnoreCase(userToBeUpdated.username))
    			return "Duplicate username!";
    		if(existingUser.email.equalsIgnoreCase(userToBeUpdated.email))
    			return "Duplicate email!";
    	}
    	return new String();
    }

}
