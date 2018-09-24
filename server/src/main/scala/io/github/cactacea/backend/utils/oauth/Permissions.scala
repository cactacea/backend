package io.github.cactacea.backend.utils.oauth

final object Permissions {

  final object basic extends Permission(1, "basic"                           , "to read a user’s profile info and media")
  final object feeds extends Permission(2, "feeds"                           , "to post and delete feeds on a user’s behalf")
  final object feedLikes extends Permission(4, "feed_likes"                  , "to like and unlike feed on a user’s behalf")
  final object comments extends Permission(8, "comments"                     , "to post and delete comments on a user’s behalf")
  final object commentLikes extends Permission(16, "comment_likes"           , "to like and unlike comment on a user’s behalf")
  final object groups extends Permission(32, "groups"                        , "to create and delete groups on a user's behalf")
  final object groupInvitations extends Permission(64, "group_invitations"   , "to create, delete, accept and reject invitations on a user's behalf")
  final object messages extends Permission(128, "messages"                   , "to post and delete messages on a user's behalf")
  final object followerList extends Permission(256, "follower_list"          , "to read the list of followers and followed-by users")
  final object relationships extends Permission(512, "relationships"         , "to follows and unfollow accounts on a user’s behalf")
  final object friendRequests extends Permission(1024, "friend_requests"     , "to create and delete friend request on a user’s behalf")
  final object media extends Permission(2048, "media"                        , "to create and delete media on a user’s behalf")
  final object reports extends Permission(4096, "reports"                    , "to report account, feed, group and comment on a user’s behalf")

  val values = List(
    basic,
    feeds,
    feedLikes,
    comments,
    commentLikes,
    groups,
    groupInvitations,
    messages,
    followerList,
    relationships,
    friendRequests,
    media,
    reports
  )

  val all = Permissions.values.map(_.key).fold(0) {_ + _}

  private def forName(name: String): Option[Permission] = {
    values.filter(_.value == name).headOption
  }

  def forScope(scope: Option[String]): List[Permission] = {
    scope match {
      case Some(s) =>
        s.split(',').map(s => Permissions.forName(s)).flatten.toList
      case None =>
        Permissions.values
    }
  }

}
