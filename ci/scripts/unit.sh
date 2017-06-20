#!/bin/bash -eux

pushd babbage
  mvn test
popd
