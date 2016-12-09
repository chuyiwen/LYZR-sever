/**
 * 
 */
package sophia.game.component.communication;

import java.util.LinkedList;

public class GameEvent<T> {
	
	private static final LinkedList<GameEvent<?>> POOL = new LinkedList<GameEvent<?>>();
	public static int MAX_POOL_SIZE = 1000;

	public static int getCurrentPoolSize() {
		return POOL.size();
	}

	public static void setMaxPoolSize(int maxSize) {
		assert (maxSize >= 0);
		MAX_POOL_SIZE = maxSize;
	}

	@SuppressWarnings("rawtypes")
	public static GameEvent<?> getInstance(String id) {
		return new GameEvent(id);
//		synchronized (POOL) {
//			GameEvent<?> event;
//			if (POOL.size() > 0) {
//				event = POOL.pollFirst();
//				event.setId(id);
//				return event;
//			} else {
//				return new GameEvent(id);
//			}
//		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> GameEvent<T> getInstance(String id, T data) {
		return new GameEvent(id, data);
//		synchronized (POOL) {
//			GameEvent event;
//			if (POOL.size() > 0) {
//				event = POOL.pollFirst();
//				event.setId(id);
//				event.setData(data);
//				return event;
//			} else {
//				return new GameEvent(id, data);
//			}
//		}
	}

	public static void pool(GameEvent<?> event) {
//		assert (event != null);
//		synchronized (POOL) {
//			if (POOL.size() < MAX_POOL_SIZE && !POOL.contains(event)) {
//				event.id = "";
//				event.data = null;
//				POOL.addLast(event);
//			}
//		}
	}

	private String id;
	private T data;

	protected GameEvent(String id) {
		assert (id != null);
		assert (id.trim().length() > 0);
		setId(id);
	}

	protected GameEvent(String id, T data) {
		this(id);
		setData(data);
	}

	public String getId() {
		return id;
	}

	private void setId(String id) {
		assert (id != null && id.trim().length() > 0);
		this.id = id.intern();
	}

	public boolean isId(String id) {
		assert (id != null);
		return this.id == id.intern();
	}

	public T getData() {
		return data;
//		synchronized (POOL) {
//			return data;
//		}
	}

	private void setData(T data) {
		this.data = data;
//		synchronized (POOL) {
//			this.data = data;
//		}
	}

	@Override
	public String toString() {
		return id;
	}
}
