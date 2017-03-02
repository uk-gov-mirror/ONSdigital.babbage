#!/bin/sh

source /docker-lib.sh
start_docker

echo
echo "Loading image elastic"

docker load -i elastic/image
docker tag "$(cat elastic/image-id)" "$(cat elastic/repository):$(cat elastic/tag)"

echo "Loading image maven"
docker load -i maven/image
docker tag "$(cat maven/image-id)" "$(cat maven/repository):$(cat maven/tag)"

# This is just to visually check in the log that images have been loaded successfully
echo "Docker Images"
docker images

# Run the maven container and its dependencies.
echo "run tests"
docker-compose --verbose -f babbage/ci/compose/compose-integration.yml run maven

mkdir -p integration-tests/cucumber-html-reports
tar zcvf integration-tests/cucumber-html-report.tar.gz babbage/target/cucumber-html-reports
cp  -r babbage/target/cucumber-html-reports/ integration-tests/


# Cleanup.
# Not sure that this is required.
# It's quite possible that Concourse is smart enough to clean up the Docker mess itself.
docker-compose -f babbage/ci/compose/compose-integration.yml down
docker volume rm $(docker volume ls -q)