package com.wsa.weatherserviceapi

import cats.Monad
import cats.effect.Sync
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router

final case class WeatherserviceapiRoutes[F[_]: Monad](nws: NWService[F])
    extends Http4sDsl[F]:
  val httpRoutes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / Extractor(loc) =>
      nws.getLocationProperties(loc.lat, loc.lon).flatMap {
        case Right(nwsp) =>
          nws.getTodaysWeather(nwsp.url).flatMap {
            case Right(wf) => Ok(wf)
            case Left(e)   => NotFound(e)
          }
        case Left(e) => NotFound(e)
      }
  }
  val routes: HttpRoutes[F] = Router(
    "/weather" -> httpRoutes
  )
