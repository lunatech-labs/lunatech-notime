package binders.routes

import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat

import play.api.mvc.JavascriptLitteral
import play.api.mvc.PathBindable

object CustomPathBinders {

  val fmt = DateTimeFormat.forPattern("dd-MM-yyyy")

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

  implicit def litteralLocalDate = new JavascriptLitteral[LocalDate] {
    def to(value: LocalDate) = value.toString(fmt)
  }

}