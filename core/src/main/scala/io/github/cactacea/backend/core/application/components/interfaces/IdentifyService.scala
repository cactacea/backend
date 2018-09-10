package io.github.cactacea.backend.core.application.components.interfaces

import com.twitter.util.Future
import io.github.cactacea.backend.core.infrastructure.identifiers.AccountId

trait IdentifyService {

  def generate(): Future[Long]
  def generate(accountId: AccountId): Future[Long]

}
