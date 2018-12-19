package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class server {

	private static ServerSocket serverSocket;
	private static HashMap<String,String> userList = new HashMap<String,String>();
	
	public static void main(String[] args) {
								
		try {
			
			//create server socket
			serverSocket = new ServerSocket(9001);
			Socket socket;
			System.out.println("FTP Server started and waiting for client to connect..");
					
			while(true){
				
				//accept client request
				socket = serverSocket.accept();
				System.out.println("Client is Connected...");
				
				//create new thread and client stream for each accepted connection
				ftpServer ser = new ftpServer(userList);
				ser.startStream(socket);
				ser.start();
			}
		}
		catch(Exception e) {
				e.printStackTrace();
		}
			
	}//main
		
}//server
