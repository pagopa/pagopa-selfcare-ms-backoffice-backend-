#!/bin/bash

# This script converts the autogenerated OpenApi 3 specifications into Swagger 2.
# This is useful to create the client with gen-api-models https://github.com/pagopa/openapi-codegen-ts

# how install api-spec-converter https://www.npmjs.com/package/api-spec-converter

if [[ "$(pwd)" =~ .*"openapi".* ]]; then
    cd ..
fi

mvn test -Dtest=OpenApiGenerationTest


if [ "$(npm list -g | grep -c api-spec-converter)" -eq 0 ]; then
  npm install -g api-spec-converter
fi

api-spec-converter  --from=openapi_3 --to=swagger_2 ./openapi/openapi.json > ./openapi/swagger.json

