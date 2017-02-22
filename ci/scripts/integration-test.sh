#!/bin/sh -eux
source /docker-lib.sh
start_docker

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
docker-compose -f babbage/ci/compose/compose-integration.yml run maven
# Cleanup.
# Not sure that this is required.
# It's quite possible that Concourse is smart enough to clean up the Docker mess itself.
docker-compose -f babbage/ci/compose/compose-integration.yml down
#      docker volume rm $(docker volume ls -q)