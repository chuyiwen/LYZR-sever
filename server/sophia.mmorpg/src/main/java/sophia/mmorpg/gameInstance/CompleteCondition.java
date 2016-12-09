package sophia.mmorpg.gameInstance;

import java.util.HashMap;
import java.util.Map;

/**
 * 默认的杀怪集合类型
 * 
 */

public class CompleteCondition {
	public static final byte TRUE = 0;

	public static final byte FALSE = -1;

	private Map<String, Short> monsterKillCondition;// 杀怪的集合需求

	public void setCompleteCondition(String completeCondition) {
		// 格式:
		// 怪物RefId1=数量&怪物RefId2=数量,多个怪物类型时使用&连接,当怪物的数量是1时,
		// 直接填: 怪物RefId1
		String[] monster = completeCondition.split("&");
		if (this.monsterKillCondition == null)
			this.monsterKillCondition = new HashMap<String, Short>();
		for (String s : monster) {
			this.monsterKillCondition.put(s.split("=")[0], Short.parseShort(s.split("=").length > 1 ? s.split("=")[1] : "1"));
		}
	}

	/**
	 * 返回0时表示满足杀怪条件,-1不满足 , 否则返回还差几个怪
	 */
	public short checkCompleteCondition(Map<String, Short> monsterKills) {
		if (monsterKillCondition == null || monsterKillCondition.size() == 0) {
			return TRUE;
		}

		for (Map.Entry<String, Short> entry : monsterKillCondition.entrySet()) {
			String monsterRefId = entry.getKey();
			if (!monsterKills.containsKey(monsterRefId)) {
				return FALSE;
			}
			short need = entry.getValue();
			Short killNum = monsterKills.get(monsterRefId);
			if (killNum == null) {
				killNum = 0;
			}
			if (killNum < need) {
				return FALSE;
			}
		}

		return TRUE;
	}

	public Map<String, Short> getKillMonsters() {
		return monsterKillCondition;
	}

}
