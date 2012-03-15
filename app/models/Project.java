package models;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;

@Entity
public class Project {

	@Id
	@GeneratedValue
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

	@OneToMany(mappedBy = "project")
	public List<ProjectAssignment> assignments;

	public enum Type {
		FIXED_PRICE, HOURLY_BASED
	}

	/**
	 * Inserts this project
	 */
	public void save() {
		JPA.em().persist(this);
	}

	/**
	 * Updates this project
	 * 
	 * @param projectId
	 *            The id of the project that is going to be updated
	 */
	public void update(Long projectId) {
		this.id = projectId;
		JPA.em().merge(this);
	}

	/**
	 * Deletes this project
	 */
	public void delete() {
		JPA.em().remove(this);
	}

	/**
	 * Find all projects
	 * 
	 * @return A list of project objects
	 */
	public static List<Project> findAll() {
		return JPA.em().createQuery("from Project").getResultList();
	}

	/**
	 * Find all project except one project
	 * 
	 * @param projectId
	 *            The id of the project that needs to be filtered
	 * @return A list of project objects
	 */
	public static List<Project> findAllExcept(Long projectId) {
		List<Project> projects = findAll();
		projects.remove(findById(projectId));
		return projects;
	}

	/**
	 * Find a project by id
	 * 
	 * @param projectId
	 *            The id of the project to be found
	 * @return A project
	 */
	public static Project findById(Long projectId) {
		return JPA.em().find(Project.class, projectId);
	}

	/**
	 * All project types
	 * @return A List with the name of the types
	 */
	public static List<String> types() {
		List<String> options = new LinkedList<String>();
		for (Type t : Type.values()) {
			options.add(t.name());
		}
		return options;
	}

	// VALIDATION METHODS NEED TO BE REPLACED BY ANNOTATIONS OR BE REWRITTEN
	public static boolean hasDuplicity(Project projectToBeCreated) {
		return !validateDuplicity(projectToBeCreated).isEmpty();
	}

	public static String validateDuplicity(Project projectToBeCreated) {
		for (Project existingProject : findAll()) {
			if (existingProject.name.equalsIgnoreCase(projectToBeCreated.name))
				return "Duplicate name!";
			if (existingProject.code.equalsIgnoreCase(projectToBeCreated.code))
				return "Duplicate code!";
		}
		return new String();
	}

	public static boolean hasDuplicity(Long id, Project projectToBeUpdated) {
		return !validateDuplicity(id, projectToBeUpdated).isEmpty();
	}

	public static String validateDuplicity(Long id, Project projectToBeUpdated) {
		for (Project existingProject : findAllExcept(id)) {
			if (existingProject.name.equalsIgnoreCase(projectToBeUpdated.name))
				return "Duplicate name!";
			if (existingProject.code.equalsIgnoreCase(projectToBeUpdated.code))
				return "Duplicate code!";
		}
		return new String();
	}

}
