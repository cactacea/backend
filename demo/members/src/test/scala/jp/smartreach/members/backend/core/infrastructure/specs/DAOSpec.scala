package jp.smartreach.members.backend.core.infrastructure.specs

import com.twitter.inject.app.TestInjector
import io.github.cactacea.backend.core.application.components.modules.{DatabaseModule, DefaultDeepLinkModule, DefaultMessageModule}
import io.github.cactacea.backend.core.helpers.specs.Spec
import jp.smartreach.members.backend.core.infrastructure.daos.MembersDAO
import jp.smartreach.members.backend.core.infrastructure.generators.ModelsGenerator

trait DAOSpec extends Spec with ModelsGenerator {

  override val injector =
    TestInjector(
      modules = Seq(
        DatabaseModule,
        DefaultMessageModule,
        DefaultDeepLinkModule
      )
    ).create

  val membersDAO = injector.instance[MembersDAO]

}
