package client;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Scanner;

public class clientMenu {
	
	Scanner input = new Scanner(System.in);
	user user;
	private Socket socket = null; 
    private ObjectInputStream  in = null; 
    private ObjectOutputStream out = null;
	private FileOutputStream fout;
	private MessageDigest md;
	private InputStream fin;
	int val;
	int attempt = 0;
	
	clientMenu(int port) {
		try {
			//connect to server
			socket = new Socket("10.234.136.55", port);
			System.out.println("Client is Connected...");
		
			// to write object output to stream
            out = new ObjectOutputStream(socket.getOutputStream()); 
			// to read object input from stream
            in  = new ObjectInputStream(socket.getInputStream()); 
    
            //create session object to communicate with server
            user = new user();
        }
	    catch (Exception e) {
			e.printStackTrace();
		} 
	}//constructor
	
	
	public int showMenu() {
		
			//to take choice form the user
			int val = 0;
			
			do{
				System.out.println("\n*****Welcome to File Transfer Application*****");
				System.out.println("\nEnter the option from below: ");
				System.out.println("1. Login");
				System.out.println("2. Register");
				System.out.println("3. Exit");
				System.out.println("Please enter your choice: ");
				
				val = input.nextInt();
				input.nextLine();
			}while(val <= 1 && val>=3);
			
			return val;
	}//showMenu
	
	public void clientListen(){
		
		try {
			
			//read the server response from object input stream
			while((user = (user) in.readObject()).getMessId() != 3){
				//System.out.println("Read: " + user.getMessId());
				
				switch (user.getMessId()) {
					case 11://login successful and ask for file
						System.out.println("Login successful!!");
						sendFile(user);
						break;
					
					case -11://login unsuccessful
						System.out.println("Incorrect username or password!! Please enter correct credentials.");
						break;
		
					case 13://receive file
						receiveFile();
						break;
						
					case -13://file not present
						System.out.println("\nRequested file not present!! Closing the connection.");
						user.setMessId(3);
						break;
					
					case 22://registraton successful
						System.out.println("User registered successfully!!");
						break;
					
					case -22://username exists
						System.out.println("Username exists!! Please select other name.");
						break;
				}// switch
				
				//show menu if not asked for file transfer			
				if(user.getMessId() != 12 && user.getMessId() != 14 && user.getMessId() != 3){
					switch(val = showMenu()){
						case 1:
							login();
							break;
						case 2:
							register();
							break;
						case 3://exit
							user.setMessId(3);
							break;
							
						default:
							System.out.println("Enter valid option!!");
					}
				}
				
				//System.out.println("msg: " +user.getMessId());	
				
				//write object to server's stream
				out.writeObject(user);
				
				//exit loop and close socket based on message ID
				if(user.getMessId() == 3){
					Thread.sleep(1000);
					break;
				}
				
			}//while	
			close_client();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}//clientListen
	
	public void login() {
		String uname, pass;
		
		//get username and passowrd and set details in session object for login
		System.out.println("Enter the user name:");
		uname = input.nextLine();
		
		System.out.println("Enter the password:");
		pass = input.nextLine();
		
		user.setIsAuth(0);
		user.setUname(uname);
		user.setPass(pass);
		user.setMessId(1);

	}//login

	public void register() {
		String uname, pass;
		
		//get username and passowrd and set details in session object for registration
		System.out.println("Enter the user name:");
		uname = input.nextLine();
		
		System.out.println("Enter the password:");
		pass = input.nextLine();
		
		user.setUname(uname);
		user.setPass(pass);
		user.setMessId(2);
			
	}//register

	public void close_client(){
		
		//close client socket
		try {
			out.close();
			in.close();
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}//close_client

	public void sendFile(user user){
		String fName;
		
		//get file request and set details in session object 
		System.out.println("Enter the file name:");
		fName = input.nextLine();
		
		user.setMessId(12);
		user.setFname(fName);
		
	}//sendFile
	
	public String checkSum(String fname){
			
		//calculate the checksum of recieved file using MD5
		try {
			md = MessageDigest.getInstance("MD5");
			fin = new FileInputStream(fname);
			byte[] buffByte = new byte[1024];
	        int readByte;
	        
	        while ((readByte = fin.read(buffByte)) != -1) {
	            md.update(buffByte, 0, readByte);
	        }
	        fin.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// convert bytes to hex
	    StringBuilder chksum = new StringBuilder();
	    for (byte b : md.digest()) {
	      	chksum.append(String.format("%02x", b));
	    }
	    
	    return chksum.toString();
	}//checkSum
	
	public void receiveFile() {

		try {
			
			//create file output stream to write file
			fout = new FileOutputStream("Docs/receive_"+user.getFname() );
			byte[] buffByte = new byte[1024];
			int readByte;

			//read 1kb of file data every time from stream and write to output file
			while ((readByte = in.read(buffByte)) > 0) {
				//System.out.println(readByte + "b "+buffByte[0]);

				//decrypting the file content (excess 3)
				for(int i=0;i< readByte;i++){
					buffByte[i] = (byte) ((buffByte[i] - 3) );
				}

				//System.out.println(readByte + "a "+buffByte[0]);
				
				//write to file 
				fout.write(buffByte, 0, readByte);
			}

			//calculate checksum of received file
			String calChkSum = checkSum("Docs/receive_"+user.getFname());
			System.out.println("Checksum of received file is : " + calChkSum);

			//checksum of server and client's vervion of file is same
			if(calChkSum.equals(user.getChkSum())){
				System.out.println("\nFile received successfully and saved with name Docs/receive_"+user.getFname());
				user.setMessId(3);
			}
			
			//checksum of server and client's vervion of file is different than ask for retransmission
			else {
				System.out.println("Error in file transmission attempt: " + attempt + "\n");
				attempt++;
				
				//delete corrupted file
				Files.deleteIfExists(Paths.get("Docs/receive_"+user.getFname()));
				
				//if 5 failed transmission than close the connection
				if(attempt == 5){
					System.out.println("\nCorrect File transmission for file " + user.getFname() + 
							" failed 5 times. So, closing the connection!!");
					user.setMessId(3);
				}
				
				//ask for retransmission
				else
					user.setMessId(14);
			}
			
			//close file output stream
			fout.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}//receiveFile
	
}//clientMenu
