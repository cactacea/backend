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
  .dependsOn(server)
  .dependsOn(externals)
  .dependsOn(oauth2)
  .dependsOn(swagger)
  .enablePlugins(JavaAppPackaging)


lazy val server = (project in file("server"))
  .settings(
    version      := versions.cactacea,
    organization := "io.github.cactacea.backend",
    name := "server",
    scalaVersion := "2.12.5",
    concurrentRestrictions in Global += Tags.limit(Tags.Test, 1),
    testOptions in Test += Tests.Argument("-oI")
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
    name := "core",
    concurrentRestrictions in Global += Tags.limit(Tags.Test, 1),
    testOptions in Test += Tests.Argument("-oI")
  )
  .settings(
    libraryDependencies ++= Seq(
      "com.iheart" %% "ficus" % "1.4.3"
    )
  )
  .settings(commonResolverSetting)
  .settings(finatraLibrarySetting)
  .settings(coreLibrarySetting)
  .dependsOn(oauth2)
  .dependsOn(swagger)
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

///////////////////////////////////////////////
//
//  Independent libraries
//
///////////////////////////////////////////////

lazy val filhouette = (project in file("filhouette"))
  .settings(
    version      := versions.cactacea,
    organization := "io.github.cactacea.filhouette",
    scalaVersion := "2.12.5",
    name := "filhouette",
    concurrentRestrictions in Global += Tags.limit(Tags.Test, 1),
    testOptions in Test += Tests.Argument("-oI")
  )
  .settings(finatraLibrarySetting)
  .settings(
    libraryDependencies ++= Seq(
      "io.jsonwebtoken" % "jjwt" % "0.9.0",
      "com.github.cb372" %% "scalacache-ehcache" % "0.24.1",
      "org.apache.commons" % "commons-lang3" % "3.7",

      "org.scalacheck" %% "scalacheck" % versions.scalaCheck % "test",
      "org.scalatest" %% "scalatest" %  versions.scalaTest % "test",
      "org.specs2" %% "specs2-core" % versions.specs2 % "test",
      "org.specs2" %% "specs2-junit" % versions.specs2 % "test",
      "org.specs2" %% "specs2-matcher-extra" % versions.specs2 % "test",
      "org.specs2" %% "specs2-mock" % versions.specs2 % "test"
    ))
  .settings(
    libraryDependencies ++= Seq(
      "de.svenkubiak" % "jBCrypt" % "0.4.1"
      //      "org.jasig.cas.client" % "cas-client-core" % "3.4.1",
      //      "org.jasig.cas.client" % "cas-client-support-saml" % "3.4.1"
    ))

lazy val swagger = (project in file("swagger"))
  .settings(
    version      := versions.cactacea,
    organization := "io.github.cactacea.swagger",
    scalaVersion := "2.12.5",
    name := "swagger",
    concurrentRestrictions in Global += Tags.limit(Tags.Test, 1),
    testOptions in Test += Tests.Argument("-oI")
  )
  .settings(
    libraryDependencies ++= Seq(
      "com.twitter" %% "finatra-http" % versions.finagle,
      "io.swagger" % "swagger-core" % "1.5.17",
      "io.swagger" %% "swagger-scala-module" % "1.0.4",
      "org.webjars" % "swagger-ui" % "3.10.0",
      "net.bytebuddy" % "byte-buddy" % "1.7.11",
      "org.scalatest" %% "scalatest" % versions.scalaTest % "test"
    )
  )

lazy val oauth2 = (project in file("oauth2"))
  .settings(
    version      := versions.cactacea,
    organization := "com.twitter.finagle.oauth2",
    scalaVersion := "2.12.5",
    name := "oauth2",
    concurrentRestrictions in Global += Tags.limit(Tags.Test, 1),
    testOptions in Test += Tests.Argument("-oI")
  )
  .settings(
    libraryDependencies ++= Seq(
      "com.twitter" %% "finagle-http" % versions.finagle,
      "org.scalacheck" %% "scalacheck" % "1.13.5" % Test,
      "org.scalatest" %% "scalatest" % "3.0.5" % Test
    )
  )
