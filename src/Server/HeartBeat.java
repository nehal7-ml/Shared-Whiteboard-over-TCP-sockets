package Server;

import java.io.DataOutputStream;
import java.net.SocketException;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class HeartBeat extends Thread{
	private ArrayList<DataOutputStream> ClientsOut = new ArrayList<DataOutputStream>();
	
	public HeartBeat(ArrayList<DataOutputStream> Out) {
		
		this.ClientsOut = Out;
		
		// TODO Auto-generated constructor stub
	}
	
	public void setClientsOut(ArrayList<DataOutputStream> clientsOut) {
		this.ClientsOut = clientsOut;
	}
	
	
	@SuppressWarnings("unchecked")
	public void run() {
		String cmd="{\"Message\":\"Request\"}";
		JSONParser jp = new JSONParser();
		JSONObject req;
		
		try {
			Thread.sleep(1500);
			req = (JSONObject) jp.parse(cmd);
			req. put("Type","HEART");
			req.put("From", "Server");
			req.put("To","All");
			while(true) {
					for (DataOutputStream dataOutputStream : ClientsOut) {			
						ServerRequestThread srt = new ServerRequestThread(dataOutputStream, req);		
				}			
				Thread.sleep(10000);
			}
		}
		catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
		
			
			
			
			
			
		
		

	

}
