package io.github.cactacea.core.application.responses

import io.github.cactacea.core.infrastructure.identifiers.MediumId

case class MediumCreated(id: MediumId, uri: String)
