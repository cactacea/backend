package io.github.cactacea.backend.core.application.services

import io.github.cactacea.backend.core.domain.enums.FeedPrivacyType
import io.github.cactacea.backend.core.helpers.ServiceSpec

class FeedsServiceSpec extends ServiceSpec {

  test("create a message feed") {

    val session = signUp("account name", "account password", "ffc1ded6f4570d557ad65f986684fc10c7f8d51f")
    val id = execute(feedsService.create("a message feed", None, Some(List("tag1, tag2, tag3, tag4")), FeedPrivacyType.everyone, false, None, session.id.toSessionId))
    val feed = execute(feedsService.find(id, session.id.toSessionId))

    assert(feed.id == id)
    assert(feed.message == "a message feed")
    assert(feed.tags == Some(List("tag1, tag2, tag3, tag4")))
    assert(feed.account.map(_.accountName) == Some("account name"))
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
