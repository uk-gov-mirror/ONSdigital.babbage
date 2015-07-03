from carboni.io/java-node-component

# Consul
WORKDIR /etc/consul.d
RUN echo '{"service": {"name": "babbage", "tags": ["blue"], "port": 8080, "check": {"script": "curl http://localhost:8080 >/dev/null 2>&1", "interval": "10s"}}}' > babbage.json

# Add the repo source
WORKDIR /usr/src
ADD . /usr/src

# Build web content
RUN npm install --prefix=src/main/web --unsafe-perm

# Build jar-with-dependencies
RUN mvn install dependency:copy-dependencies -DskipTests

# Restolino
ENV RESTOLINO_STATIC="src/main/web"
ENV RESTOLINO_CLASSES="target/classes"
ENV PACKAGE_PREFIX=com.github.onsdigital

# Update the entry point script
RUN mv /usr/entrypoint/container.sh /usr/src/
# Download build and start highcharts server
#RUN echo "./highcharts-export-server.sh" >> container.sh
RUN echo "java -Xmx2048m \
          -Drestolino.files=$RESTOLINO_STATIC \
          -Drestolino.classes=$RESTOLINO_CLASSES \
          -Drestolino.packageprefix=$PACKAGE_PREFIX \
          -Dmongo.user=$MONGO_USER \
          -Dmongo.password=$MONGO_PASSWORD \
          -cp \"target/dependency/*\" \
          com.github.davidcarboni.restolino.Main" >> container.sh
