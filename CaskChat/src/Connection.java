import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class Connection extends Thread  {

	private Socket socket;
	private boolean flag;
	private boolean isOpen;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private String name;
	private List<ConnectionListener> listeners;

	public Connection() {
		constructInit();
	}

	public Connection(Socket s) {
		socket = s;
		constructInit();
	}
	
	private void constructInit() {
		flag = true;
		listeners = new ArrayList<ConnectionListener>();
		isOpen = false;
	}
	
	@Override
	public void run() {
		initialize();
		Object message = null;

		while (flag) {
			try {
				message = in.readObject();
			} catch (IOException e) {
				System.out.println("Error trying to read line from server");
				statusMessage("Disconnected from Server");
				errorCloseClient("Disconnected from Server");
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				statusMessage("Error finding class of object from stream");
				e.printStackTrace();
			}
			if (message == null)
				continue;
			relayMessage(message);
		}

	}

	private void relayMessage(Object message) {
		final Object o = message;
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				for (ConnectionListener l : listeners) {
					l.relay(o);
				}
			}
		});
	}

	public void addConnectionListener(ConnectionListener l) {
		listeners.add(l);
	}
	
	public void removeConnectionListener(ConnectionListener l) {
		listeners.remove(l);
	}
	
	private void initialize() {

		try {
			if (socket == null) {
				statusMessage("Connecting to server...");
				socket = new Socket(Parameters.SERVER, Parameters.port);
			}
		} catch (UnknownHostException e) {
			System.out.println("Error trying to connect to host");
			statusMessage("Unable to reach server");
			this.errorCloseClient("Unable to reach server");
			e.printStackTrace();
			return;
		} catch (IOException e) {
			System.out.println("Error trying to connect to host");
			statusMessage("Unable to reach server");
			this.errorCloseClient("Unable to reach server");
			e.printStackTrace();
			return;
		}

		try {
			System.out.println("Getting outputstream to server");
			out = new ObjectOutputStream(socket.getOutputStream());
			System.out.println("getting outputstream to server successful");
		} catch (IOException e) {
			System.out.println("Error trying to get output stream");
			e.printStackTrace();
		}
		try {
			System.out.println("Getting inputstream from server");
			in = new ObjectInputStream(socket.getInputStream());
			System.out.println("getting inputstream from server successful");
		} catch (IOException e) {
			System.out.println("Error trying to get input stream");
			e.printStackTrace();
		}

		statusMessage("Connection successful!");
		isOpen = true;
		
		hasConnected();
	}

	private void hasConnected() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				for (ConnectionListener l : listeners) {
					l.hasConnected();
				}
			}
		});
	}
	
	private void errorCloseClient(String errorMessage) {
		closeObjects();
		flag = false;
		connectionClosed(errorMessage);
	}
	
	public void close() {
		closeObjects();
		flag = false;
	}
	
	private void closeObjects() {
		try {
			if (out != null)
				out.close();
			if (in != null)
				in.close();
			if (socket != null)
				socket.close();
		} catch (IOException e) {
			System.out.println("Error trying to close objects");
			e.printStackTrace();
		}
		isOpen = false;
	}
	
	private void connectionClosed(String errorMessage) {
		final String errMessage = errorMessage;
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				for (ConnectionListener l : listeners) {
					l.connectionClosed(errMessage);
				}
			}
		});
	}

	public void send(Object message) {
		if (isOpen) {
			try {
				out.writeObject(message);
				out.flush();
			} catch (IOException e) {
				statusMessage("Error writing object to stream");
				e.printStackTrace();
				errorCloseClient("Error writing object to stream");
			}
		}
	}
	
	private void statusMessage(String s) {
		final String s1 = s;
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				for (ConnectionListener l : listeners) {
					l.relayStatus(s1);
				}
			}
		});
	}
	
}
