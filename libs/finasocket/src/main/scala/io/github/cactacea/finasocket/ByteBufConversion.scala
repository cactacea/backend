package io.github.cactacea.finasocket

import com.twitter.io.Buf
import io.netty.buffer._
import com.twitter.io

// Forked from https://github.com/twitter/finagle/blob/96c8c38007098b490c22e1df562bc8cb916a3120/finagle-netty4/src/test/scala/com/twitter/finagle/netty4/ByteBufConversionTest.scala

object ByteBufConversion {

  // Assuming that bb.hasArray.
  private[this] def heapToBuf(bb: ByteBuf): Buf.ByteArray = {
    val begin = bb.arrayOffset + bb.readerIndex
    val end = begin + bb.readableBytes
    new Buf.ByteArray(bb.array, begin, end)
  }

  /** Make a copied `Buf.ByteArray` representation of the provided `ByteBuf`. */
  def copyByteBufToBuf(bb: ByteBuf): Buf.ByteArray = {
    val array = new Array[Byte](bb.readableBytes)
    bb.readBytes(array)
    new io.Buf.ByteArray(array, 0, array.length)
  }

  /**
   * A read-only and potentially non-copying `ByteBuf` representation of a [[Buf]].
   */
  def bufAsByteBuf(buf: Buf): ByteBuf = {
    val bb = buf match {
      case _ if buf.isEmpty =>
        Unpooled.EMPTY_BUFFER
      case Buf.ByteArray.Owned(bytes, begin, end) =>
        Unpooled.wrappedBuffer(bytes, begin, end - begin)
      case _ =>
        Unpooled.wrappedBuffer(Buf.ByteBuffer.Owned.extract(buf))
    }

    bb.asReadOnly
  }

  /**
   * Construct a [[Buf]] from a `ByteBuf`, releasing it.
   *
   * @note if the given is backed by a heap array, it will be coerced into `Buf.ByteArray`
   *       and then released. This basically means it's only safe to use this smart constructor
   *       with heap buffers which are unpooled, and non-heap buffers.
   */
  def byteBufAsBuf(buf: ByteBuf): Buf =
    if (buf.readableBytes == 0) Buf.Empty
    else
      try {
        if (buf.hasArray) heapToBuf(buf)
        else copyByteBufToBuf(buf)
      } finally buf.release()
}
