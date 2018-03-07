package io.github.cactacea.core.util.exceptions

import com.google.inject.Inject
import com.twitter.finatra.json.FinatraObjectMapper

case class FanOutException() extends scala.Exception with scala.Product with scala.Serializable {

  @Inject private var om: FinatraObjectMapper = _

  override def getMessage()  = {
    om.writePrettyString(this)
  }

}