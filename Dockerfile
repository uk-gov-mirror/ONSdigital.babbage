FROM onsdigital/java-node-phantom-component

# Add the build artifacts
WORKDIR /usr/src
ADD git_commit_id /usr/src/
ADD ./target/dependency /usr/src/target/dependency
ADD ./target/classes /usr/src/target/classes
ADD ./src/main/web /usr/src/src/main/web
#ADD ./target/*-jar-with-dependencies.jar /usr/src/target/

# Temporary: expose Elasticsearch
#EXPOSE 9200

# Update the entry point script
ENTRYPOINT java -Xmx2048m \
          -javaagent:/usr/src/target/dependency/newrelic/newrelic.jar \
          -Drestolino.files=src/main/web \
          -Drestolino.classes=target/classes \
          -Drestolino.packageprefix=com.github.onsdigital.babbage.api \
          -cp "target/dependency/*:target/classes/" \
          com.github.davidcarboni.restolino.Main
