package models;

import java.util.List;

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

	public static String tagsString() {
		List<String> tags = JPA.em().createQuery("select tag from Tag").getResultList();
		String tagsString = new String();
		for(int i = 0; i < tags.size(); i++) {
			if(tags.size()-1 == i) {
				tagsString += tags.get(i) + ".";
			}
			else {
				tagsString += tags.get(i) + ", ";
			}		
		}
		return tagsString;
	}
}
