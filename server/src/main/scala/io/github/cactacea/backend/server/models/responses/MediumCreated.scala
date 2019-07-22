package io.github.cactacea.backend.server.models.responses

import io.github.cactacea.backend.core.infrastructure.identifiers.MediumId

case class MediumCreated(id: MediumId, uri: String)
