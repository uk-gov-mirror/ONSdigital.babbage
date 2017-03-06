# Unit Job
Unit job uses a single custom Docker image that container created from a based Maven image with Elastic added and then 
index data 

When the integration tests are run via Maven locally then Maven will start a docker image containing the pre-canned ElasticSearch image.

When the maven integration tests are run indise the _guidof/mavenastic_ docker image then Maven will not create a start the pre-canned ElasticSearch image, as the container already has an ElasticSearch service.
Maven knows this as it has an environment variable of `CONTAINER=mavenastic`. 
```$xml
<activation>
    <property>
        <name>env.CONTAINER</name>
        <value>!mavenastic</value>
    </property>
</activation>
```