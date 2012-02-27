package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
public class Customer extends Model {

	@Id
	public Long id;
	
	@Required
	@Column(unique = true)
	public String name;
	
	@Required
	@Column(unique = true)
	public String code;
	
	public String description;
	
	@ManyToMany
	public List<User> customerManagers;
	
	public static Finder<Long, Customer> find = new Finder<Long, Customer>(Long.class, Customer.class);
	
	public static List<Customer> all() {
		return find.all();
	}
	
	public static List<Customer> allExcept(Long id) {
		List<Customer> customers = all();
		customers.remove(read(id));
		return customers;
	}
	
	public static void create(Customer customer) {
		customer.save();
		customer.saveManyToManyAssociations("customerManagers");
	}
	
	public static Customer read(Long id) {
		return find.byId(id);		
	}
	
	public static void update(Long id, Customer customerToBeUpdated) {
		Customer customer = read(id);
		customer.name = customerToBeUpdated.name;
		customer.code = customerToBeUpdated.code;
		customer.description = customerToBeUpdated.description;
		
		customer.customerManagers.size(); //Workaround: http://groups.google.com/group/ebean/msg/51cd9fabb338fa52
		customer.customerManagers.clear();
		customer.customerManagers.addAll(customerToBeUpdated.customerManagers);
		
		customer.update();
		customer.saveManyToManyAssociations("customerManagers");
	}
	
	public static void delete(Long id) {
		read(id).delete();
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