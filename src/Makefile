JFLAGS =
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	proj1/GrepProcess.java \
	proj1/m2wMessage.java \
	proj1/Main.java \
	proj1/MasterServer.java \
	proj1/MigratableProcess.java \
	proj1/NumberIn.java \
	proj1/NumberOut.java \
	proj1/NumberStdOut.java \
	proj1/ProcessController.java \
	proj1/State.java \
	proj1/TransactionalFileInputStream.java \
	proj1/TransactionalFileOutputStream.java \
	proj1/w2mMessage.java \
	proj1/WorkerHealthChecker.java \
	proj1/WorkerServer.java \

RMCLASSES = \
	proj1/GrepProcess.class \
	proj1/m2wMessage.class \
	proj1/Main.class \
	proj1/MasterServer.class \
	proj1/MigratableProcess.class \
	proj1/NumberIn.class \
	proj1/NumberOut.class \
	proj1/NumberStdOut.class \
	proj1/ProcessController.class \
	proj1/State.class \
	proj1/TransactionalFileInputStream.class \
	proj1/TransactionalFileOutputStream.class \
	proj1/w2mMessage.class \
	proj1/WorkerHealthChecker.class \
	proj1/WorkerServer.class \


default: classes

classes: $(CLASSES:.java=.class)

clean:	
	rm $(RMCLASSES)
