import play.*;
import play.libs.*;

import java.util.*;

import com.avaje.ebean.*;

import models.*;

public class Global extends GlobalSettings {
    
    public void onStart(Application app) {
        InitialData.insert(app);
    }
    
    static class InitialData {
        
        public static void insert(Application app) {
//            if(Ebean.find(User.class).findRowCount() == 0) {
//                
//                Map<String,List<Object>> all = (Map<String,List<Object>>)Yaml.load("initial-data.yml");
//
//                // Insert users first
//                Ebean.save(all.get("users"));
//
//                // Insert customers
//                Ebean.save(all.get("customers"));
//                for(Object customer : all.get("customers")) {
//                    // Insert the user/customer relation
//                    Ebean.saveManyToManyAssociations(customer, "customerManagers");
//                }
//                
//                // Insert projects
//                Ebean.save(all.get("projects"));
//                for(Object project : all.get("projects")) {
//                    // Insert the project/customer relation
//                    Ebean.saveAssociation(project, "customer");
//                    // Insert the project/user relation
//                    Ebean.saveAssociation(project, "projectManager");
//                }
//                
//                // Insert assignments
//                Ebean.save(all.get("projectAssignments"));
//                for(Object projectAssignment : all.get("projectAssignments")) {
//                	System.out.println(projectAssignment.toString());
//                	Ebean.saveAssociation(projectAssignment, "project");
//                	Ebean.saveAssociation(projectAssignment, "user");
//                }
//                
//            }
        }
        
    }
    
}