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
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import play.data.validation.Constraints.Max;
import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;
import datastructures.TotalsAssignment;
import datastructures.TotalsDay;

@Entity
public class HourEntry {

	@Id
	@GeneratedValue
	public Long id;

	@ManyToOne
	public ProjectAssignment assignment;

	@Required
	@Type(type = "org.joda.time.contrib.hibernate.PersistentLocalDate")
	public LocalDate date;

	@Required
	@Max(23)
	public Integer hours;

	@Required
	@Max(59)
	public Integer minutes;

	@ManyToMany
	@JoinTable(name = "hourentry_tag", joinColumns = @JoinColumn(name = "hourentry_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
	public List<Tag> tags;

	public boolean billable;

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
		billable = true;
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
	 * @param hourEntryId
	 *            The id of the hour entry to be searched for
	 * @return A hour entry
	 */
	public static HourEntry findById(Long hourEntryId) {
		return JPA.em().find(HourEntry.class, hourEntryId);
	}

	/**
	 * Find all hour entries
	 * 
	 * @return A List of hour entry objects
	 */
	public static List<HourEntry> findAll() {
		CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
		CriteriaQuery<HourEntry> query = cb.createQuery(HourEntry.class);
		query.from(HourEntry.class);
		return JPA.em().createQuery(query).getResultList();
	}

	/**
	 * Find all hour entries for a user
	 * 
	 * @param userId
	 *            The id of the user which entries are to be searched for
	 * @return A List of hour entry objects
	 */
	public static List<HourEntry> findAllForUser(Long userId) {
		CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
		CriteriaQuery<HourEntry> query = cb.createQuery(HourEntry.class);
		Root<HourEntry> entry = query.from(HourEntry.class);

		Join<HourEntry, ProjectAssignment> assignment = entry
				.join(HourEntry_.assignment);
		Join<ProjectAssignment, User> user = assignment
				.join(ProjectAssignment_.user);

		query.where(cb.equal(user.get(User_.id), userId));
		query.orderBy(cb.desc(entry.get(HourEntry_.id)));
		return JPA.em().createQuery(query).getResultList();
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
			LocalDate beginDate, LocalDate endDate) {
		CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
		CriteriaQuery<HourEntry> query = cb.createQuery(HourEntry.class);
		Root<HourEntry> entry = query.from(HourEntry.class);

		Join<HourEntry, ProjectAssignment> assignment = entry
				.join(HourEntry_.assignment);
		Join<ProjectAssignment, User> user = assignment
				.join(ProjectAssignment_.user);

		query.where(cb.equal(user.get(User_.id), userId),
				cb.between(entry.get(HourEntry_.date), beginDate, endDate));
		query.orderBy(cb.desc(entry.get(HourEntry_.id)));
		return JPA.em().createQuery(query).getResultList();
	}

	/**
	 * Find all hour entries for a user between for a day
	 * 
	 * @param userId
	 *            The id of the user which entries are to be searched for
	 * @param day
	 *            The date on which entries are to be searched for
	 * @return A List of hour entry objects
	 */
	public static List<HourEntry> findAllForUserForDay(Long userId,
			LocalDate day) {
		CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
		CriteriaQuery<HourEntry> query = cb.createQuery(HourEntry.class);
		Root<HourEntry> entry = query.from(HourEntry.class);

		Join<HourEntry, ProjectAssignment> assignment = entry
				.join(HourEntry_.assignment);
		Join<ProjectAssignment, User> user = assignment
				.join(ProjectAssignment_.user);

		query.where(cb.equal(user.get(User_.id), userId),
				cb.equal(entry.get(HourEntry_.date), day));
		query.orderBy(cb.desc(entry.get(HourEntry_.id)));
		return JPA.em().createQuery(query).getResultList();
	}

	/**
	 * Find all hour entries for a user where he has booked too few hours.
	 * Currently 'too few' is less than 8 hours and less than 60 minutes.
	 * 
	 * @param userId
	 *            The id of the user which entries are to be searched for
	 * @return A List of hour entry objects
	 */
	public static List<TotalsDay> findWithTooFewHoursForUser(Long userId) {
		CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
		CriteriaQuery<TotalsDay> query = cb.createQuery(TotalsDay.class);
		Root<HourEntry> entry = query.from(HourEntry.class);
		query.select(cb.construct(TotalsDay.class, entry.get(HourEntry_.date),
				cb.sumAsLong(entry.get(HourEntry_.hours)),
				cb.sumAsLong(entry.get(HourEntry_.minutes))));

		Join<HourEntry, ProjectAssignment> assignment = entry
				.join(HourEntry_.assignment);
		Join<ProjectAssignment, User> user = assignment
				.join(ProjectAssignment_.user);

		query.where(cb.equal(user.get(User_.id), userId));
		query.having(cb.lessThan(cb.sum(entry.get(HourEntry_.hours)), 8),
				cb.lessThan(cb.sum(entry.get(HourEntry_.minutes)), 60));
		query.groupBy(entry.get(HourEntry_.date));
		query.orderBy(cb.asc(entry.get(HourEntry_.date)));
		return JPA.em().createQuery(query).getResultList();
	}

	/**
	 * Calculates the totals of the hour entries for a user, per assignment,
	 * between two dates. Note that the amount of minutes can be more than 60
	 * 
	 * @param userId
	 *            The id of the user which entries are to be summed
	 * @param beginDate
	 *            The date from which entries are to be searched for
	 * @param endDate
	 *            The date till which entries are to be searched for
	 * @return A List of {@link TotalsAssignment} objects
	 */
	public static List<TotalsAssignment> findTotalsForUserPerAssignmentBetween(
			Long userId, LocalDate beginDate, LocalDate endDate) {
		CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
		CriteriaQuery<TotalsAssignment> query = cb
				.createQuery(TotalsAssignment.class);
		Root<HourEntry> entry = query.from(HourEntry.class);
		query.select(cb.construct(TotalsAssignment.class,
				entry.get(HourEntry_.assignment),
				cb.sumAsLong(entry.get(HourEntry_.hours)),
				cb.sumAsLong(entry.get(HourEntry_.minutes))));

		Join<HourEntry, ProjectAssignment> assignment = entry
				.join(HourEntry_.assignment);
		Join<ProjectAssignment, User> user = assignment
				.join(ProjectAssignment_.user);

		query.where(cb.equal(user.get(User_.id), userId),
				cb.between(entry.get(HourEntry_.date), beginDate, endDate));
		query.groupBy(entry.get(HourEntry_.assignment));
		return JPA.em().createQuery(query).getResultList();
	}

	/**
	 * Calculates the totals of the hour entries for an assignment, between two
	 * dates
	 *
	 * @param assignmentId
	 *            The id of the assignment which entries are to be summed
	 * @param beginDate
	 *            The date from which entries are to be searched for
	 * @param endDate
	 *            The date till which entries are to be searched for
	 * @return A {@link TotalsAssignment} object
	 */
	public static TotalsAssignment findTotalsForAssignmentBetween(
			Long assignmentId, LocalDate beginDate, LocalDate endDate) {
		CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
		CriteriaQuery<TotalsAssignment> query = cb
				.createQuery(TotalsAssignment.class);
		Root<HourEntry> entry = query.from(HourEntry.class);
		query.select(cb.construct(TotalsAssignment.class,
				entry.get(HourEntry_.assignment),
				cb.sumAsLong(entry.get(HourEntry_.hours)),
				cb.sumAsLong(entry.get(HourEntry_.minutes))));

		Join<HourEntry, ProjectAssignment> assignment = entry
				.join(HourEntry_.assignment);

		query.where(
				cb.equal(assignment.get(ProjectAssignment_.id), assignmentId),
				cb.between(entry.get(HourEntry_.date), beginDate, endDate));
		query.groupBy(entry.get(HourEntry_.assignment));
		Query typedQuery = JPA.em().createQuery(query);
		try {
			return (TotalsAssignment) typedQuery.getSingleResult();
		} catch (NoResultException nre) {
			return new TotalsAssignment(
					ProjectAssignment.findById(assignmentId), 0L, 0L);
		} catch (NonUniqueResultException nure) {
			return new TotalsAssignment(
					ProjectAssignment.findById(assignmentId), 0L, 0L);
		}
	}

	/**
	 * Calculates the totals of the hour entries for a user, per day, between
	 * two dates. Note that the amount of minutes can be more than 60
	 * 
	 * @param userId
	 *            The id of the user which entries are to be summed
	 * @param beginDate
	 *            The date from which entries are to be searched for
	 * @param endDate
	 *            The date till which entries are to be searched for
	 * @return A List of {@link TotalsDay} objects
	 */
	public static List<TotalsDay> findTotalsForUserPerDayBetween(Long userId,
			LocalDate beginDate, LocalDate endDate) {
		CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
		CriteriaQuery<TotalsDay> query = cb.createQuery(TotalsDay.class);
		Root<HourEntry> entry = query.from(HourEntry.class);
		query.select(cb.construct(TotalsDay.class, entry.get(HourEntry_.date),
				cb.sumAsLong(entry.get(HourEntry_.hours)),
				cb.sumAsLong(entry.get(HourEntry_.minutes))));

		Join<HourEntry, ProjectAssignment> assignment = entry
				.join(HourEntry_.assignment);
		Join<ProjectAssignment, User> user = assignment
				.join(ProjectAssignment_.user);

		query.where(cb.equal(user.get(User_.id), userId),
				cb.between(entry.get(HourEntry_.date), beginDate, endDate));
		query.groupBy(entry.get(HourEntry_.date));
		query.orderBy(cb.asc(entry.get(HourEntry_.date)));
		return JPA.em().createQuery(query).getResultList();
	}

	/**
	 * Calculates the totals of the hour entries for a user, for a day. Note
	 * that the amount of minutes can be more than 60
	 * 
	 * @param userId
	 *            The id of the user which entries are to be summed
	 * @param day
	 *            The day for which the entries are to be summed
	 * @return A {@link TotalsDay} with the totals
	 */
	public static TotalsDay findTotalsForUserForDay(Long userId, LocalDate day) {
		CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
		CriteriaQuery<TotalsDay> query = cb.createQuery(TotalsDay.class);
		Root<HourEntry> entry = query.from(HourEntry.class);
		query.select(cb.construct(TotalsDay.class, entry.get(HourEntry_.date),
				cb.sumAsLong(entry.get(HourEntry_.hours)),
				cb.sumAsLong(entry.get(HourEntry_.minutes))));

		Join<HourEntry, ProjectAssignment> assignment = entry
				.join(HourEntry_.assignment);
		Join<ProjectAssignment, User> user = assignment
				.join(ProjectAssignment_.user);

		query.where(cb.equal(user.get(User_.id), userId),
				cb.equal(entry.get(HourEntry_.date), day));
		query.groupBy(entry.get(HourEntry_.date));
		Query typedQuery = JPA.em().createQuery(query);
		try {
			return (TotalsDay) typedQuery.getSingleResult();
		} catch (NoResultException nre) {
			return new TotalsDay(day, 0L, 0L);
		} catch (NonUniqueResultException nure) {
			return new TotalsDay(day, 0L, 0L);
		}
	}

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
