#!/bin/sh

/docker-entrypoint.sh

cd  babbage
mvn clean verify
mavenReturnCode=$?
mkdir -p  /tmp/build/put/feature-tests/html
tar zcvf /tmp/build/put/feature-tests/cucumber-html-report.tar.gz target/cucumber-html-reports
cp -r target/cucumber-html-reports /tmp/build/put/feature-tests/html
exit ${mavenReturnCode}
