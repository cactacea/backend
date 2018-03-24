package io.github.cactacea.backend.util.mappers

import io.github.cactacea.backend.core.util.responses.CactaceaError

case class Errors(errors: Seq[CactaceaError])
