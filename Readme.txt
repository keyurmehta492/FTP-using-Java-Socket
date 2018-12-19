Assignment 2: Keyur Kirti Mehta

Main classes are as follows:
client/user.java
server/server.java
server/ftpServer.java
client/client.java
client/clientMenu.java

Few files are kept under Docs folder which can be transmitted. 
The file presents are 53700-1.ppt,Area51.txt, Gandhi.txt, Story.txt, temp1.txt
Any more file can be placed in this folder to transfer.
=================================================
makefile will compile all the java files and create respective class files

Below are the steps to execute the application:

1. Login to 'in-csci-rrpc01.cs.iupui.edu' (10.234.136.55) machine of CSCI to execute the server using valid credentials.
	a. go to assignment folder. In this case it will be MehtaA2 using command 'cd MehtaA2'
	b. complie and create corresponding class files using command 'make'
	c. to execute the server use 'java server/server'
*Run server on rrpc01 only as ip address is hardcoded. Socket port is 9001

2. Login to any machine of CSCI to execute the client using valid credentials.
	a. go to assignment folder. In this case it will be MehtaA2 using command 'cd MehtaA2'
	b. to execute client use 'java client/client'
=================================================
Execution:
Once client is started, it will show menu as below
1. Login
2. Register
3. Exit

a) Register first by pressing 2 and entering username and password. If username is unoccipuied, then user will be registered else
appropriate message will be displayed. 
*This assignment doesn't use persistent storage. So once server is stopped, all the registered users' credentails will be vanished.

b) Once registered, select 1 to login, If credentails are entered correctly, user will be logged in to system or else
appropriate message will be displayed. Once logged in, user will prompt to enter file name.

c) Enter the file name along with the extension. (Do not give folder name) Eg. Story.txt
If file is not present, message is displayed and connection is closed.
If file is transmitted correctly, then new file name is displaed (receive_<filename>) and connection is closed. File is placed in Docs folder.
If checksum is not matched with server's version then client will ask for automatic retransmission. And file is deleted.
If file is received 5 time incorrectly, then message will be displayed and connection is closed.

Transmission is encryped using excess 3 encryption. And checksum is calculated using MD5 algorithm.

d) if option 3 is selected, the connection is closed.

e) To stop the server, press ctrl + c

Extra Credit:
Assignment is designed handle multiple clients connection simultaneously. Run the client on different machines and server 
will create new thread for each connected client and file transfer takes place. 