#!/bin/bash


if [ -f service.pid ]; then
     OLD_PID=`cat service.pid`
     kill -9 $OLD_PID
     rm -rf service.pid
fi

VERSION="1.0.0-SNAPSHOT"
JNI_LOCATION="/usr/lib/jni"
JAR_FILE="temperature-sensor-and-rest-${VERSION}-fat.jar"

java -jar $JAR_FILE  -conf prod.json > /dev/null 2>&1 &

PID=$!

if [ $? != 0 ]; then
    echo "Process started with errors"
    exit -1
fi

echo $PID > service.pid

echo "Service started, apparently :)"

exit 0