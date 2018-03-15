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
- `Links` - Linking sns accounts.
- `Notifications` - new users, feeds, group invitations and messages. 
- `Authentication`, `Authorization` - API Key, OAuth2



## Serivces ##

Cactacea consists of the following injectable services.

- `Config Service`
- `DeepLink Service`
- `FanOut Service`
- `Identify Service`
- `Injection`
- `Notification Messages Service`
- `Publish Service`
- `Push Notifications Service`
- `Social Accounts Service`
- `Storage Service`
- `SubScribe Service`
- `Transcode Service`

## Documentation ##

- [`REST API`](https://rebilly.github.io/ReDoc/?url=http://backend.cactacea.io/swagger.yaml)
