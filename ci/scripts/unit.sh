#!/bin/sh
uname -a
cd  babbage
  mvn clean verify -D !docker-run
cd ..
