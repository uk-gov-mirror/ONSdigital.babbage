#!/bin/bash -eux

pushd babbage
  npm install --prefix src/main/web --unsafe-perm
  mvn -Dmaven.test.skip clean package dependency:copy-dependencies
  cp -r Dockerfile target/* ../build/
popd
