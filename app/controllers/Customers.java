package controllers;

import models.Customer;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

public class Customers extends Controller {
	
	public static Result all() {
		return ok(views.html.customer.customers.render(Customer.all()));
	}
	
	public static Result add() {
		Form<Customer> newForm = form(Customer.class);
		return ok(views.html.customer.createCustomer.render(newForm));
	}
	
	public static Result create() {        
		Form<Customer> filledForm = form(Customer.class).bindFromRequest();
		Customer customerToBeCreated = filledForm.get();
		
		if(filledForm.hasErrors())
			return badRequest(views.html.customer.createCustomer.render(filledForm));
		
		if(Customer.hasDuplicity(customerToBeCreated)) {
			flash("error", Customer.validateDuplicity(customerToBeCreated));
			return badRequest(views.html.customer.createCustomer.render(filledForm));
		}
		
		Customer.create(customerToBeCreated);
		return redirect(routes.Customers.all());
	}
	
	public static Result edit(Long id) {
		Form<Customer> filledForm = form(Customer.class).fill(Customer.read(id));
		return ok(views.html.customer.editCustomer.render(id, filledForm));
	}
	
	public static Result update(Long id) {
		Form<Customer> filledForm = form(Customer.class).bindFromRequest();
		Customer customerToBeUpdated = filledForm.get();
		
		if(filledForm.hasErrors())
			return badRequest(views.html.customer.editCustomer.render(id, filledForm));
		
		if(Customer.hasDuplicity(id, customerToBeUpdated)) {
			flash("error", Customer.validateDuplicity(id, customerToBeUpdated));
			return badRequest(views.html.customer.editCustomer.render(id, filledForm));
		}
		
		Customer.update(id, customerToBeUpdated);
		return redirect(routes.Customers.all());
	}
	
	public static Result delete(Long id) {
		Customer.delete(id);
		return redirect(routes.Customers.all());
	}

}
