package models;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

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

	/**
	 * A default project is a project on which all current and all new users are
	 * assigned too. If an existing default project is made non-default the
	 * existing assignments aren't removed, but new users aren't assigned
	 * automatically anymore.
	 */
	public boolean defaultProject;

	public enum Type {
		FIXED_PRICE, HOURLY_BASED
	}

	/**
	 * Inserts this project. If the project is a defaultProject, all users will
	 * be assigned to it.
	 */
	public void save() {
		JPA.em().persist(this);
		if (defaultProject)
			ProjectAssignment.assignAllUsersTo(this);
	}

	/**
	 * Updates this project. If the project is a defaultProject, all users will
	 * be assigned to it.
	 * 
	 * @param projectId
	 *            The id of the project that is going to be updated
	 */
	public void update(Long projectId) {
		id = projectId;
		JPA.em().merge(this);
		if (defaultProject)
			ProjectAssignment.assignAllUsersTo(this);
	}

	/**
	 * Deletes this project
	 */
	public void delete() {
		JPA.em().remove(this);
	}

	/**
	 * Find a project by id
	 * 
	 * @param projectId
	 *            The id of the project to be searched for
	 * @return A project
	 */
	public static Project findById(Long projectId) {
		return JPA.em().find(Project.class, projectId);
	}

	/**
	 * Find all projects
	 * 
	 * @return A list of project objects
	 */
	public static List<Project> findAll() {
		CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
		CriteriaQuery<Project> query = cb.createQuery(Project.class);
		query.from(Project.class);
		return JPA.em().createQuery(query).getResultList();
	}

	/**
	 * Find all project except one project
	 * 
	 * @param projectId
	 *            The id of the project that needs to be filtered
	 * @return A List of project objects
	 */
	public static List<Project> findAllExcept(Long projectId) {
		List<Project> projects = findAll();
		projects.remove(findById(projectId));
		return projects;
	}

	/**
	 * Find all default projects
	 * 
	 * @return A list of project objects
	 */
	public static List<Project> findAllDefaultProjects() {
		CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
		CriteriaQuery<Project> query = cb.createQuery(Project.class);
		Root<Project> project = query.from(Project.class);
		query.where(cb.equal(project.get(Project_.defaultProject), true));
		return JPA.em().createQuery(query).getResultList();
	}

	/**
	 * All project types
	 * 
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
