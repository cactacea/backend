package io.github.cactacea.backend.addons.onesignal.utils

case class OneSignalNotificationContent (
                                          en:  Option[String] = None,
                                          ar:  Option[String] = None,
                                          ca:  Option[String] = None,
                                          `zh-Hans`:  Option[String] = None,
                                          `zh-Hant`:  Option[String] = None,
                                          hr:  Option[String] = None,
                                          cs:  Option[String] = None,
                                          da:  Option[String] = None,
                                          nl:  Option[String] = None,
                                          et:  Option[String] = None,
                                          fi:  Option[String] = None,
                                          fr:  Option[String] = None,
                                          ka:  Option[String] = None,
                                          bg:  Option[String] = None,
                                          de:  Option[String] = None,
                                          el:  Option[String] = None,
                                          hi:  Option[String] = None,
                                          he:  Option[String] = None,
                                          hu:  Option[String] = None,
                                          id:  Option[String] = None,
                                          it:  Option[String] = None,
                                          ja:  Option[String] = None,
                                          ko:  Option[String] = None,
                                          lv:  Option[String] = None,
                                          lt:  Option[String] = None,
                                          ms:  Option[String] = None,
                                          nb:  Option[String] = None,
                                          fa:  Option[String] = None,
                                          pl:  Option[String] = None,
                                          pt:  Option[String] = None,
                                          ro:  Option[String] = None,
                                          ru:  Option[String] = None,
                                          sr:  Option[String] = None,
                                          sk:  Option[String] = None,
                                          es:  Option[String] = None,
                                          sv:  Option[String] = None,
                                          th:  Option[String] = None,
                                          tr:  Option[String] = None,
                                          uk:  Option[String] = None,
                                          vi:  Option[String] = None,
                                          templateId: Option[String] = None,
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