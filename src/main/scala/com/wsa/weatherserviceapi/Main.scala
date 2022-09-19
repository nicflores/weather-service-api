package com.wsa.weatherserviceapi

import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp.Simple:
  def run: IO[Unit] =
    WeatherserviceapiServer.stream[IO].compile.drain.as(ExitCode.Success)
