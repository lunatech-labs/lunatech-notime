package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import play.data.format.Formats;
import play.data.validation.Constraints.Max;
import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
import util.datastructures.TotalsPerAssignment;

@Entity
public class HourEntry {

	@Id
	@GeneratedValue
	public Long id;

	@ManyToOne
	public ProjectAssignment assignment;

	@Required
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
	
	/**
	 * Sets the tags and inserts this hour entry
	 * 
	 * @param tagsString
	 *            A String of tags, delimited by a semicolon
	 */
	public void save(String tagsString) {
		if (!tagsString.isEmpty()) {
			this.tags = new ArrayList<Tag>();
			String tags[] = tagsString.split(";");
			for (int i = 0; i < tags.length; i++)
				this.tags.add(Tag.findOrCreate(tags[i]));
		}
		JPA.em().persist(this);
	}

	/**
	 * Sets the tags and updates this hour entry
	 * 
	 * @param entryId
	 *            The id of the hour entry that is going to be updated
	 * @param tagsString
	 *            A String of tags, delimited by a semicolon
	 */
	public void update(Long entryId, String tagsString) {
		this.id = entryId;
		this.tags = new ArrayList<Tag>();
		if (!tagsString.isEmpty()) {
			String tags[] = tagsString.split(";");
			for (int i = 0; i < tags.length; i++)
				this.tags.add(Tag.findOrCreate(tags[i]));
		}
		JPA.em().merge(this);
	}

	/**
	 * Deletes this hour entry
	 */
	public void delete() {
		JPA.em().remove(this);
	}

	/**
	 * Find a hour entry by id
	 * 
	 * @param userId
	 *            The id of the hour entry to be searched for
	 * @return A hour entry
	 */
	public static HourEntry findById(Long userId) {
		return JPA.em().find(HourEntry.class, userId);
	}

	/**
	 * Find all hour entries
	 * 
	 * @return A List of hour entry objects
	 */
	public static List<HourEntry> findAll() {
		return JPA.em().createQuery("from HourEntry").getResultList();
	}

	/**
	 * Find all hour entries for a user
	 * 
	 * @param userId
	 *            The id of the user which entries are to be searched for
	 * @return A List of hour entry objects
	 */
	public static List<HourEntry> findAllForUser(Long userId) {
		return JPA
				.em()
				.createQuery(
						"from HourEntry he "
								+ "where he.assignment.user.id = :userId "
								+ "order by he.date desc")
				.setParameter("userId", userId).getResultList();
	}

	/**
	 * Find all hour entries for a user between two dates
	 * 
	 * @param userId
	 *            The id of the user which entries are to be searched for
	 * @param beginDate
	 *            The date from which entries are to be searched for
	 * @param endDate
	 *            The date till which entries are to be searched for
	 * @return A List of hour entry objects
	 */
	public static List<HourEntry> findAllForUserBetween(Long userId,
			DateTime beginDate, DateTime endDate) {
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

	/**
	 * Find the totals of the hour entries for a user, per assignment, between two dates
	 * 
	 * @param userId
	 *            The id of the user which entries are to be searched for
	 * @param beginDate
	 *            The date from which entries are to be searched for
	 * @param endDate
	 *            The date till which entries are to be searched for
	 * @return A List of {@link TotalsPerAssignment} objects
	 */
	public static List<TotalsPerAssignment> getTotalsForUserPerAssignmentBetween(
			Long userId, DateTime beginDate, DateTime endDate) {
		return JPA
				.em()
				.createQuery(
						"select new util.datastructures.TotalsPerAssignment(he.assignment, sum(he.hours), sum(he.minutes)) from HourEntry he "
								+ "where he.assignment.user.id = :userId "
								+ "and he.date between :beginDate and :endDate "
								+ "group by he.assignment")
				.setParameter("userId", userId)
				.setParameter("beginDate", beginDate)
				.setParameter("endDate", endDate).getResultList();
	}
	
//	public static List<TotalsPerAssignment> getTotalForAssignmentBetween(Long assignmentId, DateTime beginDate, DateTime endDate) {
//		CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
//		
//		CriteriaQuery<HourEntry> query = cb.createQuery(HourEntry.class);
//		Root<HourEntry> type = query.from(HourEntry.class);
//		ParameterExpression<Long> parAssignmentId = cb.parameter(Long.class);
//		ParameterExpression<DateTime> parBeginDate = cb.parameter(DateTime.class);
//		ParameterExpression<DateTime> parEndDate = cb.parameter(DateTime.class);
//		query.select(type).where(
//				cb.equal(type.get("assignment_id"), parAssignmentId),
//				cb.between(type.get("date"), parBeginDate, parEndDate));
//		
//		return null;
//		
//	}

	/**
	 * Creates a String of the related tags, delimited by a semicolon
	 * 
	 * @return String of related tags, delimited by a semicolon
	 */
	public String enteredTagsString() {
		String tagsString = new String();
		for (Tag tag : tags)
			tagsString += tag.tag + "; ";
		return tagsString;
	}

	public String formattedDate() {
		DateTimeFormatter fmt = DateTimeFormat.forPattern("dd-MM-yyyy");
		return date.toString(fmt);
	}
	
	// VALIDATION METHODS NEED TO BE REPLACED BY ANNOTATIONS OR BE REWRITTEN
	public String validate() {
		if (!isValidAssignment())
			return "Project is not valid!";
		if (!isDateInRange())
			return "Date is not in assigned range!";		
		return null;
	}
	
	public boolean isDateInRange() {
		return ProjectAssignment.isDateInAssignmentRange(date, assignment.id);
	}

	public boolean isValidAssignment() {
		return assignment.id != null;
	}

}
