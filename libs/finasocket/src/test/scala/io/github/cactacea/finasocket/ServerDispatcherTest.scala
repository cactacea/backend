package io.github.cactacea.finasocket

import java.net.SocketAddress

import com.twitter.concurrent.AsyncQueue
import com.twitter.conversions.time._
import com.twitter.finagle.stats.DefaultStatsReceiver
import com.twitter.finagle.transport.QueueTransport
import com.twitter.finagle.{Service, Status}
import com.twitter.util.{Await, Future}
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import io.netty.handler.codec.http.{DefaultHttpRequest, HttpMethod, HttpVersion}
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ServerDispatcherTest extends FunSuite {
  import ServerDispatcherTest._

  val echo = new Service[Request, Response] {
    def apply(req: Request): Future[Response] = {
      Future.value(Response(req.messages))
    }
  }

  test("invalid message") {
    val (in, out) = mkPair[Any, Any]
    val disp = new ServerDispatcher(out, echo, DefaultStatsReceiver)
    in.write("invalid")
    Await.ready(out.onClose, 1.second)
    assert(out.status == Status.Closed)
  }

  test("valid message then invalid") {
    val (in, out) = mkPair[Any, Any]
    val disp = new ServerDispatcher(out, echo, DefaultStatsReceiver)
    val req = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/")
    val addr = new SocketAddress{}
    in.write((req, addr))
    in.write(new TextWebSocketFrame("hello"))
    val frame = Await.result(in.read(), 1.second)
    assert(frame.asInstanceOf[TextWebSocketFrame].text() == "hello")
    in.write("invalid")
    assert(out.status == Status.Closed)
  }
}

object ServerDispatcherTest {
  def mkPair[A,B] = {
    val inq = new AsyncQueue[A]
    val outq = new AsyncQueue[B]
    (new QueueTransport[A, B](inq, outq), new QueueTransport[B, A](outq, inq))
  }
}
