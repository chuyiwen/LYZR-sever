package sophia.game.utils.eventBuf;

/**
 * 可执行标记接口 
 * 
 * @version 1.0
 */
public interface Executable {
	/**
	 * 执行
	 * 
	 * @param doer
	 */
	void execute(IDoer doer);
}
