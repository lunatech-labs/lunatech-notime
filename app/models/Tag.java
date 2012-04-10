package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;

@Entity
public class Tag {

	@Id
	@GeneratedValue
	public Long id;

	@Required
	@Column(unique = true)
	public String tag;

	/**
	 * Inserts this new tag
	 */
	public void save() {
		JPA.em().persist(this);
	}

	/**
	 * Inserts and returns a new tag. If the parameter is empty, no tag will be
	 * created
	 * 
	 * @param tagString
	 *            The value of the tag's tag
	 * @return The created tag
	 */
	public static Tag create(String tagString) {
		tagString = tagString.trim();
		if (tagString.isEmpty()) {
			return null;
		} else {
			Tag tag = new Tag();
			tag.tag = tagString;
			JPA.em().persist(tag);
			return tag;
		}
	}

	/**
	 * Tries to find a tag. If nothing found a new tag is created
	 * 
	 * @param tagString
	 *            The value of the tag's tag
	 * @return The found or created tag
	 */
	public static Tag findOrCreate(String tagString) {
		tagString = tagString.trim();
		Tag tag = findByTag(tagString);
		if (tag == null)
			return create(tagString);
		else
			return tag;
	}

	/**
	 * Find all tags
	 * 
	 * @return A List of tag objects
	 */
	public static List<Tag> findAll() {
		CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
		CriteriaQuery<Tag> query = cb.createQuery(Tag.class);
		query.from(Tag.class);
		return JPA.em().createQuery(query).getResultList();
	}

	/**
	 * Find the tags by a tag's tag value
	 * 
	 * @param tag
	 *            The value of the tag's tag
	 * @return A tag object
	 */
	public static Tag findByTag(String tag) {
		try {
			CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
			CriteriaQuery<Tag> query = cb.createQuery(Tag.class);
			Root<Tag> tagRoot = query.from(Tag.class);
			query.where(cb.equal(tagRoot.get(Tag_.tag), tag));
			return JPA.em().createQuery(query).getSingleResult();
		} catch (NoResultException nre) {
			return null;
		} catch (NonUniqueResultException nure) {
			return null;
		}
	}

	/**
	 * Find the tags which contain a term
	 * 
	 * @param term
	 *            The term the tag's tag needs to contain
	 * @return A List of tag objects
	 */
	public static List<String> findTagsWhichContain(String term) {
		CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
		CriteriaQuery<String> query = cb.createQuery(String.class);
		Root<Tag> tag = query.from(Tag.class);
		query.select(tag.get(Tag_.tag));
		query.where(cb.like(cb.upper(tag.get(Tag_.tag)), "%" + term.toUpperCase() + "%"));
		return JPA.em().createQuery(query).getResultList();
	}

}
