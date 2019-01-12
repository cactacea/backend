package io.github.cactacea.finachat

import java.util.concurrent.ConcurrentHashMap

import com.twitter.concurrent.AsyncStream
import com.twitter.finagle.{Service}
import com.twitter.io.Buf
import com.twitter.util.Future
import io.github.cactacea.finasocket.{Client, Frame}
import io.netty.channel.group.{ChannelGroup, DefaultChannelGroup}
import io.netty.util.concurrent.GlobalEventExecutor

class ChatService[A <: AuthInfo](chatHandler: ChatHandler[A]) extends Service[Client, Client] {

  val authInfoMap = new ConcurrentHashMap[Client, A]()
  val clientMap =  new ConcurrentHashMap[A, Client]()
  val roomMap = new ConcurrentHashMap[Client, String]()
  val channelGroupMap =  new ConcurrentHashMap[String, ChannelGroup]()

  import com.twitter.finagle.websocket.Netty4.toNetty

  def apply(client: Client): Future[Client] = {
    val response = client.onRead.flatMap(frame =>
      AsyncStream.fromFuture(handleFrame(frame, client)))

    client.onClose.flatMap(_ =>
      handleCommandDisconnect(Command("disconnect", ""), client)
    )

    Future.value(client.copy(onWrite = response))
  }

  private def handleFrame(frame: Frame, client: Client): Future[Frame] = {
    frame match {
      case Frame.Text(text) =>
        ObjectMapper.read[Command](text) match {
          case Right(command) =>
            handleCommand(command, client)
          case Left(_) =>
            Future.value(Response.InvalidCommand)
        }
      case _ =>
        Future.value(Response.InvalidCommand)
    }
  }

  private def handleCommand(command: Command, client: Client): Future[Frame] = {
    command.name match {
      case Command.Connect =>
        handleCommandConnect(command, client)

      case Command.Disconnect =>
        handleCommandDisconnect(command, client)

      case Command.Join =>
        handleCommandJoin(command, client)

      case Command.Leave =>
        handleCommandLeave(command, client)

      case Command.Send =>
        handleCommandSend(command, client)

      case _  =>
        Future.value(Response.InvalidCommand)
    }
  }

  private def handleCommandConnect(command: Command, client: Client): Future[Frame] = {
    Option(authInfoMap.get(client)) match {
      case Some(_) =>
        Future.value(Response.Done)
      case None =>
        chatHandler.connect(command.value).map(_ match {
          case Some(authInfo) =>
            authInfoMap.put(client, authInfo)
            clientMap.put(authInfo, client)
            Response.Done
          case None =>
            Response.CouldNotLogin
        })
    }

  }

  private def handleCommandDisconnect(command: Command, client: Client): Future[Frame] = {
    Option(authInfoMap.get(client)) match {
      case Some(authInfo) =>
        chatHandler.disconnect(authInfo).flatMap({ _ =>
          authInfoMap.remove(client)
          clientMap.remove(authInfo)
          handleCommandLeave(command, client)
        })
      case None =>
        Future.value(Response.Done)
    }
  }

  private def handleCommandJoin(command: Command, client: Client): Future[Frame] = {
    Option(roomMap.get(client)) match {
      case Some(_) =>
        Future.value(Response.AlreadyJoinedRoom)
      case None =>
        val authInfo = authInfoMap.get(client)
        chatHandler.join(authInfo, command.value).flatMap(_ match {
          case true =>
            roomMap.put(client, command.value)
            val cg = channelGroup(command.value)
            cg.add(client.channel)
            for {
              message <- chatHandler.joinMessage(authInfo)
              _ <- PublishService.publish(command.value, message)
            } yield (Response.Done)
          case false =>
            Future.value(Response.CouldNotJoin)
        })
    }
  }

  private def handleCommandLeave(command: Command, client: Client): Future[Frame] = {
    Option(roomMap.get(client)) match {
      case Some(_) =>
        roomMap.remove(client)
        val authInfo = authInfoMap.get(client)
        chatHandler.leave(authInfo, command.value).flatMap(_ match {
          case true =>
            val cg = channelGroup(command.value)
            cg.remove(client.channel)
            for {
              message <- chatHandler.leaveMessage(authInfo)
              _ <- PublishService.publish(command.value, message)
            } yield (Response.Done)
          case false =>
            Future.value(Response.CouldNotLeave)
        })
      case None =>
        Future.value(Response.Done)
    }
  }

  private def handleCommandSend(command: Command, client: Client): Future[Frame] = {
    Option(roomMap.get(client)) match {
      case Some(room) =>
        val authInfo = authInfoMap.get(client)
        chatHandler.send(authInfo, command.value).flatMap(_ match {
          case true =>
            for {
              message <- chatHandler.sendMessage(authInfo, command.value)
              _ <- PublishService.publish(room, message)
            } yield (Response.Done)
          case false =>
            Future.value(Response.CouldNotSend)
        })
      case None =>
        Future.value(Response.NotJoinedRoom)
    }
  }

  private def channelGroup(room: String): ChannelGroup = {
    synchronized {
      Option(channelGroupMap.get(room)) match {
        case None =>
          val channelGroup = new DefaultChannelGroup (room, GlobalEventExecutor.INSTANCE);
          channelGroupMap.put(room, channelGroup)
          channelGroup
        case Some(channelGroupMap) =>
          channelGroupMap
      }
    }
  }

  PublishService.redisClient.subscribe(Seq(PublishService.redisChannel)) { case (_: Buf, text: Buf) =>
    val message = ObjectMapper.read[FanOut](Buf.Utf8.unapply(text).get)
    message match {
      case Right(message) =>
        write(message.room, message.message)
      case Left(_) =>
    }
  }

  private def write(room: String, message: String): Future[Unit] = {
    Option(channelGroupMap.get(room)) match {
      case Some(channelGroup) =>
        val response = ObjectMapper.write(Response(0, Some(message), None))
        channelGroup.writeAndFlush(toNetty(Frame.Text(response))).sync()
        Future.Done
      case None =>
        Future.Done
    }
  }

}

