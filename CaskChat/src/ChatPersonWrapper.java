import protocols.ChatProtocol.ChatPerson;


public class ChatPersonWrapper {
	
	private ChatPerson person;
	
	public ChatPersonWrapper(ChatPerson p) {
		person = p;
	}
	
	@Override
	public String toString() {
		return person.getName();
	}
}
