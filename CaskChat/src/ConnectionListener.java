
public interface ConnectionListener {
	
	public void objectReceived(Object o);
	
	public void statusMessage(String s);
	
	public void hasConnected();
	
	public void connectionClosed(String errorMessage);
}
