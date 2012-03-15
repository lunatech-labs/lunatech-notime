package models;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;

import play.data.validation.Constraints.Required;
import play.db.jpa.JPA;

@Entity
public class Customer {

	@Id
	@GeneratedValue
	public Long id;

	@Required
	@Column(unique = true)
	public String name;

	@Required
	@Column(unique = true)
	public String code;

	public String description;

	@ManyToMany
	@JoinTable(name = "customermanager", joinColumns = @JoinColumn(name = "customer_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
	public List<User> customerManagers;

	/**
	 * Inserts this new customer
	 */
	public void save() {
		JPA.em().persist(this);
	}

	/**
	 * Updates this customer
	 * 
	 * @param customerId
	 *            The id of the customer that is going to be updated
	 */
	public void update(Long customerId) {
		this.id = customerId;
		JPA.em().merge(this);
	}

	/**
	 * Deletes this customer
	 */
	public void delete() {
		JPA.em().remove(this);
	}

	/**
	 * Find a customer by id
	 * 
	 * @param customerId
	 *            The id of the customer to be searched for
	 * @return A customer
	 */
	public static Customer findById(Long customerId) {
		return JPA.em().find(Customer.class, customerId);
	}

	/**
	 * Find all customers
	 * 
	 * @return A List of customer objects
	 */
	public static List<Customer> findAll() {
		return JPA.em().createQuery("from Customer").getResultList();
	}

	/**
	 * Find all customers except one customer
	 * 
	 * @param customerId
	 *            The id of the customer that needs to be filtered
	 * @return A List of customer objects
	 */
	public static List<Customer> findAllExcept(Long customerId) {
		List<Customer> customers = findAll();
		customers.remove(findById(customerId));
		return customers;
	}

	/**
	 * All existing customers
	 * 
	 * @return A Map with as key the customer's id and as value the customer's
	 *         name
	 */
	public static Map<String, String> options() {
		LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
		for (Customer c : findAll()) {
			options.put(c.id.toString(), c.name);
		}
		return options;
	}

	// VALIDATION METHODS NEED TO BE REPLACED BY ANNOTATIONS OR BE REWRITTEN
	public static boolean hasDuplicity(Customer customerToBeCreated) {
		return !validateDuplicity(customerToBeCreated).isEmpty();
	}

	public static String validateDuplicity(Customer customerToBeCreated) {
		for (Customer existingCustomer : findAll()) {
			if (existingCustomer.name
					.equalsIgnoreCase(customerToBeCreated.name))
				return "Duplicate name!";
			if (existingCustomer.code
					.equalsIgnoreCase(customerToBeCreated.code))
				return "Duplicate code!";
		}
		return new String();
	}

	public static boolean hasDuplicity(Long id, Customer customerToBeUpdated) {
		return !validateDuplicity(id, customerToBeUpdated).isEmpty();
	}

	public static String validateDuplicity(Long id, Customer customerToBeUpdated) {
		for (Customer existingCustomer : findAllExcept(id)) {
			if (existingCustomer.name
					.equalsIgnoreCase(customerToBeUpdated.name))
				return "Duplicate name!";
			if (existingCustomer.code
					.equalsIgnoreCase(customerToBeUpdated.code))
				return "Duplicate code!";
		}
		return new String();
	}
}