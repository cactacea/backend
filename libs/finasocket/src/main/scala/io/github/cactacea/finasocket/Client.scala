package io.github.cactacea.finasocket

import java.net.{SocketAddress, URI}

import com.twitter.finagle.client.{StackClient, StdStackClient, Transporter}
import com.twitter.finagle.netty4.Netty4Transporter
import com.twitter.finagle.param.ProtocolLibrary
import com.twitter.finagle.ssl.client.SslClientConfiguration
import com.twitter.finagle.transport.{Transport, TransportContext}
import com.twitter.finagle.{Address, Service, ServiceFactory, Stack}
import io.netty.channel.ChannelPipeline
import io.netty.handler.codec.http.{DefaultHttpHeaders, HttpClientCodec, HttpObjectAggregator}
import io.netty.handler.codec.http.websocketx.{WebSocketClientHandshakerFactory, WebSocketVersion}

case class Client(stack: Stack[ServiceFactory[Request, Response]] = StackClient.newStack,
                  params: Stack.Params = StackClient.defaultParams + ProtocolLibrary("ws"))
  extends StdStackClient[Request, Response, Client] {
  override protected type In = Any
  override protected type Out = Any
  override protected type Context = TransportContext

  var scheme: String = "ws"
  var handler: WebSocketClientHandler = _

  override protected def newTransporter(addr: SocketAddress): Transporter[In, Out, Context] = {

    val endpoint = params[Transporter.EndpointAddr].addr
    val uri = endpoint match {
      case Address.Inet(a, _) =>
        Some(new URI(s"${scheme}://${a.getHostName}:${a.getPort}"))
      case _ =>
        None
    }

    val httpHeaders = new DefaultHttpHeaders()
    val handShaker = WebSocketClientHandshakerFactory.newHandshaker(uri.get, WebSocketVersion.V13, null, false, httpHeaders, 1280000)
    handler = new WebSocketClientHandler(handShaker)

    Netty4Transporter.raw(clientPipeline(handler), addr, params)
  }

  private def clientPipeline(handler: WebSocketClientHandler)(pipeline: ChannelPipeline): Unit = {
    pipeline.addLast("client", new HttpClientCodec())
    pipeline.addLast("aggregator", new HttpObjectAggregator(65536))
    pipeline.addLast("ws", handler)
  }


  override protected def copy1(
                                stack: Stack[ServiceFactory[Request, Response]] = this.stack,
                                params: Stack.Params = this.params
                              ): Client = copy(stack, params)

  override protected def newDispatcher(transport: Transport[In, Out] {
    type Context <: TransportContext
  }): Service[Request, Response] = {

    new ClientDispatcher(transport)

  }

  def withTlsWithoutValidation: Client = withTransport.tlsWithoutValidation

  def withTls(hostname: String): Client = withTransport.tls(hostname)

  def withTls(cfg: SslClientConfiguration): Client =
    withTransport.tls.configured(Transport.ClientSsl(Some(cfg)))
}
