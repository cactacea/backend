package jp.smartreach.members.backend.core.infrastructure.specs

import com.twitter.util.logging.Logging
import io.github.cactacea.backend.core.helpers.generators.Generator
import io.github.cactacea.backend.core.helpers.tests.IntegrationFeatureTest
import org.scalatest.BeforeAndAfter
import org.scalatest.prop.GeneratorDrivenPropertyChecks

trait SpecHelper extends IntegrationFeatureTest
  with GeneratorDrivenPropertyChecks
  with Generator
  with BeforeAndAfter
  with Logging
