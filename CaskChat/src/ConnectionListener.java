
public interface ConnectionListener {
	
	public void relay(Object o);
	
	public void relayStatus(String s);
	
	public void hasConnected();
	
	public void connectionClosed(String errorMessage);
}
