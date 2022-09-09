package WhiteBoard;

import javax.swing.JFrame;
import javax.swing.JTextField;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.JLabel;
import com.jgoodies.forms.factories.DefaultComponentFactory;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.Color;
import javax.swing.JPanel;
import javax.swing.JComboBox;

public class StartUp {

	private JFrame frame;
	private JTextField UsrNme;
	private JTextField Address;
	private JTextField Port;
	private JLabel lblNewLabel_1;
	private String serv;
	private String user_name;
	private int soc;
	private JSONObject rep;
	private String cmd = "";
	private NewConnection n;
	private ReadData r;
	private JTextField Id;

	/**
	 * Launch the application.
	 */

	/**
	 * Create the application.
	 */
	public StartUp() {
		this.initialize();
	}

	public JFrame getFrame() {
		return frame;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings("rawtypes")
	private void initialize() {

		JSONParser jp = new JSONParser();

		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(10, 10, 416, 243);
		frame.getContentPane().add(panel);
		panel.setLayout(null);


		UsrNme = new JTextField();
		UsrNme.setText("Nehal");
		UsrNme.setBounds(197, 4, 96, 19);
		panel.add(UsrNme);
		UsrNme.setColumns(10);
		JLabel lblNewLabel = new JLabel("Server Address :");
		lblNewLabel.setBounds(90, 36, 79, 13);
		panel.add(lblNewLabel);

		Address = new JTextField();
		Address.setText("localhost");
		Address.setBounds(197, 33, 96, 19);
		panel.add(Address);
		Address.setColumns(10);

		lblNewLabel_1 = new JLabel("Port :");
		lblNewLabel_1.setBounds(110, 68, 26, 13);
		panel.add(lblNewLabel_1);

		Port = new JTextField();
		Port.setText("3333");
		Port.setBounds(197, 65, 96, 19);
		panel.add(Port);
		Port.setColumns(10);
		lblNewLabel_1.setLabelFor(Port);

		JButton Join = new JButton("Join");
		Join.setBounds(260, 197, 85, 21);
		panel.add(Join);

		JButton create = new JButton("Create");
		create.setBounds(110, 197, 85, 21);
		panel.add(create);

		JLabel lblNewJgoodiesLabel = DefaultComponentFactory.getInstance().createLabel("User Name :");
		lblNewJgoodiesLabel.setBounds(111, 10, 58, 13);
		panel.add(lblNewJgoodiesLabel);
		lblNewJgoodiesLabel.setLabelFor(UsrNme);

		JLabel lblNewJgoodiesLabel_1 = DefaultComponentFactory.getInstance().createLabel("Meeting Id:");
		lblNewJgoodiesLabel_1.setBounds(100, 119, 52, 13);
		panel.add(lblNewJgoodiesLabel_1);

		Id = new JTextField();
		Id.setBounds(197, 116, 96, 19);
		panel.add(Id);
		Id.setColumns(10);
		create.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				user_name = UsrNme.getText();
				soc = Integer.parseInt(Port.getText());
				serv = Address.getText();
				n = new NewConnection(serv, soc);

				RequestThread start;
				try {
					start = new RequestThread(n.getdout(), "CREATE", user_name);
					start.start();

					r = new ReadData(n.getdin());
					cmd = r.read();
					rep = (JSONObject) jp.parse(cmd);
					System.out.println(cmd);

					if (rep.get("Status").equals("SUCCESS")) {
						frame.setVisible(false);
						WhiteBoard B = new WhiteBoard(n.getSocket(), n.getdout(), user_name, 0,rep.get("Id").toString());
						B.getFrame().setVisible(true);
						ListenThread lt;
						lt = new ListenThread(r, n.getdout(), B);
						lt.start();
						frame.dispose();
						Thread.currentThread().interrupt();
						return;
					}

				} catch (ParseException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
		Join.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int i = Integer.parseInt(Id.getText());
				String name = UsrNme.getText();
				user_name = UsrNme.getText();
				soc = Integer.parseInt(Port.getText());
				serv = Address.getText();
				n = new NewConnection(serv, soc);

				if (i >= 0) {
					try {						
						RequestThread start = new RequestThread(n.getdout(), "JOIN", user_name , i);
						start.start();
						
						System.out.println("sent Join request");	
						r = new ReadData(n.getdin());
						
						cmd = r.read();
						rep = (JSONObject) jp.parse(cmd);
						System.out.println(rep.toString() + " : join request");

						if (rep.get("Status").equals("SUCCESS") && rep.get("Type").equals("JOIN")) {
							frame.setVisible(false);
							WhiteBoard B = new WhiteBoard(n.getSocket(), n.getdout(), user_name,
									Integer.parseInt(rep.get("Data").toString()),rep.get("Data").toString());
							B.getFrame().setVisible(true);
							B.getFrame().setTitle("Meeting ID:"+"000"+rep.get("Data") );
							B.revalidate();	
							ListenThread lt = new ListenThread(r, n.getdout(), B);
							lt.start();
							frame.dispose();
							Thread.currentThread().interrupt();
							return;
						} else {
							JOptionPane.showMessageDialog(null, "Couldn't connect please try again");
						}

					} catch (ParseException e1) {
						// TODO Auto-generated catch block
					} catch (Exception e1) {
						
						e1.printStackTrace();
						// TODO Auto-generated catch block
						JOptionPane.showMessageDialog(null, "Couldn't connect please try again");
					}

				}

			}
		});

	}
}
