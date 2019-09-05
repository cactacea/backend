package jp.smartreach.members.backend.core.infrastructure.specs

import com.twitter.inject.app.TestInjector
import io.github.cactacea.backend.core.application.components.modules.{DatabaseModule, DefaultDeepLinkModule, DefaultMessageModule}
import jp.smartreach.members.backend.core.infrastructure.generators.Generator

trait DAOSpec extends SpecHelper with Generator {

  override val injector =
    TestInjector(
      modules = Seq(
        DatabaseModule,
        DefaultMessageModule,
        DefaultDeepLinkModule
      )
    ).create

}
