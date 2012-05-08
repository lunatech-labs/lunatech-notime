package models.security;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import be.objectify.deadbolt.models.Role;

import play.db.jpa.JPA;

@Entity
public class UserRole implements Role {

	@Id
	@GeneratedValue
	public Long id;

	@Column(unique = true)
	public String roleName;

	/**
	 * Inserts this new role
	 */
	public void save() {
		JPA.em().persist(this);
	}

	/**
	 * Delete this role
	 */
	public void delete() {
		JPA.em().remove(this);
	}

	@Override
	public String getRoleName() {
		return roleName;
	}

	public static UserRole adminRole() {
		return findByRoleName("admin");
	}

	public static UserRole userRole() {
		return findByRoleName("user");
	}

	public static UserRole customerManagerRole() {
		return findByRoleName("customerManager");
	}
	
	public static UserRole projectManagerRole() {
		return findByRoleName("projectManager");
	}

	/**
	 * Find a role by its role name
	 * 
	 * @param roleName
	 *            The name of the role to be searched for
	 * @return A {@link UserRole}
	 */
	public static UserRole findByRoleName(String roleName) {
		try {
			CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
			CriteriaQuery<UserRole> query = cb.createQuery(UserRole.class);
			Root<UserRole> role = query.from(UserRole.class);
			query.where(cb.equal(role.get(UserRole_.roleName), roleName));
			return JPA.em().createQuery(query).getSingleResult();
		} catch (NoResultException nre) {
			return null;
		} catch (NonUniqueResultException nure) {
			return null;
		}
	}

	/**
	 * Find all roles
	 * 
	 * @return A list of {@link UserRole}s
	 */
	public static List<UserRole> findAll() {
		CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
		CriteriaQuery<UserRole> query = cb.createQuery(UserRole.class);
		query.from(UserRole.class);
		return JPA.em().createQuery(query).getResultList();
	}

	/**
	 * All existing roles
	 * 
	 * @return A Map with as key the role's id and as value the role's name
	 */
	public static Map<String, String> options() {
		Map<String, String> options = new HashMap<String, String>();
		for (UserRole r : findAll()) {
			options.put(r.id.toString(), r.roleName.toUpperCase());
		}
		return options;
	}

}
