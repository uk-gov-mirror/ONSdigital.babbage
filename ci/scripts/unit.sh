#!/bin/sh
uname -a
cd  babbage
  mvn clean surefire:test
cd ..
