logLevel := Level.Warn

resolvers += "Flyway" at "https://flywaydb.org/repo"
resolvers += "Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")

addSbtPlugin("io.github.davidmweber" % "flyway-sbt" % "5.2.0")

addSbtPlugin("com.codacy" % "sbt-codacy-coverage" % "1.3.12")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.6")
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.7.0")
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "2.3")
addSbtPlugin("io.crashbox" % "sbt-gpg" % "0.2.0")
addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.10")
