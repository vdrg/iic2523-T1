mkdir -p build/classes
CLASSPATH=$(readlink -f build/classes)

(cd src/org/hooli && javac -d $CLASSPATH Compute.java Master.java Worker.java)

