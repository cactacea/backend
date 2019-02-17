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
  .aggregate(chat, api, server, core, plugin, finagger, filhouette, finasocket, finachat, onesignal, aws, docs)
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
  .settings(libraryDependencies ++= Dependencies.oauth2)
  .settings(libraryDependencies ++= Dependencies.mysql)
  .settings(libraryDependencies ++= Dependencies.finatra)
  .settings(libraryDependencies ++= Dependencies.test)
  .settings(libraryDependencies ++= Dependencies.log)
  .enablePlugins(FlywayPlugin)
  .enablePlugins(BuildInfoPlugin)
  .dependsOn(core % "compile->compile;test->test")
  .dependsOn(finagger)


lazy val core = (project in file("core"))
  .settings(commonSettings)
  .settings(commonResolverSetting)
  .settings(publishSettings)
  .settings(libraryDependencies ++= Dependencies.core)
  .settings(libraryDependencies ++= Dependencies.finatra)
  .settings(libraryDependencies ++= Dependencies.test)
  .settings(libraryDependencies ++= Dependencies.log)
  .dependsOn(finagger)


lazy val utils = (project in file("utils"))
  .settings(commonSettings)
  .settings(commonResolverSetting)
  .settings(libraryDependencies ++= Dependencies.utils)


lazy val finagger = (project in file("libs/finagger"))
  .settings(
    swaggerUIVersion := "3.19.0",
    buildInfoPackage := "io.github.cactacea.finagger",
    buildInfoKeys := Seq[BuildInfoKey](swaggerUIVersion)
  )
  .settings(commonSettings)
  .settings(commonResolverSetting)
  .settings(publishSettings)
  .settings(libraryDependencies ++= Dependencies.finatra)
  .settings(libraryDependencies ++= Dependencies.finagger)
  .enablePlugins(BuildInfoPlugin)


lazy val filhouette = (project in file("libs/filhouette"))
  .settings(commonSettings)
  .settings(commonResolverSetting)
  .settings(publishSettings)
  .settings(libraryDependencies ++= Dependencies.finatra
  )
  .settings(libraryDependencies ++= Dependencies.filhouette)


lazy val finasocket = (project in file("libs/finasocket"))
  .settings(commonSettings)
  .settings(commonResolverSetting)
  .settings(publishSettings)
  .settings(libraryDependencies ++= Dependencies.finasocket)


lazy val finachat = (project in file("libs/finachat"))
  .settings(commonSettings)
  .settings(commonResolverSetting)
  .settings(publishSettings)
  .settings(libraryDependencies ++= Dependencies.finachat)
  .dependsOn(finasocket)


lazy val swaggerUIVersion = SettingKey[String]("swaggerUIVersion")


lazy val plugin = (project in file("plugin"))
  .settings(
    organization := "io.github.cactacea",
    name := "plugin",
    sbtPlugin     := true,
    testOptions in Test += Tests.Argument("-oI"),
    concurrentRestrictions += Tags.limit(Tags.Test, 1),
    parallelExecution := false,
    scalacOptions ++= Seq("-feature", "-deprecation")
  )
  .settings(publishSettings)
  .settings(libraryDependencies ++= Dependencies.test)
  .dependsOn(core % "compile->compile;test->test")


lazy val api = (project in file("demo/api"))
  .settings(
    mainClass in (Compile, run) := Some("io.github.cactacea.backend.APIServerApp"),
    version in Docker := ( version in ThisBuild ).value,
    maintainer in Docker := "Cactacea",
    packageName in Docker := "api",
    dockerBaseImage := "adoptopenjdk/openjdk8",
    dockerExposedPorts := Seq(9000, 9001),
    dockerRepository := Some("cactacea"),
    dockerUpdateLatest := true
  )
  .settings(commonSettings)
  .settings(commonResolverSetting)
  .settings(libraryDependencies ++= Dependencies.test)
  .settings(noPublishSettings)
  .dependsOn(aws)
  .dependsOn(onesignal)
  .dependsOn(redis)
  .dependsOn(server % "compile->compile;test->test")
  .enablePlugins(JavaAppPackaging)


lazy val chat = (project in file("demo/chat"))
  .settings(
    mainClass in (Compile, run) := Some("io.github.cactacea.backend.ChatServerApp"),
    version in Docker := ( version in ThisBuild ).value,
    maintainer in Docker := "Cactacea",
    packageName in Docker := "chat",
    dockerBaseImage := "adoptopenjdk/openjdk8",
    dockerExposedPorts := Seq(9000, 9001),
    dockerRepository := Some("cactacea"),
    dockerUpdateLatest := true
  )
  .settings(commonSettings)
  .settings(commonResolverSetting)
  .settings(libraryDependencies ++= Dependencies.test)
  .settings(noPublishSettings)
  .dependsOn(finachat)
  .enablePlugins(JavaAppPackaging)



lazy val commonSettings = Seq(
  organization := "io.github.cactacea",
  scalaVersion  := "2.12.7",
  scalacOptions ++= Seq("-Ywarn-unused", "-Ywarn-unused-import", "-Xlint"),
  testOptions in Test += Tests.Argument("-oI"),
  concurrentRestrictions += Tags.limit(Tags.Test, 1),
  parallelExecution := false,
  fork := true
)


lazy val commonResolverSetting = Seq(
  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    "Maven central" at "http://central.maven.org/maven2/"))


lazy val onesignal = (project in file("addons/onesignal"))
  .settings(addonsCommonSettings)
  .settings(commonResolverSetting)
  .settings(publishSettings)
  .settings(libraryDependencies ++= Dependencies.finatra)
  .settings(libraryDependencies ++= Dependencies.ficus)
  .dependsOn(core % "compile->compile;test->test")
  .dependsOn(utils)


lazy val aws = (project in file("addons/aws"))
  .settings(addonsCommonSettings)
  .settings(awsCommonResolverSetting)
  .settings(commonResolverSetting)
  .settings(publishSettings)
  .settings(libraryDependencies ++= Dependencies.aws)
  .settings(libraryDependencies ++= Dependencies.ficus)
  .dependsOn(core % "compile->compile;test->test")
  .dependsOn(utils)


lazy val redis = (project in file("addons/redis"))
  .settings(addonsCommonSettings)
  .settings(commonResolverSetting)
  .settings(publishSettings)
  .dependsOn(core % "compile->compile;test->test")
  .dependsOn(finachat)


lazy val addonsCommonSettings = Seq(
  organization := "io.github.cactacea.addons",
  scalaVersion  := "2.12.7",
  scalacOptions ++= Seq("-Ywarn-unused", "-Ywarn-unused-import", "-Xlint"),
  testOptions in Test += Tests.Argument("-oI"),
  concurrentRestrictions += Tags.limit(Tags.Test, 1),
  parallelExecution := false,
  fork := true
)


lazy val awsCommonResolverSetting = Seq(
  resolvers ++= Seq(
    "Monsanto Repository" at "https://dl.bintray.com/monsanto/maven/")
)


lazy val docs = project
  .settings(
    name := "backend",
    organization := "io.github.cactacea"
  )
  .settings(docSettings)
  .settings(noPublishSettings)
  .enablePlugins(MicrositesPlugin)
  .dependsOn(core, server, plugin, onesignal, aws)

lazy val docSettings = commonSettings ++ Seq(
  micrositeName := "Cactacea",
  micrositeDescription := "A framework to construct social networking applications",
  micrositeAuthor := "Takeshi Shimada",
  micrositeHighlightTheme := "atom-one-light",
  micrositeHomepage := "https://github.com/cactacea/backend",
  micrositeDocumentationUrl := "/cactacea/scaladoc",
  micrositeGithubOwner := "cactacea",
  micrositeGithubRepo := "backend",
  micrositeBaseUrl := "cactacea",
  micrositeStaticDirectory := (resourceDirectory in Compile).value / "microsite" / "static",
  micrositeDataDirectory := (resourceDirectory in Compile).value / "microsite" / "data",
    //  micrositeExtraMdFiles := Map(file("CONTRIBUTING.md") -> ExtraMdFileConfig("contributing.md", "docs")),
  micrositePalette := Map(
    "brand-primary" -> "#6FACDD",
    "brand-secondary" -> "#6FABDD",
    "brand-tertiary" -> "#6FAADD",
    "gray-dark" -> "#48494B",
    "gray" -> "#7D7E7D",
    "gray-light" -> "#E5E6E5",
    "gray-lighter" -> "#F4F3F4",
    "white-color" -> "#FFFFFF"),
//  addMappingsToSiteDir(mappings in (ScalaUnidoc, packageDoc), micrositeDocumentationUrl),
  ghpagesNoJekyll := false,
  scalacOptions in (ScalaUnidoc, unidoc) ++= Seq(
    "-groups",
    "-implicits",
    "-skip-packages", "scalaz",
    "-doc-source-url", scmInfo.value.get.browseUrl + "/tree/master€{FILE_PATH}.scala",
    "-sourcepath", baseDirectory.in(LocalRootProject).value.getAbsolutePath,
    "-doc-root-content", (resourceDirectory.in(Compile).value / "rootdoc.txt").getAbsolutePath
  ),
  scalacOptions ~= {
    _.filterNot(Set("-Yno-predef", "-Xlint", "-Ywarn-unused-import"))
  },
  git.remoteRepo := "git@github.com:cactacea/backend.git",
  unidocProjectFilter in (ScalaUnidoc, unidoc) := inProjects(core, server),
  includeFilter in makeSite := "*.html" | "*.css" | "*.png" | "*.jpg" | "*.gif" | "*.svg" | "*.js" | "*.swf" | "*.yml" | "*.md" | "*.json",
  siteSubdirName in ScalaUnidoc := "docs"
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
  organizationHomepage := Some(url("https://github.com/cactacea")),

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

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  ReleaseStep(action = Command.process("publish", _)),
  ReleaseStep(action = Command.process("api/docker:publish", _)),
  ReleaseStep(action = Command.process("chat/docker:publish", _)),
  ReleaseStep(action = Command.process("sonatypeReleaseAll", _)),
  releaseStepTask(publishMicrosite in LocalProject("docs")),
  setNextVersion,
  commitNextVersion,
  pushChanges

)
