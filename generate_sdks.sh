#!/usr/bin/env bash
swagger-codegen generate \
    -i docs/swagger.json \
    -l swift4 \
    --type-mappings number=Int64 \
    --language-specific-primitives Int16,Int32,Int64,NSDate \
    -o ../ios --additional-properties projectName=Cactacea,podAuthors=takeshishimada,responseAs=RxSwift


swagger-codegen generate \
    -i docs/swagger.json  \
    -l kotlin
    --model-package io.github.cactacea.backend \
    --model-name-suffix Json
    -o ../android
