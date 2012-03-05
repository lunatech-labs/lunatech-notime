package controllers;

import models.Customer;
import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.admin.customer.createCustomer;
import views.html.admin.customer.customers;
import views.html.admin.customer.editCustomer;

public class Customers extends Controller {
	
	@Transactional(readOnly=true)
	public static Result all() {
		return ok(customers.render(Customer.all()));
	}
	
	@Transactional(readOnly=true)
	public static Result add() {
		Form<Customer> newForm = form(Customer.class);
		return ok(createCustomer.render(newForm));
	}
	
	@Transactional
	public static Result create() {        
		Form<Customer> filledForm = form(Customer.class).bindFromRequest();
				
		if(filledForm.hasErrors())
			return badRequest(createCustomer.render(filledForm));
		
		Customer customerToBeCreated = filledForm.get();
		
		if(Customer.hasDuplicity(customerToBeCreated)) {
			flash("error", Customer.validateDuplicity(customerToBeCreated));
			return badRequest(createCustomer.render(filledForm));
		}
		
		Customer.create(customerToBeCreated);
		return redirect(routes.Customers.all());
	}
	
	@Transactional(readOnly=true)
	public static Result edit(Long id) {
		Form<Customer> filledForm = form(Customer.class).fill(Customer.read(id));
		return ok(editCustomer.render(id, filledForm));
	}
	
	@Transactional
	public static Result update(Long id) {
		Form<Customer> filledForm = form(Customer.class).bindFromRequest();
			
		if(filledForm.hasErrors())
			return badRequest(editCustomer.render(id, filledForm));
		
		Customer customerToBeUpdated = filledForm.get();
		
		if(Customer.hasDuplicity(id, customerToBeUpdated)) {
			flash("error", Customer.validateDuplicity(id, customerToBeUpdated));
			return badRequest(editCustomer.render(id, filledForm));
		}
		
		Customer.update(id, customerToBeUpdated);
		return redirect(routes.Customers.all());
	}
	
	@Transactional
	public static Result delete(Long id) {
		Customer.delete(id);
		return redirect(routes.Customers.all());
	}

}
