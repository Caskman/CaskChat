import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class ChatWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2845291333313278477L;

	public static void main(String[] args) {
		new ChatWindow();
	}

	private JTextArea textArea;
	private JTextField chatBar;
	private ChatAgent agent;
	private boolean isFirst;
	private JScrollPane messageScrollPane;
	private boolean isAtBottom;
	private JList<ChatPerson> chatListBox;
	private ChatListModel chatList;

	public ChatWindow() {
		isAtBottom = true;
		isFirst = true;
		initializeFrame();
	}

	public void setAgent(ChatAgent c) {
		agent = c;
	}

	private void initializeFrame() {
		int padding = 5;
		double ratio = (1.0 + Math.sqrt(5.0)) / 2.0;
		Dimension screenDims = Toolkit.getDefaultToolkit().getScreenSize();

		addWindowListener(new WindowListener() {
			@Override
			public void windowActivated(WindowEvent arg0) {
				
			}
			@Override
			public void windowClosed(WindowEvent arg0) {
				
			}
			@Override
			public void windowClosing(WindowEvent arg0) {
				
			}
			@Override
			public void windowDeactivated(WindowEvent arg0) {
				
			}
			@Override
			public void windowDeiconified(WindowEvent arg0) {
				
			}
			@Override
			public void windowIconified(WindowEvent arg0) {
				
			}
			@Override
			public void windowOpened(WindowEvent arg0) {
				ChatWindow.this.windowOpened();
			}
		});
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		double height = screenDims.height / 2;
		Dimension panelDims = new Dimension((int) (height * ratio),
				(int) height);
		panel.setPreferredSize(panelDims);
		panel.setMaximumSize(panelDims);
		panel.setMinimumSize(panelDims);
		
		int listMargin = 5;
		chatListBox = new JList<ChatPerson>(chatList = new ChatListModel());
		chatListBox.setSize((panelDims.width - panelDims.height)/2 - padding,panelDims.height - 2*padding);
		chatListBox.setBorder(new EmptyBorder(listMargin,listMargin,listMargin,listMargin));
		chatListBox.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				
			}
			@Override
			public void focusLost(FocusEvent e) {
				chatListBox.clearSelection();
			}
		});

		chatBar = new JTextField();
		chatBar.setSize(panelDims.width - chatListBox.getWidth() - 3 * padding, 20);

		int textMargin = 5;
		textArea = new JTextArea();
		textArea.setMargin(new Insets(textMargin, textMargin, textMargin,
				textMargin));
		textArea.setSize(panelDims.width - chatListBox.getWidth() - 3 * padding, panelDims.height - 3
				* padding - chatBar.getHeight());
		textArea.setLocation(padding, padding);
		textArea.setEditable(false);
		
		messageScrollPane = new JScrollPane(textArea);
		messageScrollPane.setSize(textArea.getSize());
		messageScrollPane.setLocation(textArea.getLocation());
		messageScrollPane.getVerticalScrollBar().addAdjustmentListener(
				new AdjustmentListener() {
					public void adjustmentValueChanged(AdjustmentEvent e) {
						isAtBottom = e.getAdjustable().getValue() == e.getAdjustable().getMaximum();
					}
				});
		panel.add(messageScrollPane);

		chatBar.setLocation(padding, textArea.getX() + textArea.getHeight()
				+ padding);
		chatBar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				messageEntered();
			}
		});
		panel.add(chatBar);
		
		JScrollPane listScrollPane = new JScrollPane(chatListBox);
		listScrollPane.setSize(chatListBox.getSize());
		listScrollPane.setLocation(textArea.getX()+textArea.getWidth()+padding,padding);
		panel.add(listScrollPane);

		addWindowFocusListener(new WindowFocusListener() {
			@Override
			public void windowGainedFocus(WindowEvent e) {
				chatBar.requestFocusInWindow();
			}
			@Override
			public void windowLostFocus(WindowEvent e) {
				
			}
		});
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		add(panel, BorderLayout.CENTER);
		pack();
		setSize(getWidth() - 10, getHeight() - 10);
		setResizable(false);
		setLocationRelativeTo(null);
		this.setVisible(true);

	}
	
	public void updateChatPersonList(ChatPerson[] list) {
		ChatPerson[] betterList = new ChatPerson[list.length-1];
		int offset = 0;
		
		for (int i = 0; i < list.length; i++) {
			if (list[i].name.compareTo(agent.getName()) == 0)
				offset = -1;
			else
				betterList[i+offset] = list[i];
		}
		
		chatList.updateList(betterList);
	}
	
	private void windowOpened() {
		agent.requestChatPersonList();
	}
	
	private void messageEntered() {
		agent.sendText(chatBar.getText());
		addMessage("You: "+chatBar.getText());
		chatBar.setText("");
	}

	public void addMessage(String message) {
		if (!isFirst)
			textArea.append("\n" + message);
		else {
			textArea.append(message);
			isFirst = false;
		}
		if (isAtBottom) {
			messageScrollPane.getVerticalScrollBar().setValue(
					messageScrollPane.getVerticalScrollBar().getMaximum());
		}
	}

}
