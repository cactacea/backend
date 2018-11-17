#!/usr/bin/env bash

# Fixme
openapi-generator generate \
    -i docs/swagger.json \
    -g swift4 \
    --type-mappings number=Int64 \
    --language-specific-primitives Int16,Int32,Int64,NSDate \
    --additional-properties projectName="Cactacea" \
    --additional-properties podVersion="18.11.2" \
    --additional-properties podSummary="Cactacea iOS SDK" \
    --additional-properties podDescription="An iOS library to interface with Cactacea backend API" \
    --additional-properties podSource="" \
    --additional-properties podAuthors="" \
    --additional-properties podLicense="" \
    --additional-properties podHomepage=https://github.com/cactacea/ios \
    --additional-properties responseAs=RxSwift \
    --skip-validate-spec \
    -o ../ios

openapi-generator generate \
    -g docs/swagger.json  \
    -l kotlin \
    --model-package io.github.cactacea.backend \
    --model-name-suffix Json \
    --skip-validate-spec \
    -o ../android
