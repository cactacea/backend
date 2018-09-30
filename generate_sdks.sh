#!/usr/bin/env bash
swagger-codegen generate -i docs/swagger.json -l swift4 --language-specific-primitives Int --type-mappings integer=Int -o ../ios --additional-properties projectName=Cactacea,podAuthors=takeshishimada,responseAs=RxSwift
