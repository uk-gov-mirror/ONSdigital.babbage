#!/bin/bash -eux

pushd babbage
  make build
  cp -r Dockerfile.concourse target/* ../build/
popd
