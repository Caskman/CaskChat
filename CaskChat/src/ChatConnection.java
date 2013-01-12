import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class ChatConnection extends Thread  {

	private Socket socket;
	private boolean flag;
	private BufferedReader in;
	private PrintWriter out;
	private String name;
	private List<ConnectionListener> listeners;

	public ChatConnection() {
		flag = true;
		listeners = new ArrayList<ConnectionListener>();
	}

	@Override
	public void run() {
		initialize();
		String message = null;

		while (flag) {
			try {
				message = in.readLine();
			} catch (IOException e) {
				System.out.println("Error trying to read line from server");
				chatWindow.addMessage("Disconnected from Server");
				closeClient();
				e.printStackTrace();
			}
			if (message == null)
				continue;
			processMessage(message);
		}

	}

	private void propagateMessage(String message) {
		for (ConnectionListener l : listeners) {
			l.messageReceived(message);
		}
	}

	public void addConnectionListener(ConnectionListener l) {
		listeners.add(l);
	}
	
	public void removeConnectionListener(ConnectionListener l) {
		listeners.remove(l);
	}
	
	private void initialize() {
		propagateMessage("Connecting to CaskChat server...");

		try {
			socket = new Socket(Parameters.SERVER, Parameters.port);
		} catch (UnknownHostException e) {
			System.out.println("Error trying to connect to host");
			propagateMessage("Unable to reach CaskChat server");
			this.closeClient();
			e.printStackTrace();
			return;
		} catch (IOException e) {
			System.out.println("Error trying to connect to host");
			propagateMessage("Unable to reach CaskChat server");
			this.closeClient();
			e.printStackTrace();
			return;
		}

		try {
			System.out.println("Getting outputstream to server");
			out = new PrintWriter(socket.getOutputStream(), true);
			System.out.println("getting outputstream to server successful");
		} catch (IOException e) {
			System.out.println("Error trying to get output stream");
			e.printStackTrace();
		}
		try {
			System.out.println("Getting inputstream from server");
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			System.out.println("getting inputstream from server successful");
		} catch (IOException e) {
			System.out.println("Error trying to get input stream");
			e.printStackTrace();
		}

		chatWindow.addMessage("Connection successful!");

		name = JOptionPane.showInputDialog("What name would you like to use?");
		chatWindow.setChatName(name);

		System.out.println("Sending name to server");
		send(name);
		try {
			System.out.println("Waiting for confirmation from server");
			String message = in.readLine();
			System.out.println("Confirmation message from server was "
					+ message);
			if (message.compareTo("Far Cry") != 0) {
				System.out.println("Confirmation from server incorrect");
				closeClient();
			} else
				System.out.println("Confirmation from server successful");
		} catch (IOException e) {
			System.out.println("Error trying to read line during verification");
			e.printStackTrace();
		}
	}

	private void closeClient() {
		try {
			if (out != null)
				out.close();
			if (in != null)
				in.close();
			if (socket != null)
				socket.close();
		} catch (IOException e) {
			System.out.println("Error trying to close down client");
			e.printStackTrace();
		}
		flag = false;
	}

	public void sendText(String s) {
		send(s);
		chatWindow.addMessage("You: " + s);
	}

	public void send(String message) {
		out.write(message + "\n");
		out.flush();
	}
}
