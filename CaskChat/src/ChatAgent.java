
public class ChatAgent implements ConnectionListener {

	private ChatWindow chatWindow;
	private Connection connection;
	
	public ChatAgent() {
	}
	
	public void setChatWindow(ChatWindow c) {
		chatWindow = c;
	}
	
	public void setConnection(Connection c) {
		connection = c;
		connection.addConnectionListener(this);
	}

	public void sendText(String s) {
		connection.send(new NetObject(NetObject.CHAT_MESSAGE,s));
	}

	@Override
	public void objectReceived(Object o) {
		NetObject n = (NetObject)o;
		switch (n.type) {
		case NetObject.CHAT_MESSAGE:
			chatWindow.addMessage(n.string);
			break;
		}
	}

	@Override
	public void statusMessage(String s) {
		chatWindow.addMessage(s);
	}

	@Override
	public void hasConnected() {
		// do nothing
	}

	@Override
	public void connectionClosed(String errorMessage) {
		chatWindow.addMessage("Disconnected from server: "+errorMessage);
	}
	
}
