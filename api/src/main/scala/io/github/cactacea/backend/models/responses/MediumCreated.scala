package io.github.cactacea.backend.models.responses

import io.github.cactacea.core.infrastructure.identifiers.MediumId

case class MediumCreated(id: MediumId, uri: String)
