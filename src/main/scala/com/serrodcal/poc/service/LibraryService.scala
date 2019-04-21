package com.serrodcal.poc.service

import cats.effect.Sync
import cats.syntax.all._
import com.serrodcal.poc.exception.UserNotFound
import com.serrodcal.poc.model.{BookCard, User}
import com.serrodcal.poc.repository.{BookRepository, UserRepository}
import com.serrodcal.poc.exception.UserNotFound
import com.serrodcal.poc.model
import com.serrodcal.poc.model.{BookCard, User}
import com.serrodcal.poc.repository.{BookRepository, UserRepository}

class LibraryService[F[_]: Sync: UserRepository: BookRepository] {

  def getUserBookCard(email: String): F[BookCard] = {
    (for {
      maybeUser <- UserRepository[F].findUserByEmail(email)
      user <- maybeUser.fold[F[User]](Sync[F].raiseError(UserNotFound))(Sync[F].pure)
      books <- BookRepository[F].findBooksByUser(user.id)
    } yield model.BookCard(user, books))
      .handleErrorWith { error =>
        Sync[F].delay(println(s"Error when getBookCard with email $email"))
          .*>(Sync[F].raiseError(error))
      }
  }
}
