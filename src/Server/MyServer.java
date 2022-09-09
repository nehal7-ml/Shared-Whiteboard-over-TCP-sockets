package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import WhiteBoard.ReadData;

public class MyServer {

	private ArrayList<Meeting> HostedMeetings = new ArrayList<Meeting>();
	private ArrayList<String> ClientsName = new ArrayList<String>();

	public MyServer() {

	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException, ParseException, InterruptedException {

		MyServer Serv = new MyServer();

		int soc = 3333;
		int id = 0;

		JSONObject req;
		JSONObject rep;
		String m = "{\"Message\":\"Response\"}";
		String cmd = "";
		JSONParser jp = new JSONParser();
		rep = (JSONObject) jp.parse(m);

		@SuppressWarnings("resource")
		ServerSocket ss = new ServerSocket(soc);
		System.out.println("server start");

		while (true) {
			Socket csoc = ss.accept();
			System.out.println("new connection");
			DataInputStream in = new DataInputStream(csoc.getInputStream());
			DataOutputStream out = new DataOutputStream(csoc.getOutputStream());
			ReadData r = new ReadData(in);
			Random random = new Random();
			System.out.println(csoc.getPort());

			cmd = r.read();
			req = (JSONObject) jp.parse(cmd);
			String username = req.get("From").toString();
			System.out.println(req.toString());
			rep.put("To", username);

			if (req.get("Type").equals("CREATE")) {
				Meeting obj = new Meeting(id, username);
				id++;
				ServerRequestThread2 srvt =new ServerRequestThread2(out, obj);
				obj.setClient(csoc, srvt, r, username);
				Serv.HostedMeetings.add(obj);
				Serv.ClientsName.add(username);
				System.out.println("meeting start");
				rep.put("Type", "CREATE");
				rep.put("Status", "SUCCESS");
				rep.put("Id",Serv.HostedMeetings.size()-1);
				out.write((rep.toString() + "\n").getBytes("UTF-8"));
				out.flush();
				obj.addClient();
				ClientThread CLT = new ClientThread(csoc, obj, 0);
				obj.setCThreads(CLT);
				
				CLT.start();
				//obj.getHb().start();
				continue;

			} else if (req.get("Type").equals("JOIN")) {
				System.out.println("JOin req");
				Integer meeting_id = Integer.parseInt(req.get("Data").toString());
				Meeting obj = Serv.HostedMeetings.get(meeting_id);
				System.out.println("meeeting no.: " + meeting_id);
				Integer Id = random.nextInt();
				req.put("Token", Id);
				obj.addjoinReq(csoc, Id);
				obj.getClientsOut().get(0).SendToUser(req);
				
				
				//ServerRequestThread srt = new ServerRequestThread(obj.getClientsOut().get(0), req);
				//srt.start();

			}

		}

	}

}
