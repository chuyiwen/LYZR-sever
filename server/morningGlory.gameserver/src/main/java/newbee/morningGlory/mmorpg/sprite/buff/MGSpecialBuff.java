package newbee.morningGlory.mmorpg.sprite.buff;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class MGSpecialBuff {
	
	public static final List<String> buffStringList = new ArrayList<String>(3);
	static{
		buffStringList.add("buff_item_8");
		buffStringList.add("buff_item_9");
		buffStringList.add("buff_item_10");
	}
	
	/**
	 * 是魔法盾buff
	 * @param buffRefId
	 * @return
	 */
	public static final boolean isMagicShield(String buffRefId){
		return StringUtils.equals(buffRefId, "buff_skill_3") || StringUtils.equals(buffRefId, "buff_skill_3_1");
	}
}
