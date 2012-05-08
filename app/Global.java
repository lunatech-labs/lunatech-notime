import java.util.LinkedList;
import java.util.List;

import models.User;
import models.security.UserRole;
import play.Application;
import play.GlobalSettings;
import play.db.jpa.JPA;
import binders.form.CustomDataBinders;

public class Global extends GlobalSettings {

	public void onStart(Application app) {
		InitialData.insert(app);
		registerFormatters();
	}

	private void registerFormatters() {
		new CustomDataBinders().register();
	}

	static class InitialData {

		public static void insert(Application app) {
			JPA.withTransaction(new play.libs.F.Callback0() {

				@Override
				public void invoke() throws Throwable {
					createDefaultRoles();

					if (!hasAnAdminUser()) {
						User adminUser = findAdminUser();
						if (adminUser == null)
							adminUser = createAdminUser();
						assignAdminRole(adminUser);
					}
				}

				private void createDefaultRoles() {
					if (UserRole.adminRole() == null) {
						UserRole adminRole = new UserRole();
						adminRole.roleName = "admin";
						adminRole.save();
					}

					if (UserRole.userRole() == null) {
						UserRole userRole = new UserRole();
						userRole.roleName = "user";
						userRole.save();
					}

					if (UserRole.customerManagerRole() == null) {
						UserRole userRole = new UserRole();
						userRole.roleName = "customerManager";
						userRole.save();
					}

					if (UserRole.projectManagerRole() == null) {
						UserRole userRole = new UserRole();
						userRole.roleName = "projectManager";
						userRole.save();
					}
				}

				private boolean hasAnAdminUser() {
					return User.findAllForRole("admin").size() > 0;
				}

				private User findAdminUser() {
					User adminUser = User.findByUsername("admin");
					if (adminUser == null)
						adminUser = User.findByEmail("change@email.now");
					return adminUser;
				}

				private User createAdminUser() {
					User adminUser = new User();
					adminUser.username = "admin";
					adminUser.password = "admin";
					adminUser.fullname = "Administrator";
					adminUser.email = "change@email.now";
					adminUser.save();
					return adminUser;
				}

				private void assignAdminRole(User adminUser) {
					List<UserRole> roles = new LinkedList<UserRole>();
					roles.add(UserRole.adminRole());
					adminUser.userRoles = roles;
					adminUser.update(adminUser.id);
				}

			});
		}

	}

}