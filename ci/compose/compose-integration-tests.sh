#!/bin/bash
pushd /babbage
curl -XGET http://elastic:9200/_count
export ELASTIC_SEARCH_SERVER=elastic
mvn clean test-compile surefire:test@integration-test

popd