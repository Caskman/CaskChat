import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.google.protobuf.InvalidProtocolBufferException;

import protocols.ChatProtocol.*;


public class MenuAgent implements ConnectionListener {

	private Connection connection;
	private ChatMenu chatMenu;
	private boolean isConnected;
	
	public MenuAgent(ChatMenu c) {
		chatMenu = c;
		isConnected = false;
	}
	
	public void connect() {
		if (connection == null)
			connection = new Connection();
		connection.addConnectionListener(this);
		connection.start();
	}
	
	private void authenticate(NetMessage n) {
		connection.send(NetMessage.newBuilder()
				.setType(MessageType.AUTHENTICATION)
				.setAuthentication(Authentication.newBuilder()
						.setVersionID(Parameters.VERSION_ID)
						.setPassword(Parameters.PASSWORD))
				.build().toByteArray());
//		switch (n.type2) {
//		case NetObject.VERSION_ID:
//			connection.send(new NetObject(NetObject.AUTHENTICATE,NetObject.VERSION_ID,Parameters.VERSION_ID));
//			break;
//		case NetObject.PASSWORD:
//			connection.send(new NetObject(NetObject.AUTHENTICATE,NetObject.PASSWORD,Parameters.PASSWORD));
//			break;
//		}
			
	}

	@Override
	public void objectReceived(Object o) {
		NetMessage n;
		try {
			n = NetMessage.parseFrom((byte[])o);
			switch (n.getType()) {
			case AUTHENTICATION:
				authenticate(n);
				break;
			case REPLY:
				acknowledge(n);
				break;
			case ICON_IMAGE:
				iconImage(n);
				break;
			default:
				System.err.println("Unhandled NetMessage in MenuAgent");
				break;
			}
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}
	
	private void iconImage(NetMessage n) {
		try {
			byte[] iconData = n.getImage().getImageData().toByteArray();
			InputStream in = new ByteArrayInputStream(iconData);
			chatMenu.iconImageReceived(new ImageIcon(ImageIO.read(in)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void acknowledge(NetMessage n) {
		ReplyMessage r = n.getReplyMessage();
		switch (r.getType()) {
		case NAME_AVAIL:
			chatMenu.nameConfirmed(r.getString(),r.getStatus());
			break;
		case NAME_SET:
			if (r.getStatus())
				connection.send(new NetObject(NetObject.JOIN_CHAT));
			else
				chatMenu.joinDeniedCuzName();
			break;
		case JOIN_CHAT:
			if (r.getStatus())
				chatMenu.joinGranted();
			else
				chatMenu.joinDenied();
			break;
		default:
			System.err.println("Unhandled acknowledge case in MenuAgent");
			break;
		}
	}
	
	public void requestNameAvailability(String name) {
		connection.send(NetMessage.newBuilder()
				.setType(MessageType.NAME_AVAIL)
				.setString(name)
				.build().toByteArray());
//		connection.send(new NetObject(NetObject.NAME_AVAIL,name));
	}

	public Connection getConnection() {
		return connection;
	}
	
	@Override
	public void statusMessage(String s) {
		chatMenu.connectionStatus(s);
	}

	@Override
	public void hasConnected() {
		chatMenu.hasConnected();
		isConnected = true;
	}

	@Override
	public void connectionClosed(String errorMessage) {
		chatMenu.connectionStatus(errorMessage);
	}
	
	public void joinChat(String name) {
		connection.send(NetMessage.newBuilder()
				.setType(MessageType.NAME_SET)
				.setString(name)
				.build().toByteArray());
//		connection.send(new NetObject(NetObject.NAME_SET,name));
	}
	
	public boolean isConnected() {
		return isConnected;
	}
	
	
	
	
}
