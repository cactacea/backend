package io.github.cactacea.backend.oauth

case class Scope(key: Int, value: String, description: String)

object Scope {

  final object basic extends Scope(1 << 0, "basic"                           , "to read a user’s profile info and media") // scalastyle:ignore
  final object tweets extends Scope(1 << 1, "tweets"                           , "to post and delete tweets on a user’s behalf") // scalastyle:ignore
  final object tweetLikes extends Scope(1 << 2, "tweet_likes"                  , "to like and unlike tweet on a user’s behalf") // scalastyle:ignore
  final object comments extends Scope(1 << 3, "comments"                     , "to post and delete comments on a user’s behalf") // scalastyle:ignore
  final object commentLikes extends Scope(1 << 4, "comment_likes"           , "to like and unlike comment on a user’s behalf") // scalastyle:ignore
  final object channels extends Scope(1 << 5, "channels"                        , "to create and delete channels on a user's behalf") // scalastyle:ignore
  final object invitations extends Scope(1 << 6, "invitations"   , "to create, delete, accept and reject invitations on a user's behalf") // scalastyle:ignore
  final object messages extends Scope(1 << 7, "messages"                   , "to post and delete messages on a user's behalf") // scalastyle:ignore
  final object followerList extends Scope(1 << 8, "follower_list"          , "to read the list of followers and followed-by users") // scalastyle:ignore
  final object relationships extends Scope(1 << 9, "relationships"         , "to follows and unfollow accounts on a user’s behalf") // scalastyle:ignore
  final object requests extends Scope(1 << 10, "requests"     , "to create and delete friend request on a user’s behalf") // scalastyle:ignore
  final object media extends Scope(1 << 11, "media"                        , "to create and delete media on a user’s behalf") // scalastyle:ignore
  final object reports extends Scope(1 << 12, "reports"                    , "to report account, tweet, channel and comment on a user’s behalf") // scalastyle:ignore

  val values = List(
    basic,
    tweets,
    tweetLikes,
    comments,
    commentLikes,
    channels,
    invitations,
    messages,
    followerList,
    relationships,
    requests,
    media,
    reports
  )

  val all = Scope.values.map(_.key).fold(0) {_ + _}

}
