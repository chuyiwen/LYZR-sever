/**
 * 
 */
package sophia.game.component;

import sophia.game.component.communication.ActionEventListener;
import sophia.game.component.communication.GameEventListener;


public interface Component extends GameEventListener, ActionEventListener {

	/**
	 * 设置组件的拥有者
	 * @param parent
	 */
	void setParent(GameObject parent);
	
	/**
	 * 获取组件的拥有者
	 * @return
	 */
	GameObject getParent();
	
	/**
	 * 准备就绪
	 */
	void ready();
	
	/**
	 * 挂起组件
	 */
	void suspend();
	
	/**
	 * 销毁
	 */
	void destroy();
	
	/**
	 * 添加监听
	 * @param listener
	 */
	void addListener(ComponentChangedListener listener);
	
	/**
	 * 移除监听
	 * @param listener
	 */
	void removeListener(ComponentChangedListener listener);
}
