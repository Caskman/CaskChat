
public class MenuAgent implements ConnectionListener {

	private Connection connection;
	private ChatMenu chatMenu;
	
	public MenuAgent(ChatMenu c) {
		chatMenu = c;
	}
	
	public void connect() {
		if (connection == null)
			connection = new Connection();
		connection.addConnectionListener(this);
		connection.start();
	}
	
	private void authenticate(NetObject o) {
		if (o == null)
			connection.send(new NetObject(NetObject.AUTHENTICATE,NetObject.VERSION_ID,Parameters.VERSION_ID));
		else if (o.type2 == NetObject.PASSWORD)
			connection.send(new NetObject(NetObject.AUTHENTICATE,NetObject.PASSWORD,Parameters.PASSWORD));
			
	}

	@Override
	public void relay(Object o) {
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
		}
	}
	
	public void checkNameAvailability(String name) {
		connection.send(new NetObject(NetObject.NAME_AVAIL,name));
	}

	public Connection getConnection() {
		return connection;
	}
	
	@Override
	public void relayStatus(String s) {
		chatMenu.connectionStatus(s);
	}

	@Override
	public void hasConnected() {
		authenticate(null);
	}


	@Override
	public void connectionClosed(String errorMessage) {
		chatMenu.connectionStatus(errorMessage);
	}
	
}
