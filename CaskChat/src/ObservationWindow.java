import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ObservationWindow extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3491204738493702095L;

	public static void main(String[] args) {
		new ChatWindow();
	}
	
	private JTextArea textArea;
	private JScrollPane scrollPane;
	private boolean isAtBottom;
	
	public ObservationWindow() {
		isAtBottom = true;
		initializeFrame();
	}
	
	private void initializeFrame() {
		int padding = 5;
//		double ratio = 16.0/9.0;
		double ratio = (1.0+Math.sqrt(5.0))/2.0;
		Dimension screenDims = Toolkit.getDefaultToolkit().getScreenSize();
//		setLocation((screenDims.width - getWidth())/2,(screenDims.height - getHeight())/2);
		// TODO add server/client closing functionality on close
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		double height = screenDims.height/2;
		Dimension panelDims = new Dimension((int)(height*ratio),(int)height);
		panel.setPreferredSize(panelDims);
		panel.setMaximumSize(panelDims);
		panel.setMinimumSize(panelDims);
		
		
		int textMargin = 3;
		textArea = new JTextArea();
		textArea.setMargin(new Insets(textMargin,textMargin,textMargin,textMargin));
		textArea.setSize(panelDims.width - 2*padding,panelDims.height - 2*padding);
		textArea.setLocation(padding,padding);
		textArea.setEditable(false);
		scrollPane = new JScrollPane(textArea);
		scrollPane.setSize(textArea.getSize());
		scrollPane.setLocation(textArea.getLocation());
		scrollPane.getVerticalScrollBar().addAdjustmentListener(
				new AdjustmentListener() {
					public void adjustmentValueChanged(AdjustmentEvent e) {
						isAtBottom = e.getAdjustable().getValue() == e.getAdjustable().getMaximum();
						System.out.println("Scroller is "+isAtBottom);
					}
				});
		scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
		panel.add(scrollPane);
		
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		add(panel,BorderLayout.CENTER);
		pack();
		setSize(getWidth()-10,getHeight()-10);
		setResizable(false);
		setLocationRelativeTo(null);
		this.setVisible(true);
		
	}
	
	public void add(String message) {
		textArea.append("\n" + message);
		if (isAtBottom) {
			scrollPane.getVerticalScrollBar().setValue(
					scrollPane.getVerticalScrollBar().getMaximum());
		}
	}

}
