package io.github.cactacea.finachat

import com.typesafe.config.ConfigFactory
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._

object Config  {

  private val config = ConfigFactory.load()
  private val redisConfig = config.as[RedisConfig]("chat.redis")

  object redis {  // scalastyle:ignore
    lazy val dest = redisConfig.dest.getOrElse("localhost:6379")
  }

  println(s"chat.redis.dest = ${redis.dest}") // scalastyle:ignore

}
