#!/bin/bash -eux

pushd babbage
  cd src/main/web
  npm audit --audit-level=high
popd