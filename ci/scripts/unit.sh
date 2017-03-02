#!/bin/sh
pushd babbage
  mvn clean surefire:test
popd
