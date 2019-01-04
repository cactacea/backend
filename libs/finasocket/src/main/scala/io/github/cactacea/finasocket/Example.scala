package io.github.cactacea.finasocket

import java.net.{InetSocketAddress, URI}

import com.twitter.concurrent._
import com.twitter.finagle.Service
import com.twitter.util.{Await, Future}

object Example extends App {

  val service = WebSocket.serve(":14000", new Service[Client, Response] {
    def apply(client: Client): Future[Response] = {

      println("connected")

      val messages = client.onRead.map({ f =>
        val text = f match {
          case Frame.Text("1") => Frame.Text("one")
          case Frame.Text("2") => Frame.Text("two")
          case Frame.Text("3") => Frame.Text("three")
          case Frame.Text("4") => Frame.Text("cuatro")
          case Frame.Text("5") => Frame.Text("five")
          case Frame.Text("6") => Frame.Text("6")
          case _ => Frame.Text("??")
        }
        println(text.text)
        text
      })

      client.onClose.ensure {
        println("disconnected")
      }

      Future.value(Response(messages))
    }
  })

  Await.ready(service)
}

object ExampleClient extends App {

  val client = WebSocket.client
    .newService(s"localhost:14000", "client")
  val path = "ws://localhost:14000/"
  val frames = AsyncStream.fromSeq(Seq(Frame.Text("1")))
  val request = Request(new URI(path), Map("Host" -> "localhost:14000"), new InetSocketAddress("localhost", 14000), frames)
  val f = Await.result(client(request))
  val boop = f.onReceived.foldLeft("")({
    case (acc, Frame.Text(text)) =>
      println("Acc")
      s"$acc $text"
    case (acc, _) => acc
  })

  val beep = Await.result(boop)
  println(s"Done $beep!")
}