<!-- [![Build Status](https://travis-ci.org/ONSdigital/tredegar.svg?branch=master)](https://travis-ci.org/ONSdigital/tredegar) -->

Babbage
========

Repository for ONS Website Babbage

#### Dependencies

To run Babbage locally:
* Install ghostscript: `brew install ghostscript`


### Cucumber Tests
The Cucumber Tests start up a Docker Elastic Image with a pre-installed data set.

When the build is run inside a guidof/mavenastic  Docker&trade; image (which contains the build 
[ONSdigital/dp-docker-maven-elastic](https://github.com/ONSdigital/dp-docker-maven-elastic) ) Docker Container then the
 environment variable `CONTAINER=mavenastic` is set; this is used by the build to stop Docker starting up as part of the build.

