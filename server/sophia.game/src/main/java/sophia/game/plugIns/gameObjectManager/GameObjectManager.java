/**
 * 
 */
package sophia.game.plugIns.gameObjectManager;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import sophia.game.component.GameObject;
import sophia.game.core.GameFrame;
import sophia.game.plugIns.gameEvent.GameEventManager;


public class GameObjectManager {
	
	private GameFrame gameFrame;
	private GameEventManager gameEventManager;
	
	private ConcurrentHashMap<Class<? extends GameObject>, GameObjectPool> controllers;
	private ConcurrentHashMap<String, GameObject> objects;
	
	public GameObjectManager(GameFrame gameFrame, GameEventManager gameEventManager) {
		assert(gameFrame != null);
		assert(gameEventManager != null);
		
		this.gameFrame = gameFrame;
		this.gameEventManager = gameEventManager;
		
		controllers = new ConcurrentHashMap<Class<? extends GameObject>, GameObjectPool>();
		objects = new ConcurrentHashMap<String, GameObject>();
	}
	
	public GameFrame getGameFrame() {
		return gameFrame;
	}
	
	public GameEventManager getGameEventManager() {
		return gameEventManager;
	}
	
	public Map<String, GameObject> getObjects() {
		return Collections.unmodifiableMap(objects);
	}

	//==============================================
	// game object management
	//==============================================
	public GameObject getObjectForId(String gameObjectId) {
		return objects.get(gameObjectId);
	}
	
	public void addGameObjectPool(Class<? extends GameObject> type, GameObjectPool pool) {
		controllers.put(type, pool);
	}

	private GameObjectPool getGameObjectPool(Class<? extends GameObject> type) {
		GameObjectPool controller = controllers.get(type);
		if (controller == null) {
			controller = new GameObjectPool(type);
			GameObjectPool absent = controllers.putIfAbsent(type, controller);
			if (absent != null) {
				controller = absent;
			}
		}
		return controller;
	}
	
	// add ==========================================
	public void addGameObject(GameObject gameObject) {
		if (objects.putIfAbsent(gameObject.getId(), gameObject) == null) {
			gameObject.setManager(this);
			gameObject.ready();
		}
	}
	
	// remove =======================================
	public void removeGameObject(GameObject gameObject) {
		objects.remove(gameObject.getId());				//remove from id map
		gameObject.suspend();
	}
	
	// destruction =================================
	public void destroyGameObject(GameObject gameObject) {
		objects.remove(gameObject.getId());
		gameObject.destroy();
	}
	
	// creation ======================================	
	public GameObject createGameObject(Class<? extends GameObject> type) {
		//auto generated id
		GameObjectPool controller = getGameObjectPool(type);
		return createGameObject(type, controller.createNewId());
	}
	
	public GameObject createGameObject(Class<? extends GameObject> type, String id) {
		try {
			
			GameObjectPool con = getGameObjectPool(type);
			GameObject go = null;
			if (con.isPoolingEnabled()) {
				go = con.grabFromPool();
			}
			
			if (go == null) {
				go = type.newInstance();
			}
			
			go.setManager(this);	
			go.setId(id);			

			assert(!objects.containsKey(go.getId()));
			objects.put(go.getId(), go);
			return go;
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	
	// pooling
	public void enablePooling(Class<? extends GameObject> type, int initialPoolSize, int poolLimit) {
		if (initialPoolSize < 0) { 
			throw new IllegalArgumentException("Initial pool size for " 
				+ type.getName() + " can not be less than zero (current: " 
				+ initialPoolSize + ")"); 
		}

		GameObjectPool controller = getGameObjectPool(type);
		controller.createPool(poolLimit);
		
		/*
		try {
			for (int i = 0; i < initialPoolSize; i++) {
				controller.putIntoPool(type.newInstance());
			}
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		*/
	}
}
