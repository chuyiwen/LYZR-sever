/**
 * 
 */
package sophia.game.component;

import java.util.LinkedList;
import java.util.List;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.game.component.communication.GameEvent;
import sophia.game.core.GameFrame;



public abstract class AbstractComponent implements Component,
		ComponentChangedListener {
	private List<ComponentChangedListener> listeners;
	private GameObject owner;
	
	protected AbstractComponent() {
		
	}
	
	@Override
	public void setParent(GameObject owner) {
		this.owner = owner;
	}
	
	@Override
	public GameObject getParent() {
		return owner;
	}
	
	public GameFrame getGameFrame() {
		return (owner != null) ? owner.getGameFrame() : null;
	}
	
	@Override
	public void addListener(ComponentChangedListener listener) {
		
		if (listeners == null) {
			listeners = new LinkedList<ComponentChangedListener>();
		}
		
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
	
	@Override
	public void removeListener(ComponentChangedListener listener) {
		
		if (listeners == null) { return; }
		
		listeners.remove(listener);
		if (listeners.size() == 0) {
			listeners = null;
		}
	}
	
	protected void notifyChanged() {
		if (listeners == null) { return; }
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).changed(this);
		}
	}
	
	@Override
	public void ready() {
		
	}
	
	@Override
	public void suspend() {
		
	}
	
	@Override
	public void destroy() {
		if (listeners != null) {
			listeners.clear();
		}
	}
	
	@Override
	public void changed(Component component) {
	
	}
	
	@Override
	public void handleGameEvent(final GameEvent<?> event) {
		
	}
	
	@Override
	public void handleActionEvent(final ActionEventBase event) {
		
	}
	
	
	/**
	 * GameEvent发送给拥有者
	 * @param id
	 * @param data
	 */
	protected void sendGameEvent(String id, Object data) {
		GameEvent<?> event = GameEvent.getInstance(id, data);
		owner.handleGameEvent(event);
		GameEvent.pool(event);
	}
	
	protected void sendGameEvent(GameEvent<?> event) {
		owner.handleGameEvent(event);
	}
	
	/**
	 * GameEvent发送给指定接收者，若没有指定接收者，则发送给拥有者
	 * @param id
	 * @param data
	 * @param receiverId
	 */
	protected void sendGameEvent(String id, Object data, String receiverId) {
		GameEvent<?> event = GameEvent.getInstance(id, data);
		if (receiverId == null) {
			owner.handleGameEvent(event);
		} else {
			GameObject go = owner.getManager().getObjectForId(receiverId);
			if (go != null) {
				go.handleGameEvent(event);
			}
		}
		GameEvent.pool(event);
	}
	
	protected void sendGameEvent(GameEvent<?> event, String receiverId) {
		if (receiverId == null) {
			owner.handleGameEvent(event);
		} else {
			GameObject go = owner.getManager().getObjectForId(receiverId);
			if (go != null) {
				go.handleGameEvent(event);
			}
		}
	}
	
	/**
	 * 向所有监听对象发送GameEvent，无事件数据 
	 * @param id
	 */
	protected void fireGameEvent(String id) {
		GameEvent<?> event = GameEvent.getInstance(id);
		owner.getManager().getGameEventManager().fireGameEvent(event);
		GameEvent.pool(event);
	}
	
	/**
	 * 向所有监听对象发送GameEvent，有事件数据
	 * @param id
	 * @param data
	 */
	protected void fireGameEvent(String id, Object data) {
		GameEvent<?> event = GameEvent.getInstance(id, data);
		owner.getManager().getGameEventManager().fireGameEvent(event);
		GameEvent.pool(event);
	}
	
	/**
	 * 添加内部GameEventListener，GameObject内部事件
	 * @param eventId
	 */
	protected void addInterGameEventListener(String eventId) {
		owner.addGameEventListener(eventId, this);
	}
	
	/**
	 * 移除内部GameEventListener，GameObject内部事件
	 * @param eventId
	 */
	protected void removeInterGameEventListener(String eventId) {
		owner.removeGameEventListener(eventId, this);
	}
	
	/**
	 * 添加全局GameEventListener
	 * @param eventId
	 */
	protected void addIntraGameEventListener(String eventId) {
		owner.getManager().getGameEventManager().addGameEventListener(eventId, this);
	}
	
	/**
	 * 移除全局GameEventListener
	 * @param eventId
	 */
	protected void removeIntraGameEventListener(String eventId) {
		owner.getManager().getGameEventManager().removeGameEventListener(eventId, this);
	}
	
	/**
	 * 添加ActionEventListener
	 * @param eventId
	 */
	protected void addActionEventListener(short eventId) {
		owner.addActionEventListener(eventId, this);
	}
	
	/**
	 * 移除ActionEventListener
	 * @param eventId
	 */
	protected void removeActionEventListener(short eventId) {
		owner.removeActionEventListener(eventId, this);
	}
}
