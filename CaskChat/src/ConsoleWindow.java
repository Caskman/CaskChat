import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ConsoleWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3491204738493702095L;

	private JTextArea textArea;

	public ConsoleWindow() {
		initializeFrame();

		OutputStream out = new OutputStream() {
			@Override
			public void write(final int b) throws IOException {
				textArea.append(String.valueOf((char) b));
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				textArea.append(new String(b, off, len));
			}

			@Override
			public void write(byte[] b) throws IOException {
				write(b, 0, b.length);
			}
		};
		System.setOut(new PrintStream(out, true));
		System.setErr(new PrintStream(out, true));
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

		int textMargin = 3;
		textArea = new JTextArea();
		textArea.setMargin(new Insets(textMargin, textMargin, textMargin,
				textMargin));
		textArea.setSize(panelDims.width - 2 * padding, panelDims.height - 2
				* padding);
		textArea.setLocation(padding, padding);
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setSize(textArea.getSize());
		scrollPane.setLocation(textArea.getLocation());
		panel.add(scrollPane);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		add(panel, BorderLayout.CENTER);
		pack();
		setSize(getWidth() - 10, getHeight() - 10);
		setResizable(false);
		setLocationRelativeTo(null);
		this.setVisible(true);
	}

	public void add(String message) {
		textArea.append("\n" + message);
	}

}
