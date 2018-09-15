import sbt.Keys.{organization, resolvers, testOptions}

lazy val versions = new {
  val cactacea = "0.3.1-SNAPSHOT"
  val finagle = "18.5.0"
  val guice = "4.0"
  val logback = "1.2.3"
  val mockito = "1.10.19"
  val scalaCheck = "1.13.4"
  val scalaTest = "3.0.4"
  val specs2 = "3.8.6"
  val aws = "1.11.289"
}

lazy val demo = (project in file("demo"))
  .settings(
    version      := versions.cactacea,
    organization := "io.github.cactacea.backend",
    name := "demo",
    scalaVersion := "2.12.5",
    mainClass in (Compile, run) := Some("io.github.cactacea.backend.DemoServerApp"),
  )
  .settings(
    version in Docker := "latest",
    maintainer in Docker := "Cactacea",
    packageName in Docker := "backend",
    dockerBaseImage := "robsonoduarte/8-jre-alpine-bash",
    dockerExposedPorts := Seq(9000, 9001),
    dockerRepository := Some("cactacea")
  )
  .dependsOn(server)
  .dependsOn(externals)
  .dependsOn(finagger)
  .enablePlugins(JavaAppPackaging)


lazy val server = (project in file("server"))
  .settings(
    version      := versions.cactacea,
    organization := "io.github.cactacea.backend",
    name := "server",
    scalaVersion := "2.12.5"
  )
  .settings(
    libraryDependencies ++= Seq(
      "com.github.finagle" %% "finagle-oauth2" % "18.4.0"
    )
  )
  .settings(commonResolverSetting)
  .settings(finatraLibrarySetting)
  .settings(coreLibrarySetting)
  .dependsOn(core)


lazy val core = (project in file("core"))
  .settings(
    version      := versions.cactacea,
    organization := "io.github.cactacea.backend",
    scalaVersion := "2.12.5",
    name := "core"
  )
  .settings(
    libraryDependencies ++= Seq(
      "com.iheart" %% "ficus" % "1.4.3"
    )
  )
  .settings(commonResolverSetting)
  .settings(finatraLibrarySetting)
  .settings(coreLibrarySetting)
  .settings(testSettings)
  .dependsOn(finagger)
  .enablePlugins(FlywayPlugin)


lazy val externals = (project in file("externals"))
  .settings(
    version      := versions.cactacea,
    organization := "io.github.cactacea.backend.externals",
    scalaVersion := "2.12.5",
    name := "externals",
    concurrentRestrictions in Global += Tags.limit(Tags.Test, 1),
    testOptions in Test += Tests.Argument("-oI")
  )
  .settings(
    libraryDependencies ++= Seq(
      "com.github.seratch" %% "awscala" % "0.6.+",
      "com.danielasfregola" %% "twitter4s" % "5.3",
      "com.google.inject" % "guice" % versions.guice
    )
  )
  .settings(commonResolverSetting)
  .settings(finatraLibrarySetting)
  .settings(coreLibrarySetting)
  .dependsOn(core)

lazy val finatraLibrarySetting = Seq(
  libraryDependencies ++= Seq(
    "com.twitter" %% "finatra-http" % versions.finagle,
    "com.twitter" %% "finatra-httpclient" % versions.finagle,
    "com.twitter" %% "finatra-http" % versions.finagle % "test",
    "com.twitter" %% "finatra-jackson" % versions.finagle % "test"
  )
)

lazy val coreLibrarySetting = Seq(
  libraryDependencies ++= Seq(

    "com.typesafe" % "config" % "1.3.2",
    "io.getquill" %% "quill-finagle-mysql" % "2.5.4",
    "io.jsonwebtoken" % "jjwt" % "0.9.0",
    "com.osinka.i18n" %% "scala-i18n" % "1.0.2",
    "mysql" % "mysql-connector-java" % "6.0.6",
    "org.flywaydb" % "flyway-core" % "5.0.7",
    "com.jsuereth" %% "scala-arm" % "2.0",
    "com.roundeights" %% "hasher" % "1.2.0",
    "com.drewnoakes" % "metadata-extractor" % "2.11.0",
    "com.iheart" %% "ficus" % "1.4.3",

    "com.twitter" %% "inject-server" % versions.finagle % "test",
    "com.twitter" %% "inject-app" % versions.finagle % "test",
    "com.twitter" %% "inject-core" % versions.finagle % "test",
    "com.twitter" %% "inject-modules" % versions.finagle % "test",
    "com.google.inject.extensions" % "guice-testlib" % versions.guice % "test",
    "com.twitter" %% "finatra-http" % versions.finagle % "test" classifier "tests",
    "com.twitter" %% "finatra-jackson" % versions.finagle % "test" classifier "tests",
    "com.twitter" %% "inject-server" % versions.finagle % "test" classifier "tests",
    "com.twitter" %% "inject-app" % versions.finagle % "test" classifier "tests",
    "com.twitter" %% "inject-core" % versions.finagle % "test" classifier "tests",
    "com.twitter" %% "inject-modules" % versions.finagle % "test" classifier "tests",
    "org.mockito" % "mockito-core" %  versions.mockito % "test",

    "org.scalacheck" %% "scalacheck" % versions.scalaCheck % "test",
    "org.scalatest" %% "scalatest" %  versions.scalaTest % "test",
    "org.specs2" %% "specs2-core" % versions.specs2 % "test",
    "org.specs2" %% "specs2-junit" % versions.specs2 % "test",
    "org.specs2" %% "specs2-mock" % versions.specs2 % "test",

    "ch.qos.logback" % "logback-classic" % versions.logback,
    "net.logstash.logback" % "logstash-logback-encoder" % "4.11"

  )
)

lazy val commonResolverSetting = Seq(
  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    "Maven central" at "http://central.maven.org/maven2/"
  )
)

lazy val dbUser = sys.env.get("CACTACEA_MASTER_DB_USERNAME").getOrElse("root")
lazy val dbPassword = sys.env.get("CACTACEA_MASTER_DB_PASSWORD").getOrElse("root")
lazy val dbDatabase = sys.env.get("CACTACEA_MASTER_DB_NAME").getOrElse("cactacea")
lazy val dbHostName = sys.env.get("CACTACEA_MASTER_DB_HOSTNAME").getOrElse("localhost")
lazy val dbPort = sys.env.get("CACTACEA_MASTER_DB_PORT").getOrElse("3306")

lazy val testSettings = Seq(
  flywayUser := dbUser,
  flywayPassword := dbPassword,
  flywayUrl := s"jdbc:mysql://$dbHostName:$dbPort/$dbDatabase",
  flywayPlaceholders := Map("schema" -> dbDatabase),
  flywayLocations := Seq("filesystem:core/src/main/resources/db/migration"),
  (test in Test) := {
    (test in Test).dependsOn(flywayClean, flywayMigrate).value
  },
  (testOnly in Test) := {
    (testOnly in Test).dependsOn(flywayClean, flywayMigrate).evaluated
  },
  (testQuick in Test) := {
    (testQuick in Test).dependsOn(flywayClean, flywayMigrate).evaluated
  },
  concurrentRestrictions in Global += Tags.limit(Tags.Test, 1),
  testOptions in Test += Tests.Argument("-oI")
)

lazy val finagger = RootProject(uri("git://github.com/cactacea/finagger.git"))
