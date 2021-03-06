package io.github.cactacea.finasocket

import java.net.{InetSocketAddress, SocketAddress, URI}

import com.twitter.concurrent.AsyncStream
import com.twitter.conversions.DurationOps._
import com.twitter.finagle.Service
import com.twitter.finagle.param.Stats
import com.twitter.finagle.stats.{NullStatsReceiver, StatsReceiver}
import com.twitter.util._
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class EndToEndTest extends FunSuite {
  import EndToEndTest._
  test("echo") {
    val echo = new Service[Client, Client] {
      def apply(client: Client): Future[Client] =
        Future.value(client)
    }

    connect(echo) { client =>
      val frames = texts("hello", "world")
      for {
        response <- client(mkRequest("/", frames))
        messages <- response.onReceived.toSeq()
      } yield assert(messages == frames)
    }
  }
}

private object EndToEndTest {
  def connect(
               service: Service[Client, Client],
               stats: StatsReceiver = NullStatsReceiver
  )(run: Service[Request, Response] => Future[Unit]): Unit = {
    val server = WebSocket.server
      .withLabel("server")
      .configured(Stats(stats))
      .serve("localhost:*", service)

    val addr = server.boundAddress.asInstanceOf[InetSocketAddress]

    val client = WebSocket.client
      .configured(Stats(stats))
      .newService(s"${addr.getHostName}:${addr.getPort}", "client")

    Await.result(run(client).ensure(Closable.all(client, server).close()), 1.second)
  }

  def texts(messages: String*): Seq[Frame] =
    messages.map(Frame.Text(_))

  def mkRequest(path: String, frames: Seq[Frame]): Request =
    Request(new URI(path), Map.empty, new SocketAddress{}, AsyncStream.fromSeq(frames))
}
