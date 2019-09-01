package io.github.cactacea.backend.server.helpers

import java.nio.file.{Files, Paths}
import com.twitter.finagle.http.Response
import com.twitter.inject.server.FeatureTest
import io.github.cactacea.backend.core.domain.enums.FeedPrivacyType
import io.github.cactacea.backend.core.infrastructure.identifiers.FeedId
import io.github.cactacea.backend.server.models.responses.{CommentCreated, FeedCreated}
import io.github.cactacea.backend.server.{APIServerSpec, Authentication}

trait Helpers extends FeatureTest
  with CommentsHelper
  with FeedsHelper
  with CommonHelper
  with MediumsHelper
  with UsersHelper
  with FollowsHelper
  with SessionHelper
  with SessionsHelper {

  self: APIServerSpec =>

  def createUser(userName: String, password: String): Authentication = {
    val authentication = signUp(userName, password)
    val mediums = uploadMedium("profile", userName + ".jpg", authentication.accessToken)
    mediums.headOption.foreach(medium =>
      updateProfileImage(medium.id, authentication.accessToken)
    )
    authentication
  }

  def createFeed(message: String, resourceName: Option[String],
                 tags: Option[Array[String]], privacyType: FeedPrivacyType, contentWarning: Boolean, by: Authentication): FeedCreated = {
    val mediumIds = resourceName.map(resourceName => uploadMedium("feed", resourceName + ".jpg", by.accessToken).map(_.id))
    postFeed(message, mediumIds, tags, privacyType, contentWarning, by.accessToken)
  }

  def createComment(id: FeedId, message: String, by: Authentication): CommentCreated = {
    postComment(id, message, by.accessToken)
  }


  def createFollow(target: Authentication, by: Authentication): Response = {
    follow(target.user.id, by.accessToken)
  }

  def cleanUp(): Unit = {
    val localPath = storageService.localPath
    val path = Paths.get(localPath)
    if (Files.exists(path)) {
      val files = Files.list(path)
      files.forEach(f => Files.deleteIfExists(f))
      if (!Files.exists(path)) {
        Files.createDirectory(path)
      }
    } else {
      Files.createDirectory(path)
    }
  }

}
