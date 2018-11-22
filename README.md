# Cactacea

[![Build Status](https://travis-ci.org/cactacea/backend.svg?branch=master)](https://travis-ci.org/cactacea/backend)
[![codecov](https://codecov.io/gh/cactacea/backend/branch/master/graph/badge.svg)](https://codecov.io/gh/cactacea/backend)
[![MIT License](http://img.shields.io/badge/license-MIT-blue.svg?style=flat)](LICENSE)

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
