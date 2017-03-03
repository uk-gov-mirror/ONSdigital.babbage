#Integration Test Tasks
There are three images that are defined in the parent flow that are used in this
task.
- name: elastic
- name: maven
- name: integration-container

Having these defined in the parent task does limit the ability to be able to have these changed 
per task instance with out getting into complicated substitutions in the templating.

The integration task invoked a `dcind` container that is a _docker compose container_ that allows the caller 
to open a _docker compose_ image from with the `integration-test.sh` file.

In this case the two docker images that make up the docker compos composition are:

* maven - contains a docker with JDK 8/Alpine and Maven software
* guidof/onswebsite-elastic - contains a pre-populated the with the Zebedee data of 2017 Jan  
![Concourse Container -> Docker Compose ->\[ Maven container + ONSWebsite Elastic Container\]](Integration%20Job.png)