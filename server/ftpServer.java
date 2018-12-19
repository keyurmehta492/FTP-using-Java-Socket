package server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Random;

import client.user;

public class ftpServer extends Thread implements Serializable {

	
	private static final long serialVersionUID = 1L;
	
	private Socket socket = null;
	private ObjectInputStream in = null;
	private ObjectOutputStream out = null;
	user user = new user();;
	HashMap<String, String> userList = null;
	int res;

	ftpServer(HashMap<String, String> userList) {
		//get user credential list
		this.userList = userList;
		
	}// constructor

	public void startStream(Socket socket){
		try {
		
		//create stream for server thread
		this.socket = socket;
		out = new ObjectOutputStream(this.socket.getOutputStream());
		in = new ObjectInputStream(this.socket.getInputStream());
		
		//send inital message to client 
		user.setMessId(0);
		out.writeObject(user);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}//startStream
	
	@Override
	public void run() {
		try {
			 while((user = (user) in.readObject()).getMessId() != 3){
			
					//System.out.println("Read:" + user.getMessId());

					switch (user.getMessId()) {
					case 1: // login
						res = loginUser(user.getUname(), user.getPass());
						if (res == 11)
							user.setIsAuth(1);
						user.setMessId(res);
						break;

					case 2: // register
						res = registerUser(user.getUname(), user.getPass());
						user.setMessId(res);
						break;

					case 12: // send file
						if (user.getIsAuth() == 1) {
							System.out.println("server file is" + user.getFname());
							sendFile("Docs/" + user.getFname());
						} else
							System.out.println("Only authenticate user is allowed to request file!");
						break;

					case 14: // retransmit the file
						if (user.getIsAuth() == 1) {
							System.out.println("server file is" + user.getFname());
							sendFile("Docs/" + user.getFname());
						} else
							System.out.println("Only authenticate user is allowed to request file!");
						break;

					}// switch
					
					//System.out.println("msg:" + user.getMessId());
					
					//write object to client's stream
					out.writeObject(user);
					
			} // while
			System.out.println("Closing the client connection...");
			
			//close the client socket
			close_server();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}// run

	public int registerUser(String uname, String pass) {

		//check username exists or not. if not register the user.
		if (userList.containsKey(uname)) {
			System.out.println("Username already exists!!");
			return -22;
		} else {
			userList.put(uname, pass);
			System.out.println("User registered successfully!!");
			return 22;
		}
	}// registerUser

	int loginUser(String uname, String pass) {

		//check client credentials
		if (userList.containsKey(uname)) {
			if (userList.get(uname).equals(pass)) {
				System.out.println("User's credentails are valid!!Logging In..");
				return 11;
			} else {
				System.out.println("Incorrect Password");
				return -11;
			}
		} else {
			System.out.println("Incorrect Username");
			return -11;
		}
	}// loginUser

	public String checkSum(String fname) {

		MessageDigest md = null;
		InputStream fin;

		//calculate the checksum of transmitted file using MD5
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

	public void sendFile(String Fname) throws IOException {
		int readByte;
		int flag = 0;
		Random rand = new Random();
		FileInputStream fin = null;
		
		try {

			fin = new FileInputStream(Fname);
			byte[] buffByte = new byte[1024];

			// calculate checksum of the file
			String chkSum = checkSum(Fname);
			System.out.println("The checksum of the transmitted file is: " + chkSum);
			user.setChkSum(chkSum);
			user.setMessId(13);
			out.writeObject(user);

			while ((readByte = fin.read(buffByte)) > 0) {
				// System.out.println(readByte + "b "+buffByte[0]);

				// encrypt the content of the file (excess 3)
				for (int i = 0; i < readByte; i++) {
					buffByte[i] = (byte) ((buffByte[i] + 3) );
					
				}

				// Byzantine Behavior of the server 
				// change last byte of 1st iteration to 0 based on probability of less htan 0.4
				if(flag == 0)
					if(rand.nextInt(10) < 4 ){
						System.out.println("Server's Byzantine behavior");
						buffByte[readByte - 1] = 0;
					}
				// System.out.println(readByte + "a "+buffByte[0]);
				
				//write to client's stream
				out.write(buffByte, 0, readByte);
				flag = 1;
			}
						
			System.out.println("File " + user.getFname() + " transferred successfully!!");
			
			//close the file stream
			fin.close();

		} catch (FileNotFoundException e) {
			user.setMessId(-13);
			System.out.println("Requested file " + Fname + " not present.");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}//sendFile

	public void close_server() {
		try {
			in.close();
			out.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//close_server

}//ftpServer
