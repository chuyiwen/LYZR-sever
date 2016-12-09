package newbee.morningGlory.checker.refObjectChecker.sectonQuest;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import newbee.morningGlory.checker.BaseRefChecker;
import newbee.morningGlory.mmorpg.player.sectionQuest.MGSectionQuestRef;
import sophia.foundation.property.PropertyDictionary;
import sophia.game.GameRoot;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class SectionQuestRefChecker extends BaseRefChecker<MGSectionQuestRef> {

	@Override
	public String getDescription() {
		return "变强任务";
	}

	@Override
	public void check(GameRefObject gameRefObject) {
		MGSectionQuestRef info = (MGSectionQuestRef) gameRefObject;
		PropertyDictionary pd = info.getProperty();
		int questType = MGPropertyAccesser.getQuestType(pd);
		if (questType != 4) {
			error(gameRefObject, "变强任务<questType>错误 , questType 必须为 4!!! 错误的questType为: " + questType);
		}
		
		Map<String, Integer> acceptCondition = info.getAcceptCondition();
		Set<String> keySet = acceptCondition.keySet();
		for (String key : keySet) {
			int level = acceptCondition.get(key);
			if (level < 0) {
				error(gameRefObject, "变强任务<conditionField>错误 , 限制条件必须大于 0!!! 错误的level为: " + level);
			}
		}
		
		Map<String, Integer> orderCondition = info.getOrderCondition();
		Set<String> keySet2 = orderCondition.keySet();
		for (String key : keySet2) {
			int level = orderCondition.get(key);
			if (level < 0) {
				error(gameRefObject, "变强任务<orderField>错误 , 限制条件必须大于 0!!! 错误的level为: " + level);
			}
		}
		
		List<ItemPair> itemList = info.getRewardList();
		for (ItemPair item : itemList) {
			String itemRefId = item.getItemRefId();
			if (!StringUtils.isEmpty(itemRefId)) {
				if (GameRoot.getGameRefObjectManager().getManagedObject(itemRefId) == null) {
					error(gameRefObject, "变强任务<rewardField>错误 , itemRefId不存在 !!! 错误的itemCount为: " + itemRefId);
				}
			}
		}
	}

}
