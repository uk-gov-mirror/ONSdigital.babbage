#!/bin/bash

### 1 - BUILD WEB FRONT-END

# cd web && \
# build.sh && \
# cd ..

### 2 - BUILD API

cd api && \
./build.sh && \
cd ..

### 3 - START SERVER

#Back to project root

export JAVA_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"

#External Taxonomy
#export TAXONOMY_DIR=api/target/taxonomy

# Restolino configuration
export RESTOLINO_STATIC="web/src"
export RESTOLINO_CLASSES="api/target/classes"
export PACKAGE_PREFIX=com.github.onsdigital

# For testing out HTTP basic auth
#export USERNAME=user
#export PASSWORD=password
#export REALM=onsalpha

# Elasticsearch
export BONSAI_URL=http://localhost:9200
export BONSAI_CLUSTERNAME=elasticsearch
export BONSAI_HOSTNAME=localhost
export BONSAI_TRANSPORT_PORT=9300

# Mongodb
export MONGO_USER=ons
export MONGO_PASSWORD=uJlVY2FDGI5SFawS/PN+jnZpymKWpU7C

# Development: reloadable
$JAVA_HOME/bin/java $JAVA_OPTS \
 -Drestolino.realm=$REALM \
 -Drestolino.files=$RESTOLINO_STATIC \
 -Drestolino.classes=$RESTOLINO_CLASSES \
 -Drestolino.packageprefix=$PACKAGE_PREFIX \
 -Dmongo.user=$MONGO_USER \
 -Dmongo.password=$MONGO_PASSWORD \
 -cp "api/target/dependency/*" \
 com.github.davidcarboni.restolino.Main

# Production: non-reloadable
#$JAVA_HOME/bin/java $JAVA_OPTS -jar target/*-jar-with-dependencies.jar
