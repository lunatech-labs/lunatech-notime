package binders.routes

import play.api.mvc.PathBindable
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import play.api.mvc.JavascriptLitteral

object CustomPathBinders {

  val fmt = org.joda.time.format.DateTimeFormat.forPattern("dd-MM-yyyy")
  
  implicit def bindableDateTime = new PathBindable[DateTime] {
    
    def bind(key: String, value: String) = {      
      try {
        Right(fmt parseDateTime value)
      } catch {
        case e: IllegalArgumentException => Left("Cannot parse parameter " + key + " as org.joda.time.DateTime: " + e.getMessage)
      }      
    }
    
    def unbind(key: String, value: DateTime) = value.toString(fmt)
    
  }
  
  implicit def litteralBoolean = new JavascriptLitteral[DateTime] {
    def to(value: DateTime) = value.toString(fmt)
  }

}