/**
 * 
 */
package sophia.game.plugIns.gameEvent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sophia.game.component.communication.GameEvent;
import sophia.game.component.communication.GameEventListener;



public class GameEventManager {
	
	/**
	 * 防止出现
	 * fireGameEvent() -> comp receives event -> component destroys itself -> removeGameEventListener -> error
	 */
	class PendingListener {
		String eventId;
		GameEventListener listener;
		boolean register;
		
		public PendingListener(String eventId, GameEventListener listener, boolean register) {
			this.eventId = eventId;
			this.listener = listener;
			this.register = register;
		}
	}
	
	private Map<String, List<GameEventListener>> listeners;
	private List<PendingListener> pendingListeners;
	
	private boolean isFiring;			
	private String curFiredEventId;		
	
	public GameEventManager() {
		listeners = new HashMap<String, List<GameEventListener>>();
		pendingListeners = new LinkedList<PendingListener>();
	}
	
	public boolean hasRegistered(String eventId, GameEventListener listener) {
		assert(eventId != null);
		assert(listener != null);
		
		List<GameEventListener> registered = listeners.get(eventId);
		return (registered == null) ? false : registered.contains(listener);	
	}
	
	public void removeGameEventListener(String eventId, GameEventListener listener) {
		assert(eventId != null);
		assert(listener != null);
		
		if (isFiring && eventId.compareTo(curFiredEventId) == 0) { 
			pendingListeners.add(new PendingListener(eventId, listener, false));
			return;
		}
		
		List<GameEventListener> registered = listeners.get(eventId);
		if (registered == null) {
			registered = new LinkedList<GameEventListener>();
			listeners.put(eventId, registered);
		}
		
		registered.remove(listener);
	}
	
	public void addGameEventListener(String eventId, GameEventListener listener) {

		if (isFiring && eventId.compareTo(curFiredEventId) == 0) { 
			pendingListeners.add(new PendingListener(eventId, listener, true));
			return;
		}
		
		List<GameEventListener> registered = listeners.get(eventId);
		if (registered == null) {
			registered = new LinkedList<GameEventListener>();
			listeners.put(eventId, registered);
		}
		
		if (!registered.contains(listener)) { registered.add(listener); }
	}
	
	public void fireGameEvent(GameEvent<?> event) {
		
		List<GameEventListener> registered = listeners.get(event.getId());
		if (registered == null) { return; }
		
		isFiring = true;
		curFiredEventId = event.getId();
		
		for (int i = 0; i < registered.size(); i++) {
			registered.get(i).handleGameEvent(event);
		}
		
		isFiring = false;
		curFiredEventId = null;
		
		handlePendingListeners();
	}
	
	private void handlePendingListeners() {
		
		PendingListener cur;
		for (int i = 0; i < pendingListeners.size(); i++) {
			cur = pendingListeners.get(i);
			if (cur.register) {
				addGameEventListener(cur.eventId, cur.listener);
			} else {
				removeGameEventListener(cur.eventId, cur.listener);
			}
		}
	}
}
