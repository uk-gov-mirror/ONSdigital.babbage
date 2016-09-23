#!/bin/bash

### 1 - BUILD WEB FRONT-END

./build-web.sh

### 2 - BUILD API
./build-api.sh

### 4 - START BABBAGE
export JAVA_OPTS="-Xmx512m -Xdebug -Xrunjdwp:transport=dt_socket,address=8010,server=y,suspend=n"

#External Taxonomy
#export TAXONOMY_DIR=target/content

# Restolino configuration
export RESTOLINO_STATIC="src/main/web"
export RESTOLINO_CLASSES="target/classes"
export PACKAGE_PREFIX=com.github.onsdigital

export DEV_ENVIRONMENT="Y"
export RELOAD_TEMPLATES="Y"
export TEMPLATES_DIR=src/main/web/templates/handlebars

# Development: reloadable
java $JAVA_OPTS \
 -Drestolino.realm=$REALM \
 -Drestolino.files=$RESTOLINO_STATIC \
 -Drestolino.classes=$RESTOLINO_CLASSES \
 -Drestolino.packageprefix=$PACKAGE_PREFIX \
 -cp "target/dependency/*" \
 com.github.davidcarboni.restolino.Main

kill $exportserverpid
# Production: non-reloadable
#$JAVA_HOME/bin/java $JAVA_OPTS -jar target/*-jar-with-dependencies.jar
