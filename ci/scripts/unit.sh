#!/bin/bash -x

/docker-entrypoint.sh

cd  babbage
mvn clean verify
mavenReturnCode=$?
mv target/cucumber-html-reports ../reports
exit ${mavenReturnCode}
