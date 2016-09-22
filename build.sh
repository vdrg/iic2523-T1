mkdir -p build/classes
CLASSPATH=$(readlink -f build/classes)

(cd src/org/hooli && javac -d $CLASSPATH MasterInterface.java WorkerInterface.java Master.java Worker.java)

