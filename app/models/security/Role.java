package models.security;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import play.db.jpa.JPA;

@Entity
public class Role implements be.objectify.deadbolt.models.Role {

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
	
	public static Role findByRoleName(String roleName) {
		try {
			CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
			CriteriaQuery<Role> query = cb.createQuery(Role.class);
			Root<Role> role = query.from(Role.class);
			query.where(cb.equal(role.get(Role_.roleName), roleName));
			return JPA.em().createQuery(query).getSingleResult();
		} catch (NoResultException nre) {
			return null;
		} catch (NonUniqueResultException nure) {
			return null;
		}
	}

}
