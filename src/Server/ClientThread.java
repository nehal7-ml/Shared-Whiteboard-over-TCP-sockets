package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import WhiteBoard.ReadData;

public class ClientThread extends Thread {
	private Socket cs;
	private Meeting meeting;
	private JSONParser jp = new JSONParser();
	private DataOutputStream dout; // manager outputStream
	private DataOutputStream cout; // participant outputSstream
	private ReadData r;
	private JSONObject rep;
	private int user_id;

	public ClientThread(Socket c, Meeting me, int id) throws IOException, ParseException {
		this.cs = c;
		this.meeting = me;
		this.user_id = id;
		DataInputStream din = new DataInputStream(this.cs.getInputStream());
		this.cout = new DataOutputStream(this.cs.getOutputStream());
		String m = "{\"message\":\"response\"}";
		this.rep = (JSONObject) jp.parse(m);
		this.r = new ReadData(din);
	}

	public DataOutputStream getCout() {
		return cout;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		System.out.println("client thread started");
		JSONObject req;
		String cmd = "";

		while (!this.cs.isClosed()) {
			ArrayList<ServerRequestThread2> Clist = this.meeting.getClientsOut();
			try {
				synchronized (r) {
					cmd = this.r.read();
				}
				req = (JSONObject) jp.parse(cmd);
				System.out.println(req.toString() + ": recieved command");

				if (req.get("Message").equals("Request")) {

					if (req.get("Type").equals("UNMUTEU")) {

						meeting.getClientsOut().get(Integer.parseInt(req.get("Data").toString())).SendToUser(req);

						// ServerRequestThread srtc = new ServerRequestThread(
						// Clist.get(Integer.parseInt(req.get("Data").toString())), req);
						// srtc.start();
					} else if (req.get("Type").equals("MUTEU")) {

						meeting.getClientsOut().get(Integer.parseInt(req.get("Data").toString())).SendToUser(req);

						// ServerRequestThread srtc = new ServerRequestThread(
						// Clist.get(Integer.parseInt(req.get("Data").toString())), req);
						// srtc.start();

					}

					else if (req.get("Type").equals("TEXT")) {
						if (Integer.parseInt(req.get("Data").toString())==99) {
							for (int i = 0; i < Clist.size(); i++) {
								if (i != user_id) {
									req.put("To",meeting.getClientsName().get(i));
									meeting.getClientsOut().get(i).SendToUser(req);
									// ServerRequestThread srtc = new ServerRequestThread(Clist.get(i), req);
									// srtc.start();
								}
							}

						} else {
							System.out.println("send personal");
							int rid = Integer.parseInt(req.get("Data").toString()); //    send message to
							req.put("To",meeting.getClientsName().get(rid));
							meeting.getClientsOut().get(rid).SendToUser(req);
							// ServerRequestThread srtc = new ServerRequestThread(
							// Clist.get(Integer.parseInt(req.get("Data").toString())), req);
							// srtc.start();

						}

					} else if (req.get("Type").equals("DISCONNECT")) {

						System.out.println("closing socket");
						this.meeting.removeClient(user_id);
						this.cs.close();

					} else {

						for (int i = 0; i < Clist.size(); i++) {
							if (i != user_id) {
								
								meeting.getClientsOut().get(i).SendToUser(req);
								
								//ServerRequestThread srtc = new ServerRequestThread(Clist.get(i), req);
								//srtc.start();
							}
						}
					}

				}

				else {
					if (req.get("Type").equals("JOIN")) {

						if (req.get("Status").equals("SUCCESS")) {

							int Tocken = Integer.parseInt(req.get("Token").toString());
							int index = this.meeting.getJoinReqId().indexOf(Tocken);
							System.out.println(index);
							Socket csoc = this.meeting.getJoinReqSocks().get(index);

							System.out.println(csoc.getPort());

							DataInputStream in = new DataInputStream(csoc.getInputStream());
							DataOutputStream out = new DataOutputStream(csoc.getOutputStream());

							System.out.println(": host rccpt");
							ServerRequestThread2 srv = new ServerRequestThread2(out, this.meeting);
							this.meeting.setClient(csoc, srv, new ReadData(in), req.get("To").toString());
							ClientThread obj = new ClientThread(csoc, this.meeting,
									this.meeting.getClientsName().size() - 1);
							obj.start();
							System.out.println("Client Added");
							rep.put("Type", "JOIN");
							rep.put("Status", "SUCCESS");
							rep.put("Data", this.meeting.getClientsName().size() - 1);
							System.out.println(Clist.size());

							meeting.getClientsOut().get(Clist.size() - 1).SendToUser(rep);

							// ServerRequestThread srtc = new ServerRequestThread(Clist.get(Clist.size() -
							// 1), rep);
							// srtc.setPriority(10);
							// srtc.start();
							this.meeting.addClient();
							this.meeting.setCThreads(obj);

						}

					} else if (req.get("Type").equals("BOARD")) {
						this.meeting.setBi(req.get("Data").toString());

					}

				}

			} catch (SocketException e) {
				try {
					this.meeting.removeClient(user_id);
					this.cs.close();
				} catch (ParseException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Thread.currentThread().interrupt();
				return;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				System.out.println("parse Error");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}
