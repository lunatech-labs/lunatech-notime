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
public class Permission implements be.objectify.deadbolt.models.Permission {

	@Id
	@GeneratedValue
	public Long id;

	@Column(unique = true)
	public String value;

	/**
	 * Inserts this new permission
	 */
	public void save() {
		JPA.em().persist(this);
	}

	/**
	 * Delete this permission
	 */
	public void delete() {
		JPA.em().remove(this);
	}
	
	@Override
	public String getValue() {
		return value;
	}
	
	public static Permission findByValue(String value) {
		try {
			CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
			CriteriaQuery<Permission> query = cb.createQuery(Permission.class);
			Root<Permission> permission = query.from(Permission.class);
			query.where(cb.equal(permission.get(Permission_.value), value));
			return JPA.em().createQuery(query).getSingleResult();
		} catch (NoResultException nre) {
			return null;
		} catch (NonUniqueResultException nure) {
			return null;
		}
	}

}
