package com.serrocal.poc.formatter

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.serrocal.poc.model.{Book, BookCard, User}
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  /*
  Here is one additional quirk: If you explicitly declare the companion
  object for your case class the notation above will stop working.
  You'll have to explicitly refer to the companion objects apply method to fix this
   */
  implicit val userFormat = jsonFormat2(User.apply)
  implicit val bookFormat = jsonFormat3(Book.apply)
  implicit val bookCardFormat = jsonFormat2(BookCard.apply)
}

