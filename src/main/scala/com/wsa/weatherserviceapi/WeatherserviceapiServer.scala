package com.wsa.weatherserviceapi

import cats.effect.Async
import cats.effect.Resource
import cats.syntax.all._
import com.comcast.ip4s._
import com.wsa.weatherserviceapi.NWService
import fs2.Stream
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.middleware.Logger

object WeatherserviceapiServer:
  def stream[F[_]: Async]: Stream[F, Nothing] = {
    for {
      client <- Stream.resource(EmberClientBuilder.default[F].build)
      nwsApi = NWService[F](client)

      httpApp = WeatherserviceapiRoutes[F](nwsApi).routes.orNotFound

      finalHttpApp = Logger.httpApp(true, true)(httpApp)

      exitCode <- Stream.resource(
        EmberServerBuilder
          .default[F]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8080")
          .withHttpApp(finalHttpApp)
          .build >>
          Resource.eval(Async[F].never)
      )
    } yield exitCode
  }.drain
