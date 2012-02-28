package models;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
public class Project extends Model {

	@Id
	public Long id;
	
	@Required
	@Column(unique = true)
	public String name;
	
	@Required
	@Column(unique = true)
	public String code;
	
	public String description;
	
	@Required
	public Type type;
	
	@ManyToOne
	@Required
	public Customer customer;
	
	public String customerContact;
	
	@ManyToOne
	@Required
	public User projectManager;
	
	@OneToMany
	public List<ProjectAssignment> assignments;
	
	public enum Type {
		FIXED_PRICE, HOURLY_BASED
	}
	
	public static Finder<Long, Project> find = new Finder<Long, Project>(Long.class, Project.class);
	
	public static List<Project> all() {
		return find.findList();
	}
	
	public static List<Project> allExcept(Long id) {
		List<Project> projects = all();
		projects.remove(read(id));
		return projects;
	}
	
	public static void create(Project project) {
		project.save();
	}
	
	public static Project read(Long id) {
		return find.byId(id);		
	}
	
	public static void update(Long id, Project projectToBeUpdated) {
		Project project = read(id);
		project.name = projectToBeUpdated.name;
		project.code = projectToBeUpdated.code;
		project.description = projectToBeUpdated.description;
		project.type = projectToBeUpdated.type;
		project.customer = projectToBeUpdated.customer;
		project.customerContact = projectToBeUpdated.customerContact;
		project.projectManager = projectToBeUpdated.projectManager;
		project.update();
	}
	
	public static void delete(Long id) {
		read(id).delete();
	}
	
    public static Map<String,String> types() {
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        for(Type t: Type.values()) {
            options.put(t.name(), t.name());
        }
        return options;
    }
	
	public static boolean hasDuplicity(Project projectToBeCreated) {
        return !validateDuplicity(projectToBeCreated).isEmpty();
    }

    public static String validateDuplicity(Project projectToBeCreated) {
        for(Project existingProject : all()) {
            if(existingProject.name.equalsIgnoreCase(projectToBeCreated.name))
                return "Duplicate name!";
            if(existingProject.code.equalsIgnoreCase(projectToBeCreated.code))
                return "Duplicate code!";
        }
        return new String();
    }

    public static boolean hasDuplicity(Long id, Project projectToBeUpdated) {
        return !validateDuplicity(id, projectToBeUpdated).isEmpty();
    }
    
    public static String validateDuplicity(Long id, Project projectToBeUpdated) {
    	for(Project existingProject : allExcept(id)) {
            if(existingProject.name.equalsIgnoreCase(projectToBeUpdated.name))
                return "Duplicate name!";
            if(existingProject.code.equalsIgnoreCase(projectToBeUpdated.code))
                return "Duplicate code!";
        }
        return new String();
    }
	
}
