package models;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
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
@SequenceGenerator(name = "customer_seq", sequenceName = "customer_seq")
public class Customer {

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_seq")
	public Long id;
	
	@Required
	@Column(unique = true)
	public String name;
	
	@Required
	@Column(unique = true)
	public String code;
	
	public String description;
	
	@ManyToMany
	@JoinTable(
		name="customermanager", 
		joinColumns=@JoinColumn(name="customer_id"), 
		inverseJoinColumns=@JoinColumn(name="user_id")
	)
	public List<User> customerManagers;
		
	public static List<Customer> all() {
		return JPA.em().createQuery("from Customer").getResultList();
	}
	
	public static List<Customer> allExcept(Long id) {
		List<Customer> customers = all();
		customers.remove(read(id));
		return customers;
	}
	
	public static void create(Customer customer) {
		JPA.em().persist(customer);
	}
	
	public static Customer read(Long id) {
		return JPA.em().find(Customer.class, id);
	}
	
	public static void update(Long id, Customer customerToBeUpdated) {
		Customer customer = read(id);
		customer.name = customerToBeUpdated.name;
		customer.code = customerToBeUpdated.code;
		customer.description = customerToBeUpdated.description;		
		customer.customerManagers = customerToBeUpdated.customerManagers;		
		JPA.em().merge(customer);
	}
	
	public static void delete(Long id) {
		JPA.em().remove(Customer.read(id));
	}
	
    public static Map<String,String> options() {
        List<Customer> customers = all();
        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
        for(Customer c : customers) {
            options.put(c.id.toString(), c.name);
        }
        return options;
    }
	
	public static boolean hasDuplicity(Customer customerToBeCreated) {
        return !validateDuplicity(customerToBeCreated).isEmpty();
    }

    public static String validateDuplicity(Customer customerToBeCreated) {
        for(Customer existingCustomer : all()) {
            if(existingCustomer.name.equalsIgnoreCase(customerToBeCreated.name))
                return "Duplicate name!";
            if(existingCustomer.code.equalsIgnoreCase(customerToBeCreated.code))
                return "Duplicate code!";
        }
        return new String();
    }

    public static boolean hasDuplicity(Long id, Customer customerToBeUpdated) {
        return !validateDuplicity(id, customerToBeUpdated).isEmpty();
    }
    
    public static String validateDuplicity(Long id, Customer customerToBeUpdated) {
    	for(Customer existingCustomer : allExcept(id)) {
            if(existingCustomer.name.equalsIgnoreCase(customerToBeUpdated.name))
                return "Duplicate name!";
            if(existingCustomer.code.equalsIgnoreCase(customerToBeUpdated.code))
                return "Duplicate code!";
        }
        return new String();
    }
}