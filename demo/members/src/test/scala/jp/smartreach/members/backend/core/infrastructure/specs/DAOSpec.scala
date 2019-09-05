package jp.smartreach.members.backend.core.infrastructure.specs

import com.twitter.inject.app.TestInjector
import io.github.cactacea.backend.core.application.components.modules.{DefaultDeepLinkModule, DefaultMessageModule}
import io.github.cactacea.backend.core.helpers.specs.Spec
import jp.smartreach.members.backend.core.application.components.moduels.MembersDatabaseModule
import jp.smartreach.members.backend.core.infrastructure.daos.MembersDAO

trait DAOSpec extends Spec {

  override val injector =
    TestInjector(
      modules = Seq(
        MembersDatabaseModule,
        DefaultMessageModule,
        DefaultDeepLinkModule
      )
    ).create

  val membersDAO = injector.instance[MembersDAO]

}
