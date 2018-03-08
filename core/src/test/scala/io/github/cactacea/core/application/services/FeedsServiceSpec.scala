package io.github.cactacea.core.application.services

import com.twitter.util.Await
import io.github.cactacea.core.domain.enums.FeedPrivacyType
import io.github.cactacea.core.helpers.ServiceSpec

class FeedsServiceSpec extends ServiceSpec {

  val feedsService = injector.instance[FeedsService]
  val timeService = injector.instance[TimeService]

  test("create a message feed") {

    val session = signUp("account name", "account password", "ffc1ded6f4570d557ad65f986684fc10c7f8d51f").account
    val id = Await.result(feedsService.create("a message feed", None, Some(List("tag1, tag2, tag3, tag4")), FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val feed = Await.result(feedsService.find(id, session.id.toSessionId))

    assert(feed.id == id)
    assert(feed.message == "a message feed")
    assert(feed.tags == Some(List("tag1, tag2, tag3, tag4")))
    assert(feed.account.forall(_.displayName == "account name"))
    assert(feed.contentWarning == false)
    assert(feed.commentCount == 0)

  }

  // 全体公開のフィードを投稿する
  //
  // フォローしているアカウントへ
  //　　通知がくる
  //　　投稿者のフィード検索で表示される
  //　　フォロワーフィード検索で表示される
  //　　タイムラインにフィードが表示されない
  //　　
  // フォローしているがミュートしているアカウントへ
  //　　通知が来ない
  //　　投稿者のフィード検索で表示される
  //　　フォロワーフィード検索で表示される
  //　　タイムラインにフィードが表示されない
  //　　
  // フレンドになっているアカウントへ
  //　　通知がくる
  //　　投稿者のフィード検索で表示される
  //　　フレンドのフィード検索で表示される
  //　　タイムラインにフィードが表示される
  //　　
  // フレンドしているがミュートしているアカウントへ
  //　　通知がこない
  //　　投稿者のフィード検索で表示される
  //　　フレンドのフィード検索で表示される
  //　　タイムラインにフィードが表示されない




}
