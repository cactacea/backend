package io.github.cactacea.backend.core.util.media

import java.io.ByteArrayInputStream

import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.gif.GifHeaderDirectory
import com.drew.metadata.jpeg.JpegDirectory
import com.drew.metadata.mp4.media.Mp4VideoDirectory
import com.drew.metadata.png.PngDirectory
import io.github.cactacea.backend.core.domain.enums.MediumType

object MediaExtractor {

  def extract(contentType: Option[String], data: Array[Byte]): Option[MediaMetadata] = {
    contentType match {
      case Some("image/jpeg") =>
        val stream = new ByteArrayInputStream(data)
        val metadata = ImageMetadataReader.readMetadata(stream)
        val d = metadata.getFirstDirectoryOfType(classOf[JpegDirectory])
        val mediumMetadata = MediaMetadata(
          MediumType.image,
          d.getImageWidth,
          d.getImageHeight,
          data,
          contentType)

        stream.close()
        Some(mediumMetadata)

      case Some("image/png") =>
        val stream = new ByteArrayInputStream(data)
        val metadata = ImageMetadataReader.readMetadata(stream)
        val d = metadata.getFirstDirectoryOfType(classOf[PngDirectory])
        val mediumMetadata = MediaMetadata(
          MediumType.image,
          d.getInt(PngDirectory.TAG_IMAGE_WIDTH),
          d.getInt(PngDirectory.TAG_IMAGE_HEIGHT),
          data,
          contentType)

        stream.close()
        Some(mediumMetadata)

      case Some("image/gif") =>
        val stream = new ByteArrayInputStream(data)
        val metadata = ImageMetadataReader.readMetadata(stream)
        val d = metadata.getFirstDirectoryOfType(classOf[GifHeaderDirectory])
        val mediumMetadata = MediaMetadata(
          MediumType.image,
          d.getInt(GifHeaderDirectory.TAG_IMAGE_WIDTH),
          d.getInt(GifHeaderDirectory.TAG_IMAGE_HEIGHT),
          data,
          contentType)

        stream.close()
        Some(mediumMetadata)

      case Some("video/mp4") =>
        val stream = new ByteArrayInputStream(data)
        val metadata = ImageMetadataReader.readMetadata(stream)
        val d = metadata.getFirstDirectoryOfType(classOf[Mp4VideoDirectory])
        val mediumMetadata = MediaMetadata(
          MediumType.movie,
          d.getInt(Mp4VideoDirectory.TAG_WIDTH),
          d.getInt(Mp4VideoDirectory.TAG_HEIGHT),
          data,
          contentType)

        stream.close()
        Some(mediumMetadata)

      case _ =>
        None
    }
  }

}
