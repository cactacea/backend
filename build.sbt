import sbt.Keys.{libraryDependencies, publishArtifact, resolvers, scalacOptions}
import sbt.url
import sbtbuildinfo.BuildInfoPlugin.autoImport.buildInfoKeys

lazy val root = (project in file("."))
  .settings(
    name := "backend"
  )
  .settings(commonSettings)
  .settings(noPublishSettings)
  .settings(Migration.settings)
  .aggregate(server, core, plugin, finagger, filhouette)
  .enablePlugins(FlywayPlugin)


lazy val server = (project in file("server"))
  .settings(
    buildInfoPackage := "io.github.cactacea.backend",
    buildInfoKeys := Seq[BuildInfoKey](version, baseDirectory),
    buildInfoObject := "CactaceaBuildInfo"
  )
  .settings(commonSettings)
  .settings(commonResolverSetting)
  .settings(publishSettings)
  .settings(libraryDependencies ++= Dependencies.oauth2LibrarySettings)
  .settings(libraryDependencies ++= Dependencies.mysqlLibrarySettings)
  .settings(libraryDependencies ++= Dependencies.finatraLibrarySettings)
  .settings(libraryDependencies ++= Dependencies.testLibrarySettings)
  .settings(libraryDependencies ++= Dependencies.logLibrarySettings)
  .enablePlugins(FlywayPlugin)
  .enablePlugins(BuildInfoPlugin)
  .dependsOn(core % "compile->compile;test->test")
  .dependsOn(finagger % "compile->compile;test->test")


lazy val core = (project in file("core"))
  .settings(commonSettings)
  .settings(commonResolverSetting)
  .settings(publishSettings)
  .settings(libraryDependencies ++= Dependencies.coreLibrarySettings)
  .settings(libraryDependencies ++= Dependencies.finatraLibrarySettings)
  .settings(libraryDependencies ++= Dependencies.testLibrarySettings)
  .settings(libraryDependencies ++= Dependencies.logLibrarySettings)
  .dependsOn(finagger % "compile->compile;test->test")


lazy val filhouette = (project in file("filhouette"))
  .settings(commonSettings)
  .settings(commonResolverSetting)
  .settings(publishSettings)
  .settings(libraryDependencies ++= Dependencies.finatraLibrarySettings)
  .settings(libraryDependencies ++= Dependencies.filhouette)


lazy val swaggerUIVersion = SettingKey[String]("swaggerUIVersion")

lazy val finagger = (project in file("finagger"))
  .settings(
    swaggerUIVersion := "3.19.0",
    buildInfoPackage := "io.github.cactacea.finagger",
    buildInfoKeys := Seq[BuildInfoKey](swaggerUIVersion)
  )
  .settings(commonSettings)
  .settings(commonResolverSetting)
  .settings(publishSettings)
  .settings(libraryDependencies ++= Dependencies.finagger)
  .enablePlugins(BuildInfoPlugin)

lazy val plugin = (project in file("plugin"))
  .settings(
    sbtPlugin     := true,
    scalacOptions ++= Seq("-feature", "-deprecation")
  )
  .settings(commonSettings)
  .settings(commonResolverSetting)
  .settings(publishSettings)

lazy val demo = (project in file("demo"))
  .settings(
    mainClass in (Compile, run) := Some("io.github.cactacea.backend.DemoServerApp"),
    version in Docker := ( version in ThisBuild ).value,
    maintainer in Docker := "Cactacea",
    packageName in Docker := "backend",
    dockerBaseImage := "adoptopenjdk/openjdk8",
    dockerExposedPorts := Seq(9000, 9001),
    dockerRepository := Some("cactacea"),
    dockerUpdateLatest := true
  )
  .settings(commonSettings)
  .settings(commonResolverSetting)
  .settings(noPublishSettings)
  .dependsOn(server % "compile->compile;test->test")
  .enablePlugins(JavaAppPackaging)


lazy val commonSettings = Seq(
  organization := "io.github.cactacea",
  scalaVersion  := "2.12.7",
  crossScalaVersions := Seq("2.11.12", "2.12.7"),
  scalacOptions ++= Seq("-Ywarn-unused", "-Ywarn-unused-import", "-Xlint"),
  testOptions in Test += Tests.Argument("-oI"),
  concurrentRestrictions += Tags.limit(Tags.Test, 1),
  parallelExecution := false,
  fork := true
)

lazy val commonResolverSetting = Seq(
  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    "Maven central" at "http://central.maven.org/maven2/")
  )



// Publish Settings

lazy val publishSettings = Seq(

  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },


  licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
  homepage := Some(url("https://github.com/cactacea/backend")),

  publishMavenStyle := true,
  publishArtifact in Test := false,
  publishTo := Some(
    if (isSnapshot.value)
      Opts.resolver.sonatypeSnapshots
    else
      Opts.resolver.sonatypeStaging
  ),

  scmInfo := Some(
    ScmInfo(
      url("https://github.com/cactacea/backend"),
      "scm:git:https://github.com/cactacea/backend.git"
    )
  ),

  developers := List(
    Developer(
      id    = "takeshishimada",
      name  = "Takeshi Shimada",
      email = "expensivegasprices@gmail.com",
      url   = url("https://github.com/takeshishimada")
    )
  )
)



// No Publish Settings

lazy val noPublishSettings = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false,
  // sbt-pgp's publishSigned task needs this defined even though it is not publishing.
  publishTo := Some(Resolver.file("Unused transient repository", file("target/unusedrepo")))
)


// Release Process

import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._

releaseVersionFile := baseDirectory.value / "version.sbt"

releaseCrossBuild := true
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  ReleaseStep(action = Command.process("publish", _)),
  ReleaseStep(action = Command.process("demo/docker:publish", _)),
  setNextVersion,
  commitNextVersion,
  ReleaseStep(action = Command.process("sonatypeReleaseAll", _)),
  pushChanges

)
