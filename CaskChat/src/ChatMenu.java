import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


public class ChatMenu extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4097860442432321662L;
	private static int CHECK_DELAY = 1000;
	private static int STARTUP_CONNECT_DELAY = 1000;
	
	private JLabel statusBox;
	private JLabel nameStatus;
	private JButton joinButton;
	private JTextField nameField;
	private String name;
	private Timer timer;
	private MenuAgent agent;
	private ActionListener joinAction;
	
	public ChatMenu() {
		new ConsoleWindow().setTitle("CaskChat Client");
		initialize();
	}
	
	private void initialize() {
		int padding = 5;
		double ratio = (1.0+Math.sqrt(5.0))/2.0;
		Dimension screenDims = Toolkit.getDefaultToolkit().getScreenSize();
		
		joinAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (joinButton.isEnabled())
					joinChat();
			}
		};
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		double width = screenDims.width/4;
		Dimension panelDims = new Dimension((int)(width),(int)(width*ratio));
		panel.setPreferredSize(panelDims);
		panel.setMaximumSize(panelDims);
		panel.setMinimumSize(panelDims);
		
		JLabel title = new JLabel("CaskChat");
		title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
		title.setSize(100,30);
		title.setLocation((panelDims.width - title.getWidth())/2,10);
		panel.add(title);
		
		statusBox = new JLabel();
		statusBox.setSize(3*panelDims.width/4,20);
		statusBox.setLocation((panelDims.width - statusBox.getWidth())/2,title.getX() + title.getHeight() + padding);
		panel.add(statusBox);
		
		nameField = new JTextField();
		nameField.setSize(100,20);
		nameField.setLocation((panelDims.width - nameField.getWidth())/2,panelDims.height/2);
		nameField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				nameChanged();
			}
			@Override
			public void insertUpdate(DocumentEvent e) {
				nameChanged();
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				nameChanged();
			}
		});
		nameField.addActionListener(joinAction);
		panel.add(nameField);
		
		nameStatus = new JLabel();
		nameStatus.setSize(100,20);
		nameStatus.setLocation((panelDims.width - nameStatus.getWidth())/2,nameField.getX() + nameField.getHeight() + padding);
		panel.add(nameStatus);
		
		joinButton = new JButton("Join Chat");
		joinButton.setSize(100,20);
		joinButton.setLocation((panelDims.width - joinButton.getWidth())/2,3*panelDims.height/4);
		joinButton.addActionListener(joinAction);
		joinButton.setEnabled(false);
		panel.add(joinButton);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		add(panel,BorderLayout.CENTER);
		pack();
		setSize(getWidth()-10,getHeight()-10);
		setResizable(false);
		setLocationRelativeTo(null);
		this.setVisible(true);
		
		agent = new MenuAgent(this);
		timer = new Timer(STARTUP_CONNECT_DELAY,new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				agent.connect();
				timer.stop();
			}
		});
		timer.start();
	}
	
	private void joinChat() {
		nameField.setEditable(false);
		agent.joinChat(name);
	}
	
	public void joinDeniedCuzName() {
		nameField.setEditable(true);
		nameStatus.setText("Name unavailable");
	}
	
	public void joinDenied() {
		nameField.setEditable(true);
		statusBox.setText("Unable to join for unknown reasons");
	}
	
	public void joinGranted() {
		ChatWindow w = new ChatWindow();
		Connection c = agent.getConnection();
		c.removeConnectionListener(agent);
		ChatAgent a = new ChatAgent();
		a.setConnection(c);
		w.setAgent(a);
		a.setChatWindow(w);
		w.setTitle(name);
		dispose();
	}
	
	public void connectionStatus(String s) {
		statusBox.setText(s);
	}
	
	private void nameChanged() {
		timer.stop();
		joinButton.setEnabled(false);
		String text = nameField.getText().trim();
		if (text.compareTo("") == 0) {
			nameStatus.setText("");
			return;
		}
		if (checkName(text)) {
			name = text;
			timer.restart();
			nameStatus.setText("");
		} else 
			nameStatus.setText("Invalid Name");
	}
	
	public void nameConfirmed(String s,boolean isAvail) {
		if (s.compareTo(nameField.getText()) == 0) {
			nameStatus.setText((isAvail)?"Name available!":"Name unavailable");
			joinButton.setEnabled(true);
			name = s;
		} else
			joinButton.setEnabled(false);
	}
	
	private void checkNameAvailability(String s) {
		if (agent.isConnected())
			agent.requestNameAvailability(s);
	}
	
	private boolean checkName(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == ' ')
				return false;
		}
		return true;
	}
	
	public void hasConnected() {
		timer = new Timer(CHECK_DELAY,new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkNameAvailability(name);
				timer.stop();
			}
		});
		if (nameField.getText().compareTo("") != 0) {
			nameChanged();
		}
	}
	
}
