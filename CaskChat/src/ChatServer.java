import java.io.IOException;
import java.net.ServerSocket;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


public class ChatServer {
	
	public static void main(String[] args) {
		try {
			// Set System L&F
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException e) {
			// handle exception
		} catch (ClassNotFoundException e) {
			// handle exception
		} catch (InstantiationException e) {
			// handle exception
		} catch (IllegalAccessException e) {
			// handle exception
		}

		new ChatServer();
	}
	

	private ServerSocket server;
	private ClientManager manager;
	
	public ChatServer() {
		new ConsoleWindow().setTitle("CaskChat Server Console");
		Parameters.load();
		try {
			server = new ServerSocket(Parameters.port);
		} catch (IOException e) {
			System.out.println("Error trying to open serversocket");
			e.printStackTrace();
		}
		manager = new ClientManager();
		new ServerPortListener(server,manager).start();
	}
	
	

}
