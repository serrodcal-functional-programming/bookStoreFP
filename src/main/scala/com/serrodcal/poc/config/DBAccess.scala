package com.serrodcal.poc.config

import cats.Monad
import cats.effect.Sync
import cats.implicits._
import com.typesafe.config.Config
import scalikejdbc._

trait DBAccess[F[_]] {

  def initConfig(): F[Unit]
}

object DBAccess {

  def impure[F[_]](implicit M: Monad[F], config: Config): DBAccess[F] = new DBAccess[F] {

    def initConfig(): F[Unit] =
      M.pure(Class.forName(config.getString("database.driver")))
        .>>(M.pure(ConnectionPool.singleton(config.getString("database.url"), config.getString("database.user"), config.getString("database.pass"))))
  }

  def pure[F[_]](implicit S: Sync[F], config: Config): DBAccess[F] = new DBAccess[F] {

    def initConfig(): F[Unit] =
      S.delay(Class.forName(config.getString("database.driver")))
        .>>(S.delay(ConnectionPool.singleton(config.getString("database.url"),
          config.getString("database.user"),
          config.getString("database.pass"))))
  }
}
