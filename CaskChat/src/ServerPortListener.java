import java.awt.EventQueue;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


	public class ServerPortListener extends Thread {
		
		private ServerSocket server;
		private boolean flag;
		private ClientManager manager;
		
		public ServerPortListener(ServerSocket server,ClientManager manager) {
			super();
			this.server = server;
			flag = true;
			this.manager = manager;
		}
		
		@Override
		public void run() {
			Socket potentialClient = null;
			
			while (flag) {
				try {
					potentialClient = server.accept();
					System.out.println("Client Discovered");
				} catch (IOException e) {
					System.out.println("Error accepting socket from client");
					e.printStackTrace();
					continue;
				}
				if (potentialClient == null)
					continue;
				
				final Socket socket = potentialClient;
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						manager.addClient(socket);
					}
				});
				
			}
		}
	}
