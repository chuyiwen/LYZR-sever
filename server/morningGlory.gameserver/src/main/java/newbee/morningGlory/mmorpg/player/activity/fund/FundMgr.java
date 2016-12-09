package newbee.morningGlory.mmorpg.player.activity.fund;

import java.util.HashMap;
import java.util.Map;

import newbee.morningGlory.mmorpg.player.activity.fund.ref.FundRef;

/**
 * 遮天基金管理
 * 
 * @author lixing
 * 
 */
public class FundMgr {

	/**
	 * 基金礼包配置 key 类型号 value 基金
	 */
	private static Map<FundType, FundRef> giftMap = new HashMap<FundType, FundRef>();

	public static Map<FundType, FundRef> getGiftMap() {
		return giftMap;
	}

	public static FundRef getGiftMapByFundType(FundType funType) {
		return giftMap.get(funType);
	}

}
