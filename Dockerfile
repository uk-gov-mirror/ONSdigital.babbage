from carboni.io/java-node-component

# Consul

WORKDIR /etc/consul.d
RUN echo '{"service": {"name": "babbage", "tags": ["blue"], "port": 8080, "check": {"script": "curl http://localhost:8080 >/dev/null 2>&1", "interval": "10s"}}}' > babbage.json

# Check out from Github

WORKDIR /usr/src
RUN git clone -b staging --single-branch --depth 1 https://github.com/ONSdigital/babbage.git .

# Build web content

RUN npm install --prefix=src/main/web --unsafe-perm

# Build

RUN mvn clean compile dependency:copy-dependencies

# Generate content

# RUN java -Xmx2048m -cp "target/classes:target/dependency/*" com.github.onsdigital.generator.ContentGenerator

# Now copy files to the target

# RUN mvn process-resources

# Restolino

ENV RESTOLINO_STATIC="src/main/web"
ENV RESTOLINO_CLASSES="target/classes"
ENV PACKAGE_PREFIX=com.github.onsdigital

# Mongodb

ENV MONGO_USER=ons
ENV MONGO_PASSWORD=uJlVY2FDGI5SFawS/PN+jnZpymKWpU7C



# Update the entry point script

RUN mv /usr/entrypoint/container.sh /usr/src/
# Download build and start highcharts server
RUN echo "./highcharts-export-server.sh" >> container.sh
RUN echo "java $JAVA_OPTS \
          -Drestolino.files=$RESTOLINO_STATIC \
          -Drestolino.classes=$RESTOLINO_CLASSES \
          -Drestolino.packageprefix=$PACKAGE_PREFIX \
          -Dmongo.user=$MONGO_USER \
          -Dmongo.password=$MONGO_PASSWORD \
          -cp \"target/dependency/*\" \
          com.github.davidcarboni.restolino.Main" >> container.sh
