package io.github.cactacea.backend.core.application.services

import io.github.cactacea.backend.core.helpers.specs.ServiceSpec

class TweetsServiceSpec extends ServiceSpec {

  feature("create a message tweet") {

//    val session = signUp("user name", "user password", "ffc1ded6f4570d557ad65f986684fc10c7f8d51f")
//    val id = execute(tweetsService.create("a message tweet", None, Some(Seq("tag1, tag2, tag3, tag4")), TweetPrivacyType.everyone, false, None, session.id.toSessionId))
//    val tweet = execute(tweetsService.find(id, session.id.toSessionId))
//
//    assert(tweet.id == id)
//    assert(tweet.message == "a message tweet")
//    assert(tweet.tags == Some(Seq("tag1, tag2, tag3, tag4")))
//    assert(tweet.user.map(_.userName) == Some("user name"))
//    assert(tweet.contentWarning == false)
//    assert(tweet.commentCount == 0)

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
