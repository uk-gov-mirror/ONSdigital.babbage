#!/bin/bash -eux

pushd babbage
  mvn clean verify
popd

cp -r target/cucumber-json-report .
mvn clean