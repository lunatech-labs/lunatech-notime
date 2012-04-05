import binders.form.CustomDataBinders;
import play.Application;
import play.GlobalSettings;
import play.db.jpa.Transactional;

public class Global extends GlobalSettings {

	@Transactional
	public void onStart(Application app) {
		InitialData.insert(app);
		registerFormatters();
	}

	private void registerFormatters() {
		new CustomDataBinders().register();
	}

	static class InitialData {

		public static void insert(Application app) {
			// JPA.em().persist("null");
		}
	}

}