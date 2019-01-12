package io.github.cactacea.finachat

import com.twitter.finagle.Redis
import com.twitter.finagle.redis.util.StringToBuf
import com.twitter.util.Future

object PublishService {

  val redisClient = Redis.newRichClient(Config.redis.dest)
  val redisChannel = StringToBuf("chat")

  def publish(room: String, message: String): Future[Long] = {
    redisClient.publish(redisChannel, StringToBuf(ObjectMapper.write(FanOut(room, message))))
  }


}
