package com.serrocal.poc.service

import cats.effect.Sync
import cats.syntax.all._
import com.serrocal.poc.exception.UserNotFound
import com.serrocal.poc.model.{BookCard, User}
import com.serrocal.poc.repository.{BookRepository, UserRepository}

class LibraryService[F[_]: Sync: UserRepository: BookRepository] {

  def getUserBookCard(email: String): F[BookCard] = {
    (for {
      maybeUser <- UserRepository[F].findUserByEmail(email)
      user <- maybeUser.fold[F[User]](Sync[F].raiseError(UserNotFound))(Sync[F].pure)
      books <- BookRepository[F].findBooksByUser(user.id)
    } yield BookCard(user, books))
      .handleErrorWith { error =>
        Sync[F].delay(println(s"Error when getBookCard with email $email"))
          .*>(Sync[F].raiseError(error))
      }
  }
}
