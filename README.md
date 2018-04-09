# Cactacea v0.1.1-SNAPSHOT

[![Build Status](https://travis-ci.org/cactacea/backend.svg?branch=master)](https://travis-ci.org/cactacea/backend)
[![codecov](https://codecov.io/gh/cactacea/backend/branch/master/graph/badge.svg)](https://codecov.io/gh/cactacea/backend)
[![MIT License](http://img.shields.io/badge/license-MIT-blue.svg?style=flat)](LICENSE)

**Cactacea** is a framework to construct social networking applications and built on top of [Finatra](https://twitter.github.io/finatra/), [Finagle](https://twitter.github.io/finagle/), Twitter Server.

## Documentation ##

- [`REST API`](https://rebilly.github.io/ReDoc/?url=http://backend.cactacea.io/swagger.yaml)

## Features ##

Cactacea provides the following features.

- `Accounts` - friends, following, followers, blocks, muting.
- `Groups` - privacy settings and group invitations.
- `Messages` - direct messages and group messages.
- `Feeds` - text, images, movies, content warning and privacy settings.
- `Links` - Linking social network accounts.
- `Notifications` - new users, feeds, group invitations and messages. 
- `Authentication`, `Authorization` - API Key, OAuth2

## Serivces ##

Cactacea consists of the following injectable services.

- `Config Service` - condig max groups count etc.
- `DeepLink Service`
- `FanOut Service` - fan out feeds, messages and etc.
- `Identify Service` - generate a identity.
- `Injection Service` - inject actions.
- `NotificationMessages Service` - define notifidation messages.
- `Publish Service` -  enqueue to fan out feeds, messages and etc.
- `PushNotifications Service` - push notifications to users.
- `Social Accounts Service`
- `Storage Service` - store uploaded images.
- `SubScribe Service` - dequeue to fan out feeds, messages and etc.
- `Transcode Service`

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

