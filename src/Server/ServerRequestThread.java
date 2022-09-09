package Server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketException;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class ServerRequestThread extends Thread {

	private Meeting m;
	private int userid;
	private DataOutputStream dout;
	private JSONObject req;

	public ServerRequestThread(DataOutputStream d, JSONObject r) throws ParseException {
		this.dout = d;
		this.req = r;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {

		synchronized (dout) {
			try {
				dout.write((req.toString() + "\n").getBytes("UTF-8"));
				dout.flush();
				System.out.println(req.toString() + ":::sending::::");

			} catch (SocketException e) {

				try {
					this.m.removeClient(userid);
				} catch (ParseException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			} catch (IOException e) {

			}

		}

	}

}
