package io.github.cactacea.backend.core.util.configs

import com.twitter.util.Duration
import com.typesafe.config.{Config => TypeSafeConfig}
import net.ceedubs.ficus.readers.ValueReader

import scala.concurrent.duration.NANOSECONDS

trait DurationReader {

  /**
    * A reader for for a scala.concurrent.duration.FiniteDuration. This reader should be able to read any valid duration
    * format as defined by the <a href="https://github.com/typesafehub/config/blob/master/HOCON.md">HOCON spec</a>.
    * For example, it can read "15 minutes" or "1 day".
    */
  implicit def twitterDurationReader: ValueReader[Duration] = new ValueReader[Duration] {
    def read(config: TypeSafeConfig, path: String): Duration = {
      val nanos = config.getDuration(path, NANOSECONDS)
      Duration.fromNanoseconds(nanos)
    }
  }

}

object DurationReader extends DurationReader