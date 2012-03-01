import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "NoTime"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
    		"org.mindrot" % "jbcrypt" % "0.3m",
    		"org.hibernate" % "hibernate-entitymanager" % "3.6.9.Final"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
    		ebeanEnabled := false
    )

}
