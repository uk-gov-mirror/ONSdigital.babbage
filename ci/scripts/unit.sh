#!/bin/bash -eux

pushd babbage
  mvn clean surefire:test
popd
