#!/bin/bash -eux

cp -r assets/ babbage/src/main/

pushd babbage
  mvn clean package dependency:copy-dependencies -Dmaven.test.skip
popd

cp -r babbage/target/* target/
