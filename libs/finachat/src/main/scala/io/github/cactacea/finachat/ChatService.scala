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
    val result = frame match {
      case Frame.Text(text) =>
        ObjectMapper.read[Command](text) match {
          case Right(command) =>
            handleCommand(command, client)
          case Left(_) =>
            Future.value(Response.invalidCommand)
        }
      case _ =>
        Future.value(Response.invalidCommand)
    }
    result.map(Frame.Text(_))
  }

  private def handleCommand(command: Command, client: Client): Future[String] = {
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
        Future.value(Response.invalidCommand)
    }
  }

  private def handleCommandConnect(command: Command, client: Client): Future[String] = {
    Option(authInfoMap.get(client)) match {
      case Some(_) =>
        Future.value(Response.done)
      case None =>
        chatHandler.connect(command.value).map(_ match {
          case Some(authInfo) =>
            authInfoMap.put(client, authInfo)
            clientMap.put(authInfo, client)
            Response.done
          case None =>
            Response.couldNotLogin
        })
    }

  }

  private def handleCommandDisconnect(command: Command, client: Client): Future[String] = {
    Option(authInfoMap.get(client)) match {
      case Some(authInfo) =>
        chatHandler.disconnect(authInfo).flatMap({ _ =>
          authInfoMap.remove(client)
          clientMap.remove(authInfo)
          handleCommandLeave(command, client)
        })
      case None =>
        Future.value(Response.done)
    }
  }

  private def handleCommandJoin(command: Command, client: Client): Future[String] = {
    val room = command.value
    Option(roomMap.get(client)) match {
      case Some(_) =>
        Future.value(Response.alreadyJoinedRoom)
      case None =>
        val authInfo = authInfoMap.get(client)
        chatHandler.canJoin(authInfo, room).flatMap(_ match {
          case true =>
            roomMap.put(client, room)
            val cg = channelGroup(room)
            cg.add(client.channel)
            for {
              message <- chatHandler.joinMessage(authInfo, room).map(Response.memberJoined(_))
              _ <- PublishService.publish(room, message)
            } yield (Response.done)
          case false =>
            Future.value(Response.couldNotJoin)
        })
    }
  }

  private def handleCommandLeave(command: Command, client: Client): Future[String] = {
    val room = command.value
    Option(roomMap.get(client)) match {
      case Some(_) =>
        roomMap.remove(client)
        val authInfo = authInfoMap.get(client)
        chatHandler.canLeave(authInfo, room).flatMap(_ match {
          case true =>
            val cg = channelGroup(room)
            cg.remove(client.channel)
            for {
              message <- chatHandler.joinMessage(authInfo, room).map(Response.memberLeft(_))
              _ <- PublishService.publish(room, message)
            } yield (Response.done)
          case false =>
            Future.value(Response.couldNotLeave)
        })
      case None =>
        Future.value(Response.done)
    }
  }

  private def handleCommandSend(command: Command, client: Client): Future[String] = {
    val message = command.value
    Option(roomMap.get(client)) match {
      case Some(room) =>
        val authInfo = authInfoMap.get(client)
        chatHandler.canSend(authInfo, room).flatMap(_ match {
          case true =>
            for {
              message <- chatHandler.sendMessage(authInfo, room, message).map(Response.messageArrived(_))
              _ <- PublishService.publish(room, message)
            } yield (Response.done)
          case false =>
            Future.value(Response.couldNotSend)
        })
      case None =>
        Future.value(Response.notJoinedRoom)
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
        channelGroup.writeAndFlush(toNetty(Frame.Text(message))).sync()
        Future.Done
      case None =>
        Future.Done
    }
  }

}

