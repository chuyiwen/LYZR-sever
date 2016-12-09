package newbee.morningGlory.mmorpg.player.activity.fund;

import java.util.HashMap;
import java.util.Map;

public enum FundType {
	FUND_LowLevel(1, "初级基金"), FUND_MidLevel(2, "中级基金"), FUND_HighLevel(3, "高级基金");

	private int type;
	private String name;

	private FundType(int type, String name) {
		this.type = type;
		this.name = name;
	}

	private static Map<Integer, FundType> typeMap = new HashMap<Integer, FundType>();

	static {

		FundType missionTypes[] = FundType.values();
		for (int i = 0; i < missionTypes.length; i++) {
			FundType event = missionTypes[i];
			typeMap.put(event.getType(), event);
		}
	}

	/**
	 * 
	 * @return
	 */
	public int getType() {
		return type;
	}

	/**
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param type
	 * @return
	 */
	public static FundType getFundType(int type) {
		return typeMap.get(type);
	}

}