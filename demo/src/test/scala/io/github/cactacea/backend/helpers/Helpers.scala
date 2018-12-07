package io.github.cactacea.backend.helpers

import com.twitter.finagle.http.Response
import com.twitter.inject.server.FeatureTest
import io.github.cactacea.backend.DemoServerSpec
import io.github.cactacea.backend.models.responses.Authentication

trait Helpers extends FeatureTest
  with CommonHelper
  with MediumsHelper
  with AccountsHelper
  with FollowsHelper
  with SessionHelper
  with SessionsHelper {

  self: DemoServerSpec =>

  def createAccount(accountName: String, password: String): Authentication = {
    val authentication = signUp(accountName, password)
    val mediums = uploadMedium(accountName + ".jpg", authentication.accessToken)
    mediums.headOption.foreach(medium =>
      updateProfileImage(medium.id, authentication.accessToken)
    )
    authentication
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
