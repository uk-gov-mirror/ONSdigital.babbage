#!/bin/bash

### 1 - BUILD WEB FRONT-END

./build-web.sh

### 2 - BUILD API
./build-api.sh 

### 3 - START BABBAGE
export JAVA_OPTS="-Xdebug -Xmx256m -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"

# Restolino configuration
export RESTOLINO_STATIC="src/main/web"
export RESTOLINO_CLASSES="target/classes"
export PACKAGE_PREFIX=com.github.onsdigital
export DP_COLOURED_LOGGING=true
export DP_LOGGING_FORMAT=pretty_json

export PHANTOMJS_PATH=`which phantomjs`
export DEV_ENVIRONMENT="Y"
export IS_PUBLISHING="Y"
export RELOAD_TEMPLATES="Y"
export TEMPLATES_DIR=src/main/web/templates/handlebars
export ENABLE_LOOP11=false
export ENABLE_COOKIES_CONTROL=false

# Development: reloadable
java $JAVA_OPTS \
 -Drestolino.realm=$REALM \
 -Drestolino.files=$RESTOLINO_STATIC \
 -Drestolino.classes=$RESTOLINO_CLASSES \
 -Drestolino.packageprefix=$PACKAGE_PREFIX \
 -cp "target/classes/:target/dependency/*" \
 com.github.davidcarboni.restolino.Main

