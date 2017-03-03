#!/bin/sh
uname -a
exec /docker-entrypoint.sh
cd  babbage
  mvn clean verify
cd ..
