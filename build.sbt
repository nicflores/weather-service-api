val CirceVersion = "0.14.3"
val Http4sVersion = "0.23.15"
val LogbackVersion = "1.2.6"
val WeaverVersion = "0.8.0"

lazy val root = (project in file("."))
  .settings(
    organization := "com.wsa",
    name := "weather-service-api",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "3.1.0",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-ember-server" % Http4sVersion,
      "org.http4s" %% "http4s-ember-client" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "io.circe" %% "circe-parser" % CirceVersion,
      "io.circe" %% "circe-generic" % CirceVersion,
      "com.disneystreaming" %% "weaver-cats" % WeaverVersion % Test,
      "ch.qos.logback" % "logback-classic" % LogbackVersion
    ),
    testFrameworks += new TestFramework("weaver.framework.CatsEffect")
  )
