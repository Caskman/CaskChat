
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
	
	private void authenticate(NetObject n) {
		switch (n.type2) {
		case NetObject.VERSION_ID:
			connection.send(new NetObject(NetObject.AUTHENTICATE,NetObject.VERSION_ID,Parameters.VERSION_ID));
			break;
		case NetObject.PASSWORD:
			connection.send(new NetObject(NetObject.AUTHENTICATE,NetObject.PASSWORD,Parameters.PASSWORD));
			break;
		}
			
	}

	@Override
	public void objectReceived(Object o) {
		NetObject n = (NetObject)o;
		switch (n.type) {
		case NetObject.AUTHENTICATE:
			authenticate(n);
			break;
		case NetObject.ACKNOWLEDGE:
			acknowledge(n);
			break;
		}
	}
	
	private void acknowledge(NetObject n) {
		switch (n.type2) {
		case NetObject.NAME_AVAIL:
			chatMenu.nameConfirmed(n.string,n.bool);
			break;
		case NetObject.NAME_SET:
			if (n.bool)
				connection.send(new NetObject(NetObject.JOIN_CHAT));
			else
				chatMenu.joinDeniedCuzName();
			break;
		case NetObject.JOIN_CHAT:
			if (n.bool)
				chatMenu.joinGranted();
			else
				chatMenu.joinDenied();
			break;
		}
	}
	
	public void requestNameAvailability(String name) {
		connection.send(new NetObject(NetObject.NAME_AVAIL,name));
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
		connection.send(new NetObject(NetObject.NAME_SET,name));
	}
	
	public boolean isConnected() {
		return isConnected;
	}
	
}
