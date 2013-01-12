
public class ChatAgent {

	private ChatWindow chatWindow;
	private Connection connection;
	
	public ChatAgent() {
	}
	
	public void setChatWindow(ChatWindow c) {
		chatWindow = c;
	}
	
	public void setConnection(Connection c) {
		connection = c;
	}

	public void sendText(String s) {
		connection.send(new NetObject(NetObject.CHAT_MESSAGE,s));
	}
	
}
