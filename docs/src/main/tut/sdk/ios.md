---
layout: docs
title:  "iOS"
number: 1
---

## iOS SDK

[![Version](https://img.shields.io/cocoapods/v/Cactacea.svg?style=flat)](https://cocoapods.org/pods/Cactacea)
[![License](https://img.shields.io/cocoapods/l/Cactacea.svg?style=flat)](https://cocoapods.org/pods/Cactacea)
[![Platform](https://img.shields.io/cocoapods/p/Cactacea.svg?style=flat)](https://cocoapods.org/pods/Cactacea)

- インストール

CocoaPods

CactaceaはCocoaPodsを通して入手可能です。インストールするには、Podfileに次の行を追加します。

```
pod 'Cactacea'
```


- 初期処理

```
CactaceaAPI.basePath = "http://ホスト名:ポート番号"
CactaceaAPI.customHeaders["X-API-KEY"] = "iOS用のAPIキー"
CactaceaAPI.customHeaders["Content-Type"] = "application/json"
```

