import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;


public class ChatListModel implements ListModel<ChatPerson> {

	private List<ChatPerson> list;
	private List<ListDataListener> listeners;
	
	public ChatListModel() {
		list = new ArrayList<ChatPerson>();
		listeners = new LinkedList<ListDataListener>();
	}

	public void updateList(ChatPerson[] array) {
		list.clear();
		for (ChatPerson p : array) {
			list.add(p);
		}
		notifyListeners(new ListDataEvent(this,ListDataEvent.CONTENTS_CHANGED,0,list.size()-1));
	}
	
	private void notifyListeners(ListDataEvent e) {
		for (ListDataListener l : listeners) {
			l.contentsChanged(e);
		}
	}
	
	@Override
	public void addListDataListener(ListDataListener l) {
		listeners.add(l);
	}

	@Override
	public ChatPerson getElementAt(int i) {
		return list.get(i);
	}

	@Override
	public int getSize() {
		return list.size();
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		listeners.remove(l);
	}

}
