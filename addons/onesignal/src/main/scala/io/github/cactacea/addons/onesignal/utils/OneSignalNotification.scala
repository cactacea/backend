package io.github.cactacea.addons.onesignal.utils

// https://documentation.onesignal.com/reference

case class OneSignalNotification(

                                  // App
                                  appId: String,

                                  // Send to Specific Devices
                                  includePlayerIds: Option[List[String]],
                                  includeIosTokens: Option[List[String]],
                                  includeWpUrls: Option[List[String]],
                                  includeWpWnsUris: Option[List[String]],
                                  includeAmazonRegIds: Option[List[String]],
                                  includeChromeRegIds: Option[List[String]],
                                  includeChromeWebRegIds: Option[List[String]],
                                  includeAndroidRegIds: Option[List[String]],

                                  // Content & Language
                                  contents: OneSignalNotificationContent,
                                  headings: Option[OneSignalNotificationContent],
                                  subtitle: Option[String],
                                  templateId: Option[String],
                                  contentAvailable: Option[Boolean],
                                  mutableContent: Option[Boolean],
                                  includedSegments: Option[List[String]],
                                  excludedSegments: Option[List[String]],

                                  // Attachments
                                  data: Option[Map[String, String]],
                                  url: Option[String],
                                  iosAttachments: Option[Map[String, String]],
                                  bigPicture: Option[String],
                                  admBigPicture: Option[String],
                                  chromeBigPicture: Option[String],

                                  // Action Buttons
                                  buttons: Option[List[Map[String, String]]],
                                  webButtons: Option[List[Map[String, String]]],
                                  iosCategory: Option[String],

                                  // Appearance
                                  androidChannelId: Option[String],
                                  existingAndroidChannelId: Option[String],
                                  androidBackgroundLayout: Option[List[Map[String, String]]],
                                  smallIcon: Option[String],
                                  largeIcon: Option[String],
                                  admSmallIcon: Option[String],
                                  admLargeIcon: Option[String],
                                  chromeWebIcon: Option[String],
                                  chromeWebImage: Option[String],
                                  firefoxIcon: Option[String],
                                  chromeIcon: Option[String],
                                  iosSound: Option[String],
                                  androidSound: Option[String],
                                  admSound: Option[String],
                                  wpSound: Option[String],
                                  wpWnsSound: Option[String],
                                  androidLedColor: Option[String],
                                  androidAccentColor: Option[String],
                                  androidVisibility: Option[Long],
                                  iosBadgeType: Option[Long],
                                  iosBadgeCount: Option[Long],
                                  collapseId: Option[String],

                                  // Delivery
                                  sendAfter: Option[String],
                                  delayedOption: Option[String],
                                  deliveryTimeOfDay: Option[String],
                                  ttl: Option[Long],
                                  priority: Option[Long],

                                  // Grouping & Collapsing
                                  androidGroup: Option[String],
                                  androidGroupMessage: Option[OneSignalNotificationContent],
                                  admGroup: Option[String],
                                  admGroupMessage: Option[OneSignalNotificationContent],

                                  // Platform to Deliver To
                                  isIos: Option[Boolean],
                                  isAndroid: Option[Boolean],
                                  isAnyWeb: Option[Boolean],
                                  isChromeWeb: Option[Boolean],
                                  isFirefox: Option[Boolean],
                                  isSafari: Option[Boolean],
                                  isWP: Option[Boolean],
                                  isWPWNS: Option[Boolean],
                                  isAdm: Option[Boolean],
                                  isChrome: Option[Boolean] = None

                                )

object OneSignalNotification {

  def apply(appId: String, includePlayerIds: List[String], en: String, ja: String, url: String): OneSignalNotification = {
    val contents = OneSignalNotificationContent(en = Some(en), ja = Some(ja))
    OneSignalNotification(
      appId,
      Some(includePlayerIds),
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      contents,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      Some(url),
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None
    )
  }

}