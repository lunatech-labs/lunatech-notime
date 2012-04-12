package binders.routes

import play.api.mvc.PathBindable
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import play.api.mvc.JavascriptLitteral
import org.joda.time.LocalDate

object CustomPathBinders {

  val fmt = org.joda.time.format.DateTimeFormat.forPattern("dd-MM-yyyy")
  
  implicit def bindableLocalDate = new PathBindable[LocalDate] {
    
    def bind(key: String, value: String) = {      
      try {
        Right(fmt parseLocalDate value)
      } catch {
        case e: IllegalArgumentException => Left("Cannot parse parameter " + key + " as org.joda.time.LocalDate: " + e.getMessage)
      }      
    }
    
    def unbind(key: String, value: LocalDate) = value.toString(fmt)
    
  }
  
  implicit def litteralBoolean = new JavascriptLitteral[LocalDate] {
    def to(value: LocalDate) = value.toString(fmt)
  }

}