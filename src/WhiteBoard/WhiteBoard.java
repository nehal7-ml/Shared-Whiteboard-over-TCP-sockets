package WhiteBoard;

import java.util.ArrayList;

import javax.swing.JFrame;
import java.awt.Canvas;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JMenu;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;

import org.json.simple.parser.ParseException;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JEditorPane;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ListSelectionModel;
import javax.swing.JComboBox;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import java.awt.Dimension;
import javax.swing.JToggleButton;
import java.awt.event.WindowStateListener;
import java.awt.event.WindowEvent;
import java.awt.Cursor;

public class WhiteBoard extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Socket csoc;
	private JFrame frame;
	private JPanel board;
	private DataOutputStream dout;
	private String Tool = "DRAW_PATH";
	private JTable table;
	private String username;
	private JTextArea ChatBox;
	private Graphics2D img;
	private int msg_to = 99;
	private int userId;
	private String meeting_id;
	private Boolean isHost;
	private Boolean isMuted = false;
	private Boolean isRaised = false;
	private Color SetCol = Color.BLACK;
	@SuppressWarnings("rawtypes")
	private JComboBox comboBox;
	private BufferedImage bi = null;
	Boolean textInput = false;

	public WhiteBoard(Socket soc, DataOutputStream d, String name, int id, String mid) throws IOException {
		this.csoc = soc;
		this.dout = d;
		this.userId = id;
		this.username = name;
		this.meeting_id=mid;
		if (id == 0) {
			this.isHost = true;
		} else {
			this.isHost = false;
		}
		initialize();
		System.out.println("started whiteboard");
	}

	/**
	 * Initialize the contents of the frame.
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings({ "serial", "rawtypes", "unchecked" })
	private void initialize() throws IOException {

		frame = new JFrame();
		frame.addWindowStateListener(new WindowStateListener() {
			public void windowStateChanged(WindowEvent e) {
			}
		});
		frame.setTitle("Meeting ID:"+"000"+meeting_id);
		frame.setBounds(100, 100, 1095, 780);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		ArrayList<Integer> xPoints = new ArrayList<Integer>();
		ArrayList<Integer> yPoints = new ArrayList<Integer>();
		ArrayList<JEditorPane> TextInput = new ArrayList<JEditorPane>();

		JMenuBar menuBar_1 = new JMenuBar();
		menuBar_1.setBounds(10, 0, 617, 23);
		frame.getContentPane().add(menuBar_1);

		JButton btnNewButton = new JButton("Free Draw");
		btnNewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Tool = "DRAW_PATH";
				textInput = false;
			}
		});
		JButton btnNewButton_1 = new JButton("Rectangle");
		btnNewButton_1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Tool = "DRAW_RECT";
				textInput = false;

			}
		});
		menuBar_1.add(btnNewButton_1);
		menuBar_1.add(btnNewButton);

		JButton btnNewButton_2 = new JButton("Line");
		btnNewButton_2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Tool = "DRAW_LINE";
				textInput = false;
			}

		});
		menuBar_1.add(btnNewButton_2);

		JButton btnNewButton_5 = new JButton("Erase");
		btnNewButton_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Tool = "ERASE";
				textInput = false;
			}
		});
		
		JButton btnNewButton_4 = new JButton("Circle");
		btnNewButton_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Tool="DRAW_CIRCLE";
				textInput = false;
			}
		});
		menuBar_1.add(btnNewButton_4);
		menuBar_1.add(btnNewButton_5);

		JComboBox comboBox_1 = new JComboBox();
		comboBox_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SetCol = findColor(comboBox_1.getSelectedIndex());
			}
		});

		JButton btnNewButton_3 = new JButton("Text");
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				Tool = "DRAW_TEXT";
				textInput = true;

			}
		});
		menuBar_1.add(btnNewButton_3);
		comboBox_1.setModel(new DefaultComboBoxModel(new String[] { "BLACK", "BLUE", "RED" }));
		menuBar_1.add(comboBox_1);
		comboBox_1.setAutoscrolls(true);

		bi = new BufferedImage(627, 685, BufferedImage.TYPE_INT_ARGB);
		img = (Graphics2D) bi.getGraphics();

		board = new JPanel();
		board.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		board.setMinimumSize(new Dimension(10, 33));
		board.setPreferredSize(new Dimension(10, 33));
		board.setAutoscrolls(true);
		board.setFocusable(true);
		board.requestFocusInWindow();
		board.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {

			}
		});
		board.setFont(new Font("Dialog", Font.PLAIN, 5));
		board.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				xPoints.clear();
				yPoints.clear();
				RequestThread req;
				try {
					req = new RequestThread(dout, "CLEAR_ARRAY", username);
					req.start();
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (TextInput.size() >= 1) {

					JEditorPane tet = TextInput.get(0);
					String dtext = tet.getText();
					Draw d2 = new Draw(img, SetCol, img.getBackground());
					d2.text(dtext, tet.getX(), tet.getY());

					if (!isMuted) {
						RequestThread req;
						try {
							req = new RequestThread(dout, e.getX(), e.getY(), Tool, username, dtext,
									comboBox_1.getSelectedIndex());
							req.start();
						} catch (ParseException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}

				xPoints.clear();
				yPoints.clear();

				xPoints.add(e.getX());
				yPoints.add(e.getY());
				if (textInput) {

					JEditorPane tet = new JEditorPane();
					tet.setBackground(new Color(255, 255, 255));
					tet.setForeground(SetCol);
					tet.setBounds(e.getX(), e.getY(), 200, 31);
					tet.setEditable(true);
					board.add(tet);
					board.revalidate();
					TextInput.add(tet);
				}

				System.out.println("new input");

			}
		});

		board.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				Draw d1 = new Draw(board.getGraphics(), SetCol, board.getBackground());
				Draw d2 = new Draw(img, SetCol, img.getBackground());

				if (Tool.equals("ERASE")) {

					d1.clear(e.getX(), e.getY());
					d2.clear(e.getX(), e.getY());
					if (!isMuted) {
						RequestThread req;
						try {
							req = new RequestThread(dout, e.getX(), e.getY(), Tool, username);
							req.start();
						} catch (ParseException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}

				}

				else if (Tool.equals("DRAW_PATH")) {
					if (xPoints.size() >= 2) {
						xPoints.add(e.getX());
						yPoints.add(e.getY());
						int Dx[] = { xPoints.get(xPoints.size() - 2), e.getX() };
						int Dy[] = { yPoints.get(yPoints.size() - 2), e.getY() };
						d1.paintFree(Dx, Dy);
						d2.paintFree(Dx, Dy);

						if (!isMuted) {
							RequestThread req;
							try {
								req = new RequestThread(dout, Dx, Dy, Tool, username, comboBox_1.getSelectedIndex());
								req.start();
							} catch (ParseException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}

					} else {
						xPoints.add(e.getX());
						yPoints.add(e.getY());
					}

				}
				if (Tool.equals("DRAW_TEXT")) {
					System.out.println("drawText");
					try {
						ImageIO.write(bi, "png", new File("lalal.png"));
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}

				else {

					if (xPoints.size() >= 2) {

						int x = e.getX();
						int y = e.getY();
						int l = xPoints.size();

						xPoints.add(x);
						yPoints.add(y);

						int Cx[] = { xPoints.get(0), xPoints.get(l - 2) };
						int Cy[] = { yPoints.get(0), yPoints.get(l - 2) };

						int Dx[] = { xPoints.get(0), x };
						int Dy[] = { yPoints.get(0), y };

						if (Tool.equals("DRAW_RECT")) {

							if (xPoints.size() >= 3) {
								d1.clearRect(Cx, Cy);
								d2.clearRect(Cx, Cy);
							}
							d1.paintRect(Dx, Dy);
							d2.paintRect(Dx, Dy);

							if (!isMuted) {
								RequestThread req;
								try {
									req = new RequestThread(dout, Dx, Dy, Tool, username,
											comboBox_1.getSelectedIndex());
									req.start();
								} catch (ParseException e1) {
									e1.printStackTrace();
								}
							}

						} else if(Tool.equals("DRAW_CIRCLE")){
							if (xPoints.size() >= 3) {
								d1.clearCircle(Cx, Cy);
								d2.clearCircle(Cx, Cy);
							}
							d1.paintCircle(Dx, Dy);
							d2.paintCircle(Dx, Dy);
							RequestThread req;
							if (!isMuted) {
								try {
									req = new RequestThread(dout, Dx, Dy, Tool, username,
											comboBox_1.getSelectedIndex());
									req.start();
								} catch (ParseException e1) {
									e1.printStackTrace();
								}

							}

						}						
							
							else if (Tool.equals("DRAW_LINE")) {
					
							if (xPoints.size() >= 3) {
								d1.clearLine(Cx, Cy);
								d2.clearLine(Cx, Cy);
							}
							d1.paintLine(Dx, Dy);
							d2.paintLine(Dx, Dy);
							RequestThread req;
							if (!isMuted) {
								try {
									req = new RequestThread(dout, Dx, Dy, Tool, username,
											comboBox_1.getSelectedIndex());
									req.start();
								} catch (ParseException e1) {
									e1.printStackTrace();
								}

							}

						}

					} else {
						xPoints.add(e.getX());
						yPoints.add(e.getY());
					}

				}

			}
		});
		board.setBackground(Color.WHITE);
		board.setBounds(10, 23, 627, 685);
		frame.getContentPane().add(board);
		board.setLayout(null);

		JInternalFrame internalFrame = new JInternalFrame("Participants");
		internalFrame.getContentPane().setBackground(Color.WHITE);
		internalFrame.getContentPane().setLayout(null);

		table = new JTable();
		table.setToolTipText("Chose to raise hand or mute yourself, or disconnect");
		table.setRowSelectionAllowed(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int target = table.getSelectedRow();
				int action = table.getSelectedColumn();
				RequestThread req;
				if (action > 0) {

					if (action == 1 && target == userId) {

						if (isRaised) {
							ImageIcon handIcon = new ImageIcon("hand.png");
							table.getModel().setValueAt(handIcon, target, action);
							try {
								req = new RequestThread(dout, "LOWERHAND", username, target);
								req.start();
							} catch (ParseException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							isRaised = false;

						} else {

							ImageIcon handIcon = new ImageIcon("question.png");
							table.getModel().setValueAt(handIcon, target, action);

							try {
								req = new RequestThread(dout, "RAISEHAND", username, target);
								req.start();
							} catch (ParseException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							isRaised = true;
						}

						// raise hand
					} else if (action == 2 && (target == userId || isHost)) {

						if (isMuted) {
							ImageIcon handIcon = new ImageIcon("silent.png");
							table.getModel().setValueAt(handIcon, target, action);

							if (isHost) {
								try {
									req = new RequestThread(dout, "UNMUTEU", username, target);
									req.start();
								} catch (ParseException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}

							}

							try {
								req = new RequestThread(dout, "UNMUTE", username, target);
								req.start();
							} catch (ParseException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							isMuted = false;

						} else {

							ImageIcon handIcon = new ImageIcon("mute.png");
							table.getModel().setValueAt(handIcon, target, action);

							if (isHost) {
								try {
									req = new RequestThread(dout, "MUTEU", username, target);
									req.start();
								} catch (ParseException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}

							}

							try {
								req = new RequestThread(dout, "MUTE", username, target);
								req.start();
							} catch (ParseException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							isMuted = true;
						}

						// mute
					} else if ((action == 3)&&(target == userId || isHost)) {
						
						if(isHost) {
							try {
								req = new RequestThread(dout, "DISCONNECT", username, target);
								req.start();
							} catch (ParseException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
					}
						
						
						
						try {
							req = new RequestThread(dout, "DISCONNECT", username);
							req.start();
						} catch (ParseException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						try {

							int s = JOptionPane.showConfirmDialog(frame, "Comfirm Disconnect");
							if (s == 0) {
								csoc.close();
								System.exit(0);
							}
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						// Kick disconne

					}

				}

			}
		});
		table.setCellSelectionEnabled(true);
		table.setModel(
				new DefaultTableModel(new Object[][] {}, new String[] { "User Id", "Raise hand", "Mute", "Kick" }) {
					@Override
					public Class getColumnClass(int column) {
						return getValueAt(0, column).getClass();
					}

					boolean[] columnEditables = new boolean[] { false, false, false, false };

					public boolean isCellEditable(int row, int column) {
						return columnEditables[column];
					}
				});
		table.getColumnModel().getColumn(0).setPreferredWidth(144);
		table.setBounds(12, 12, 374, 294);
		internalFrame.getContentPane().add(table);

		JEditorPane editorPane = new JEditorPane();
		editorPane.setBounds(224, 112, 107, 22);
		internalFrame.getContentPane().add(editorPane);
		internalFrame.setBounds(661, 23, 408, 351);
		frame.getContentPane().add(internalFrame);

		JInternalFrame internalFrame_1 = new JInternalFrame("Chat");
		internalFrame_1.getContentPane().setBackground(Color.WHITE);
		internalFrame_1.getContentPane().setLayout(null);

		JEditorPane Msg = new JEditorPane();
		Msg.setBackground(Color.LIGHT_GRAY);
		Msg.setBounds(12, 237, 309, 31);
		internalFrame_1.getContentPane().add(Msg);

		JButton Send = new JButton("Send");
		Send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String data = Msg.getText();
				RequestThread req;
				try {
					if (!isMuted) {
						req = new RequestThread(dout, "TEXT", username, msg_to, data);						
						req.start();
						data = username+" to "+"All"+" :"+data+"\n";
						ChatBox.append(data);         
						
					}
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} finally {
					Msg.setText("");
				}

			}
		});
		Send.setBounds(321, 237, 58, 31);
		internalFrame_1.getContentPane().add(Send);

		ChatBox = new JTextArea();
		ChatBox.setBackground(Color.LIGHT_GRAY);
		ChatBox.setEditable(false);
		ChatBox.setBounds(12, 48, 367, 177);
		internalFrame_1.getContentPane().add(ChatBox);

		comboBox = new JComboBox();
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				int i = comboBox.getSelectedIndex();
				if (i == 0) {
					msg_to = 99;

				} else {
					msg_to = i;

				}

			}
		});
		comboBox.setModel(new DefaultComboBoxModel(new String[] { "Everyone" }));
		comboBox.setBounds(12, 12, 367, 25);
		internalFrame_1.getContentPane().add(comboBox);
		internalFrame_1.setBounds(668, 395, 401, 313);
		// = new JLabel(new ImageIcon(bi));

		frame.getContentPane().add(internalFrame_1);
		internalFrame_1.setVisible(true);
		internalFrame.setVisible(true);
	}

	public Color findColor(int S) {
		switch (S) {
		case 0:
			return Color.BLACK;
		case 1:
			return Color.BLUE;
		case 2:
			return Color.RED;
		default:
			return Color.BLACK;

		}
	}

	public void setSetCol(Color setCol) {
		this.SetCol = setCol;
	}

	public void setBoardLabel(ImageIcon i) {
		JLabel t = new JLabel(new ImageIcon(bi));
		t.setLocation(100, 100);
		this.board.add(t);
		t.setVisible(true);
		this.board.repaint();
		this.board.revalidate();
		System.out.println("added screen");
	}

	public void setIsMuted(Boolean isMuted) {
		this.isMuted = isMuted;
	}

	public void setIsRaised(Boolean isRaised) {
		this.isRaised = isRaised;
	}

	public JFrame getFrame() {
		return this.frame;
	}

	public JPanel getBoard() {
		return this.board;
	}

	public JTable getTable() {
		return this.table;
	}

	public JTextArea getChatBox() {
		return this.ChatBox;
	}

	public String getUserName() {
		return this.username;
	}

	public JComboBox getComboBox() {
		return this.comboBox;
	}

	public int getUserId() {
		return this.userId;
	}

	public Graphics2D getImg() {
		return img;
	}

	public BufferedImage getBi() {
		return this.bi;
	}

	public Color getSetCol() {
		return this.SetCol;
	}
}
