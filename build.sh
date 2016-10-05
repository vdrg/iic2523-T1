#!/bin/bash
mkdir -p build/classes
CLASSPATH=$(readlink -f build/classes)
echo "Created $CLASSPATH"
(cd src/org/hooli && javac -d $CLASSPATH MasterInterface.java WorkerInterface.java Master.java Worker.java RandomExtended.java)

