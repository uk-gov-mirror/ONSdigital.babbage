#!/bin/sh

/docker-entrypoint.sh

cd  babbage
mvn clean verify
mavenReturnCode=$?
tar zcvf feature-tests/cucumber-html-report.tar.gz target/cucumber-html-reports
exit ${mavenReturnCode}
