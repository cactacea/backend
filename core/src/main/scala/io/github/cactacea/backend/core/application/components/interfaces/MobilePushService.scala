package io.github.cactacea.backend.core.application.components.interfaces

import com.twitter.util.Future
import io.github.cactacea.backend.core.infrastructure.identifiers._

trait MobilePushService {

  def sendTweet(tweetId: TweetId): Future[Unit]
  def sendComment(commentId: CommentId): Future[Unit]
  def sendMessage(id: MessageId): Future[Unit]
  def sendFriendRequest(id: FriendRequestId): Future[Unit]
  def sendInvitation(id: InvitationId): Future[Unit]

}
