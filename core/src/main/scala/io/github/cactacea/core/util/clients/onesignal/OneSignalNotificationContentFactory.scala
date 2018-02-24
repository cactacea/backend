package io.github.cactacea.core.infrastructure.clients.onesignal

import com.osinka.i18n._
import io.github.cactacea.core.infrastructure.identifiers.MediumId

object OneSignalNotificationContentFactory {

  private val newMessageArrivalTemplate = "85e9ba63-c73a-48b2-a502-7cb8a4073a5a"

  private val EN = Lang("en")
  private val JP = Lang("jp")
  private val TW = Lang("tw")
  private val CH = Lang("ch")
  private val KR = Lang("kr")

  private val youHaveGotAMessage = "you_have_got_a_message"
  private val sendATextMessage = "send_a_text"
  private val sendAnImageMessage = "send_an_image"
  private val postedAFeed = "posted_a_feed"
  private val postedAComment = "posted_a_comment"
  private val invitedAGroup = "invited_a_group"

  def createComment() = {
    OneSignalNotificationContent(
      en = Some(Messages(postedAComment)(EN)),
      jp = Some(Messages(postedAComment)(JP)),
      kr = Some(Messages(postedAComment)(KR)),
      `zh-Hans` = Some(Messages(postedAComment)(CH)),
      `zh-Hant` = Some(Messages(postedAComment)(TW)),
      templateId = Some(newMessageArrivalTemplate)
    )
  }

  def createGroupInvite(displayName: String) = {
    OneSignalNotificationContent(
      en = Some(Messages(invitedAGroup, displayName)(EN)),
      jp = Some(Messages(invitedAGroup, displayName)(JP)),
      kr = Some(Messages(invitedAGroup, displayName)(KR)),
      `zh-Hans` = Some(Messages(invitedAGroup, displayName)(CH)),
      `zh-Hant` = Some(Messages(invitedAGroup, displayName)(TW)),
      templateId = Some(newMessageArrivalTemplate)
    )
  }

  def createFeed(displayName: String) = {
    OneSignalNotificationContent(
      en = Some(Messages(postedAFeed, displayName)(EN)),
      jp = Some(Messages(postedAFeed, displayName)(JP)),
      kr = Some(Messages(postedAFeed, displayName)(KR)),
      `zh-Hans` = Some(Messages(postedAFeed, displayName)(CH)),
      `zh-Hant` = Some(Messages(postedAFeed, displayName)(TW)),
      templateId = Some(newMessageArrivalTemplate)
    )
  }

  def createMessage(displayName: String, message: Option[String], mediumId: Option[MediumId], showMessage: Boolean) = {
    showMessage match {
      case true =>
        if (message.isDefined) {
          createTextMessage(displayName, message.getOrElse(""))
        } else if (mediumId.isDefined) {
          createImageMessage(displayName)
        } else {
          createYouHaveGotAMessage()
        }
      case false =>
        createYouHaveGotAMessage()
    }
  }

  private def createTextMessage(displayName: String, message: String): OneSignalNotificationContent = {
    OneSignalNotificationContent(
      en = Some(Messages(sendATextMessage, displayName, message)(EN)),
      jp = None,
      kr = None,
      `zh-Hans` = None,
      `zh-Hant` = None,
      templateId = Some(newMessageArrivalTemplate)
    )
  }

  private def createImageMessage(displayName: String): OneSignalNotificationContent = {
    OneSignalNotificationContent(
      en = Some(Messages(sendAnImageMessage, displayName)(EN)),
      jp = Some(Messages(sendAnImageMessage, displayName)(JP)),
      kr = Some(Messages(sendAnImageMessage, displayName)(KR)),
      `zh-Hans` = Some(Messages(sendAnImageMessage, displayName)(CH)),
      `zh-Hant` = Some(Messages(sendAnImageMessage, displayName)(TW)),
      templateId = Some(newMessageArrivalTemplate)
    )
  }

  private def createYouHaveGotAMessage(): OneSignalNotificationContent = {
    OneSignalNotificationContent(
      en = Some(Messages(youHaveGotAMessage)(EN)),
      jp = Some(Messages(youHaveGotAMessage)(JP)),
      kr = Some(Messages(youHaveGotAMessage)(KR)),
      `zh-Hans` = Some(Messages(youHaveGotAMessage)(CH)),
      `zh-Hant` = Some(Messages(youHaveGotAMessage)(TW)),
      templateId = Some(newMessageArrivalTemplate)
    )
  }


}
