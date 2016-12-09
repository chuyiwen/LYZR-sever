package newbee.morningGlory.mmorpg.sortboard;

import newbee.morningGlory.mmorpg.sortboard.impl.MountLvlSortboard;
import newbee.morningGlory.mmorpg.sortboard.impl.PlayerFightPowerSortboard;
import newbee.morningGlory.mmorpg.sortboard.impl.PlayerLvlSortboard;
import newbee.morningGlory.mmorpg.sortboard.impl.PlayerMeritSortboard;
import newbee.morningGlory.mmorpg.sortboard.impl.PlayerMoneySortboard;
import newbee.morningGlory.mmorpg.sortboard.impl.PlayerWingLvlSortboard;
import newbee.morningGlory.mmorpg.sortboard.impl.TalismanLvlSortboard;

public enum SortboardType {
	
	//	类型修改要与限时冲榜同步修改
	// --------角色----------
	/** 角色战斗力 */
	PlayerFightPower(SortboardMgr.PlayerTypeStart + 1, "战斗力排行榜", PlayerFightPowerSortboard.class),
	/** 角色等级 */
	PlayerLvl(SortboardMgr.PlayerTypeStart + 2, "等级排行榜", PlayerLvlSortboard.class),
	/** 角色财富 */
	PlayerMoney(SortboardMgr.PlayerTypeStart + 3, "财富排行榜", PlayerMoneySortboard.class),
	/** 角色爵位 */
	PlayerMerit(SortboardMgr.PlayerTypeStart + 4, "爵位排行榜", PlayerMeritSortboard.class),
	/** 玩家翅膀 */
	PlayerWingLvl(SortboardMgr.PlayerTypeStart + 5, "翅膀排行榜", PlayerWingLvlSortboard.class),
	/** 玩家坐骑 */
	MountLvl(SortboardMgr.PlayerTypeStart + 6, "坐骑排行榜", MountLvlSortboard.class),
	/** 玩家法宝 */
	TalismanLvl(SortboardMgr.PlayerTypeStart + 7, "法宝排行榜", TalismanLvlSortboard.class), 
	;
	
	private int sortboardType;
	private String name;
	private Class<? extends Sortboard> clazz;

	private SortboardType(int sortboardType, String name, Class<? extends Sortboard> clazz) {
		this.sortboardType = sortboardType;
		this.name = name;
		this.clazz = clazz;
	}

	public int getSortboardType() {
		return sortboardType;
	}
	
	public String getName() {
		return name;
	}
	
	public Class<? extends Sortboard> getClazz() {
		return clazz;
	}
	
	public static SortboardType get(int sortboardType) {
		for (SortboardType t : values()) {
			if (t.getSortboardType() == sortboardType) {
				return t;
			}
		}
		
		return null;
	}
}
