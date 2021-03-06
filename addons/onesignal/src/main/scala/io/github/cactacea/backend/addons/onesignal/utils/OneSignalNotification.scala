package io.github.cactacea.backend.addons.onesignal.utils

// https://documentation.onesignal.com/reference

case class OneSignalNotification(

                                  // App
                                  appId: String,

                                  // Send to Specific Devices
                                  includePlayerIds: Option[Seq[String]],
                                  includeIosTokens: Option[Seq[String]],
                                  includeWpUrls: Option[Seq[String]],
                                  includeWpWnsUris: Option[Seq[String]],
                                  includeAmazonRegIds: Option[Seq[String]],
                                  includeChromeRegIds: Option[Seq[String]],
                                  includeChromeWebRegIds: Option[Seq[String]],
                                  includeAndroidRegIds: Option[Seq[String]],

                                  // Content & Language
                                  contents: OneSignalNotificationContent,
                                  headings: Option[OneSignalNotificationContent],
                                  subtitle: Option[String],
                                  templateId: Option[String],
                                  contentAvailable: Option[Boolean],
                                  mutableContent: Option[Boolean],
                                  includedSegments: Option[Seq[String]],
                                  excludedSegments: Option[Seq[String]],

                                  // Attachments
                                  data: Option[Map[String, String]],
                                  url: Option[String],
                                  iosAttachments: Option[Map[String, String]],
                                  bigPicture: Option[String],
                                  admBigPicture: Option[String],
                                  chromeBigPicture: Option[String],

                                  // Action Buttons
                                  buttons: Option[Seq[Map[String, String]]],
                                  webButtons: Option[Seq[Map[String, String]]],
                                  iosCategory: Option[String],

                                  // Appearance
                                  androidChannelId: Option[String],
                                  existingAndroidChannelId: Option[String],
                                  androidBackgroundLayout: Option[Seq[Map[String, String]]],
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

  // scalastyle:ignore
  def apply(appId: String, includePlayerIds: Seq[String], en: String, ja: String, url: String): OneSignalNotification = {
    val contents = OneSignalNotificationContent(en = Option(en), ja = Option(ja))
    OneSignalNotification(
      appId,
      Option(includePlayerIds),
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
      Option(url),
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
