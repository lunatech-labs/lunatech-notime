import play.Application;
import play.GlobalSettings;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

public class Global extends GlobalSettings {
    
	@Transactional
    public void onStart(Application app) {
        InitialData.insert(app);
    }
    
    static class InitialData {
        
        public static void insert(Application app) {
        	//JPA.em().persist("null");
        }
    }
    
}