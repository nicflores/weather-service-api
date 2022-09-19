package com.wsa.weatherserviceapi

import cats.implicits._
import cats.effect.Sync

import cats.Monad
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router

final case class WeatherserviceapiRoutes[F[_]: Monad](nws: NWService[F])
    extends Http4sDsl[F]:
  val prefixPath = "/weather"
  val httpRoutes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / Extractor(loc) =>
      nws.getLocationProperties(loc.lat, loc.lon).flatMap {
        case Right(nwsp) =>
          nws.getTodaysWeather(nwsp.url).flatMap {
            case Right(wf) => Ok(wf)
            case Left(e)   => BadRequest(e)
          }
        case Left(e) => BadRequest(e)
      }
  }
  val routes: HttpRoutes[F] = Router(prefixPath -> httpRoutes)
