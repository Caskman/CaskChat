import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;


public class ClientManager {
	
	private List<ClientHandler> handlers;
	private String chatlog;
	private ChatWindow chatWindow;

	public ClientManager(ChatWindow chatWindow) {
		handlers = new LinkedList<ClientHandler>();
		chatlog = "Chat Begins!";
		this.chatWindow = chatWindow;
	}
	
	public void addClient(Socket client) {
		ClientHandler listener = new ClientHandler(client);
		handlers.add(listener);
		listener.start();
	}
	
	private void sendToAllBut(String name,String message) {
		for (ClientHandler cl : handlers) {
			if (cl.getClientName().compareTo(name) != 0)
				cl.send(message);
		}
	}
	
	private void addChatMessage(String name,String message) {
		String toSend = name+": "+message;
		sendToAllBut(name,toSend);
		chatlog += "\n"+toSend;
		chatWindow.addMessage(toSend);
	}
	
	private void clientDisconnected(String name) {
		String message = name+" disconnected.";
		sendToAllBut(name,message);
		chatlog += "\n"+message;
		chatWindow.addMessage(message);
	}
	
	private void clientJoined(String name) {
		String message = name+" joined chat.";
		sendToAllBut(name,message);
		chatlog += "\n"+message;
		chatWindow.addMessage(message);
	}

	
	private class ClientHandler extends Thread {
		
		private boolean flag;
		private BufferedReader in;
		private PrintWriter out;
		private Socket socket;
		private String name;
		
		public ClientHandler(Socket client) {
			super();
			flag = true;
			socket = client;
		}
		
		public String getClientName() {
			return name;
		}
		
		@Override
		public void run() {
			initializeClient();
			String inMessage = null;
			
			while (flag) {
				try {
					inMessage = in.readLine();
				} catch (IOException e) {
					System.out.println("Error trying to read message from client");
					e.printStackTrace();
					closeClient();
					clientDisconnected(name);
					break;
				}
				if (inMessage == null)
					continue;
				
				processMessage(inMessage);
			}
		}
		
		public void send(String s) {
			out.print(s+"\n");
			out.flush();
		}
		
		private void processMessage(String message) {
			addChatMessage(name,message);
		}
		
		private void initializeClient() {
			
			try {
				System.out.println("Getting outputstream to client");
				out = new PrintWriter(socket.getOutputStream(), true);
				System.out.println("outputstream to client successful");
			} catch (IOException e) {
				System.out.println("Error trying to get outputstream");
				e.printStackTrace();
			}
			try {
				System.out.println("Getting inputstream from client");
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				System.out.println("Getting inputstream from client successful");
			} catch (IOException e) {
				System.out.println("Error trying to get input stream");
				e.printStackTrace();
			}
			
			if (!verifyClient()) {
				closeClient();
			} else {
				System.out.println("Client Accepted");
				clientJoined(name);
			}
		}
		
		private void closeClient() {
			try {
				out.close();
				in.close();
				socket.close();
			} catch (IOException e) {
				System.out.println("Error trying to close client");
				e.printStackTrace();
			} finally {
				handlers.remove(this);
			}
			flag = false;
		}
		
		private boolean verifyClient() {
			try {
				System.out.println("Waiting for name from client");
				String message = in.readLine();
				System.out.println("Name received from client is "+message);
				name = message;
				send("Far Cry");
				System.out.println("Client verified");
				return true;
			} catch (IOException e) {
				System.out.println("Error in trying to receive verification from client");
				e.printStackTrace();
				return false;
			}
		}
		
	}
}
