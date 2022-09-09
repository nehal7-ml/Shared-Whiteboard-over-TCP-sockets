package WhiteBoard;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ReadData {
	private DataInputStream in;
	private String msg;
	public ReadData(DataInputStream in) throws IOException {
		this.in = in;
	}

	public String read() throws IOException {
		byte[] messageByte = new byte[100];
		boolean end = false;
		//StringBuilder dataString = new StringBuilder();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String dataString = br.readLine();
		/*
		synchronized(in) {
		while (!end) {
			
			int currentBytesRead = in.read(messageByte);	
		    dataString.append(new String(messageByte, 0, currentBytesRead, StandardCharsets.UTF_8));

			if (dataString.toString().endsWith("\n")) {
				end = true;
			}
		}
		}*/

		return dataString.toString();

	}

}
