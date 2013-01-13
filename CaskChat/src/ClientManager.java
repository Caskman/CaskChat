import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;


public class ClientManager {
	
	private List<ClientAgent> handlers;
	private ObservationWindow statusWindow;
	private ObservationWindow chatWindow;

	public ClientManager() {
		handlers = new LinkedList<ClientAgent>();
		statusWindow = new ObservationWindow();
		statusWindow.setTitle("CaskChat Server Status");
		chatWindow = new ObservationWindow();
		chatWindow.setTitle("CaskChat Server");
	}
	
	public boolean hasIconImage() {
		return Parameters.APP_ICON != null;
	}
	
	public ImageIcon getIconImage() {
		return Parameters.APP_ICON;
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
		chatWindow.add(toSend);
	}
	
	public void clientDisconnected(String name,String errorMessage) {
		String message = name+" disconnected: " + errorMessage;
		sendToAllBut(name,message);
		chatWindow.add(message);
		updateClientChatPersonLists();
	}
	
	public void clientJoined(String name) {
		String message = name+" joined chat.";
		sendToAllBut(name,message);
		chatWindow.add(message);
		
		updateClientChatPersonLists();
	}
	
	private void updateClientChatPersonLists() {
		ChatPerson[] list = getChatPersonList();
		
		for (ClientAgent a : handlers) {
			a.updateChatPersonList(list);
		}
	}
	
	public void statusMessage(int id,String name,String message) {
		String statusMessage = "Client "+id+" "+name+": "+message;
		statusWindow.add(statusMessage);
	}

	public boolean isNameAvailable(String s) {
		for (ClientAgent a : handlers) {
			if (s.compareTo(a.getClientName()) == 0)
				return false;
		}
		return true;
	}
	
	public ChatPerson[] getChatPersonList() {
		List<ChatPerson> list = new LinkedList<ChatPerson>();
		
		for (ClientAgent a : handlers) {
			if (a.isInChat())
				list.add(a.getChatPerson());
		}
		return list.toArray(new ChatPerson[list.size()]);
	}
	
	
	
}
