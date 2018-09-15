package io.github.cactacea.backend.core.domain.repositories

import com.twitter.util.Await
import io.github.cactacea.backend.core.domain.enums._
import io.github.cactacea.backend.core.helpers.RepositorySpec
import io.github.cactacea.backend.core.infrastructure.identifiers._
import io.github.cactacea.backend.core.util.responses.CactaceaErrors.{AccountNotFound, CommentNotFound, FeedNotFound, GroupNotFound}
import io.github.cactacea.backend.core.util.exceptions.CactaceaException

class ReportsRepositorySpec extends RepositorySpec {

  val reportsRepository = injector.instance[ReportsRepository]
  val feedsRepository = injector.instance[FeedsRepository]
  val mediumRepository = injector.instance[MediumsRepository]
  val commentsRepository = injector.instance[CommentsRepository]
  var groupsRepository = injector.instance[GroupsRepository]

  test("report a user") {

    val sessionUser = signUp("ReportsRepositorySpec1", "session user password", "session udid")
    val user = signUp("ReportsRepositorySpec2", "user password", "user udid")
    Await.result(reportsRepository.createAccountReport(user.id, ReportType.inappropriate, sessionUser.id.toSessionId))

    assert(intercept[CactaceaException] {
      Await.result(reportsRepository.createAccountReport(AccountId(0L), ReportType.inappropriate, sessionUser.id.toSessionId))
    }.error == AccountNotFound)

  }

  test("report a feed") {

    val session = signUp("ReportsRepositorySpec3", "session password", "udid")
    val user = signUp("ReportsRepositorySpec4", "user password", "user udid")

    val tags = Some(List("tag1", "tag2", "tag3"))
    val (id, _) = Await.result(mediumRepository.create("key", "http://cactacea.io/test.jpeg", Some("http://cactacea.io/test.jpeg"), MediumType.image, 120, 120, 58L, session.id.toSessionId))
    val mediums = Some(List(id))
    val feedId = Await.result(feedsRepository.create("feed message", mediums, tags, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    Await.result(reportsRepository.createFeedReport(feedId, ReportType.inappropriate, user.id.toSessionId))

    assert(intercept[CactaceaException] {
      Await.result(reportsRepository.createFeedReport(FeedId(0L), ReportType.inappropriate, user.id.toSessionId))
    }.error == FeedNotFound)

  }

  test("report a comment") {

    val session = signUp("ReportsRepositorySpec5", "session password", "udid")
    val user = signUp("ReportsRepositorySpec6", "user password", "user udid")
    val feedId = Await.result(feedsRepository.create("feed message", None, None, FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val commentId = Await.result(commentsRepository.create(feedId, "comment", session.id.toSessionId))
    reportsRepository.createCommentReport(commentId, ReportType.inappropriate, user.id.toSessionId)

    assert(intercept[CactaceaException] {
      Await.result(reportsRepository.createCommentReport(CommentId(0L), ReportType.inappropriate, user.id.toSessionId))
    }.error == CommentNotFound)

  }

  test("report a group") {

    val sessionUser = signUp("ReportsRepositorySpec7", "session user password", "session user udid")
    val user = signUp("ReportsRepositorySpec8", "user password", "user udid")
    val groupId = Await.result(groupsRepository.create(Some("group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    Await.result(reportsRepository.createGroupReport(groupId, ReportType.inappropriate, user.id.toSessionId))

    assert(intercept[CactaceaException] {
      Await.result(reportsRepository.createGroupReport(GroupId(0L), ReportType.inappropriate, user.id.toSessionId))
    }.error == GroupNotFound)

  }

}
