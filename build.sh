#!/bin/bash

mvn clean compile dependency:copy-dependencies

 if [ $? -eq 0 ]
    then
      echo "Successfully compiled"
    else
       echo "Compilation failed"
       exit 1
 fi


#Generate content if not generated before
if [ -d src/main/content ]
   then
       echo "Content already generated. Skipping content generation"
       mvn clean compile dependency:copy-dependencies
   else
   	   echo "Content not available, generating now"
       $JAVA_HOME/bin/java -Xmx2048m -cp "target/classes:target/dependency/*" com.github.onsdigital.generator.ContentGenerator
	   if [ $? -eq 0 ]
	   		then
	   			echo "Content successfully generated"
	   		else
	   			echo "Failed generating content"
	   			rm -rf src/main/content
	   			exit 1
   		fi
fi


# Now build the JAR:
mvn process-resources
