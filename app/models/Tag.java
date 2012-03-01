package models;

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
	public String tag;
	
	public static void create(Tag tag) {
		JPA.em().persist(tag);
	}
	
	public static Tag create(String tagString) {
		Tag tag = new Tag();
		tag.tag = tagString.trim();
		JPA.em().persist(tag);
		return tag;
	}
}
