import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "NoTime"
    val appVersion      = "1.0-SNAPSHOT"

    // "mysql" % "mysql-connector-java" % "5.1.18"
    // "postgresql" % "postgresql" % "9.1-901-1.jdbc4"
    val appDependencies = Seq(
    		"org.mindrot" % "jbcrypt" % "0.3m",
    		"joda-time" % "joda-time" % "2.0",
    		"org.hibernate" % "hibernate-entitymanager" % "3.6.9.Final",    		
    		"org.hibernate" % "hibernate-jpamodelgen" % "1.2.0.Final",
    		"postgresql" % "postgresql" % "9.1-901-1.jdbc4"
    )
    
    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
    		ebeanEnabled := false,
    		routesImport ++= Seq("binders.routes.CustomPathBinders._", "org.joda.time.LocalDate"),
    		javacOptions ++= Seq("-s", "metamodel")
    )

}
