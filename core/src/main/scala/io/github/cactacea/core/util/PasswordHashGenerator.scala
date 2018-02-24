package io.github.cactacea.core.util

object PasswordHashGenerator {

  import com.roundeights.hasher.Implicits._

  def create(password: String) = {
    "v1" + password.pbkdf2("cactacea", 1000, 128)
  }

}