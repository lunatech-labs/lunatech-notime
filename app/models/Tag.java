package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

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
		if (findByTag(tagString).isEmpty())
			return create(tagString);
		else
			return findByTag(tagString).get(0);
	}

	/**
	 * Find all tags
	 * 
	 * @return A List of tag objects
	 */
	public static List<Tag> findAll() {
		return JPA.em().createQuery("from Tag").getResultList();
	}

	/**
	 * Find the tags by a tag's tag value
	 * 
	 * @param tag
	 *            The value of the tag's tag
	 * @return A tag object
	 */
	public static List<Tag> findByTag(String tag) {
		return JPA.em().createQuery("from Tag t where t.tag = :tag")
				.setParameter("tag", tag).getResultList();
	}

	/**
	 * Find the tags which contain a term
	 * 
	 * @param term
	 *            The term the tag's tag needs to contain
	 * @return A List of tag objects
	 */
	public static List<String> findTagsWhichContain(String term) {
		return JPA.em().createQuery("select tag from Tag where tag like :term")
				.setParameter("term", "%" + term + "%").getResultList();
	}

}
