package io.github.cactacea.backend.helpers

import com.twitter.finagle.http.Response
import com.twitter.inject.server.FeatureTest
import io.github.cactacea.backend.APIServerSpec
import io.github.cactacea.backend.core.domain.enums.FeedPrivacyType
import io.github.cactacea.backend.core.infrastructure.identifiers.FeedId
import io.github.cactacea.backend.models.responses.{Authentication, CommentCreated, FeedCreated}

trait Helpers extends FeatureTest
  with CommentsHelper
  with FeedsHelper
  with CommonHelper
  with MediumsHelper
  with AccountsHelper
  with FollowsHelper
  with SessionHelper
  with SessionsHelper {

  self: APIServerSpec =>

  def createAccount(accountName: String, password: String): Authentication = {
    val authentication = signUp(accountName, password)
    val mediums = uploadMedium("profile", accountName + ".jpg", authentication.accessToken)
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
    follow(target.account.id, by.accessToken)
  }

  def cleanUp(): Unit = {
    val localPath = storageService.localPath
    import java.nio.file.{Files, Paths}
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
