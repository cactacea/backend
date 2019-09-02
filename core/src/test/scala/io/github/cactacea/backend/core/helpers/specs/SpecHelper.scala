package io.github.cactacea.backend.core.helpers.specs

import com.twitter.util.logging.Logging
import io.github.cactacea.backend.core.helpers.generators.{Generator, ModelsGenerator, StatusGenerator}
import io.github.cactacea.backend.core.helpers.tests.IntegrationFeatureTest
import org.scalatest.BeforeAndAfter
import org.scalatest.prop.GeneratorDrivenPropertyChecks

trait SpecHelper extends IntegrationFeatureTest
  with GeneratorDrivenPropertyChecks
  with Generator
  with StatusGenerator
  with ModelsGenerator
  with BeforeAndAfter
  with Logging
