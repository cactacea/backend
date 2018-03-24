package io.github.cactacea.backend.util.media

import java.io.ByteArrayInputStream

import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.gif.GifHeaderDirectory
import com.drew.metadata.jpeg.JpegDirectory
import com.drew.metadata.mp4.media.Mp4VideoDirectory
import com.drew.metadata.png.PngDirectory

object MediaExtractor {

  def extract(contentType: Option[String], data: Array[Byte]): Option[MediaMetadata] = {
    contentType match {
      case Some("image/jpeg") =>
        val stream = new ByteArrayInputStream(data)
        val metadata = ImageMetadataReader.readMetadata(stream)
        val d = metadata.getFirstDirectoryOfType(classOf[JpegDirectory])
        val mediumMetadata = MediaMetadata(d.getImageWidth, d.getImageHeight, data, Some("image/jpeg"))
        Some(mediumMetadata)
      case Some("image/png") =>
        val stream = new ByteArrayInputStream(data)
        val metadata = ImageMetadataReader.readMetadata(stream)
        val d = metadata.getFirstDirectoryOfType(classOf[PngDirectory])
        val mediumMetadata = MediaMetadata(d.getInt(PngDirectory.TAG_IMAGE_WIDTH), d.getInt(PngDirectory.TAG_IMAGE_HEIGHT), data, Some("image/png"))
        Some(mediumMetadata)
      case Some("image/gif") =>
        val stream = new ByteArrayInputStream(data)
        val metadata = ImageMetadataReader.readMetadata(stream)
        val d = metadata.getFirstDirectoryOfType(classOf[GifHeaderDirectory])
        val mediumMetadata = MediaMetadata(d.getInt(PngDirectory.TAG_IMAGE_WIDTH), d.getInt(PngDirectory.TAG_IMAGE_HEIGHT), data, Some("image/gif"))
        Some(mediumMetadata)
      case Some("video/mp4") =>
        val stream = new ByteArrayInputStream(data)
        val metadata = ImageMetadataReader.readMetadata(stream)
        val d = metadata.getFirstDirectoryOfType(classOf[Mp4VideoDirectory])
        val mediumMetadata = MediaMetadata(d.getInt(Mp4VideoDirectory.TAG_WIDTH), d.getInt(Mp4VideoDirectory.TAG_HEIGHT), data, Some("video/mp4"))
        Some(mediumMetadata)

      case _ =>
        None
    }
  }

}