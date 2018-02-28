sbt ++$TRAVIS_SCALA_VERSION clean coverage test coverageReport 'set concurrentRestrictions in Global += Tags.limit(Tags.Test, 1)'
