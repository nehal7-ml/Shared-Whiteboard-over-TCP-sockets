package Server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketException;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class  ServerRequestThread2  {

	private Meeting m;
	private DataOutputStream dout;
	private JSONObject req;

	public ServerRequestThread2(DataOutputStream d, Meeting m) throws ParseException {
		this.dout = d;
		// TODO Auto-generated constructor stub
	}
	
	
	public synchronized void SendToUser(JSONObject req) {
		
		synchronized (this) {
			try {
				dout.write((req.toString() +"\n").getBytes("UTF-8"));
				dout.flush();
				System.out.println(req.toString() + ":::sending::::");

			} catch (SocketException e) {
				/*

				try {
					//this.m.removeClient(userid);
				} catch (ParseException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}*/

			} catch (IOException e) {

			}

		}
		
		
		
		
	}
	
	
	
	

}
