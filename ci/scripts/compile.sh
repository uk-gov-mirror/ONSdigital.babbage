#!/bin/bash -eux

cp -r assets/ babbage/src/main/

ls -la babbage/src/main/web/
ls -la babbage/src/main/web/js/

sleep 10

pushd babbage
  mvn clean package dependency:copy-dependencies -Dmaven.test.skip
popd

cp -r babbage/target/* target/
