FROM onsdigital/java-node-phantom-ghostscript-component

# Add the build artifacts
WORKDIR /usr/src
ADD ./target/dependency /usr/src/target/dependency
ADD ./target/classes /usr/src/target/classes
ADD ./target/web /usr/src/target/web

# Update the entry point script
ENTRYPOINT java -Xmx2048m \
          -Drestolino.files=target/web \
          -Drestolino.classes=target/classes \
          -Drestolino.packageprefix=com.github.onsdigital.babbage.api \
          -cp "target/dependency/*:target/classes/" \
          com.github.davidcarboni.restolino.Main
