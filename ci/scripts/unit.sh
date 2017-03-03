#!/bin/sh
uname -a
/docker-entrypoint.sh

cd  babbage
  mvn clean verify
cd ..
