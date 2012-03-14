package models;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import org.joda.time.DateTime;
import org.hibernate.annotations.Type;

import play.data.format.Formats;
import play.data.format.Formatters;
import play.data.validation.Constraints;
import play.data.validation.Constraints.Max;
import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
import util.datastructures.TotalsForUserPerAssignment;

@Entity
public class HourEntry {

	@Id
	@GeneratedValue
	public Long id;

	@ManyToOne
	public ProjectAssignment assignment;

	@Constraints.Required
	@Formats.DateTime(pattern = "dd-MM-yyyy")
	@Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
	public DateTime date;

	@Required
	@Max(23)
	public Integer hours;

	@Required
	@Max(59)
	public Integer minutes;

	@ManyToMany
	@JoinTable(name = "hourentry_tag", joinColumns = @JoinColumn(name = "hourentry_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
	public List<Tag> tags;

	public static List<HourEntry> all() {
		return JPA.em().createQuery("from HourEntry").getResultList();
	}

	public static List<HourEntry> allForUser(Long userId) {
		return JPA
				.em()
				.createQuery(
						"from HourEntry he "
								+ "where he.assignment.user.id = :userId "
								+ "order by he.date desc")
				.setParameter("userId", userId).getResultList();
	}
	
	public static List<HourEntry> allForUserBetween(Long userId, DateTime beginDate,
			DateTime endDate) {
		return JPA
				.em()
				.createQuery(
						"from HourEntry he "
								+ "where he.assignment.user.id = :userId "
								+ "and he.date between :beginDate and :endDate")
				.setParameter("userId", userId)
				.setParameter("beginDate", beginDate)
				.setParameter("endDate", endDate).getResultList();
	}
	
	public static List<TotalsForUserPerAssignment> getTotalsForUserBetween(
			Long userId, DateTime beginDate, DateTime endDate) {
		return JPA
				.em()
				.createQuery(
						"select new util.datastructures.TotalsForUserPerAssignment(he.assignment, sum(he.hours), sum(he.minutes)) from HourEntry he "
								+ "where he.assignment.user.id = :userId "
								+ "and he.date between :beginDate and :endDate "
								+ "group by he.assignment")
				.setParameter("userId", userId)
				.setParameter("beginDate", beginDate)
				.setParameter("endDate", endDate).getResultList();
	}

	public static void create(HourEntry entry, String tagsString) {
		if (!tagsString.isEmpty()) {
			entry.tags = new ArrayList<Tag>();
			String tags[] = tagsString.split(";");
			for (int i = 0; i < tags.length; i++)
				entry.tags.add(Tag.findOrCreate(tags[i]));
		}
		JPA.em().persist(entry);
	}

	public static HourEntry read(Long id) {
		return JPA.em().find(HourEntry.class, id);
	}

	public static void update(Long entryId, HourEntry entryToBeUpdated,
			String tagsString) {
		HourEntry entry = read(entryId);
		entry.assignment = entryToBeUpdated.assignment;
		entry.date = entryToBeUpdated.date;
		entry.hours = entryToBeUpdated.hours;
		entry.minutes = entryToBeUpdated.minutes;

		entry.tags.clear();
		if (!tagsString.isEmpty()) {
			String tags[] = tagsString.split(";");
			for (int i = 0; i < tags.length; i++)
				entry.tags.add(Tag.findOrCreate(tags[i]));
		}

		JPA.em().merge(entry);
	}

	public static void delete(Long id) {
		JPA.em().remove(HourEntry.read(id));
	}

	public static boolean hasValidDate(HourEntry hourEntry) {
		return validateDate(hourEntry).isEmpty();
	}

	public static String validateDate(HourEntry hourEntry) {
		return ProjectAssignment.isDateInAssignmentRange(hourEntry.date,
				hourEntry.assignment.id) ? new String()
				: "Date not in assignment date range";
	}

	public String enteredTagsString() {
		String tagsString = new String();
		for (Tag tag : tags)
			tagsString += tag.tag + "; ";
		return tagsString;
	}

}
