package io.github.cactacea.core.infrastructure.clients.onesignal

case class OneSignalNotificationContent (
                                          en: Option[String],
                                          jp: Option[String],
                                          kr: Option[String],
                                          `zh-Hans`: Option[String],
                                          `zh-Hant`: Option[String],
                                          templateId: Option[String]
                                        )

