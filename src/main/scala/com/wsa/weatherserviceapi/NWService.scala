package com.wsa.weatherserviceapi

import cats.effect.Concurrent
import cats.effect.IO
import cats.syntax.all._
import org.http4s.Method
import org.http4s.Request
import org.http4s.Status
import org.http4s.Uri
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.circe.decodeUri
import org.http4s.circe.jsonOf
import org.http4s.client.Client
import org.http4s.syntax.literals.uri

trait NWService[F[_]]:
  def getLocationProperties(
      lat: Double,
      lon: Double
  ): F[Either[NWSError, NWSProperties]]
  def getTodaysWeather(url: Uri): F[Either[NWSError, WeatherForcast]]

object NWService:
  def apply[F[_]: Concurrent](client: Client[F]): NWService[F] =
    new NWService[F]:
      override def getLocationProperties(
          lat: Double,
          lon: Double
      ): F[Either[NWSError, NWSProperties]] =
        client.run(Request(Method.GET, nwsPointsUrl / s"$lat,$lon")).use { r =>
          r.status match {
            case Status.Ok => r.as[NWSProperties].map(_.asRight)
            case _         => r.as[NWSError].map(_.asLeft)
          }
        }

      override def getTodaysWeather(
          url: Uri
      ): F[Either[NWSError, WeatherForcast]] =
        client.run(Request(Method.GET, url)).use { r =>
          r.status match {
            case Status.Ok => r.as[WeatherForcast].map(_.asRight)
            case _         => r.as[NWSError].map(_.asLeft)
          }
        }
  private val nwsPointsUrl = uri"https://api.weather.gov/points/"
