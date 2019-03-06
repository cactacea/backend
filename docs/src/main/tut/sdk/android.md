---
layout: docs
title:  "Android"
number: 2
---

## Android SDK

### Requires

* Kotlin 1.1.2
* Gradle 3.3

### Build

gradleのラッパースクリプトを作成します。

```
gradle wrapper
```

実行します。

```
./gradlew check assemble
```

すべてのテストが実行され、ライブラリがパッケージ化されます。
