#!/bin/bash
CLASSPATH=$(readlink -f build/classes)

java -classpath $CLASSPATH org.hooli.Worker $1 $2


