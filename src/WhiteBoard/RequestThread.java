package WhiteBoard;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import java.util.Base64;
import java.util.Base64.Encoder;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import Server.ServerRequestThread;

public class RequestThread extends Thread {

	private DataOutputStream dout;
	private JSONObject req;
	private JSONObject data;
	private String type;
	private String username;
    private String Text;
	private int sendTO;
	private int color;

	public RequestThread(DataOutputStream d, int[] x, int[] y, String s,String uname,int col) throws ParseException {
		this.dout = d;
		this.type = s;
		this.username=uname;
		this.color=col;
		String m = "{\"Message\":\"Request\"}";
		String xs = String.valueOf(x[0]);
		String ys = String.valueOf(y[0]);
		String xe = String.valueOf(x[1]);
		String ye = String.valueOf(y[1]);
		String dt = "{\"x\":[" + xs + "," + xe + "],\"y\":[" + ys + "," + ye + "]}";
		JSONParser jp = new JSONParser();
		this.req = (JSONObject) jp.parse(m);
		this.data = (JSONObject) jp.parse(dt);
		this.req.put("From",this.username);
		this.req.put("Color",this.color);

	}
	
	
	public RequestThread(DataOutputStream d, int x, int y, String s,String uname) throws ParseException {
		this.dout = d;
		this.type = s;
		this.username=uname;
		String m = "{\"Message\":\"Request\"}";
		String dt = "{\"x\":" + x + ",\"y\":" + y + "}";
		JSONParser jp = new JSONParser();
		this.req = (JSONObject) jp.parse(m);
		this.data = (JSONObject) jp.parse(dt);
		this.req.put("From",this.username);
		this.req.put("Color",this.color);

	}
	

	public RequestThread(DataOutputStream d, String s, String uname) throws ParseException {
		this.dout = d;
		this.type = s;
		this.username=uname;

		String m = "{\"Message\":\"Request\"}";
		JSONParser jp = new JSONParser();
		this.req = (JSONObject) jp.parse(m);
		this.req.put("From",this.username);


	}
	@SuppressWarnings("unchecked")
	public RequestThread(DataOutputStream d, String s, String uname,BufferedImage bi) throws ParseException, IOException {
		this.dout = d;
		this.type = s;
		this.username=uname;
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write( bi, "png", baos );
		baos.flush();
		byte[] imageInByte = baos.toByteArray();
		baos.close();
        String imageString = Base64.getEncoder().encodeToString(imageInByte);

		String m = "{\"Message\":\"Response\"}";
		JSONParser jp = new JSONParser();
		this.req = (JSONObject) jp.parse(m);
		this.req.put("Data", imageString);
		this.req.put("From",this.username);
	}
	public RequestThread(DataOutputStream d, String s, String uname, int D) throws ParseException {
		this.dout = d;
		this.type = s;
		this.username=uname;
		String m = "{\"Message\":\"Request\"}";
		JSONParser jp = new JSONParser();
		this.req = (JSONObject) jp.parse(m);
		this.req.put("Data", D);
		this.req.put("From",this.username);
	}
	
	public RequestThread(DataOutputStream d, String s, String uname, int D,String Msg) throws ParseException {
		this.dout = d;
		this.type = s;
		this.username=uname;
		this.Text=Msg;
		String m = "{\"Message\":\"Request\"}";
		JSONParser jp = new JSONParser();
		this.req = (JSONObject) jp.parse(m);
		this.req.put("Data", D);
		this.req.put("From",this.username);
		this.req.put("To", "SERVER");
	}
	
	

	public RequestThread(DataOutputStream d, int x, int y, String s, String username2, String dtext,int col) throws ParseException {
		this.dout = d;
		this.type = s;
		this.username=username2;
		this.color=col;
		String m = "{\"Message\":\"Request\"}";
		String dt = "{\"x\":" + x + ",\"y\":" + y + ",\"S\":\""+dtext+"\"}";
		JSONParser jp = new JSONParser();
		this.req = (JSONObject) jp.parse(m);
		this.data = (JSONObject) jp.parse(dt);
		this.req.put("From",this.username);
		this.req.put("Color",this.color);
		
		// TODO Auto-generated constructor stub
	}


	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		if (this.type.equals("DRAW_PATH")) {
			req.put("Type", "DRAW_PATH");
			req.put("Data", this.data);
			req.put("To", "ALL");

		} else if (this.type.equals("DRAW_RECT")) {
			req.put("Type", "DRAW_RECT");
			req.put("Data", this.data);
			req.put("To", "ALL");

		}else if(this.type.equals("DRAW_CIRCLE")) {
			req.put("Type", "DRAW_CIRCLE");
			req.put("Data", this.data);
			req.put("To", "ALL");
			
		}

		else if (this.type.equals("DRAW_LINE")) {
			req.put("Type", "DRAW_LINE");
			req.put("Data", this.data);
			System.out.println("add req");
			req.put("To", "ALL");

		} else if (this.type.equals("CLEAR_ARRAY")) {
			req.put("Type", "CLEAR_ARRAY");
			System.out.println("add req");
			req.put("To", "ALL");

		}   else if (this.type.equals("CREATE")) {
			req.put("Type", "CREATE");
			req.put("To", "SERVER");

		}
		else if (this.type.equals("GET_LIST")) {
			req.put("Type", "GET_LIST");
			req.put("To", "SERVER");

		}else if (this.type.equals("JOIN")) {
			req.put("Type", "JOIN");
			req.put("From", username);
			req.put("To", "SERVER");

		}else if (this.type.equals("DRAW_TEXT")) {
			req.put("Type", "DRAW_TEXT");
			req.put("Data", this.data);
			req.put("To", "Server");
		}else if (this.type.equals("RAISEHAND")) {
			req.put("Type", "RAISEHAND");
			req.put("To", "SERVER");
		}else if (this.type.equals("MUTE")) {
			req.put("Type", "MUTE");
			req.put("To", "SERVER");
		}else if (this.type.equals("TEXT")) {
			req.put("Type", "TEXT");
			req.put("Text",Text);
		}else if (this.type.equals("LOWERHAND")) {
			req.put("Type", "LOWERHAND");
			req.put("To", "SERVER");
		}else if (this.type.equals("UNMUTE")) {
			req.put("Type", "UNMUTE");
			req.put("To", "SERVER");
		}else if (this.type.equals("UNMUTEU")) {
			req.put("Type", "UNMUTEU");
			req.put("To", "SERVER");
		}else if (this.type.equals("MUTEU")) {
			req.put("Type", "MUTEU");
			req.put("To", "SERVER");
		}else if (this.type.equals("ERASE")) {
			req.put("Type", "ERASE");
			req.put("Data",this.data);
			req.put("To", "SERVER");
		}else if (this.type.equals("BOARD")) {
			req.put("Type", "BOARD");
			req.put("To", "SERVER");
		}else if (this.type.equals("DISCONNECT")) {
			req.put("Type", "DISCONNECT");
			req.put("To", "SERVER");
		}

		try {
			// System.out.println(req.toString());
			synchronized (dout) {
				dout.write((req.toString() + "\n").getBytes("UTF-8"));
				dout.flush();
			}
			System.out.println(req.toString()+":::::Sending::::");

		}catch (SocketException e) {
			
			JOptionPane.showMessageDialog(null,"Disconnected");
			
		}
		
		
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
