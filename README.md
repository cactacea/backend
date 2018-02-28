# Cactacea

[![Build Status](https://travis-ci.org/cactacea/backend.svg?branch=master)](https://travis-ci.org/cactacea/backend)
[![codecov](https://codecov.io/gh/cactacea/backend/branch/master/graph/badge.svg)](https://codecov.io/gh/cactacea/backend)

**Cactacea** is a framework to construct social networking applications and built on top of [Finatra](https://twitter.github.io/finatra/), [Finagle](https://twitter.github.io/finagle/), Twitter Server.

### Features ###

Cactacea provides the following features.

- `Accounts` - friends, following, followers, blocks, muting.
- `Groups` - privacy settings and group invitations.
- `Messages` - direct messages and group messages.
- `Feeds` - text, images, movies, content warning and privacy settings.
- `Links` - Linking sns accounts.
- `Notifications` - new users, feeds, group invitations and messages. 
- `Authentication`, `Authorization` - API Key, OAuth2


### Components ###

Cactacea consists of four main components.

- `PushNotification` - push notifications to users.
- `Storage` - store uploaded images.
- `Message` - define notifidation messages.
- `Queue` - queue to fan out feeds, messages and etc.

### Modules ###

Modules are implements of above componentsa and the following modules are provided.

- [`DefaultPushNotificationModule`](https://github.com/cactacea/backend/blob/master/backend/src/main/scala/io/github/cactacea/backend/modules/DefaultPushNotificationModule.scala) - no push notifications module.
- [`DefaultStorateModule`](https://github.com/cactacea/backend/blob/master/backend/src/main/scala/io/github/cactacea/backend/modules/DefaultStorageModule.scala) - store files to local volumes.
- DefaultMessagingModule - define default messages.
- [`DefaultQueueModule`](https://github.com/cactacea/backend/blob/master/backend/src/main/scala/io/github/cactacea/backend/modules/DefaultQueueModule.scala) - no queue module.
- `OneSignalModule` - push notifications via [OneSignal](https://onesignal.com/).
- `S3Module` - store files to Amazon [S3](https://aws.amazon.com/s3/).
