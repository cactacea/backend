package jp.smartreach.members.backend.core.application.components.moduels

import java.util.TimeZone

import com.google.inject.{Provides, Singleton}
import com.twitter.inject.TwitterModule
import io.github.cactacea.backend.core.application.components.services.DatabaseService
import io.github.cactacea.backend.core.util.configs.Config
import io.github.cactacea.backend.core.util.db.ClientFactory
import jp.smartreach.members.backend.core.application.components.services.MembersDatabaseService

object MembersDatabaseModule extends TwitterModule {

  @Singleton
  @Provides
  def context() : DatabaseService = {
    Config.db.useSlave match {
      case true =>
        val master = ClientFactory.create(Config.db.master)
        val slave = ClientFactory.create(Config.db.slave)
        new MembersDatabaseService(master, slave, TimeZone.getDefault)
      case false =>
        val master = ClientFactory.create(Config.db.master)
        new MembersDatabaseService(master, TimeZone.getDefault)
    }
  }

}

