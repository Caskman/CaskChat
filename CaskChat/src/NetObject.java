
public class NetObject {

	public static final int AUTHENTICATE = 0;
	public static final int VERSION_ID = 1;
	public static final int PASSWORD = 2;
	public static final int NAME_AVAIL = 3;
	public static final int ACKNOWLEDGE = 4;
	public static final int CHAT_MESSAGE = 4;
	
	public int type;
	public int type2;
	public double decimal;
	public String string;
	public String string2;
	public boolean bool;
	
	public NetObject() {
	}

	public NetObject(int type,int type2,double num) { // version id auth
		this.type = type;
		this.type2 = type2;
		decimal = num;
	}
	
	public NetObject(int type,int type2, String s) {// password auth
		this.type = type;
		this.type2 = type2;
		string = s;
	}
	
	public NetObject(int type,int type2,boolean b) { // reply whether name is available or not
		this.type = type;
		this.type2 = type2;
		bool = b;
	}
	
	public NetObject(int type,String s) {// name availability check, sending a chat message
		this.type = type;
		string = s;
	}
	
	
	public NetObject(int type) {
		this.type = type;
	}
	
}
