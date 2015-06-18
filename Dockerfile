from java:7


# Clean image repository metadata
# http://serverfault.com/questions/690639/api-get-error-reading-from-server-under-docker
RUN apt-get clean && apt-get update


# We need to use a later version of Node than is currently available in the Ubuntu package manager (2015-06-17)
RUN apt-get install curl
RUN curl -sL https://deb.nodesource.com/setup_0.12 | bash -


# Install node, git and maven
RUN \
  apt-get clean && \
  apt-get update && \
  apt-get install -y git maven nodejs


# Consul agent - /usr/local/bin
ADD https://dl.bintray.com/mitchellh/consul/0.5.2_linux_amd64.zip /tmp/0.5.2_linux_amd64.zip
WORKDIR /usr/local/bin
RUN unzip /tmp/0.5.2_linux_amd64.zip
WORKDIR /etc/consul.d
RUN echo '{"service": {"name": "babbage", "tags": ["blue"], "port": 8080, "check": {"script": "curl http://localhost:8080 >/dev/null 2>&1", "interval": "10s"}}}' > babbage.json


# Check out from Github
WORKDIR /usr/src
RUN git clone https://github.com/ONSdigital/babbage.git
WORKDIR babbage
RUN git checkout develop


# Build web content
RUN npm install --prefix=src/main/web


# Build Jar and copy dependencyes
RUN mvn clean compile dependency:copy-dependencies


# Generate content
RUN java -Xmx2048m -cp "target/classes:target/dependency/*" com.github.onsdigital.generator.ContentGenerator


# Now copy files to the target
RUN mvn process-resources


# Expose port
EXPOSE 8080


# Restolino configuration
ENV RESTOLINO_STATIC="src/main/web"
ENV RESTOLINO_CLASSES="target/classes"
ENV PACKAGE_PREFIX=com.github.onsdigital


# Mongodb
ENV MONGO_USER=ons
ENV MONGO_PASSWORD=uJlVY2FDGI5SFawS/PN+jnZpymKWpU7C


# Entrypoint script
RUN echo "#!/bin/bash" >> container.sh
## Disabled for now: RUN echo "consul agent -data-dir /tmp/consul -config-dir /etc/consul.d -join=dockerhost &" > container.sh
RUN echo "java $JAVA_OPTS \
          -Drestolino.files=$RESTOLINO_STATIC \
          -Drestolino.classes=$RESTOLINO_CLASSES \
          -Drestolino.packageprefix=$PACKAGE_PREFIX \
          -Dmongo.user=$MONGO_USER \
          -Dmongo.password=$MONGO_PASSWORD \
          -cp \"target/dependency/*\" \
          com.github.davidcarboni.restolino.Main" >> container.sh
RUN chmod u+x container.sh


ENTRYPOINT ["./container.sh"]