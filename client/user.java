package client;

import java.io.Serializable;

public class user implements Serializable {
	/**
	 * 
	 */
	
	//session object class to hold different information
	private static final long serialVersionUID = 1L;
	private String uname;
	private String pass;
	private String fname;
	private int messId;
	private int isAuth;
	private String chkSum;
			
	public int getIsAuth() {
		return isAuth;
	}

	public void setIsAuth(int isAuth) {
		this.isAuth = isAuth;
	}
	public String getChkSum() {
		return chkSum;
	}
	public void setChkSum(String chkSum) {
		this.chkSum = chkSum;
	}
	
	public int getMessId() {
		return messId;
	}
	public void setMessId(int messId) {
		this.messId = messId;
	}
	public String getFname() {
		return fname;
	}
	public void setFname(String fname) {
		this.fname = fname;
	}
	public String getUname() {
		return uname;
	}
	public void setUname(String uname) {
		this.uname = uname;
	}
	public String getPass() {
		return pass;
	}
	public void setPass(String pass) {
		this.pass = pass;
	}
	
}//user
