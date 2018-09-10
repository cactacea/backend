package io.github.cactacea.backend.utils.media

case class MediaMetadata(
                           width: Int,
                           height: Int,
                           data: Array[Byte],
                           contentType: Option[String]
                         )
