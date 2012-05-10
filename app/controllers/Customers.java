package controllers;

import be.objectify.deadbolt.actions.Restrict;
import models.Customer;
import play.data.Form;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.customer.createCustomer;
import views.html.customer.customers;
import views.html.customer.editCustomer;

@Security.Authenticated(Secured.class)
@Restrict("admin")
public class Customers extends Controller {

	@Transactional(readOnly = true)
	public static Result all() {
		return ok(customers.render(Customer.findAll()));
	}

	@Transactional(readOnly = true)
	public static Result add() {
		Form<Customer> newForm = form(Customer.class);
		return ok(createCustomer.render(newForm));
	}

	@Transactional
	public static Result create() {
		Form<Customer> filledForm = form(Customer.class).bindFromRequest();

		if (filledForm.hasErrors())
			return badRequest(createCustomer.render(filledForm));

		filledForm.get().save();
		return redirect(routes.Customers.all());
	}

	@Transactional(readOnly = true)
	public static Result edit(Long id) {
		Form<Customer> filledForm = form(Customer.class).fill(
				Customer.findById(id));
		return ok(editCustomer.render(id, filledForm));
	}

	@Transactional
	public static Result update(Long id) {
		Form<Customer> filledForm = form(Customer.class).bindFromRequest();

		if (filledForm.hasErrors())
			return badRequest(editCustomer.render(id, filledForm));

		filledForm.get().update(id);
		return redirect(routes.Customers.all());
	}

	@Transactional
	public static Result delete(Long id) {
		if (!Customer.findById(id).delete()) {
			flash("error", Messages.get("customer.notDeletable"));
		}
		return redirect(routes.Customers.all());
	}

}
