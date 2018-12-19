JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
client/user.java\
server/server.java\
server/ftpServer.java\
client/client.java\
client/clientMenu.java\


default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) */*.class