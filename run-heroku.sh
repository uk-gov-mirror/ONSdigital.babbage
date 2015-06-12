#!/usr/bin/env bash

######This script is run by Heroku to start the server

#java -Xmx2048m -cp "target/classes:target/dependency/*" com.github.onsdigital.generator.ContentGenerator


### 1 - BUILD WEB FRONT-END

./build-web.sh

### 2 - Start server
java $JAVA_OPTS -Drestolino.files="target/web" -Drestolino.classes="target/classes" -Drestolino.packageprefix=com.github.onsdigital -cp "target/dependency/*" com.github.davidcarboni.restolino.Main
