#!/bin/zsh -eux

    source /docker-lib.sh
    start_docker

    # Strictly speaking, preloading of images is not required.
    # However you might want to do it for a couple of reasons:
    # - If the image is from a private repository, it is much easier to let concourse pull it,
    #   and then pass it through to the task.
    # - When the image is passed to the task, Concourse can often get the image from its cache.
    docker load -i elastic/image
    docker tag "$(cat elastic/image-id)" "$(cat elastic/repository):$(cat elastic/tag)"

    docker load -i maven/image
    docker tag "$(cat maven/image-id)" "$(cat maven/repository):$(cat maven/tag)"

    # This is just to visually check in the log that images have been loaded successfully
    docker images

    # Run the tests container and its dependencies.
    docker-compose -f code/example/integration.yml run tests

    # Cleanup.
    # Not sure that this is required.
    # It's quite possible that Concourse is smart enough to clean up the Docker mess itself.
    docker-compose -f code/example/integration.yml down
    docker volume rm $(docker volume ls -q)





pushd babbage
  mvn clean verify
popd

cp -r target/cucumber-json-report .
mvn clean