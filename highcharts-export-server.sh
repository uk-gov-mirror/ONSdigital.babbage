#!/bin/bash

if [ ! -d highcharts ]
   then
    echo "Downloading highchart export server"
    git clone --depth 1 -b v4.1.6 https://github.com/highslide-software/highcharts.com.git highcharts
   else
       echo "Highcharts already available. will not download"
fi

export EXPORT_SERVER_DIR="highcharts/exporting-server/java/highcharts-export"
export EXPORT_SERVER_WEB="highcharts-export-web"
export CWD=`pwd`
export JAVA_OPTS="-Xrunjdwp:transport=dt_socket,address=9000,server=y,suspend=n"

cd $EXPORT_SERVER_DIR && \
mvn  clean install && \
cd $EXPORT_SERVER_WEB && \
echo -e "\nexec = $CWD/src/main/web/node_modules/phantomjs/bin/phantomjs"  >> src/main/webapp/WEB-INF/spring/app-convert.properties

if [ $? -eq 0 ]
    then
      echo "Everything alright, starting the server"
    else
       echo "Something went wrong, server will not be started"
       exit 1
 fi

cd $CWD
mvn -f "$EXPORT_SERVER_DIR/$EXPORT_SERVER_WEB/pom.xml" -Djetty.port=9999 -Dlog4j.logger.exporter=DEBUG jetty:run > export-server.log 2>&1&
exportserverpid=$!
echo "export server pid: $exportserverpid"
