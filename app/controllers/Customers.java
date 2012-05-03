package controllers;

import be.objectify.deadbolt.actions.Restrict;
import models.Customer;
import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.admin.customer.createCustomer;
import views.html.admin.customer.customers;
import views.html.admin.customer.editCustomer;

public class Customers extends Controller {

	@Transactional(readOnly = true)
	@Restrict("admin")
	public static Result all() {
		return ok(customers.render(Customer.findAll()));
	}

	@Transactional(readOnly = true)
	@Restrict("admin")
	public static Result add() {
		Form<Customer> newForm = form(Customer.class);
		return ok(createCustomer.render(newForm));
	}

	@Transactional
	@Restrict("admin")
	public static Result create() {
		Form<Customer> filledForm = form(Customer.class).bindFromRequest();

		if (filledForm.hasErrors())
			return badRequest(createCustomer.render(filledForm));

		filledForm.get().save();
		return redirect(routes.Customers.all());
	}

	@Transactional(readOnly = true)
	@Restrict("admin")
	public static Result edit(Long id) {
		Form<Customer> filledForm = form(Customer.class).fill(
				Customer.findById(id));
		return ok(editCustomer.render(id, filledForm));
	}

	@Transactional
	@Restrict("admin")
	public static Result update(Long id) {
		Form<Customer> filledForm = form(Customer.class).bindFromRequest();

		if (filledForm.hasErrors())
			return badRequest(editCustomer.render(id, filledForm));

		filledForm.get().update(id);
		return redirect(routes.Customers.all());
	}

	@Transactional
	@Restrict("admin")
	public static Result delete(Long id) {
		if (!Customer.findById(id).delete()) {
			flash("error",
					"The customer could not be deleted. Probably one of its projects is not deletable.");
		}
		return redirect(routes.Customers.all());
	}

}
