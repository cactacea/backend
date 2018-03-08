package io.github.cactacea.util.media

case class MediaMetadata(
                           width: Int,
                           height: Int,
                           data: Array[Byte],
                           contentType: Option[String]
                         )
