/**
 * 
 */
package sophia.game.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.property.PropertyDictionary;
import sophia.foundation.util.DebugUtil;
import sophia.game.component.communication.ActionEventListener;
import sophia.game.component.communication.GameEvent;
import sophia.game.component.communication.GameEventListener;
import sophia.game.core.GameFrame;
import sophia.game.plugIns.gameObjectManager.GameObjectManager;

import com.google.common.base.Strings;

public class GameObject implements Component {
	
	private static final Logger logger = Logger.getLogger(GameObject.class);

	/**
	 * 游戏对象ID的分隔符，默认游戏对象ID为(GameObject.class.getSimpleName()#number)
	 */
	public static final String ID_SEPERATOR = "#";

	private GameObjectManager manager;
	private String id;
	private GameObject parent;

	private List<ComponentChangedListener> listeners;
	private ArrayList<Component> components;
	private Map<String, Component> tags;
	private Map<String, List<GameEventListener>> gameEvents;
	private Map<Short, List<ActionEventListener>> actionEvents;

	private PropertyDictionary property = new PropertyDictionary();
	
	public GameObject() {
		components = new ArrayList<Component>();
	}
	
	public PropertyDictionary getProperty() {
		return property;
	}

	public void setProperty(PropertyDictionary property) {
		this.property = property;
	}

	public Map<Short, List<ActionEventListener>> getActionEvents() {
		return actionEvents;
	}

	public void setId(String id) {
		assert(id != null && id.trim().length() > 0);
		this.id = id.intern(); 
	}

	public String getId() {
		return id.intern();
	}

	public void setManager(GameObjectManager manager) {
		this.manager = manager;
	}

	public GameObjectManager getManager() {
		return manager;
	}

	public GameFrame getGameFrame() {
		return (manager != null) ? manager.getGameFrame() : null;
	}

	public GameObject getRoot() {
		return (parent == null) ? this : parent.getRoot();
	}

	public boolean isRoot() {
		return parent == null;
	}

	public GameObject getParent() {
		return parent;
	}

	@Override
	public void setParent(GameObject parent) {
		this.parent = parent;
		if (gameEvents != null) {
			getRoot().addGameEventListener(gameEvents);
		}
	}

	@Override
	public void ready() {
		components.trimToSize();
		for (int i = 0; i < components.size(); i++) {
			components.get(i).ready();
		}
	}
	
	@Override
	public void suspend() {
		for (int i = 0; i < components.size(); i++) {
			components.get(i).suspend();
		}
	}

	@Override
	public void destroy() {
		Component comp;
		for (int i = 0; i < components.size(); i++) {
			comp = components.get(i);
			if (comp instanceof GameObject) {
				getManager().destroyGameObject((GameObject) comp);
			} else {
				components.get(i).destroy();
			}
		}

		components.clear();
		if (gameEvents != null) {
			gameEvents.clear();
		}
		if (actionEvents != null) {
			actionEvents.clear();
		}
		if (listeners != null) {
			listeners.clear();
		}
		if (tags != null) {
			tags.clear();
		}
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

		if (listeners == null) {
			return;
		}

		listeners.remove(listener);
		if (listeners.size() == 0) {
			listeners = null;
		}
	}

	protected void notifyChanged() {
		if (listeners == null) {
			return;
		}
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).changed(this);
		}
	}
	
	protected List<Component> getComponents() {
		return components;
	}

	public void addComponent(Component component) {
		assert (component != null);

		if (!components.contains(component)) {
			components.add(component);
		}
	}

	public void addComponent(Component component, String tag) {
		addComponent(component);
		tag(tag, component);
	}

	public Component createComponent(Class<? extends Component> type) {
		try {
			Component component = type.newInstance();
			component.setParent(this);
			components.add(component);
			return component;
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public Component createComponent(Class<? extends Component> type,
			String tag) {
		Component component = createComponent(type);
		tag(tag, component);
		return component;
	}

	public GameObject createGameObject(Class<? extends GameObject> type) {
		GameObject gameObject = manager.createGameObject(type);
		gameObject.setParent(this);
		addComponent(gameObject);
		return gameObject;
	}

	public GameObject createGameObject(Class<? extends GameObject> type,
			String tag) {
		GameObject gameObject = manager.createGameObject(type);
		gameObject.setParent(this);
		addComponent(gameObject, tag);
		return gameObject;
	}

	public GameObject createGameObject(Class<? extends GameObject> type,
			String tag, String id) {
		GameObject gameObject = manager.createGameObject(type, id);
		gameObject.setParent(this);
		addComponent(gameObject, tag);
		return gameObject;
	}

	public void removeComponent(Component component) {
		if (components.contains(component)) {
			components.remove(component);
			component.setParent(null);
		}
	}

	protected int getIndex(Component component) {
		return components.indexOf(component);
	}

	protected Component getComponent(int index) {
		return components.get(index);
	}

	public void tag(String tag, Component component) {
		if (tags == null) {
			tags = new HashMap<String, Component>();
		}

		tags.put(tag, component);
	}

	public Component getTagged(String tag) {
		return (tags != null) ? tags.get(tag) : null;
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {

		if (parent != null) {
			parent.handleGameEvent(event);
			return;
		}

		if (gameEvents == null) {
			return;
		}

		List<GameEventListener> listeners = gameEvents.get(event.getId());
		if (listeners == null) {
			return;
		}

		for (int i = 0; i < listeners.size(); i++) {
			GameEventListener gameEventListener = listeners.get(i);
			long l = System.currentTimeMillis();
			
			try {
				gameEventListener.handleGameEvent(event);
			} catch (Exception e) {
				logger.error("handleGameEvent error, " + DebugUtil.printStack(e));
			}
			
			l = System.currentTimeMillis() - l;
			if (l >= 100) {
				if (logger.isDebugEnabled()) {
					logger.debug(gameEventListener + " 处理请求GameEvent：" + event + "时间过久：" + l + "毫秒");
				}
			}
		}
	}

	public void addGameEventListener(String eventId, GameEventListener listener) {

		if (!isRoot()) {
			getRoot().addGameEventListener(eventId, listener);
			return;
		}

		if (gameEvents == null) {
			gameEvents = new HashMap<String, List<GameEventListener>>();
		}

		List<GameEventListener> listeners = gameEvents.get(eventId);
		if (listeners == null) {
			listeners = new LinkedList<GameEventListener>();
			gameEvents.put(eventId, listeners);
		}

		listeners.add(listener);
	}

	protected void addGameEventListener(
			Map<String, List<GameEventListener>> listeners) {

		if (!isRoot()) {
			getRoot().addGameEventListener(listeners);
		}

		if (gameEvents == null) {
			gameEvents = new HashMap<String, List<GameEventListener>>();
		}

		for (String eventId : listeners.keySet()) {
			List<GameEventListener> listenerList = gameEvents.get(eventId);
			if (listenerList != null) {
				listenerList.addAll(listeners.get(eventId));
			} else {
				gameEvents.put(eventId, listeners.get(eventId));
			}
		}
	}

	public void removeGameEventListener(String eventId,
			GameEventListener listener) {

		if (!isRoot()) {
			getRoot().removeGameEventListener(eventId, listener);
		}

		if (gameEvents == null) {
			return;
		}
		List<GameEventListener> listeners = gameEvents.get(eventId);
		if (listeners == null) {
			return;
		}
		listeners.remove(listener);

		if (listeners.size() == 0) {
			gameEvents.remove(eventId);
			if (gameEvents.size() == 0) {
				gameEvents = null;
			}
		}
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {

		if (parent != null) {
			parent.handleActionEvent(event);
			return;
		}

		if (actionEvents == null) {
			return;
		}

		List<ActionEventListener> listeners = actionEvents.get(event
				.getActionEventId());
		if (listeners == null) {
			return;
		}

		for (int i = 0; i < listeners.size(); i++) {
			ActionEventListener actionEventListener = listeners.get(i);
			Class<? extends ActionEventListener> clazz = actionEventListener.getClass();
			if (!ComponentShield.containComponent(clazz.getSimpleName())) {
				actionEventListener.handleActionEvent(event);
			}
		}
	}

	public void addActionEventListener(Short eventId,
			ActionEventListener listener) {

		if (!isRoot()) {
			getRoot().addActionEventListener(eventId, listener);
			return;
		}

		if (actionEvents == null) {
			actionEvents = new HashMap<Short, List<ActionEventListener>>();
		}

		List<ActionEventListener> listeners = actionEvents.get(eventId);
		if (listeners == null) {
			listeners = new LinkedList<ActionEventListener>();
			actionEvents.put(eventId, listeners);
		}

		listeners.add(listener);
	}

	protected void addActionEventListener(
			Map<Short, List<ActionEventListener>> listeners) {

		if (!isRoot()) {
			getRoot().addActionEventListener(listeners);
		}

		if (actionEvents == null) {
			actionEvents = new HashMap<Short, List<ActionEventListener>>();
		}

		actionEvents.putAll(listeners);
	}

	public void removeActionEventListener(Short eventId,
			ActionEventListener listener) {

		if (!isRoot()) {
			getRoot().removeActionEventListener(eventId, listener);
		}

		if (actionEvents == null) {
			return;
		}
		List<ActionEventListener> listeners = actionEvents.get(eventId);
		if (listeners == null) {
			return;
		}
		listeners.remove(listener);

		if (listeners.size() == 0) {
			actionEvents.remove(eventId);
			if (actionEvents.size() == 0) {
				actionEvents = null;
			}
		}
	}

	@Override
	public String toString() {
		if (Strings.isNullOrEmpty(id)) {
			return "default";
		}
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GameObject other = (GameObject) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
