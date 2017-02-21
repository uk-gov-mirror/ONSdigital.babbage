#!/bin/bash -eux

cp -r assets/ babbage/src/main/

pushd babbage
  ls -lart
  mvn clean verify
popd

cp -r babbage/target/* target/
