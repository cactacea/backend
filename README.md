# Cactacea
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.cactacea/server_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.cactacea/server_2.12)
[![Docker Hub](https://images.microbadger.com/badges/version/cactacea/backend.svg)](https://microbadger.com/images/cactacea/backend "Get your own version badge on microbadger.com")
[![Build Status](https://travis-ci.org/cactacea/backend.svg?branch=master)](https://travis-ci.org/cactacea/backend)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/3ccea187e3644f4d89666516b46bce67)](https://www.codacy.com/app/cactacea/backend?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=cactacea/backend&amp;utm_campaign=Badge_Grade)
[![MIT License](http://img.shields.io/badge/license-MIT-blue.svg?style=flat)](LICENSE)
[![codecov](https://codecov.io/gh/cactacea/backend/branch/master/graph/badge.svg)](https://codecov.io/gh/cactacea/backend)

**Cactacea** is a framework to construct social networking applications and built on top of [Finatra](https://twitter.github.io/finatra/), [Finagle](https://twitter.github.io/finagle/), Twitter Server.

### Getting Started

To get started, add a dependency on cactacea depending.

```
"io.github.cactacea" %% "server" % "18.11.7"
```
Or similarily with Maven:
```
<dependency>
  <groupId>io.github.cactacea</groupId>
  <artifactId>server_2.12</artifactId>
  <version>18.11.7</version>
</dependency>
```

### Launch Demo

```
### Clone this repo
git clone https://github.com/cactacea/backend.git

### Start the containners
docker-compose up -d
```

### Documentation

- [`API Reference`](https://cactacea.github.io/backend/)
- [`Japanese Documents(TODO)`](https://cactacea.github.io/docs/)

### Related Projects

- [`Fiagger`](https://github.com/cactacea/backend/finagger) - Swagger Finatra Integration
- [`Filhouette`](https://github.com/cactacea/backend/filhouette) - Authentication library for Finatra
