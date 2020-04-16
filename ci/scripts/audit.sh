#!/bin/bash -eux

pushd babbage/src/main/web
  npm audit --audit-level=high
popd