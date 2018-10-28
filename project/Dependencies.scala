import Dependencies.log.{logback, logstash}
import sbt._

object Dependencies {

  val versions = new {
    val finagle = "18.9.1"
    val guice = "4.0"
    val logback = "1.2.3"
    val mockito = "1.10.19"
    val scalaCheck = "1.13.4"
    val scalaTest = "3.0.4"
    val specs2 = "3.8.6"
    val mysql = "6.0.6"
    val config = "1.3.2"
    val ficus = "1.4.3"
    val jjwt = "0.9.0"
    val flyway = "5.0.7"
    val quill = "2.5.5-SNAPSHOT"
    val arm = "2.0"
    val hasher = "1.2.0"
    val extractor = "2.11.0"
    val logstash = "4.11"
    val awscala = "0.6.+"
    val twitter4s = "5.3"
    val i18n = "1.0.2"
  }

  object finagle {
    val http = "com.twitter" %% "finatra-http" % versions.finagle
  }

  object finatra {
    val http = "com.twitter" %% "finatra-http" % versions.finagle
    val httpClient = "com.twitter" %% "finatra-httpclient" % versions.finagle
  }

  object core {
    val config = "com.typesafe" % "config" % versions.config
    val ficus = "com.iheart" %% "ficus" % versions.ficus
    val getquill = "io.getquill" %% "quill-finagle-mysql" % versions.quill
    val jwt = "io.jsonwebtoken" % "jjwt" % versions.jjwt
    val i18n = "com.osinka.i18n" %% "scala-i18n" % versions.i18n
    val flyway = "org.flywaydb" % "flyway-core" % versions.flyway
    val arm = "com.jsuereth" %% "scala-arm" % versions.arm
    val hasher = "com.roundeights" %% "hasher" % versions.hasher
    val extractor = "com.drewnoakes" % "metadata-extractor" % versions.extractor
  }

  object oauth2 {
    val oauth2 = "com.github.finagle" %% "finagle-oauth2" % "18.8.0"
  }

  object mysql {
    val connector = "mysql" % "mysql-connector-java" % versions.mysql
  }

  object tests {

    object finatra {
      val http = "com.twitter" %% "finatra-http" % versions.finagle % "test"
      val jackson = "com.twitter" %% "finatra-jackson" % versions.finagle % "test"
    }

    object finatraClassifier {
      val http = "com.twitter" %% "finatra-http" % versions.finagle % "test" classifier "tests"
      val jackson = "com.twitter" %% "finatra-jackson" % versions.finagle % "test" classifier "tests"
    }

    object inject {
      val server = "com.twitter" %% "inject-server" % versions.finagle % "test"
      val app = "com.twitter" %% "inject-app" % versions.finagle% "test"
      val core = "com.twitter" %% "inject-core" % versions.finagle % "test"
      val modules = "com.twitter" %% "inject-modules" % versions.finagle % "test"
    }

    object injectClassifier {
      val server = "com.twitter" %% "inject-server" % versions.finagle % "test" classifier "tests"
      val app = "com.twitter" %% "inject-app" % versions.finagle % "test" classifier "tests"
      val core = "com.twitter" %% "inject-core" % versions.finagle % "test" classifier "tests"
      val modules = "com.twitter" %% "inject-modules" % versions.finagle % "test" classifier "tests"
    }

    object guice {
      val extensions = "com.google.inject.extensions" % "guice-testlib" % versions.guice % "test"
    }

    object mockito {
      val core = "org.mockito" % "mockito-core" %  versions.mockito % "test"
    }

    object scalacheck {
      val scalacheck = "org.scalacheck" %% "scalacheck" % versions.scalaCheck % "test"
    }

    object scalatest {
      val scalatest = "org.scalatest" %% "scalatest" %  versions.scalaTest % "test"
    }

    object spec2 {
      val core = "org.specs2" %% "specs2-core" % versions.specs2 % "test"
      val junit = "org.specs2" %% "specs2-junit" % versions.specs2 % "test"
      val mock = "org.specs2" %% "specs2-mock" % versions.specs2 % "test"
    }

  }

  object log {

    object logback {
      val classic = "ch.qos.logback" % "logback-classic" % versions.logback
    }

    object logstash {
      val encoder = "net.logstash.logback" % "logstash-logback-encoder" % versions.logstash
    }

  }

  object addons {
    val awscala = "com.github.seratch" %% "awscala" % versions.awscala
    val twitters = "com.danielasfregola" %% "twitter4s" % versions.twitter4s
    val guice = "com.google.inject" % "guice" % versions.guice
  }

  lazy val finatraLibrarySettings = Seq(
    finatra.http,
    finatra.httpClient
  )

  lazy val coreLibrarySettings = Seq(
    core.config,
    core.ficus,
    core.getquill,
    core.jwt,
    core.i18n,
    core.flyway,
    core.arm,
    core.hasher,
    core.extractor,
  )

  lazy val oauth2LibrarySettings = Seq(
    oauth2.oauth2
  )

  lazy val mysqlLibrarySettings = Seq(
    mysql.connector
  )

  lazy val addonsLibrarySettings = Seq(addons.awscala, addons.twitters, addons.guice)

  lazy val logLibrarySettings = Seq(logback.classic, logstash.encoder)

  lazy val testLibrarySettings = Seq(
    tests.finatra.http,
    tests.finatra.jackson,
    tests.finatraClassifier.http,
    tests.finatraClassifier.jackson,
    tests.guice.extensions,
    tests.inject.app,
    tests.inject.core,
    tests.inject.modules,
    tests.inject.server,
    tests.injectClassifier.app,
    tests.injectClassifier.core,
    tests.injectClassifier.modules,
    tests.injectClassifier.server,
    tests.mockito.core,
    tests.scalacheck.scalacheck,
    tests.scalatest.scalatest,
    tests.spec2.core,
    tests.spec2.junit,
    tests.spec2.mock

  )

}
