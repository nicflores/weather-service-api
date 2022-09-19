package com.wsa

import cats.syntax.all._
import cats.effect.Concurrent

import io.circe.HCursor
import io.circe.Decoder
import io.circe.Encoder
import io.circe.Json
import io.circe.HCursor

import org.http4s.Uri
import org.http4s.Method
import org.http4s.Status
import org.http4s.Request
import org.http4s.EntityEncoder
import org.http4s.EntityDecoder
import org.http4s.client.Client
import org.http4s.circe.decodeUri
import org.http4s.circe.jsonOf
import org.http4s.circe.jsonEncoderOf
import org.http4s.syntax._
import org.http4s.implicits._

package object weatherserviceapi:

  final case class Location(lat: Double, lon: Double)

  object Extractor:
    def unapply(str: String): Option[Location] =
      str.split(',').toList match {
        case la :: lo :: Nil =>
          (la.toDoubleOption, lo.toDoubleOption) match {
            case (Some(lat), Some(lon)) =>
              Some(Location(f"$lat%2.4f".toDouble, f"$lon%3.4f".toDouble))
            case _ => None
          }
        case _ => None
      }

  final case class NWSProperties(url: Uri)

  object NWSProperties:
    given Decoder[NWSProperties] =
      Decoder.instance[NWSProperties] { c =>
        for {
          forcastUrl <- c
            .downField("properties")
            .downField("forecast")
            .as[Uri]
        } yield NWSProperties(forcastUrl)
      }

    given [F[_]: Concurrent]: EntityDecoder[F, NWSProperties] =
      jsonOf[F, NWSProperties]
    given Encoder[NWSProperties] =
      Encoder.forProduct1("url")(u => u.url.renderString)
    given [F[_]]: EntityEncoder[F, NWSProperties] =
      jsonEncoderOf[F, NWSProperties]

  final case class WeatherForcast(temp: String, summary: String)

  object WeatherForcast:
    given Decoder[WeatherForcast] = Decoder.instance[WeatherForcast] { c =>
      for {
        today <- c
          .downField("properties")
          .downField("periods")
          .downN(0)
          .as[HCursor]
        temp <- today
          .downField("temperature")
          .as[Int]
        sf <- today
          .downField("shortForecast")
          .as[String]
      } yield {
        if (temp < 30)
          WeatherForcast("Freezing Cold!", sf)
        else if (temp > 30 && temp <= 35)
          WeatherForcast("Very Cold", sf)
        else if (temp > 35 && temp <= 45)
          WeatherForcast("Cold", sf)
        else if (temp > 45 && temp <= 50)
          WeatherForcast("Chilly", sf)
        else if (temp > 50 && temp <= 55)
          WeatherForcast("Cool", sf)
        else if (temp > 55 && temp <= 60)
          WeatherForcast("Pleasant, a little cool if there's a breeze", sf)
        else if (temp > 60 && temp <= 65)
          WeatherForcast("Pleasant", sf)
        else if (temp > 65 && temp <= 70)
          WeatherForcast("Pleasant Temperature", sf)
        else if (temp > 70 && temp <= 80)
          WeatherForcast("Warm", sf)
        else if (temp > 80 && temp <= 90)
          WeatherForcast("Really Hot", sf)
        else if (temp > 90 && temp <= 100)
          WeatherForcast("Very Hot!", sf)
        else
          WeatherForcast("Ridiculously Hot!", sf)
      }
    }
    given [F[_]: Concurrent]: EntityDecoder[F, WeatherForcast] =
      jsonOf[F, WeatherForcast]
    given Encoder[WeatherForcast] = Encoder.AsObject.derived[WeatherForcast]
    given [F[_]]: EntityEncoder[F, WeatherForcast] = jsonEncoderOf

  final case class NWSError(message: String) extends Exception

  object NWSError:
    given Decoder[NWSError] = Decoder.instance[NWSError] { c =>
      for {
        details <- c
          .downField("detail")
          .as[String]
      } yield NWSError(details)
    }
    given Encoder[NWSError] = Encoder.AsObject.derived[NWSError]
    given [F[_]]: EntityEncoder[F, NWSError] = jsonEncoderOf
