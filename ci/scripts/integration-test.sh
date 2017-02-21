#!/bin/bash -eux

pushd babbage
  ls -lart
  mvn clean verify
popd

cp -r babbage/target/* target/
