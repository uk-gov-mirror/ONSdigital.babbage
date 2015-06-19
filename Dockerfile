from onsdigital/java-component


# Node.js

# We need to use a later version of Node than is currently available in the Ubuntu package manager (2015-06-17)
# The tar and bzip2 packages are required for Phantom.js installation in npm: https://github.com/Medium/phantomjs/issues/326
RUN apt-get install -y curl
RUN curl -sL https://deb.nodesource.com/setup_0.12 | bash -
RUN apt-get install -y nodejs tar bzip2

# Consul

WORKDIR /etc/consul.d
RUN echo '{"service": {"name": "babbage", "tags": ["blue"], "port": 8080, "check": {"script": "curl http://localhost:8080 >/dev/null 2>&1", "interval": "10s"}}}' > babbage.json

# Check out from Github

WORKDIR /usr/src
RUN git clone https://github.com/ONSdigital/babbage.git .
RUN git checkout develop

# Build web content

RUN npm install --prefix=src/main/web

# Build

RUN mvn clean compile dependency:copy-dependencies

# Generate content

RUN java -Xmx2048m -cp "target/classes:target/dependency/*" com.github.onsdigital.generator.ContentGenerator

# Now copy files to the target

RUN mvn process-resources

# Restolino

ENV RESTOLINO_STATIC="src/main/web"
ENV RESTOLINO_CLASSES="target/classes"
ENV PACKAGE_PREFIX=com.github.onsdigital

# Mongodb

ENV MONGO_USER=ons
ENV MONGO_PASSWORD=uJlVY2FDGI5SFawS/PN+jnZpymKWpU7C

#Download build and start highchart server

RUN echo -e "./highcharts-export-server.sh\n\n" >> container.sh

# Update the entry point script

RUN echo "java $JAVA_OPTS \
          -Drestolino.files=$RESTOLINO_STATIC \
          -Drestolino.classes=$RESTOLINO_CLASSES \
          -Drestolino.packageprefix=$PACKAGE_PREFIX \
          -Dmongo.user=$MONGO_USER \
          -Dmongo.password=$MONGO_PASSWORD \
          -cp \"target/dependency/*\" \
          com.github.davidcarboni.restolino.Main" >> container.sh
RUN pwd
RUN ls -lah container.sh
