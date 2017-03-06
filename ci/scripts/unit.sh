#!/bin/sh

/docker-entrypoint.sh

cd  babbage
mvn clean verify
mavenReturnCode=$?

exit ${mavenReturnCode}
