#web:    java $JAVA_OPTS -jar target/*-jar-with-dependencies.jar
worker:  java -Xmx1024m -cp "target/classes:target/dependency/*"  com.github.onsdigital.generator.ContentGenerator
web:     java $JAVA_OPTS -Drestolino.files="target/web" -Drestolino.classes="target/classes" -Drestolino.pack
         ageprefix=com.github.onsdigital -cp "target/dependency/*" com.github.davidcarboni.restolino.Main

