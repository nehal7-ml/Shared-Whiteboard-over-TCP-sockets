package WhiteBoard;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.TableView.TableRow;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ListenThread extends Thread {

	private ReadData r;
	private JSONParser jp = new JSONParser();
	private Graphics gr;
	private WhiteBoard WB;
	private DataOutputStream dout;
	private JSONObject rep;
	private Graphics img;
	private Color setcol;

	public ListenThread(ReadData in, DataOutputStream out, WhiteBoard J) throws IOException, ParseException {
		this.r = in;
		this.WB = J;
		this.dout = out;
		JPanel frame = J.getBoard();
		this.gr = frame.getGraphics();
		String m = "{\"Message\":\"Response\"}";
		this.rep = (JSONObject) jp.parse(m);
		this.img = WB.getImg();
	}

	@Override
	@SuppressWarnings({ "serial", "unchecked", "rawtypes" })
	public void run() {
		System.out.println("listen started");
		JSONObject req;
		String cmd = "";
		ArrayList<Integer> xPoints = new ArrayList<Integer>();
		ArrayList<Integer> yPoints = new ArrayList<Integer>();

		while (true) {
			try {
				cmd = this.r.read();
				System.out.print(cmd);
				req = (JSONObject) jp.parse(cmd);

				if (req.get("Message").equals("Request")) {

					try {
						this.setcol = WB.findColor(Integer.parseInt(req.get("Color").toString()));

					} catch (Exception e) {

					}

					if (req.get("Type").equals("HEART")) {
						rep.put("Type", "HEART");
						rep.put("Status", "SUCCESS");
						rep.put("From", WB.getUserName());
						rep.put("To", req.get("From"));
						synchronized (dout) {
							dout.write((rep.toString() + "\n").getBytes("UTF-8"));
							dout.flush();
							System.out.println(rep.toString());
						}

					} else if (req.get("Type").equals("DRAW_PATH")) {
						Draw d1 = new Draw(gr, setcol, WB.getBoard().getBackground());
						Draw d2 = new Draw(img, setcol, WB.getBoard().getBackground());
						System.out.println("path");
						JSONObject coord = (JSONObject) req.get("Data");
						String ax[] = coord.get("x").toString().replace("[", "").replace("]", "").split(",");
						String ay[] = coord.get("y").toString().replace("[", "").replace("]", "").split(",");
						int X_axis[] = { Integer.parseInt(ax[0]), Integer.parseInt(ax[1]) };
						int Y_axis[] = { Integer.parseInt(ay[0]), Integer.parseInt(ay[1]) };
						d1.paintFree(X_axis, Y_axis);
						d2.paintFree(X_axis, Y_axis);

					} else if(req.get("Type").equals("DRAW_CIRCLE")) {
						Draw d1 = new Draw(gr, setcol, WB.getBoard().getBackground());
						Draw d2 = new Draw(img, setcol, WB.getBoard().getBackground());

						int l = xPoints.size();
						JSONObject coord = (JSONObject) req.get("Data");
						String ax[] = coord.get("x").toString().replace("[", "").replace("]", "").split(",");
						String ay[] = coord.get("y").toString().replace("[", "").replace("]", "").split(",");
						int xs = Integer.parseInt(ax[0]);
						int ys = Integer.parseInt(ay[0]);
						int xe = Integer.parseInt(ax[1]);
						int ye = Integer.parseInt(ay[1]);
						
						
						if (l >= 2) {
							int Dx[] = { xPoints.get(0), xe };
							int Dy[] = { yPoints.get(0), ye };
							int Cx[] = { xPoints.get(0), xPoints.get(l - 2) };
							int Cy[] = { yPoints.get(0), yPoints.get(l - 2) };
							xPoints.add(xe);
							yPoints.add(ye);
							System.out.println("Line clear");
							d1.clearCircle(Cx, Cy);
							d1.paintCircle(Dx, Dy);
							d2.clearCircle(Cx, Cy);
							d2.paintCircle(Dx, Dy);
						} else if (l == 0) {
							xPoints.add(xs);
							yPoints.add(ys);
							xPoints.add(xe);
							yPoints.add(ye);
							int Dx[] = { xPoints.get(0), xe };
							int Dy[] = { yPoints.get(0), ye };
							d1.paintCircle(Dx, Dy);
							d2.paintCircle(Dx, Dy);
						}
						
						
						
						
					}
						else  if (req.get("Type").equals("DRAW_RECT")) {

						Draw d1 = new Draw(gr, setcol, WB.getBoard().getBackground());
						Draw d2 = new Draw(img, setcol, WB.getBoard().getBackground());

						int l = xPoints.size();
						JSONObject coord = (JSONObject) req.get("Data");
						String ax[] = coord.get("x").toString().replace("[", "").replace("]", "").split(",");
						String ay[] = coord.get("y").toString().replace("[", "").replace("]", "").split(",");
						int xs = Integer.parseInt(ax[0]);
						int ys = Integer.parseInt(ay[0]);
						int xe = Integer.parseInt(ax[1]);
						int ye = Integer.parseInt(ay[1]);

						if (l >= 2) {
							int Dx[] = { xPoints.get(0), xe };
							int Dy[] = { yPoints.get(0), ye };
							int Cx[] = { xPoints.get(0), xPoints.get(l - 2) };
							int Cy[] = { yPoints.get(0), yPoints.get(l - 2) };
							xPoints.add(xe);
							yPoints.add(ye);
							System.out.println("Line clear");
							d1.clearRect(Cx, Cy);
							d1.paintRect(Dx, Dy);
							d2.clearRect(Cx, Cy);
							d2.paintRect(Dx, Dy);
						} else if (l == 0) {
							xPoints.add(xs);
							yPoints.add(ys);
							xPoints.add(xe);
							yPoints.add(ye);
							int Dx[] = { xPoints.get(0), xe };
							int Dy[] = { yPoints.get(0), ye };
							d1.paintRect(Dx, Dy);
							d2.paintRect(Dx, Dy);
						}

					} else if(req.get("Type").equals("DRAW_TEXT")) {
						Draw d1 = new Draw(gr, setcol, WB.getBoard().getBackground());
						Draw d2 = new Draw(img, setcol, WB.getBoard().getBackground());
						JSONObject data = (JSONObject) req.get("Data");
						int x = Integer.parseInt(data.get("x").toString());
						int y =Integer.parseInt(data.get("y").toString());
						String s = data.get("S").toString();
						d1.text(s, x, y);
						d2.text(s, x, y);
						
					}else if (req.get("Type").equals("DISCONNECT")) {				
						
						JOptionPane.showMessageDialog(null, "Disconnected");						
						
					}
					
					
					
					else if (req.get("Type").equals("DRAW_LINE")) {
						Draw d1 = new Draw(gr, setcol, WB.getBoard().getBackground());
						Draw d2 = new Draw(img, setcol, WB.getBoard().getBackground());

						int l = xPoints.size();
						System.out.println(l);
						JSONObject coord = (JSONObject) req.get("Data");
						String ax[] = coord.get("x").toString().replace("[", "").replace("]", "").split(",");
						String ay[] = coord.get("y").toString().replace("[", "").replace("]", "").split(",");
						int xs = Integer.parseInt(ax[0]);
						int ys = Integer.parseInt(ay[0]);
						int xe = Integer.parseInt(ax[1]);
						int ye = Integer.parseInt(ay[1]);

						if (l >= 2) {
							int Dx[] = { xPoints.get(0), xe };
							int Dy[] = { yPoints.get(0), ye };
							int Cx[] = { xPoints.get(0), xPoints.get(l - 2) };
							int Cy[] = { yPoints.get(0), yPoints.get(l - 2) };
							xPoints.add(xe);
							yPoints.add(ye);
							System.out.println("Line clear");
							d1.clearLine(Cx, Cy);
							d1.paintLine(Dx, Dy);
							d2.clearLine(Cx, Cy);
							d2.paintLine(Dx, Dy);

						} else if (l == 0) {
							xPoints.add(xs);
							yPoints.add(ys);
							xPoints.add(xe);
							yPoints.add(ye);
							int Dx[] = { xPoints.get(0), xe };
							int Dy[] = { yPoints.get(0), ye };
							d1.paintLine(Dx, Dy);
							d2.paintLine(Dx, Dy);
						}

					} else if (req.get("Type").equals("CLEAR_ARRAY")) {
						System.out.println("cleared");
						xPoints.clear();
						yPoints.clear();

					} else if (req.get("Type").equals("JOIN")) {
						String message = req.get("From").toString() + " Wants to join the meeting";
						System.out.println(message);
						int s = JOptionPane.showConfirmDialog(WB.getFrame(), message);

						if (s == 0) {
							rep.put("Type", "JOIN");
							rep.put("Status", "SUCCESS");
							rep.put("From", req.get("To"));
							rep.put("To", req.get("From"));
							rep.put("Token", req.get("Token"));
							rep.put("Id",req.get("Data"));

							synchronized (dout) {
								dout.write((rep.toString() + "\n").getBytes("UTF-8"));
								dout.flush();
							}
							continue;
							// req.put("Type", "UPDATE_BOARD");
						} else {
							rep.put("Type", "JOIN");
							rep.put("Status", "FAILED");
							rep.put("From", req.get("To"));
							rep.put("To", req.get("From"));
						}
					} else if (req.get("Type").equals("UPDATE_BOARD")) {

						// parse image
						String imageString = req.get("Data").toString();
						byte[] imageByte = Base64.getDecoder().decode(imageString);
						ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
						BufferedImage  rec=ImageIO.read(bis);
						bis.close();
						
					
						Draw d1 = new Draw(gr, setcol, WB.getBoard().getBackground());
						d1.updateSc(rec);
						
						

					} else if (req.get("Type").equals("BOARD")) {

						BufferedImage rec = WB.getBi();
						RequestThread rep;
						try {
							rep = new RequestThread(dout, "BOARD", WB.getUserName(), rec);
							rep.start();
						} catch (ParseException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					} else if (req.get("Type").equals("UPDATE_LIST")) {
						String[] clist = req.get("Data").toString().replace("[", "").replace("]", "").split(",");
						ImageIcon handIcon = new ImageIcon("hand.png");
						ImageIcon muteIcon = new ImageIcon("silent.png");
						ImageIcon discIcon = new ImageIcon("exit.png");
						DefaultTableModel modelTable = (DefaultTableModel) this.WB.getTable().getModel();
						DefaultComboBoxModel chatList = (DefaultComboBoxModel) this.WB.getComboBox().getModel();
						int l = modelTable.getRowCount();

						for (int j = l; j < clist.length; j++) {
							Object[] rowdata = { clist[j], handIcon, muteIcon, discIcon };
							modelTable.addRow(rowdata);
							if (j != WB.getUserId()) {
								chatList.addElement(clist[j]);

							}
							System.out.println("update list");
						}

					}else if(req.get("Type").equals("UPDATE_LIST_REMOVE")){ 
						DefaultTableModel modelTable = (DefaultTableModel) this.WB.getTable().getModel();
						DefaultComboBoxModel chatList = (DefaultComboBoxModel) this.WB.getComboBox().getModel();
						chatList.removeElementAt(Integer.parseInt(req.get("Data").toString()));
						modelTable.removeRow(Integer.parseInt(req.get("Data").toString()));
								
					}
					else if (req.get("Type").equals("TEXT")) {
						
						String msg = req.get("From").toString()+" to "+req.get("To").toString()+" :"+req.get("Text").toString()+"\n";

						WB.getChatBox().append(msg);

					} else if (req.get("Type").equals("RAISEHAND")) {
						ImageIcon handIcon = new ImageIcon("question.png");
						WB.getTable().getModel().setValueAt(handIcon, Integer.parseInt(req.get("Data").toString()), 1);

					} else if (req.get("Type").equals("LOWERHAND")) {
						ImageIcon handIcon = new ImageIcon("hand.png");
						WB.getTable().getModel().setValueAt(handIcon, Integer.parseInt(req.get("Data").toString()), 1);

					}

					else if (req.get("Type").equals("MUTE")) {
						ImageIcon handIcon = new ImageIcon("mute.png");
						WB.getTable().getModel().setValueAt(handIcon, Integer.parseInt(req.get("Data").toString()), 2);
						// WB.setIsMuted(true);

					} else if (req.get("Type").equals("MUTEU")) {
						ImageIcon handIcon = new ImageIcon("mute.png");
						WB.getTable().getModel().setValueAt(handIcon, Integer.parseInt(req.get("Data").toString()), 2);
						WB.setIsMuted(true);

					} else if (req.get("Type").equals("UNMUTE")) {
						ImageIcon handIcon = new ImageIcon("silent.png");
						WB.getTable().getModel().setValueAt(handIcon, Integer.parseInt(req.get("Data").toString()), 2);
						// WB.setIsMuted(false);
					} else if (req.get("Type").equals("UNMUTEU")) {
						ImageIcon handIcon = new ImageIcon("silent.png");
						WB.getTable().getModel().setValueAt(handIcon, Integer.parseInt(req.get("Data").toString()), 2);
						WB.setIsMuted(false);
					}

				}

			} catch (SocketException e) {
				
				JOptionPane.showMessageDialog(null, "Disconnected");

			} catch (IOException e) {
				e.printStackTrace();

			} catch (ParseException e) {
				e.printStackTrace();

			}catch(java.lang.StringIndexOutOfBoundsException e) {
				e.printStackTrace();
				
			}

		}
	}
}
