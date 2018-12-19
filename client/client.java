package client;

public class client {
	
	public static void main(String[] args) {
		
		//Create client socket
		clientMenu cm = new clientMenu(9001);
				
		cm.clientListen();
		
	}//main
}

