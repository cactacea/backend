logLevel := Level.Warn

resolvers += "Flyway" at "https://flywaydb.org/repo"
resolvers += "Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/"
resolvers += Resolver.sonatypeRepo("snapshots")

addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")

addSbtPlugin("io.github.davidmweber" % "flyway-sbt" % "6.0.7")

addSbtPlugin("com.codacy" % "sbt-codacy-coverage" % "2.112")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.6.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.4.1")
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.9.0")
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "2.5")
addSbtPlugin("io.crashbox" % "sbt-gpg" % "0.2.0")
addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.11")

addSbtPlugin("com.47deg"  % "sbt-microsites" % "0.9.3")
addSbtPlugin("com.eed3si9n" % "sbt-unidoc" % "0.4.2")

addSbtPlugin("pl.project13.scala" % "sbt-jmh" % "0.3.7")