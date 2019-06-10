import sbt._

object Dependencies {

  val versions = new {
    val twitter = "19.5.1"
    val guice = "4.0"
    val logback = "1.2.3"
    val mockito = "1.10.19"
    val scalaCheck = "1.13.4"
    val scalaTest = "3.0.4"
    val specs2 = "3.8.6"
    val mysql = "8.0.15"
    val config = "1.3.2"
    val ficus = "1.4.5"
    val jjwt = "0.9.1"
    val flyway = "5.2.4"
    val quill = "3.2.2"
    val arm = "2.0"
    val hasher = "1.2.0"
    val extractor = "2.11.0"
    val logstash = "4.11"
    val i18n = "1.0.2"
    val junit = "4.12"
  }


  lazy val finatra = Seq(
    "com.twitter" %% "finatra-http" % versions.twitter,
    "com.twitter" %% "finatra-httpclient" % versions.twitter
  )

  lazy val core = Seq(
    "com.typesafe" % "config" % versions.config,
    "com.iheart" %% "ficus" % versions.ficus,
    "io.getquill" %% "quill-finagle-mysql" % versions.quill,
    "io.jsonwebtoken" % "jjwt" % versions.jjwt,
    "com.osinka.i18n" %% "scala-i18n" % versions.i18n,
    "org.flywaydb" % "flyway-core" % versions.flyway,
    "com.jsuereth" %% "scala-arm" % versions.arm,
    "com.roundeights" %% "hasher" % versions.hasher,
    "com.drewnoakes" % "metadata-extractor" % versions.extractor
  )

  lazy val oauth2 = Seq(
    "com.github.finagle" %% "finagle-oauth2" % "19.4.0"
  )

  lazy val mysql = Seq(
    "mysql" % "mysql-connector-java" % versions.mysql
  )

  lazy val log = Seq(
    "ch.qos.logback" % "logback-classic" % versions.logback,
    "net.logstash.logback" % "logstash-logback-encoder" % versions.logstash
  )


  lazy val test = Seq(
    "com.twitter" %% "finatra-http" % versions.twitter % "test",
    "com.twitter" %% "finatra-jackson" % versions.twitter % "test",
    "com.twitter" %% "finatra-http" % versions.twitter % "test" classifier "tests",
    "com.twitter" %% "finatra-jackson" % versions.twitter % "test" classifier "tests",
    "com.twitter" %% "inject-server" % versions.twitter % "test",
    "com.twitter" %% "inject-app" % versions.twitter% "test",
    "com.twitter" %% "inject-core" % versions.twitter % "test",
    "com.twitter" %% "inject-modules" % versions.twitter % "test",
    "com.twitter" %% "inject-server" % versions.twitter % "test" classifier "tests",
    "com.twitter" %% "inject-app" % versions.twitter % "test" classifier "tests",
    "com.twitter" %% "inject-core" % versions.twitter % "test" classifier "tests",
    "com.twitter" %% "inject-modules" % versions.twitter % "test" classifier "tests",
    "com.google.inject.extensions" % "guice-testlib" % versions.guice % "test",
    "org.mockito" % "mockito-core" %  versions.mockito % "test",
    "org.scalacheck" %% "scalacheck" % versions.scalaCheck % "test",
    "org.scalatest" %% "scalatest" %  versions.scalaTest % "test",
    "org.specs2" %% "specs2-core" % versions.specs2 % "test",
    "org.specs2" %% "specs2-junit" % versions.specs2 % "test",
    "org.specs2" %% "specs2-mock" % versions.specs2 % "test"
  )


  lazy val aws = Seq(
    "com.github.seratch" %% "awscala" % "0.8.+"
  )

  lazy val ficus = Seq(
    "com.iheart" %% "ficus" % versions.ficus
  )

  lazy val utils = Seq(
    "com.roundeights" %% "hasher" % versions.hasher
  )

  
  // Libraries

  lazy val filhouette = Seq(
    "io.jsonwebtoken" % "jjwt" % versions.jjwt,
    "ch.qos.logback" % "logback-classic" % versions.logback,
    "org.apache.commons" % "commons-lang3" % "3.8.1",
    "de.svenkubiak" % "jBCrypt" % "0.4.1",
    "org.scalacheck" %% "scalacheck" % versions.scalaCheck % "test",
    "org.scalatest" %% "scalatest" %  versions.scalaTest % "test",
    "org.specs2" %% "specs2-core" % versions.specs2 % "test",
    "org.specs2" %% "specs2-junit" % versions.specs2 % "test",
    "org.specs2" %% "specs2-matcher-extra" % versions.specs2 % "test",
    "org.specs2" %% "specs2-mock" % versions.specs2 % "test"
  )

  lazy val finagger = Seq(
    "com.twitter" %% "finatra-http" % versions.twitter,
    "org.joda" % "joda-convert" % "1.2",
    "org.reflections" % "reflections" % "0.9.10",
    "org.projectlombok" % "lombok" % "1.16.22" % "provided",
    "org.assertj" % "assertj-core" % "3.0.0" % Test,
    "junit" % "junit" % versions.junit % Test,
    "uk.co.jemos.podam" % "podam" % "4.7.2.RELEASE" % Test,
    "io.swagger" % "swagger-core" % "1.5.21",
    "io.swagger" %% "swagger-scala-module" % "1.0.4",
    "org.webjars" % "swagger-ui" % "3.19.0",
    "net.bytebuddy" % "byte-buddy" % "1.8.17",
    "javax.xml.bind" % "jaxb-api" % "2.1"
  )

  lazy val finasocket = Seq(
    "com.twitter" %% "finagle-core" % versions.twitter,
    "com.twitter" %% "finagle-netty4" % versions.twitter,
    "org.scalatest" %% "scalatest" % versions.scalaTest % Test,
    "junit" % "junit" % versions.junit % Test,
    "org.mockito" % "mockito-core" % "2.23.4" % Test
  )

  lazy val finachat = Seq(
    "com.twitter" %% "twitter-server" % versions.twitter,
    "com.twitter" %% "inject-server" % versions.twitter,
    "com.twitter" %% "inject-utils" % versions.twitter,
    "com.twitter" %% "finagle-http" % versions.twitter,
    "com.twitter" %% "finagle-redis" % versions.twitter,
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.9.8",
    "com.iheart" %% "ficus" % versions.ficus,
    "com.typesafe" % "config" % versions.config,
    "org.scalatest" %% "scalatest" % versions.scalaTest % Test,
    "junit" % "junit" % versions.junit % Test
  )

}

