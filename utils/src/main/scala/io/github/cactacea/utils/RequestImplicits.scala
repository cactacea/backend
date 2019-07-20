package io.github.cactacea.utils

import java.util.Locale

import com.twitter.finagle.http.Request

object RequestImplicits {

  implicit class HttpRequest(request: Request) {

    def locales(): Seq[Locale] = {
      import scala.collection.JavaConverters._
      request.headerMap.get("Accept-Language").fold(Seq(Locale.getDefault())) { lang =>
        Locale.LanguageRange.parse(lang).asScala.map(f => Locale.forLanguageTag(f.getRange))
      }
    }

    def currentLocale(): Locale = {
      locales().headOption.getOrElse(Locale.US)
    }

  }

}
