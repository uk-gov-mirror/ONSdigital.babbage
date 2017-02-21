#!/bin/bash -eux

pushd babbage
  mvn clean test-compile  surefire:test@integration-test
popd

cp -r target/cucumber-json-report .
mvn clean