# Cactacea

[![Build Status](https://travis-ci.org/cactacea/backend.svg?branch=master)](https://travis-ci.org/cactacea/backend)
[![codecov](https://codecov.io/gh/cactacea/backend/branch/master/graph/badge.svg)](https://codecov.io/gh/cactacea/backend)

**Cactacea** is a framework to construct social networking applications and built on top of [Finatra](https://twitter.github.io/finatra/), [Finagle](https://twitter.github.io/finagle/), Twitter Server.

## Features ##

Cactacea provides the following features.

- `Accounts` - friends, following, followers, blocks, muting.
- `Groups` - privacy settings and group invitations.
- `Messages` - direct messages and group messages.
- `Feeds` - text, images, movies, content warning and privacy settings.
- `Links` - Linking social network accounts.
- `Notifications` - new users, feeds, group invitations and messages. 
- `Authentication`, `Authorization` - API Key, OAuth2



## Components ##

Cactacea consists of four main components.

- `Config` - condig max groups count etc.
- `FanOut` - fan out feeds, messages and etc.
- `Injection` - inject actions.
- `NotificationMessages` - define notifidation messages.
- `Publish` -  enqueue to fan out feeds, messages and etc.
- `PushNotification` - push notifications to users.
- `Storage` - store uploaded images.
- `SubScribe` - dequeue to fan out feeds, messages and etc.


## Modules ##

Modules are implements of above components and the following modules are provided.

- [`DefaultConfigModule`](https://github.com/cactacea/backend/blob/master/core/src/main/scala/io/github/cactacea/core/application/components/modules/DefaultConfigModule.scala)
- [`DefaultFanOutModule`](https://github.com/cactacea/backend/blob/master/core/src/main/scala/io/github/cactacea/core/application/components/modules/DefaultFanOutModule.scala)
- [`DefaultInjectionModule`](https://github.com/cactacea/backend/blob/master/core/src/main/scala/io/github/cactacea/core/application/components/modules/DefaultInjectionModule.scala)
- [`DefaultNotificationMessagesModule`](https://github.com/cactacea/backend/blob/master/core/src/main/scala/io/github/cactacea/core/application/components/modules/DefaultNotificationMessagesModule.scala)
- [`DefaultPublishModule`](https://github.com/cactacea/backend/blob/master/core/src/main/scala/io/github/cactacea/core/application/components/modules/DefaultPublishModule.scala)
- [`DefaultPushNotificationModule`](https://github.com/cactacea/backend/blob/master/core/src/main/scala/io/github/cactacea/core/application/components/modules/DefaultPushNotificationModule.scala)
- [`DefaultStorageModule`](https://github.com/cactacea/backend/blob/master/core/src/main/scala/io/github/cactacea/core/application/components/modules/DefaultStorageModule.scala)
- [`DefaultSubScribeModule`](https://github.com/cactacea/backend/blob/master/core/src/main/scala/io/github/cactacea/core/application/components/modules/DefaultSubScribeModule.scala)
- [`OneSignalModule`](https://github.com/cactacea/backend/blob/master/core/src/main/scala/io/github/cactacea/core/application/components/thirdparties/onesignal/OneSignalModule.scala)
- [`S3Module`](https://github.com/cactacea/backend/blob/master/core/src/main/scala/io/github/cactacea/core/application/components/thirdparties/s3/S3ServiceModule.scala)
