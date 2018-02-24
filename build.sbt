import sbt.Keys.{organization, resolvers, testOptions}

scalaVersion := "2.12.4"

lazy val backend = (project in file("backend"))
  .settings(
      organization := "io.github.cactacea.backend",
      name := "backend",
      scalaVersion := "2.12.4",
      parallelExecution in ThisBuild := false,
      mainClass in (Compile, run) := Some("io.github.cactacea.backend.CactaceaServerApp"),
      testOptions in Test += Tests.Argument("-oI")
  )
  .settings(
      version in Docker := "latest",
      maintainer in Docker := "Cactacea",
      packageName in Docker := "backend",
      dockerBaseImage := "robsonoduarte/8-jre-alpine-bash",
      dockerExposedPorts := Seq(9000, 9001),
      dockerRepository := Some("cactacea"),
      dockerEntrypoint := Seq("bin/%s" format executableScriptName.value, "-storage=s3")
  )
  .dependsOn(core)
  .enablePlugins(JavaAppPackaging)

lazy val core = (project in file("core"))
  .settings(coreSetting)
  .settings(coreLibrarySetting)

lazy val coreSetting = Seq(
    organization := "jp.github.cactacea.core",
    name := "core",
    scalaVersion := "2.12.4",
    parallelExecution in ThisBuild := false,
    testOptions in Test += Tests.Argument("-oI")
)

lazy val versions = new {
    val finatra = "18.2.0"
    val guice = "4.0"
    val logback = "1.2.3"
    val mockito = "1.10.19"
    val scalaCheck = "1.13.4"
    val scalaTest = "3.0.4"
    val specs2 = "3.8.6"
}

lazy val coreLibrarySetting = Seq(
    resolvers ++= Seq(
        Resolver.sonatypeRepo("releases"),
        "Maven central" at "http://repo1.maven.org/maven2/"
    ),
    libraryDependencies ++= Seq(
        "io.zipkin.finagle" %% "zipkin-finagle" % "1.2.2",
        "com.twitter" %% "finagle-core" % versions.finatra,
        "com.twitter" %% "finatra-http" % versions.finatra,
        "com.twitter" %% "finatra-httpclient" % versions.finatra,
        "com.github.finagle" % "finagle-oauth2_2.12" % "17.12.0",
        "ch.qos.logback" % "logback-classic" % versions.logback,
        "net.logstash.logback" % "logstash-logback-encoder" % "4.11",
        "io.getquill" % "quill-finagle-mysql_2.12" % "2.3.2",
        "com.typesafe" % "config" % "1.3.2",
        "io.jsonwebtoken" % "jjwt" % "0.9.0",
        "com.osinka.i18n" %% "scala-i18n" % "1.0.2",
        "com.jsuereth" %% "scala-arm" % "2.0",
        "com.danielasfregola" %% "twitter4s" % "5.3",
        "com.github.seratch" %% "awscala" % "0.6.+",
        "mysql" % "mysql-connector-java" % "6.0.6",

        "org.flywaydb" % "flyway-core" % "4.2.0",

        "com.roundeights" %% "hasher" % "1.2.0",
        "com.drewnoakes" % "metadata-extractor" % "2.11.0",

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
        "org.specs2" %% "specs2-mock" % versions.specs2 % "test"
    )
)

// for sbt flyway:migration command
libraryDependencies ++= Seq(
    "mysql" % "mysql-connector-java" % "6.0.6"
)
