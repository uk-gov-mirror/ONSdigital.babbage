#!/bin/bash

ECR_REPOSITORY_URI=
GIT_COMMIT=

if [[ $DEPLOYMENT_GROUP_NAME =~ [a-z]+-publishing ]]; then
  docker run -d                                   \
    --env=CONTENT_SERVICE_URL=http://zebedee:8080 \
    --env=GHOSTSCRIPT_PATH=/usr/bin/gs            \
    --env=IS_PUBLISHING=Y                         \
    --name=babbage                                \
    --net=publishing                              \
    --restart=always                              \
    $ECR_REPOSITORY_URI/babbage:$GIT_COMMIT
else
  docker run -d                                          \
    --env=CONTENT_SERVICE_MAX_CONNECTION=1000            \
    --env=CONTENT_SERVICE_URL=http://zebedee-reader:8080 \
    --env=ELASTIC_SEARCH_CLUSTER=cluster                 \
    --env=ELASTIC_SEARCH_SERVER=elasticsearch            \
    --env=ENABLE_CACHE=Y                                 \
    --env=GHOSTSCRIPT_PATH=/usr/bin/gs                   \
    --env=GLOBAL_CACHE_SIZE=5000                         \
    --env=PHANTOMJS_PATH=/usr/local/bin/phantomjs        \
    --name=babbage                                       \
    --net=website                                        \
    --restart=always                                     \
    $ECR_REPOSITORY_URI/babbage:$GIT_COMMIT
fi
