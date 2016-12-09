package newbee.morningGlory.mmorpg.sortboard;

import sophia.mmorpg.player.Player;

public abstract class Sortboard {
	protected SortboardType type;
	protected String name = "";
	protected int size = 0;

	/**
	 * 初始化事件
	 */
	protected void init() {
	}

	/**
	 * 类型
	 * 
	 * @return
	 */
	public final SortboardType getType() {
		return type;
	}

	/**
	 * 长度
	 * 
	 * @return
	 */
	public final int getSize() {
		return size;
	}

	/**
	 * 名称
	 * 
	 * @return
	 */
	public final String getName() {
		return name;
	}

	/**
	 * 相应的分数
	 * 
	 * @param playerCharacter
	 *            玩家，因为都是基于玩家来进行，所以传入参数为玩家。宠物或者装备的排名可以根据玩家获取。
	 * @return
	 */
	public abstract int getScore(Player player);
	
	public abstract SortboardScoreData getSortboard(Player player);
	
}
