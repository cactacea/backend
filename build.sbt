import sbt.Keys.{organization, resolvers, testOptions}

version in ThisBuild      := "18.9.1"
organization in ThisBuild := "io.github.cactacea.backend"
scalaVersion in ThisBuild := "2.12.6"
testOptions in Test in ThisBuild += Tests.Argument("-oI")
concurrentRestrictions in Global += Tags.limit(Tags.Test, 1)
parallelExecution in ThisBuild := false
fork in ThisBuild := true

lazy val root = (project in file("."))
  .settings(
    name := "backend"
  )
  .aggregate(server, core, addons)
  .settings(Migration.settings)
  .enablePlugins(FlywayPlugin)


lazy val doc = (project in file("doc"))
  .settings(
    name := "doc",
    mainClass in (Compile, run) := Some("io.github.cactacea.backend.DocServerApp"),
  )
  .settings(
    version in Docker := "latest",
    maintainer in Docker := "Cactacea",
    packageName in Docker := "backend",
    dockerBaseImage := "adoptopenjdk/openjdk10",
    dockerExposedPorts := Seq(9000, 9001),
    dockerRepository := Some("cactacea")
  )
  .dependsOn(server % "compile->compile;test->test")
  .dependsOn(finagger)
  .enablePlugins(JavaAppPackaging)


lazy val server = (project in file("server"))
  .settings(
    name := "server"
  )
  .settings(
    buildInfoPackage := "io.github.cactacea.backend",
    buildInfoKeys := Seq[BuildInfoKey](version, baseDirectory),
    buildInfoObject := "CactaceaBuildInfo"
  )
  .settings(commonResolverSetting)
  .settings(libraryDependencies ++= Dependencies.oauth2LibrarySettings)
  .settings(libraryDependencies ++= Dependencies.mysqlLibrarySettings)
  .settings(libraryDependencies ++= Dependencies.finatraLibrarySettings)
  .settings(libraryDependencies ++= Dependencies.testLibrarySettings)
  .settings(libraryDependencies ++= Dependencies.logLibrarySettings)
  .enablePlugins(FlywayPlugin)
  .enablePlugins(BuildInfoPlugin)
  .dependsOn(core % "compile->compile;test->test")


lazy val core = (project in file("core"))
  .settings(
    name := "core"
  )
  .settings(commonResolverSetting)
  .settings(libraryDependencies ++= Dependencies.coreLibrarySettings)
  .settings(libraryDependencies ++= Dependencies.finatraLibrarySettings)
  .settings(libraryDependencies ++= Dependencies.testLibrarySettings)
  .settings(libraryDependencies ++= Dependencies.logLibrarySettings)
  .dependsOn(finagger)


lazy val addons = (project in file("addons"))
  .settings(
    name := "addons",
    concurrentRestrictions in Global += Tags.limit(Tags.Test, 1),
    testOptions in Test += Tests.Argument("-oI")
  )
  .settings(commonResolverSetting)
  .settings(libraryDependencies ++= Dependencies.addonsLibrarySettings)
  .settings(libraryDependencies ++= Dependencies.finatraLibrarySettings)
  .settings(libraryDependencies ++= Dependencies.testLibrarySettings)
  .settings(libraryDependencies ++= Dependencies.logLibrarySettings)
  .dependsOn(core)

lazy val commonResolverSetting = Seq(
  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    "Maven central" at "http://central.maven.org/maven2/"
  )
)

lazy val finagger = RootProject(uri("git://github.com/cactacea/finagger.git"))

