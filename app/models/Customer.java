package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import models.security.UserRole;
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

	public boolean active;

	/**
	 * Inserts this new customer
	 */
	public void save() {
		active = true;
		JPA.em().persist(this);

		if (customerManagers != null) {
			for (User u : customerManagers) {
				User customerManager = User.findById(u.id);
				customerManager.assignRole(UserRole.customerManagerRole());
			}
		}
	}

	/**
	 * Updates this customer
	 * 
	 * @param customerId
	 *            The id of the customer that is going to be updated
	 */
	public void update(Long customerId) {
		this.id = customerId;
		updateCustomerManagerRoles();
		JPA.em().merge(this);
		if (!active)
			inactivateProjects();
	}

	/**
	 * Deletes this customer
	 * 
	 * @return true if the project is removed
	 */
	public boolean delete() {
		boolean deletable = isDeletable();
		if (deletable) {
			JPA.em().remove(this);
		}
		return deletable;
	}

	/**
	 * Inactivates this customer.
	 */
	public void inactivate() {
		active = false;
		update(id);
		inactivateProjects();
	}

	/**
	 * Inactivates all its projects.
	 */
	private void inactivateProjects() {
		for (Project project : Project.findAllActiveForCustomer(this)) {
			project.inactivate();
		}
	}

	/**
	 * Checks if a customer is deletable. A customer is deletable when all its
	 * projects are deletable
	 * 
	 * @return true if the project is deletable
	 */
	public boolean isDeletable() {
		for (Project project : Project.findAllForCustomer(this)) {
			if (!project.isDeletable()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Updates the CustomerManager roles. Must be called before the actual
	 * update is done! This is because the updates are based on the differences
	 * between the saved customerManagers and the submitted customersManagers
	 */
	public void updateCustomerManagerRoles() {
		UserRole customerManagerRole = UserRole.customerManagerRole();
		List<User> savedCustomerManagers = findById(id).customerManagers;
		List<User> currentCustomerManagers = new ArrayList<User>();

		if (customerManagers != null) {
			currentCustomerManagers.addAll(customerManagers);
		}

		if (savedCustomerManagers.isEmpty()
				&& !currentCustomerManagers.isEmpty()) {
			// assign role to all current managers
			for (User currentUser : currentCustomerManagers) {
				// current CustomerManagers aren't loaded
				User user = User.findById(currentUser.id);
				if (!user.isCustomerManager())
					user.assignRole(customerManagerRole);
			}
		}
		if (!savedCustomerManagers.isEmpty()
				&& currentCustomerManagers.isEmpty()) {
			// remove role form all saved managers
			for (User user : savedCustomerManagers) {
				// is this the user's last customer he was manager of?
				if (findAllForCustomerManager(user).size() == 1)
					user.removeRole(customerManagerRole);
			}
		}
		if (!savedCustomerManagers.isEmpty()
				&& !currentCustomerManagers.isEmpty()) {
			List<User> newCustomerManagers = currentCustomerManagers;

			for (User savedUser : savedCustomerManagers) {
				if (currentCustomerManagers.contains(savedUser)) {
					// savedUser is already a customer manager
					newCustomerManagers.remove(savedUser);
				} else {
					// is this the user's last customer he was manager of?
					if (findAllForCustomerManager(savedUser).size() == 1) {
						// savedUser is no longer customer manager
						savedUser.removeRole(customerManagerRole);
					}
				}
			}
			for (User newUser : newCustomerManagers) {
				User loadedNewUser = User.findById(newUser.id);
				if (!loadedNewUser.containsRole(customerManagerRole)) {
					// user isn't loaded
					loadedNewUser.assignRole(customerManagerRole);
				}

			}
		}

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
		CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
		CriteriaQuery<Customer> query = cb.createQuery(Customer.class);
		query.from(Customer.class);
		return JPA.em().createQuery(query).getResultList();
	}

	/**
	 * Find all active customers
	 * 
	 * @return @return A List of customer objects
	 */
	public static List<Customer> findAllActive() {
		CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
		CriteriaQuery<Customer> query = cb.createQuery(Customer.class);
		Root<Customer> customer = query.from(Customer.class);
		query.where(cb.isTrue(customer.get(Customer_.active)));
		return JPA.em().createQuery(query).getResultList();
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
	 * Find all customers from which a user is customer manager
	 * 
	 * @param user
	 *            The {@link User} which should be the customer manager
	 * @return A List of customer objects
	 */
	public static List<Customer> findAllForCustomerManager(User user) {
		CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
		CriteriaQuery<Customer> query = cb.createQuery(Customer.class);
		Root<Customer> customer = query.from(Customer.class);
		Join<Customer, User> customerManager = customer
				.join(Customer_.customerManagers);
		query.where(cb.equal(customerManager, user));
		return JPA.em().createQuery(query).getResultList();
	}

	/**
	 * All existing customers
	 * 
	 * @return A Map with as key the customer's id and as value the customer's
	 *         name
	 */
	public static Map<String, String> options() {
		LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
		for (Customer c : findAllActive()) {
			options.put(c.id.toString(), c.name);
		}
		return options;
	}

	public String validate() {
		if (hasDuplicateName())
			return "Duplicate name!";
		if (hasDuplicateCode())
			return "Duplicate code!";
		return null;
	}

	public boolean hasDuplicateName() {
		List<Customer> customers = Collections.emptyList();
		if (id != null)
			customers = findAllExcept(id);
		else
			customers = findAll();

		for (Customer customer : customers) {
			if (customer.name.equalsIgnoreCase(name))
				return true;
		}
		return false;
	}

	public boolean hasDuplicateCode() {
		List<Customer> customers = Collections.emptyList();
		if (id != null)
			customers = findAllExcept(id);
		else
			customers = findAll();

		for (Customer customer : customers) {
			if (customer.code.equalsIgnoreCase(code))
				return true;
		}
		return false;
	}

}