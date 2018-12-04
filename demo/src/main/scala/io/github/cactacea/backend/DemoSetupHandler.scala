package io.github.cactacea.backend

import com.google.inject.{Inject, Singleton}
import com.twitter.finatra.http.routing.HttpWarmup
import com.twitter.finatra.json.FinatraObjectMapper
import com.twitter.inject.annotations.Flag
import com.twitter.inject.utils.Handler

@Singleton
class DemoSetupHandler  @Inject()(@Flag("storage.localPath") localPath: String, mapper: FinatraObjectMapper, warmup: HttpWarmup) extends Handler {


  override def handle(): Unit = {
    DemoSetup.migrate()
    DemoSetup.setupData(localPath)
  }



}
