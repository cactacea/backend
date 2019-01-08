package io.github.cactacea.finachat

import java.net.InetSocketAddress

import com.twitter.app.Flag
import com.twitter.conversions.time._
import com.twitter.finagle.{ListeningServer, NullServer, Service}
import com.twitter.inject.annotations.Lifecycle
import com.twitter.inject.conversions.string._
import com.twitter.inject.server.{PortUtils, TwitterServer}
import com.twitter.util.{Await, Duration}
import io.github.cactacea.finasocket.{Client, WebSocket}

private object BaseChatServer {
  /**
    * Sentinel used to indicate no chat announcement.
    */
  val NoChatAnnouncement: String = ""
}

trait BaseChatServer extends TwitterServer {

  protected def defaultChatPort: String = ":14000"
  private val chatPortFlag = flag("chat.port", defaultChatPort, "External Chat server port")

  protected def defaultShutdownTimeout: Duration = 1.minute
  private val shutdownTimeoutFlag = flag(
    "shutdown.time",
    defaultShutdownTimeout,
    "Maximum amount of time to wait for pending requests to complete on shutdown"
  )

  protected def defaultChatAnnouncement: String = BaseChatServer.NoChatAnnouncement
  private val chatAnnounceFlag = flag[String]("chat.announce", defaultChatAnnouncement,
    "Address for announcing Chat server. Empty string indicates no announcement.")

  /* Private Mutable State */

  private var chatServer: ListeningServer = NullServer

  private lazy val baseChatServer: WebSocket.Server = {
    WebSocket.Server()
  }

  protected def chatService: Service[Client, Client] = {
    NullService
  }

  /**
    * This method allows for further configuration of the chat server for parameters not exposed by
    * this trait or for overriding defaults provided herein, e.g.,
    *
    * override def configureChatServer(server: WebSocket.Server): WebSocket.Server = {
    *  server
    *    .withRequestTimeout(10.minutes)
    * }
    *
    * @param server - the [[io.github.cactacea.finasocket.WebSocket.Server]] to configure.
    *
    * @return a configured WebSocket.Server.
    */
  protected def configureChatServer(server: WebSocket.Server): WebSocket.Server = {
    server
  }

  /* Lifecycle */

  @Lifecycle
  override protected def postWarmup(): Unit = {
    super.postWarmup()

    startChatServer()
  }


  /* Private */

  /* We parse the port as a string, so that clients can
     set the port to "" to prevent a http server from being started */
  private def parsePort(port: Flag[String]): Option[InetSocketAddress] = {
    port().toOption.map(PortUtils.parseAddr)
  }

  private def startChatServer(): Unit = {
    for (port <- parsePort(chatPortFlag)) {
      val serverBuilder =
        configureChatServer(
          baseChatServer
        )

      chatServer = serverBuilder.serve(port, chatService)
      onExit {
        Await.result(chatServer.close(shutdownTimeoutFlag().fromNow))
      }
      await(chatServer)
      chatAnnounceFlag() match {
        case BaseChatServer.NoChatAnnouncement => // no-op
        case addr =>
          info(s"chat server announced to $addr")
          chatServer.announce(addr)
      }
      info(s"chat server started on port: $port")
    }
  }

}