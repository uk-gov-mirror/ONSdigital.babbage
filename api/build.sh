
#Generate taxonomy
mvn clean compile dependency:copy-dependencies && \
rm -rf src/main/taxonomy && \
$JAVA_HOME/bin/java -Xmx2048m -cp "target/classes:target/dependency/*" com.github.onsdigital.generator.TaxonomyGenerator

# Now build the JAR:
mvn process-resources
