import java.awt.EventQueue;
import java.net.Socket;

public class ClientAgent implements ConnectionListener {

	private static int idCount = 0;
	private String name;
	private Connection connection;
	private boolean isVerified;
	private boolean isInChat;
	private boolean hasName;
	private int id;
	private ClientManager manager;

	public ClientAgent(ClientManager m, Socket client) {
		super();
		manager = m;
		isVerified = false;
		isInChat = false;
		hasName = false;
		id = idCount++;
		connection = new Connection(client);
		connection.addConnectionListener(this);
		connection.start();
	}

	public String getClientName() {
		if (hasName)
			return name;
		else 
			return "";
	}

	public void sendChatMessage(String name, String message) {
		connection.send(new NetObject(NetObject.CHAT_MESSAGE, message));
	}

	@Override
	public void objectReceived(Object o) {
		NetObject n = (NetObject) o;
		switch (n.type) {
		case NetObject.AUTHENTICATE:
			authenticate(n);
			break;
		case NetObject.CHAT_MESSAGE:
			chatMessageReceived(n);
			break;
		case NetObject.NAME_AVAIL:
			checkNameAvailability(n);
			break;
		case NetObject.NAME_SET:
			setName(n);
			break;
		case NetObject.JOIN_CHAT:
			joinChat();
			break;
		}
	}
	
	private void joinChat() {
		if (isVerified && hasName) {
			isInChat = true;
			connection.send(new NetObject(NetObject.ACKNOWLEDGE,NetObject.JOIN_CHAT,true));
		} else 
			connection.send(new NetObject(NetObject.ACKNOWLEDGE,NetObject.JOIN_CHAT,false));
	}
	
	private void setName(NetObject n) {
		if (manager.isNameAvailable(n.string)) {
			name = n.string;
			connection.send(new NetObject(NetObject.ACKNOWLEDGE,NetObject.NAME_SET,true));
			hasName = true;
		} else {
			connection.send(new NetObject(NetObject.ACKNOWLEDGE,NetObject.NAME_SET,false));
		}
	}
	
	private void checkNameAvailability(NetObject n) {
		connection.send(new NetObject(NetObject.ACKNOWLEDGE,NetObject.NAME_AVAIL,manager.isNameAvailable(n.string)));
	}

	private void close() {
		manager.removeAgent(this);
	}

	private void chatMessageReceived(NetObject n) {
		if (isInChat) {
			final String message = n.string;
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					manager.addChatMessage(name, message);
				}
			});
		}
	}

	private void authenticate(NetObject n) {
		switch (n.type2) {
		case NetObject.VERSION_ID:
			if (n.decimal == Parameters.VERSION_ID)
				connection.send(new NetObject(NetObject.PASSWORD));
			else
				connection.close();
			break;
		case NetObject.PASSWORD:
			if (n.string.compareTo(Parameters.PASSWORD) == 0)
				isVerified = true;
			else
				connection.close();
			break;
		}
	}

	@Override
	public void statusMessage(String s) {
		if (hasName)
			manager.statusMessage(id, name, s);
		else
			manager.statusMessage(id, "NoName", s);
	}

	@Override
	public void hasConnected() {
		// do nothing
	}

	@Override
	public void connectionClosed(String errorMessage) {
		if (isInChat)
			manager.clientDisconnected(name,errorMessage);
		close();
	}

}
