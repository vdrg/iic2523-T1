#!/bin/bash
CLASSPATH=$(readlink -f build/classes)

(cd $CLASSPATH && rmiregistry &)
java -classpath $CLASSPATH -Djava.rmi.server.codebase=file:${CLASSPATH}/ org.hooli.Master


