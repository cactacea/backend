import sbt.Keys.{organization, resolvers, testOptions}

lazy val demo = (project in file("demo"))
  .settings(
    organization := "io.github.cactacea.backend",
    name := "demo",
    scalaVersion := "2.12.4",
    concurrentRestrictions in Global += Tags.limit(Tags.Test, 1),
    mainClass in (Compile, run) := Some("io.github.cactacea.backend.DemoServerApp")
  )
  .settings(
    version in Docker := "latest",
    maintainer in Docker := "Cactacea",
    packageName in Docker := "backend",
    dockerBaseImage := "robsonoduarte/8-jre-alpine-bash",
    dockerExposedPorts := Seq(9000, 9001),
    dockerRepository := Some("cactacea")
  )
  .dependsOn(api)
  .dependsOn(onesignal)
  .dependsOn(s3)
  .enablePlugins(JavaAppPackaging)


lazy val api = (project in file("api"))
  .settings(
      organization := "io.github.cactacea.backend.api",
      name := "api",
      scalaVersion := "2.12.4",
      concurrentRestrictions in Global += Tags.limit(Tags.Test, 1),
      testOptions in Test += Tests.Argument("-oI")
  )
  .settings(commonResolverSetting)
  .settings(commonLibrarySetting)
  .dependsOn(core)


lazy val core = (project in file("core"))
  .settings(
    organization := "io.github.cactacea.backend.core",
    scalaVersion := "2.12.4",
    name := "core",
    concurrentRestrictions in Global += Tags.limit(Tags.Test, 1),
    testOptions in Test += Tests.Argument("-oI")
  )
  .settings(commonResolverSetting)
  .settings(commonLibrarySetting)
  .dependsOn(util)
  .dependsOn(oauth2)
  .dependsOn(swagger)


lazy val util = (project in file("util"))
  .settings(
    organization := "io.github.cactacea.backend.util",
    scalaVersion := "2.12.4",
    name := "util",
    concurrentRestrictions in Global += Tags.limit(Tags.Test, 1),
    testOptions in Test += Tests.Argument("-oI")
  )
  .settings(
    libraryDependencies ++= Seq(
      "com.danielasfregola" %% "twitter4s" % "5.3",
      "com.github.seratch" %% "awscala" % "0.6.+"
    )
  )
  .settings(commonResolverSetting)
  .settings(commonLibrarySetting)


lazy val onesignal = (project in file("onesignal"))
  .settings(
    organization := "io.github.cactacea.backend.onesignal",
    scalaVersion := "2.12.4",
    name := "onesignal",
    concurrentRestrictions in Global += Tags.limit(Tags.Test, 1),
    testOptions in Test += Tests.Argument("-oI")
  )
  .settings(commonResolverSetting)
  .settings(commonLibrarySetting)
  .dependsOn(core)


lazy val s3 = (project in file("s3"))
  .settings(
    organization := "io.github.cactacea.backend.s3",
    scalaVersion := "2.12.4",
    name := "s3",
    concurrentRestrictions in Global += Tags.limit(Tags.Test, 1),
    testOptions in Test += Tests.Argument("-oI")
  )
  .settings(
    libraryDependencies ++= Seq(
      "com.github.seratch" %% "awscala" % "0.6.+"
    )
  )
  .settings(commonResolverSetting)
  .settings(commonLibrarySetting)
  .dependsOn(core)


lazy val versions = new {
  val finatra = "17.12.0"
  val guice = "4.0"
  val logback = "1.2.3"
  val mockito = "1.10.19"
  val scalaCheck = "1.13.4"
  val scalaTest = "3.0.4"
  val specs2 = "3.8.6"
  val aws = "1.11.289"
}

lazy val commonLibrarySetting = Seq(
  libraryDependencies ++= Seq(

    "com.typesafe" % "config" % "1.3.2",
    "com.twitter" %% "finatra-http" % versions.finatra,
    "com.twitter" %% "finatra-httpclient" % versions.finatra,
    "com.twitter" %% "finagle-core" % versions.finatra,

    "com.twitter" %% "finatra-http" % versions.finatra % "test",
    "com.twitter" %% "finatra-jackson" % versions.finatra % "test",
    "com.twitter" %% "inject-server" % versions.finatra % "test",
    "com.twitter" %% "inject-app" % versions.finatra % "test",
    "com.twitter" %% "inject-core" % versions.finatra % "test",
    "com.twitter" %% "inject-modules" % versions.finatra % "test",
    "com.google.inject.extensions" % "guice-testlib" % versions.guice % "test",
    "com.twitter" %% "finatra-http" % versions.finatra % "test" classifier "tests",
    "com.twitter" %% "finatra-jackson" % versions.finatra % "test" classifier "tests",
    "com.twitter" %% "inject-server" % versions.finatra % "test" classifier "tests",
    "com.twitter" %% "inject-app" % versions.finatra % "test" classifier "tests",
    "com.twitter" %% "inject-core" % versions.finatra % "test" classifier "tests",
    "com.twitter" %% "inject-modules" % versions.finatra % "test" classifier "tests",
    "org.mockito" % "mockito-core" %  versions.mockito % "test",
    "org.scalacheck" %% "scalacheck" % versions.scalaCheck % "test",
    "org.scalatest" %% "scalatest" %  versions.scalaTest % "test",
    "org.specs2" %% "specs2-core" % versions.specs2 % "test",
    "org.specs2" %% "specs2-junit" % versions.specs2 % "test",
    "org.specs2" %% "specs2-mock" % versions.specs2 % "test",

    "ch.qos.logback" % "logback-classic" % versions.logback,
    "net.logstash.logback" % "logstash-logback-encoder" % "4.11",
    "io.getquill" %% "quill-finagle-mysql" % "2.3.2",
    "io.jsonwebtoken" % "jjwt" % "0.9.0",
    "com.osinka.i18n" %% "scala-i18n" % "1.0.2",

    "mysql" % "mysql-connector-java" % "6.0.6",
    "org.flywaydb" % "flyway-core" % "4.2.0",

    "com.jsuereth" %% "scala-arm" % "2.0",
    "com.roundeights" %% "hasher" % "1.2.0",
    "com.drewnoakes" % "metadata-extractor" % "2.11.0"

  )
)

lazy val commonResolverSetting = Seq(
  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    "Maven central" at "http://central.maven.org/maven2/"
  )
)

lazy val swagger = (project in file("swagger"))
  .settings(
    organization := "com.jakehschwartz.finatra.swagger",
    scalaVersion := "2.12.4",
    name := "swagger",
    concurrentRestrictions in Global += Tags.limit(Tags.Test, 1),
    testOptions in Test += Tests.Argument("-oI")
  )
  .settings(
    libraryDependencies ++= Seq(
      "com.twitter" %% "finatra-http" % versions.finatra,
      "io.swagger" % "swagger-core" % "1.5.17",
      "io.swagger" %% "swagger-scala-module" % "1.0.4",
      "org.webjars" % "swagger-ui" % "3.10.0",
      "net.bytebuddy" % "byte-buddy" % "1.7.11",
      "org.scalatest" %% "scalatest" % versions.scalaTest % "test"
    )
  )

lazy val oauth2 = (project in file("oauth2"))
  .settings(
    organization := "com.twitter.finagle.oauth2",
    scalaVersion := "2.12.4",
    name := "oauth2",
    concurrentRestrictions in Global += Tags.limit(Tags.Test, 1),
    testOptions in Test += Tests.Argument("-oI")
  )
  .settings(
    libraryDependencies ++= Seq(
      "com.twitter" %% "finagle-http" % versions.finatra,
      "org.scalacheck" %% "scalacheck" % "1.13.5" % Test,
      "org.scalatest" %% "scalatest" % "3.0.5" % Test
    )
  )
