package sophia.game.core;


public interface GameFrameStatusListener {
	/**
	 * 通知GameFrame的状态变化
	 * @param gameFrame
	 * @param status
	 */
	void statusChanged(GameFrame gameFrame, GameFrame.Status status);
}
