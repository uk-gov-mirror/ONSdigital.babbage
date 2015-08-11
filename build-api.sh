#!/bin/bash

mvn clean compile dependency:copy-dependencies

 if [ $? -eq 0 ]
    then
      echo "Successfully compiled"
    else
       echo "Compilation failed"
       exit 1
 fi

# Now build the JAR:
mvn process-resources
