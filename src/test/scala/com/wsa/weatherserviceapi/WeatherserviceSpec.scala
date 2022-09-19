package com.wsa.weatherserviceapi

import cats.effect.IO
import org.http4s._
import org.http4s.implicits._
import munit.CatsEffectSuite
import org.http4s.ember.client.EmberClientBuilder
import cats.effect.Resource
import org.http4s.client.Client

class WeatherserviceSpec extends CatsEffectSuite {

  val eClient: Resource[IO, Client[IO]] = EmberClientBuilder.default[IO].build

  test("Valid location returns 200") {
    eClient.use { client =>
      {
        val nws = NWService[IO](client)
        val wClient =
          Client.fromHttpApp(WeatherserviceapiRoutes[IO](nws).routes.orNotFound)
        wClient
          .run(Request[IO](Method.GET, uri"/weather" / "40.7484,-73.9856"))
          .use(resp => assertIO(IO(resp.status), Status.Ok))
      }
    }
  }
}
