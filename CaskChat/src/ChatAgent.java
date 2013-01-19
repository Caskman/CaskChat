import com.google.protobuf.InvalidProtocolBufferException;
import protocols.ChatProtocol.*;
//import protocols.ChatProtocol.NetMessage;
//import protocols.ChatProtocol.MessageType;
//import protocols.ChatProtocol.ChatMessage;

public class ChatAgent implements ConnectionListener {

	private ChatWindow chatWindow;
	private Connection connection;
	private String name;
	
	public ChatAgent(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setChatWindow(ChatWindow c) {
		chatWindow = c;
	}
	
	public void setConnection(Connection c) {
		connection = c;
		connection.addConnectionListener(this);
	}

	public void sendText(String s) {
        NetMessage.Builder message = NetMessage.newBuilder()
                                        .setType(MessageType.CHAT_MESSAGE)
                                        .setChatMessage(ChatMessage.newBuilder().setMessage(s));
        connection.send(message.build().toByteArray());
// 		connection.send(new NetObject(NetObject.CHAT_MESSAGE,s));
	}

	public void requestChatPersonList() {
        NetMessage.Builder request = NetMessage.newBuilder()
                .setType(MessageType.LIST_UPDATE);
        connection.send(request.build().toByteArray());
//		connection.send(new NetObject(NetObject.CHAT_PERSON_LIST_UPDATE));
	}
	
	@Override
	public void objectReceived(Object o) {
        try {
		    NetMessage n = NetMessage.parseFrom((byte[])o);
		    System.out.println(n.getType().toString() + " message received");
            switch (n.getType()) {
                case CHAT_MESSAGE:
                    chatWindow.addMessage(n.getChatMessage().getMessage());
                    break;
                case LIST_UPDATE:
                    chatWindow.updateChatPersonList(n.getChatList().getPersonList());
                    break;
			default:
				System.err.println("Unhandled NetMessage in ChatAgent");
				break;
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
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
