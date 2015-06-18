export EXPORT_SERVER_DIR="highcharts/exporting-server/java/highcharts-export/highcharts-export-web"
export EXPORT_SERVER_POM="highcharts/exporting-server/java/highcharts-export/highcharts-export-web/pom.xml"
export CWD=`pwd`

if [ ! -d highcharts ]
   then
    echo "Downloading highchart export server"
    git clone --depth 1 -b v4.1.6 git@github.com:highslide-software/highcharts.com.git highcharts
   else
       echo "Highcharts already available. will not download"
fi

export JAVA_OPTS="-Xrunjdwp:transport=dt_socket,address=9000,server=y,suspend=n"

cd $EXPORT_SERVER_DIR

mvn  clean install && \
nohup mvn -Djetty.port=9999 -Dlog4j.logger.exporter=DEBUG jetty:run > export-server.log 2>&1&

