sbt ++$TRAVIS_SCALA_VERSION
sbt clean coverage test coverageReport -Denv=test
