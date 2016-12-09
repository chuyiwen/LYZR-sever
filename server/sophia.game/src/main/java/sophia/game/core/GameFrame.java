/**
 * 
 */
package sophia.game.core;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import sophia.game.framework.GameSimulator;



public class GameFrame {
	/**
	 * GameFrame的状态
	 */
	public static enum Status {
		/**
		 * 初始状态
		 */
		ORIGINAL,

		/**
		 * 准备初始化
		 */
		INITIALIZING,

		/**
		 * 初始化完成
		 */
		INITIALIZED,

		/**
		 * 准备开始
		 */
		STARTING,

		/**
		 * 开始完成
		 */
		STARTED,

		/**
		 * 准备停止
		 */
		STOPPING,

		/**
		 * 停止完成
		 */
		STOPPED,

		/**
		 * 准备清除
		 */
		CLEANING,

		/**
		 * 清除完成
		 */
		CLEANED
	};

	private Status curStatus;
	private GameFrameStatusListener statusListener;
	private GameSimulator gameApp;
	private DependencyManager dependencyManager;

	private List<PlugIn<?>> plugIns;

	public GameFrame() {
		plugIns = new LinkedList<PlugIn<?>>();
		plugIns.add(new GameFramePlugIn(this));
		this.dependencyManager = new DependencyManager(plugIns);
		this.curStatus = Status.ORIGINAL;
	}

	public GameFrame(GameFrameStatusListener listener) {
		this();
		this.setStatusListener(listener);
	}
	
	public void initialize(GameSimulator gameApp) {
		if (gameApp == null) {
			throw new NullPointerException("GameSimulator is null");
		}

		if (this.gameApp != null) {
			throw new GameFrameException(gameApp, this.gameApp);
		}
		
		this.setCurStatus(Status.ORIGINAL);
		
		// 根据依赖，排序插件
		dependencyManager.sortPlugIns();

		this.gameApp = gameApp;
		gameApp.initialize();
		
		initialize();
	}
	
	public void startUp() {
		start();
		gameApp.start();
	}
	
	public void shutDown() {
		stop();
		gameApp.stop();

		cleanUp();
		gameApp.cleanUp();

		gameApp = null;
	}

	private void initialize() {
		
		setCurStatus(Status.INITIALIZING);

		List<PlugIn<?>> inited = new LinkedList<PlugIn<?>>();
		PlugIn<?> curPlugIn;
		for (int i = 0; i < plugIns.size(); i++) {
			curPlugIn = plugIns.get(i);
			dependencyManager.injectDependencies(curPlugIn, inited);
			curPlugIn.initialize();
			assert (curPlugIn.getModule() != null);
			inited.add(curPlugIn);
		}

		setCurStatus(Status.INITIALIZED);
	}

	private void start() {
		setCurStatus(Status.STARTING);

		for (int i = plugIns.size() - 1; i >= 0; i--) {
			plugIns.get(i).start();
		}

		setCurStatus(Status.STARTED);
	}

	private void stop() {
		setCurStatus(Status.STOPPING);

		for (int i = plugIns.size() - 1; i >= 0; i--) {
			plugIns.get(i).stop();
		}

		setCurStatus(Status.STOPPED);
	}

	private void cleanUp() {
		setCurStatus(Status.CLEANING);

		for (int i = plugIns.size() - 1; i >= 0; i--) {
			plugIns.get(i).cleanUp();
		}

		setCurStatus(Status.CLEANED);
	}

	public Status getCurStatus() {
		return curStatus;
	}

	public void setCurStatus(Status curStatus) {
		this.curStatus = curStatus;
	}

	public GameFrameStatusListener getStatusListener() {
		return statusListener;
	}

	public void setStatusListener(GameFrameStatusListener statusListener) {
		this.statusListener = statusListener;
	}

	public List<PlugIn<?>> getPlugIns() {
		return Collections.unmodifiableList(plugIns);
	}

	public boolean containsPlugIn(PlugIn<?> plugIn) {
		if (plugIn == null) {
			throw new NullPointerException("Given plug-in is null.");
		}
		return plugIns.contains(plugIn);
	}

	public PlugIn<?> requestPlugIn(Class<PlugIn<?>> plugInClass) {
		Iterator<PlugIn<?>> iter = plugIns.iterator();
		PlugIn<?> plugIn;
		while (iter.hasNext()) {
			plugIn = iter.next();
			if (plugInClass.isAssignableFrom(dependencyManager
					.getModuleClass(plugIn))) {
				return plugIn;
			}
		}
		throw new PlugInException(plugInClass);
	}

	public void registerPlugIn(PlugIn<?> plugIn) {

		if (plugIn == null) {
			throw new NullPointerException("The given plug in is null.");
		}

		// 只能在状态为ORIGINAL的时候注册插件
		if (curStatus != Status.ORIGINAL) {
			throw new PlugInException(plugIn, curStatus);
		}

		synchronized (plugIns) {
			// 每个插件只能注册一次
			if (plugIns.contains(plugIn)) {
				throw new PlugInException(plugIn);
			}
			plugIns.add(plugIn);
		}
	}

	public void unregisterPlugIn(PlugIn<?> plugIn) {

		if (plugIn == null) {
			throw new NullPointerException(
					"Given plug in for deregistration is null.");
		}

		if (curStatus != Status.CLEANED) {
			throw new PlugInException(plugIn, curStatus);
		}

		synchronized (plugIns) {
			if (!plugIns.contains(plugIn)) {
				throw new PlugInException(plugIn);
			}
			plugIns.remove(plugIn);
		}
	}

	public boolean containsModule(Class<?> moduleClass) {
		assert (moduleClass != null);
		return dependencyManager.containsModule(moduleClass, plugIns);
	}

	public Object requestModule(Class<?> moduleClass) {
		assert (moduleClass != null);
		Object module = dependencyManager.getModule(moduleClass, plugIns);

		if (module == null) {
			throw new ModuleException(moduleClass);
		}
		return module;
	}
}
