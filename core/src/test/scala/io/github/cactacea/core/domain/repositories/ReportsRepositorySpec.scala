package io.github.cactacea.core.domain.repositories

import com.twitter.util.Await
import io.github.cactacea.core.domain.enums._
import io.github.cactacea.core.infrastructure.identifiers._
import io.github.cactacea.core.specs.RepositorySpec
import io.github.cactacea.core.util.responses.CactaceaError.{AccountNotFound, CommentNotFound, FeedNotFound, GroupNotFound}
import io.github.cactacea.core.util.exceptions.CactaceaException

class ReportsRepositorySpec extends RepositorySpec {

  val reportsRepository = injector.instance[ReportsRepository]
  val feedsRepository = injector.instance[FeedsRepository]
  val mediumRepository = injector.instance[MediumsRepository]
  val commentsRepository = injector.instance[CommentsRepository]
  var groupsRepository = injector.instance[GroupsRepository]

  test("report a user") {

    val sessionUser = signUp("session user name", "session user password", "session udid").account
    val user = signUp("user name", "user password", "user udid").account
    Await.result(reportsRepository.createAccountReport(user.id, ReportType.inappropriate, sessionUser.id.toSessionId))

    assert(intercept[CactaceaException] {
      Await.result(reportsRepository.createAccountReport(AccountId(0L), ReportType.inappropriate, sessionUser.id.toSessionId))
    }.error == AccountNotFound)

  }

  test("report a feed") {

    val session = signUp("session name", "session password", "udid").account
    val user = signUp("user name", "user password", "user udid").account

    val tags = Some(List("tag1", "tag2", "tag3"))
    val medium = Await.result(mediumRepository.create("key", "http://cactacea.io/test.jpeg", Some("http://cactacea.io/test.jpeg"), MediumType.image, 120, 120, 58L, session.id.toSessionId))
    val mediums = Some(List(medium.id))
    val feedId = Await.result(feedsRepository.create("feed message", mediums, tags, FeedPrivacyType.everyone, false, session.id.toSessionId))
    Await.result(reportsRepository.createFeedReport(feedId, ReportType.inappropriate, user.id.toSessionId))

    assert(intercept[CactaceaException] {
      Await.result(reportsRepository.createFeedReport(FeedId(0L), ReportType.inappropriate, user.id.toSessionId))
    }.error == FeedNotFound)

  }

  test("report a comment") {

    val session = signUp("session name", "session password", "udid").account
    val user = signUp("user name", "user password", "user udid").account
    val feedId = Await.result(feedsRepository.create("feed message", None, None, FeedPrivacyType.everyone, false, session.id.toSessionId))
    val commentId = Await.result(commentsRepository.create(feedId, "comment", session.id.toSessionId))
    reportsRepository.createCommentReport(commentId, ReportType.inappropriate, user.id.toSessionId)

    assert(intercept[CactaceaException] {
      Await.result(reportsRepository.createCommentReport(CommentId(0L), ReportType.inappropriate, user.id.toSessionId))
    }.error == CommentNotFound)

  }

  test("report a group") {

    val sessionUser = signUp("session user name", "session user password", "session user udid").account
    val user = signUp("user name", "user password", "user udid").account
    val groupId = Await.result(groupsRepository.create(Some("group name"), false, GroupPrivacyType.everyone, GroupAuthorityType.member, sessionUser.id.toSessionId))
    Await.result(reportsRepository.createGroupReport(groupId, ReportType.inappropriate, user.id.toSessionId))

    assert(intercept[CactaceaException] {
      Await.result(reportsRepository.createGroupReport(GroupId(0L), ReportType.inappropriate, user.id.toSessionId))
    }.error == GroupNotFound)

  }

}
