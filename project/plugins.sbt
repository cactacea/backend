logLevel := Level.Warn

resolvers += "Flyway" at "https://flywaydb.org/repo"

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.3")
addSbtPlugin("io.github.davidmweber" % "flyway-sbt" % "5.0.0-RC2")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")