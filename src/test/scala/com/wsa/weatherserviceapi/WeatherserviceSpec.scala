package com.wsa.weatherserviceapi

import cats.effect.IO
import org.http4s._
import org.http4s.implicits._
import weaver.SimpleIOSuite
import org.http4s.ember.client.EmberClientBuilder
import cats.effect.Resource
import org.http4s.client.Client

object WeatherserviceSpec extends SimpleIOSuite {

  val eClient: Resource[IO, Client[IO]] = EmberClientBuilder.default[IO].build

  test("Valid location returns 200") {
    eClient.use { client =>
      {
        val nws = NWService[IO](client)
        val wClient =
          Client.fromHttpApp(WeatherserviceapiRoutes[IO](nws).routes.orNotFound)
        wClient
          .run(Request[IO](Method.GET, uri"/weather" / "40.7484,-73.9856"))
          .use(resp => IO.pure(expect.same(resp.status, Status.Ok)))
      }
    }
  }

  test("Non supported locations returns 404") {
    eClient.use { client =>
      {
        val nws = NWService[IO](client)
        val wClient =
          Client.fromHttpApp(WeatherserviceapiRoutes[IO](nws).routes.orNotFound)
        wClient
          .run(Request[IO](Method.GET, uri"/weather" / "77.6177,42.4944"))
          .use(resp => IO.pure(expect.same(resp.status, Status.BadRequest)))
      }
    }
  }
}
