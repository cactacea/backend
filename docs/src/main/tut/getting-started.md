---
layout: docs
title: Getting Started
position: 1
---

## Getting Started


`build.sbt`に下記の依存関係を追加します。

```
"io.github.cactacea" %% "server" % "19.9.2"
```

`Maven`による表記:

```
<dependency>
  <groupId>io.github.cactacea</groupId>
  <artifactId>server_2.12</artifactId>
  <version>19.9.2</version>
</dependency>
```


### Example

`CactaceaServer`の継承するクラスを作成します。

```$xslt
import io.github.cactacea.backend.server.CactaceaServer

class DemoServer extends CactaceaServer
```

エントリーポイントとなるオブジェクトを作成します。

```
object DemoServerApp extends DemoServer
```

作成したオブジェクトを実行します。