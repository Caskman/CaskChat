import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

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

	public ChatWindow() {
		isFirst = true;
		initializeFrame();
	}

	public void setChatName(String name) {
		this.setTitle(name);
	}

	public void setAgent(ChatAgent c) {
		agent = c;
	}

	private void initializeFrame() {
		int padding = 5;
		// double ratio = 16.0/9.0;
		double ratio = (1.0 + Math.sqrt(5.0)) / 2.0;
		Dimension screenDims = Toolkit.getDefaultToolkit().getScreenSize();
		// setLocation((screenDims.width - getWidth())/2,(screenDims.height -
		// getHeight())/2);
		// TODO add server/client closing functionality on close

		JPanel panel = new JPanel();
		panel.setLayout(null);
		double height = screenDims.height / 2;
		Dimension panelDims = new Dimension((int) (height * ratio),
				(int) height);
		panel.setPreferredSize(panelDims);
		panel.setMaximumSize(panelDims);
		panel.setMinimumSize(panelDims);

		chatBar = new JTextField();
		chatBar.setSize(panelDims.width - 2 * padding, 20);

		int textMargin = 3;
		textArea = new JTextArea();
		textArea.setMargin(new Insets(textMargin, textMargin, textMargin,
				textMargin));
		textArea.setSize(panelDims.width - 2 * padding, panelDims.height - 3
				* padding - chatBar.getHeight());
		textArea.setLocation(padding, padding);
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setSize(textArea.getSize());
		scrollPane.setLocation(textArea.getLocation());
		scrollPane.getVerticalScrollBar().addAdjustmentListener(
				new AdjustmentListener() {
					public void adjustmentValueChanged(AdjustmentEvent e) {
						e.getAdjustable().setValue(
								e.getAdjustable().getMaximum());
					}
				});
		panel.add(scrollPane);

		chatBar.setLocation(padding, textArea.getX() + textArea.getHeight()
				+ padding);
		chatBar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				agent.sendText(chatBar.getText());
				chatBar.setText("");
			}
		});
		panel.add(chatBar);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		add(panel, BorderLayout.CENTER);
		pack();
		setSize(getWidth() - 10, getHeight() - 10);
		setResizable(false);
		setLocationRelativeTo(null);
		this.setVisible(true);

	}

	public void addMessage(String message) {
		if (!isFirst)
			textArea.append("\n" + message);
		else {
			textArea.append(message);
			isFirst = false;
		}
	}

}
