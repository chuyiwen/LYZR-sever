/**
 * 
 */
package sophia.game.plugIns.gameObjectManager;

import java.util.LinkedList;
import java.util.List;

import sophia.game.component.GameObject;




public class GameObjectPool {

	private Class<? extends GameObject> gameObjectClass;
	private int curId;
	
	private List<GameObject> pool;
	private int poolLimit = 0;
	
	public GameObjectPool(Class<? extends GameObject> gameObjectClass) {
		if (gameObjectClass == null) {
			throw new NullPointerException("Given class is null.");
		}
		
		this.gameObjectClass = gameObjectClass;
		//could also be set to Integer.MIN_VALUE but 0 is more readable
		curId = 0;
	}
	
	//==============================================
	// id creation
	//==============================================
	
	protected String createNewId() {
		StringBuffer id = new StringBuffer();
		id.append(gameObjectClass.getSimpleName());
		id.append(GameObject.ID_SEPERATOR);
		id.append(curId);
		curId++;
		
		if (curId == Integer.MAX_VALUE) {
			throw new RuntimeException("GameObjectController for " + gameObjectClass + " reached max id limit.");
		}
		
		return id.toString();
	}

	//==============================================
	// game object pooling
	//==============================================
	
	protected void createPool(int poolLimit) {
		if (poolLimit < 1) {
			throw new IllegalArgumentException("Pool limit (current: " + poolLimit 
				+ " can not be less than 1.");
			
		}
		
		this.poolLimit = poolLimit;
		pool = new LinkedList<GameObject>();
		
	}

	protected GameObject grabFromPool() {
		if (pool == null || pool.size() == 0) { 
			return null; 
		}
		
		GameObject go = pool.get(0);
		pool.remove(0);
		return go;
	}
	
	protected void putIntoPool(GameObject gameObject) {
		if (pool.size() < poolLimit) {
			pool.add(gameObject);
		}
	}
	
	protected boolean isPoolingEnabled() {
		return pool != null;
	}
}
