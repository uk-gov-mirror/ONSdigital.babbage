#!/bin/bash


###Starts handlebars java server which will render handlebars templates under web/templates/handlebars with accompanying .json files
###e.g. example.handlebars under web/templates/handlebars with example.json under same directory. Or passing a get parameter of data file name
###Access server using http://localhost:6780/exampe.handlebars
###For more info: https://github.com/jknack/handlebars.java#the-handlebarsjava-server


#1- Download Handlebars java server using maven without compiling the code (Actually downloads all dependencies :) )

cd api && \
mvn dependency:copy-dependencies && \
cd ..


#2- Run the server

$JAVA_HOME/bin/java $JAVA_OPTS \
 -jar api/target/dependency/handlebars-proto-2.1.0.jar \
 -dir web/templates/handlebars \
 -suffix .handlebars
