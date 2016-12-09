/**
 * 
 */
package sophia.game.core;


public interface PlugIn<T> {
	/**
	 * 返回插件对应Module的引用 
	 * @return 返回插件对应Module的对象
	 */
	T getModule();
	
	/**
	 * 初始化
	 */
	void initialize();
	
	/**
	 * 启动
	 */
	void start();
	
	/**
	 * 结束
	 */
	void stop();
	
	/**
	 * 清理
	 */
	void cleanUp();
}
