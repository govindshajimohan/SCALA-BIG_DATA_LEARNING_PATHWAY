ThisBuild / scalaVersion     := "3.6.3"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "stock-analysis",
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.client3" %% "core" % "3.9.0",  // Use latest version
      "com.softwaremill.sttp.client3" %% "circe" % "3.9.0", // JSON support
      "io.circe" %% "circe-generic" % "0.14.5",
      "io.circe" %% "circe-parser" % "0.14.5"
    )
  )
libraryDependencies ++= Seq(
  "com.softwaremill.sttp.client3" %% "okhttp-backend" % "3.9.0"
)
libraryDependencies += "com.yahoofinance-api" % "YahooFinanceAPI" % "3.17.0"
libraryDependencies ++= Seq(
  "com.softwaremill.sttp.client3" %% "core" % "3.8.16",
  "com.softwaremill.sttp.client3" %% "circe" % "3.8.16",
)

libraryDependencies += "io.circe" %% "circe-parser" % "0.15.0-M1"
