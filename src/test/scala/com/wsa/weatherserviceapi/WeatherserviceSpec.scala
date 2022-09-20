package com.wsa.weatherserviceapi

import cats.effect.IO
import cats.effect.Resource
import org.http4s._
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.implicits._
import weaver.SimpleIOSuite
import weaver.Expectations

object WeatherserviceSpec extends SimpleIOSuite {

  val eClient: Resource[IO, Client[IO]] = EmberClientBuilder.default[IO].build

  test("Valid location returns 200") {
    checkWeatherApiStatus(
      eClient,
      uri"/weather" / "40.7484,-73.9856",
      Status.Ok
    )
  }

  test("Valid location with more than 4 decimal digits places returns 200") {
    checkWeatherApiStatus(
      eClient,
      uri"/weather" / "40.748412,-73.985621",
      Status.Ok
    )
  }

  test("Invalid location returns 404") {
    checkWeatherApiStatus(
      eClient,
      uri"/weather" / "40.7484,aabb",
      Status.NotFound
    )
  }

  test("Non supported locations returns 404") {
    checkWeatherApiStatus(
      eClient,
      uri"/weather" / "77.6177,42.4944",
      Status.NotFound
    )
  }

  def checkWeatherApiStatus[A](
      emberClient: Resource[IO, Client[IO]],
      endpoint: Uri,
      found: A
  ): IO[Expectations] =
    eClient.use { client =>
      {
        val nws = NWService[IO](client)
        val wClient =
          Client.fromHttpApp(WeatherserviceapiRoutes[IO](nws).routes.orNotFound)
        wClient
          .run(Request[IO](Method.GET, endpoint))
          .use(resp => IO.pure(expect.same(resp.status, found)))
      }
    }
}
