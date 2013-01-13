import java.io.Serializable;


public class ChatPerson implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6493174239474390478L;
	public String name;
	
	public ChatPerson(String n) {
		name = n;
	}

	@Override
	public String toString() {
		return name;
	}
	
	
}
