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
@SequenceGenerator(name = "tag_seq", sequenceName = "tag_seq")
public class Tag {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tag_seq")
	public Long id;

	@Required
	@Column(unique = true)
	public String tag;

	public static List<Tag> all() {
		return JPA.em().createQuery("from Tag").getResultList();
	}

	public static List<Tag> find(String tag) {
		return JPA.em().createQuery("from Tag t where t.tag = :tag")
				.setParameter("tag", tag).getResultList();
	}

	public static List<String> findTagsWhichContain(String term) {
		return JPA.em().createQuery("select tag from Tag where tag like :term")
				.setParameter("term", "%" + term + "%").getResultList();
	}

	public static void create(Tag tag) {
		JPA.em().persist(tag);
	}

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

	public static Tag findOrCreate(String tagString) {
		tagString = tagString.trim();
		if (find(tagString).isEmpty())
			return create(tagString);
		else
			return find(tagString).get(0);
	}

}
