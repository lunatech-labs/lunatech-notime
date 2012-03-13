import play.Application;
import play.GlobalSettings;
import play.db.jpa.Transactional;
import util.form.binders.CustomDataBinders;

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