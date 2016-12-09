package sophia.mmorpg.player.quest.ref.order;

public final class QuestChineseOrderDefines {
	/**
	 * 进入副本类型（接受任务立即传送进副本）
	 */
	public static final short GameInstanceEnter = 1;
	/**
	 * 提升爵位类型
	 */
	public static final short PeerageLevelUp = 2;
	/**
	 * 坐骑升级类型
	 */
	public static final short MountLevelUp = 3;
	/**
	 * 通关副本类型
	 */
	public static final short GameInstanceFinish = 4;
	/**
	 * 通关过副本类型（接受任务时，进入过副本立即完成。未进入过则一进入即完成）
	 */
	public static final short GameInstanceEverEnter = 5;
	/**
	 * 翅膀升级类型
	 */
	public static final short WingLevelUp = 6;
	/**
	 * 商店购买类型
	 */
	public static final short BuyStoreItem = 7;
	/**
	 * Buffer检测类型
	 */
	public static final short HasBuffer = 8;
	/**
	 * 竞技场任务类型
	 */
	public static final short Ladder = 9;
	/**
	 * 仓库任务类型
	 */
	public static final short Depot = 10;
}
