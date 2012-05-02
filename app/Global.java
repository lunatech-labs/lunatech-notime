import java.util.LinkedList;
import java.util.List;

import models.User;
import models.security.Role;
import play.Application;
import play.GlobalSettings;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
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
					if (Role.findByRoleName("admin") == null) {
						Role adminRole = new Role();
						adminRole.roleName = "admin";
						adminRole.save();
					}

					if (Role.findByRoleName("user") == null) {
						Role userRole = new Role();
						userRole.roleName = "user";
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
					List<Role> roles = new LinkedList<Role>();
					roles.add(Role.findByRoleName("admin"));
					adminUser.roles = roles;
					adminUser.update(adminUser.id);
				}

			});
		}

	}

}