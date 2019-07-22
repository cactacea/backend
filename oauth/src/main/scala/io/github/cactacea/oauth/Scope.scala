package io.github.cactacea.oauth

case class Scope(key: Int, value: String, description: String)

object Scope {

  final object basic extends Scope(1 << 0, "basic"                           , "to read a user’s profile info and media") // scalastyle:ignore
  final object feeds extends Scope(1 << 1, "feeds"                           , "to post and delete feeds on a user’s behalf") // scalastyle:ignore
  final object feedLikes extends Scope(1 << 2, "feed_likes"                  , "to like and unlike feed on a user’s behalf") // scalastyle:ignore
  final object comments extends Scope(1 << 3, "comments"                     , "to post and delete comments on a user’s behalf") // scalastyle:ignore
  final object commentLikes extends Scope(1 << 4, "comment_likes"           , "to like and unlike comment on a user’s behalf") // scalastyle:ignore
  final object groups extends Scope(1 << 5, "groups"                        , "to create and delete groups on a user's behalf") // scalastyle:ignore
  final object invitations extends Scope(1 << 6, "group_invitations"   , "to create, delete, accept and reject invitations on a user's behalf") // scalastyle:ignore
  final object messages extends Scope(1 << 7, "messages"                   , "to post and delete messages on a user's behalf") // scalastyle:ignore
  final object followerList extends Scope(1 << 8, "follower_list"          , "to read the list of followers and followed-by users") // scalastyle:ignore
  final object relationships extends Scope(1 << 9, "relationships"         , "to follows and unfollow accounts on a user’s behalf") // scalastyle:ignore
  final object friendRequests extends Scope(1 << 10, "friend_requests"     , "to create and delete friend request on a user’s behalf") // scalastyle:ignore
  final object media extends Scope(1 << 11, "media"                        , "to create and delete media on a user’s behalf") // scalastyle:ignore
  final object reports extends Scope(1 << 12, "reports"                    , "to report account, feed, group and comment on a user’s behalf") // scalastyle:ignore

  val values = List(
    basic,
    feeds,
    feedLikes,
    comments,
    commentLikes,
    groups,
    invitations,
    messages,
    followerList,
    relationships,
    friendRequests,
    media,
    reports
  )

  val all = Scope.values.map(_.key).fold(0) {_ + _}

//  private def forName(name: String): Option[Scope] = {
//    values.filter(_.value == name).headOption
//  }

//  def forScope(scope: Option[String]): List[Scope] = {
//    scope match {
//      case Some(s) =>
//        s.split(' ').map(s => Scope.forName(s)).flatten.toList
//      case None =>
//        Scope.values
//    }
//  }

}
