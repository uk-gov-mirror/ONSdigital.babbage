#!/bin/bash
source /docker-lib.sh
rm -rf /var/lib/docker/aufs
start_docker
docker images
# Cleaning up through docker avoids these errors
#   ERROR: Service 'master' failed to build:
#     open /var/lib/docker/aufs/layers/<container_id>: no such file or directory
#   ERROR: Service 'master' failed to build: failed to register layer:
#     open /var/lib/docker/aufs/layers/<container_id>: no such file or directory
docker rm -f $(docker ps -a -q)
docker rmi -f $(docker images -a -q)
mvn clean install
stop_docker
