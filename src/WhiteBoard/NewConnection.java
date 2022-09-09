package WhiteBoard;
import java.net.Socket;

import java.io.*;

 
public class NewConnection{
	
	private String hostadress ;
    private int soc ;
    private Socket s ; 
    private DataInputStream din;
    private DataOutputStream dout;
    
    
    public NewConnection(String x,int soc) {
    	
    	this.soc=soc;
    	this.hostadress=x;
    	
    	try {
    		this.s = new Socket(x,soc);
    		this.dout=new DataOutputStream(s.getOutputStream()); 
    		this.din=new DataInputStream(s.getInputStream());
    	}
    	
    	catch (Exception e){    		
    		System.out.print("Can't connect");   		
    	}
    	
    	
    }
    
   public Socket getSocket() {
	   
	   return this.s;
		// TODO Auto-generated method stub

	}
    
    public void closeConnection() throws Exception {
    	this.dout.close();  
        this.s.close(); 
    }
    
    public DataInputStream getdin() {
    	return this.din;
    }
    
    public DataOutputStream getdout() {
    	return this.dout;
    }


}
