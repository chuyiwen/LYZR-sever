/**
 * 
 */
package sophia.game.framework;


public interface GameSimulator {
	/**
	 * 注册插件，初始化工作
	 */
	void initialize();
	
	/**
	 * 启动游戏服务
	 */
	void start();
	
	/**
	 * 停止游戏服务
	 */
	void stop();
	
	/**
	 * 清理工作
	 */
	void cleanUp();
}
