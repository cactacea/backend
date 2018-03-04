package io.github.cactacea.core.application.components.interfaces

import com.osinka.i18n.Lang
import io.github.cactacea.core.domain.enums.PushNotificationType

trait NotificationMessagesService {

  def get(pushNotificationType: PushNotificationType, lang: Lang, args : Any*): String

}
