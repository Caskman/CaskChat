import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;


public class ClientManager {
	
	private List<ClientAgent> handlers;
	private String chatlog;
	private ChatWindow chatWindow;

	public ClientManager(ChatWindow chatWindow) {
		handlers = new LinkedList<ClientAgent>();
		chatlog = "Chat Begins!";
		this.chatWindow = chatWindow;
	}
	
	public void addClient(Socket client) {
		ClientAgent listener = new ClientAgent(this,client);
		handlers.add(listener);
	}
	
	public void removeAgent(ClientAgent a) {
		handlers.remove(a);
	}
	
	private void sendToAllBut(String name,String message) {
		for (ClientAgent cl : handlers) {
			if (cl.getClientName().compareTo(name) != 0)
				cl.sendChatMessage(name,message);
		}
	}
	
	public void addChatMessage(String name,String message) {
		String toSend = name+": "+message;
		sendToAllBut(name,toSend);
		chatlog += "\n"+toSend;
		chatWindow.addMessage(toSend);
	}
	
	public void clientDisconnected(String name,String errorMessage) {
		String message = name+" disconnected: " + errorMessage;
		sendToAllBut(name,message);
		chatlog += "\n"+message;
		chatWindow.addMessage(message);
	}
	
	public void clientJoined(String name) {
		String message = name+" joined chat.";
		sendToAllBut(name,message);
		chatlog += "\n"+message;
		chatWindow.addMessage(message);
	}
	
	public void statusMessage(int id,String name,String message) {
		chatlog += "\n"+"Client "+id+" "+name+": "+message;
	}

	public boolean isNameAvailable(String s) {
		for (ClientAgent a : handlers) {
			if (s.compareTo(a.getClientName()) == 0)
				return false;
		}
		return true;
	}
	
	
}
