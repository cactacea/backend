package io.github.cactacea.backend.components.pushnotifications.onesignal

case class OneSignalNotificationContent (
                                          en: Option[String],
                                          ar: Option[String],
                                          ca: Option[String],
                                          `zh-Hans`: Option[String],
                                          `zh-Hant`: Option[String],
                                          hr: Option[String],
                                          cs: Option[String],
                                          da: Option[String],
                                          nl: Option[String],
                                          et: Option[String],
                                          fi: Option[String],
                                          fr: Option[String],
                                          ka: Option[String],
                                          bg: Option[String],
                                          de: Option[String],
                                          el: Option[String],
                                          hi: Option[String],
                                          he: Option[String],
                                          hu: Option[String],
                                          id: Option[String],
                                          it: Option[String],
                                          ja: Option[String],
                                          ko: Option[String],
                                          lv: Option[String],
                                          lt: Option[String],
                                          ms: Option[String],
                                          nb: Option[String],
                                          fa: Option[String],
                                          pl: Option[String],
                                          pt: Option[String],
                                          ro: Option[String],
                                          ru: Option[String],
                                          sr: Option[String],
                                          sk: Option[String],
                                          es: Option[String],
                                          sv: Option[String],
                                          th: Option[String],
                                          tr: Option[String],
                                          uk: Option[String],
                                          vi: Option[String],
                                          templateId: Option[String]
                                        )

object OneSignalNotificationContent {

  def apply(en: String): OneSignalNotificationContent = {
    OneSignalNotificationContent(
      en = Some(en),
      ar = None,
      ca = None,
      `zh-Hans` = None,
      `zh-Hant` = None,
      hr = None,
      cs = None,
      da = None,
      nl = None,
      et = None,
      fi = None,
      fr = None,
      ka = None,
      bg = None,
      de = None,
      el = None,
      hi = None,
      he = None,
      hu = None,
      id = None,
      it = None,
      ja = None,
      ko = None,
      lv = None,
      lt = None,
      ms = None,
      nb = None,
      fa = None,
      pl = None,
      pt = None,
      ro = None,
      ru = None,
      sr = None,
      sk = None,
      es = None,
      sv = None,
      th = None,
      tr = None,
      uk = None,
      vi = None,
      templateId = None
    )
  }

}