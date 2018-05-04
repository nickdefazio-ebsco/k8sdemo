#!/bin/sh
echo '==================='
echo 'Running : java ' $JVM_ARGS '-jar app.jar'
echo '==================='

exec java -jar app.jar
