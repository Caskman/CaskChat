import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


public class ChatMenu extends JFrame {

	private static int CHECK_DELAY = 1000;
	private static int STARTUP_CONNECT_DELAY = 2000;
	
	private JLabel statusBox;
	private JLabel nameStatus;
	private JButton joinButton;
	private JTextField nameField;
	private String name;
	private Timer timer;
	private MenuAgent agent;
	
	public ChatMenu() {
		initialize();
	}
	
	private void initialize() {
		int padding = 5;
		double ratio = (1.0+Math.sqrt(5.0))/2.0;
		Dimension screenDims = Toolkit.getDefaultToolkit().getScreenSize();
		
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
				nameChanged(e);
			}
			@Override
			public void insertUpdate(DocumentEvent e) {
				nameChanged(e);
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				nameChanged(e);
			}
		});
		panel.add(nameField);
		
		nameStatus = new JLabel();
		nameStatus.setSize(100,20);
		nameStatus.setLocation((panelDims.width - nameStatus.getWidth())/2,nameField.getX() + nameField.getHeight() + padding);
		panel.add(nameStatus);
		
		joinButton = new JButton("Join Chat");
		joinButton.setSize(100,20);
		joinButton.setLocation((panelDims.width - joinButton.getWidth())/2,3*panelDims.height/4);
		joinButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				joinChat();
			}
		});
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
				timer = new Timer(CHECK_DELAY,new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						checkNameAvailability(name);
					}
				});
			}
		});
		timer.start();
	}
	
	private void joinChat() {
		ChatWindow w = new ChatWindow();
		Connection c = agent.getConnection();
		ChatAgent a = new ChatAgent();
		a.setConnection(c);
		w.setAgent(a);
		a.setChatWindow(w);
	}
	
	public void connectionStatus(String s) {
		statusBox.setText(s);
	}
	
	private void nameChanged(DocumentEvent e) {
		timer.stop();
		String text = nameField.getText().trim();
		if (text.compareTo("") == 0) {
			nameStatus.setText("");
			return;
		}
		if (checkName(text)) {
			name = text;
			timer.restart();
			nameStatus.setText("Valid");
		}
	}
	
	public void nameConfirmed(String s,boolean isAvail) {
		if (name.compareTo(s) == 0) {
			nameStatus.setText((isAvail)?"Name available!":"Name unavailable");
			joinButton.setEnabled(isAvail);
		}
	}
	
	private void checkNameAvailability(String s) {
		agent.checkNameAvailability(s);
	}
	
	private boolean checkName(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == ' ')
				return false;
		}
		return true;
	}
	
}
