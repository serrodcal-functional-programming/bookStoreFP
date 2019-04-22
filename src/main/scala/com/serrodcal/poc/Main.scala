package com.serrodcal.poc

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.stream.{ActorMaterializer, Materializer}
import akka.http.scaladsl.server.Directives._
import cats.effect.IO
import com.serrodcal.poc.service.LibraryService
import com.typesafe.config.ConfigFactory
import config.DBAccess
import model.BookCard
import scalikejdbc.AutoSession

import scala.concurrent.Future
import scala.io.StdIn
import scala.util.{Failure, Success}

object Main extends App{

  implicit val config = ConfigFactory.load()

  implicit val system: ActorSystem = ActorSystem("bookCard")
  implicit val materializer: Materializer = ActorMaterializer()

  // needed for the future flatMap/onComplete in the end
  implicit val executionContext = system.dispatcher

  implicit val logger = Logging(system, getClass)

  val dbConfig = DBAccess.pure[IO].initConfig()
  implicit val session = AutoSession

  dbConfig.unsafeRunSync()

  val host = config.getString("server.host")
  val port = config.getString("server.port")

  val route = get{
    path("bookCard" / Segment ) { email =>
      logger.info(s"Received request with email: ${email}")
      val program: IO[BookCard] = new LibraryService[IO].getUserBookCard(email)
      val resultAsync: Future[BookCard] = program.unsafeToFuture()
      onComplete(resultAsync) {
        case Success(bookCard) => complete(bookCard.toString)
        case Failure(_)        => complete(StatusCodes.NotFound, "User not found.")
      }
    }
  }

  val bindingFuture = Http().bindAndHandle(route, host, port.toInt)

  logger.info(s"Server online at http://$host:$port/\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return
  logger.info(s"Server stopped :(")
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done

}
