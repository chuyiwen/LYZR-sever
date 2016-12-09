/**
 * 
 */
package sophia.foundation.tick;

/**      
 * <b>游戏世界过去的一个时间间隔tick的监听者</b>
 * 
 * <br>这个名字送给丢失的岁月和姑娘。:)
 * <br>也可以"装B"，献给马塞尔 普鲁斯特
 * <br>要你是美剧迷，也可以赞下Lost
 * 
 * <br>--- 注释？！！
 * 
 * <br>黄昏来临的时候
 * <br>我知道 您会在那儿
 * <br>这已足够
 * <br>命运 让人止步
 * <br>却不能夺走我眼中的微笑
 * <br>若爱 只是一个答案
 * <br>我愿像这些花草 
 * <br>默默遥望
 * 		<br>-- 摘自遥望2里无名骑士写给娜迪菲尔拉主教的一首诗 ：）
 * <br>
 * <br>
 * <br>如果你期望以给定的参数来推算时间，下面的说明你将需要注意：
 * 如果因为Thread Context或者别的原因，tick形成了误差，我并没有修正该数值
 * 所以给的参数是不可以信任作为游戏消逝的时间计数的。
 * <br>
 * 另外在{@link TickService#getStartTime()} 你可以获取到游戏世界开始的时间，如果可以，你总是
 * 可以推算和比较该tick的实际误差。并总是以{@link TickService#getStartTime()}
 * 来获取游戏开始的时间，和System.currentTimeMillis()配合，来获取到实际消逝的
 * 游戏时间。<br>
 * 
 * 太慢了，我们去买彩票吧<br>
 *      <br>-- 熊三山
 *      <br>-- 抢银行吧。
 *      <br>-- 拉皮条
 */
public interface LostedTimeListener {
	public String name();
	
	/**
	 * 服务关闭前的回调
	 */
	public void handleTickClose();
	/**
	 * 参数其实没有意义，但每个时间都在消逝，知道消逝了多少时间很好玩。
	 * 免费送给所有要在losted time要做处理的代码和写代码的人。
	 * 
	 * @param tickCount 过去了多少个tick
	 * @param intervalTime tick的单位时间，毫秒
	 */
	public void lostedTimeEvent(long tickCounter, long intervalTime);
}
