#!/usr/bin/env bash

# Fixme
swagger-codegen generate \
    -i docs/swagger.json \
    -l swift4 \
    --type-mappings number=Int64 \
    --language-specific-primitives Int16,Int32,Int64,NSDate \
    --additional-properties projectName="Cactacea" \
    --additional-properties podVersion="18.12.8" \
    --additional-properties podSummary="Cactacea iOS SDK" \
    --additional-properties podDescription="An iOS library to interface with Cactacea backend API" \
    --additional-properties podSource="" \
    --additional-properties podAuthors="" \
    --additional-properties podLicense="" \
    --additional-properties podHomepage=https://github.com/cactacea/ios \
    --additional-properties responseAs=RxSwift \
    -o ../ios

swagger-codegen generate \
    -i docs/swagger.json  \
    -l kotlin \
    --model-package io.github.cactacea.backend \
    --model-name-suffix Json \
    -o ../android

