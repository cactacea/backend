package io.github.cactacea.core.util

case class MediaMetadata(
                           width: Int,
                           height: Int,
                           data: Array[Byte],
                           contentType: Option[String]
                         )
