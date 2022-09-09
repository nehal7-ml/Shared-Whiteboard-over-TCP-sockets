package Server;

import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import WhiteBoard.ReadData;

public class Meeting {
	private ArrayList<Socket> ClientSocks = new ArrayList<Socket>();
	private ArrayList<Socket> joinReqSocks = new ArrayList<Socket>();
	private ArrayList<Integer> joinReqId = new ArrayList<Integer>();
	private ArrayList<ServerRequestThread2> ClientsOut = new ArrayList<ServerRequestThread2>();
	//private ArrayList<DataOutputStream> ClientsOut = new ArrayList<DataOutputStream>();
	private ArrayList<ReadData> ClientsIn = new ArrayList<ReadData>();
	private ArrayList<String> ClientsName = new ArrayList<String>();
	private ArrayList<ClientThread> CThreads = new ArrayList<ClientThread>();
	private String HostUser;
	private HeartBeat hb;
	private String bi;
	private Boolean gotImage;
	private int id;

	public Meeting(int i, String s) {
		this.id = i;
		this.HostUser = s;
		//this.hb = new HeartBeat(this.ClientsOut);

	}

	public HeartBeat getHb() {
		return hb;
	}

	@SuppressWarnings("unchecked")
	public void setBi(String b) throws ParseException {
		this.bi = b;
		this.gotImage = true;
		int l = ClientsOut.size();
		String m = "{\"Message\":\"Request\"}";
		JSONParser jp = new JSONParser();
		JSONObject req = (JSONObject) jp.parse(m);
		req.put("Type", "UPDATE_BOARD");
		req.put("Data", this.bi);
		req.put("From", "Server");
		req.put("To", "All");
		
		this.ClientsOut.get(l-1).SendToUser(req);
		//ServerRequestThread srt = new ServerRequestThread(this.ClientsOut.get(l-1), req);
		//srt.setPriority(1);
		//srt.start();

	}

	public ArrayList<ReadData> getClientsIn() {
		return ClientsIn;
	}

	public ArrayList<ServerRequestThread2> getClientsOut() {
		return this.ClientsOut;
	}

	public ArrayList<String> getClientsName() {
		return ClientsName;
	}

	public String getHostUser() {
		return HostUser;
	}

	public void setClient(Socket soc, ServerRequestThread2 out, ReadData in, String s) {
		ClientsOut.add(out);
		ClientsIn.add(in);
		ClientsName.add(s);
		ClientSocks.add(soc);
		//hb.setClientsOut(this.ClientsOut);
	}

	@SuppressWarnings("unchecked")
	public void addClient() throws ParseException, InterruptedException {
		int l = ClientsOut.size();
		String m = "{\"Message\":\"Request\"}";
		JSONParser jp = new JSONParser();
		JSONObject req = (JSONObject) jp.parse(m);
		req.put("Type", "UPDATE_LIST");
		req.put("Data", this.ClientsName.toString());
		req.put("From", "Server");
		req.put("To", "All");
		for (int i = 0; i < l; i++) {
			
			this.ClientsOut.get(i).SendToUser(req);
			
			/*
			ServerRequestThread srt = new ServerRequestThread(this.ClientsOut.get(i), req);
			srt.setPriority(5);
			srt.start();*/
		}

		if (l > 1) {
			String re = "{\"Message\":\"Request\"}";
			JSONObject Breq = (JSONObject) jp.parse(m);
			Breq.put("Type", "BOARD");
			Breq.put("From", "Server");
			Breq.put("To", "Host");
			this.ClientsOut.get(0).SendToUser(Breq);
			//ServerRequestThread srtc = new ServerRequestThread(this.ClientsOut.get(0), Breq);
			//srtc.setPriority(1);
			//srtc.start();
			this.gotImage = false;
		}

	}

	@SuppressWarnings("unchecked")
	public void removeClient(int i) throws ParseException, IOException, InterruptedException {
		
		System.out.println("removing client");

		if (i != 0) {
			ClientSocks.remove(i);
			ClientsOut.remove(i);
			ClientsIn.remove(i);
			ClientsName.remove(i);
			CThreads.remove(i);
			String m = "{\"Message\":\"Request\"}";
			JSONParser jp = new JSONParser();
			JSONObject req = (JSONObject) jp.parse(m);
			req.put("Type", "IDCHG");
			req.put("From", "Server");
			req.put("To", "All");			
			
			for (int j = i; j < CThreads.size(); j++) {
				CThreads.get(j).setUser_id(j);
				req.put("Data", j);				
				this.ClientsOut.get(j).SendToUser(req);
				//ServerRequestThread srt = new ServerRequestThread(this.ClientsOut.get(j), req);
				//srt.start();
			}
			
			int l = ClientsOut.size();
			String m1 = "{\"Message\":\"Request\"}";
			JSONObject req1 = (JSONObject) jp.parse(m1);
			req1.put("Type", "UPDATE_LIST_REMOVE");
			req1.put("Data", i);
			req1.put("From", "Server");
			req1.put("To", "All");
			for (int c = 0; c < l;c++) {
				
				this.ClientsOut.get(c).SendToUser(req1);
				//ServerRequestThread srt = new ServerRequestThread(this.ClientsOut.get(c), req1);
				//srt.setPriority(1);
				//srt.start();
			}
			
			
		} else {
			String m = "{\"Message\":\"Request\"}";
			JSONParser jp = new JSONParser();
			JSONObject req = (JSONObject) jp.parse(m);
			req.put("Type", "DISCONNECT");
			req.put("From", "Server");
			req.put("To", "All");

			for (int k = 1; k < ClientsName.size(); i++) {	
				this.ClientsOut.get(1).SendToUser(req);
				//ServerRequestThread srt = new ServerRequestThread(this.ClientsOut.get(i), req);
				//srt.start();
				ClientSocks.get(k).close();
			}

		}

	}

	public void addjoinReq(Socket soc, Integer id) {
		this.joinReqSocks.add(soc);
		this.joinReqId.add(id);

	}
	public void setCThreads(ClientThread cThread) {
		this.CThreads.add(cThread);
	}

	public ArrayList<Socket> getJoinReqSocks() {
		return this.joinReqSocks;
	}

	public ArrayList<Integer> getJoinReqId() {
		return this.joinReqId;
	}

}
