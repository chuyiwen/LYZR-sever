package newbee.morningGlory.checker.refObjectChecker.wing;

import newbee.morningGlory.checker.BaseRefChecker;
import newbee.morningGlory.mmorpg.player.wing.MGPlayerWingRef;

import org.apache.commons.lang3.StringUtils;

import sophia.foundation.property.PropertyDictionary;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class WingRefChecker extends BaseRefChecker<MGPlayerWingRef> {

	@Override
	public String getDescription() {
		return "翅膀";
	}

	@Override
	public void check(GameRefObject gameRefObject) {
		MGPlayerWingRef info = (MGPlayerWingRef) gameRefObject;
		if (!info.getId().startsWith("wing_")) {
			error(gameRefObject, "翅膀<refId>错误 , 请以wing_开头!!! 错误的refId为: " + info.getId());
		}

		PropertyDictionary pd = info.getProperty();
		String wingPreRefId = MGPropertyAccesser.getWingPreRefId(pd);
		if (!StringUtils.isEmpty(wingPreRefId)) {
			if (!wingPreRefId.startsWith("wing_")) {
				error(gameRefObject, "翅膀<wingPreRefId>错误 , 请以 wing_ 开头!!! 错误的wingPreRefId为: " + wingPreRefId);
			}
		}

		String wingNextRefId = MGPropertyAccesser.getWingNextRefId(pd);
		if (!StringUtils.isEmpty(wingNextRefId)) {
			if (!wingNextRefId.startsWith("wing_")) {
				error(gameRefObject, "翅膀<wingNextRefId>错误 , 请以 wing_ 开头!!! 错误的wingNextRefId为: " + wingNextRefId);
			}
		}

		int wingStageLevel = MGPropertyAccesser.getStageLevel(pd);
		int wingStarLevel = MGPropertyAccesser.getStartLevel(pd);
		
		if (wingStageLevel < 0) {
			error(gameRefObject, "翅膀<wingStageLevel>错误 , wingStageLevel 必须大于 0!!! 错误的wingStageLevel为: " + wingStageLevel);
		}
		
		if (wingStarLevel < 0) {
			error(gameRefObject, "翅膀<wingStarLevel>错误 , wingStarLevel 必须不小于 0!!! 错误的wingStarLevel为: " + wingStarLevel);
		}

//		Map<String, Integer> wingNeed = info.getUpdataNeed();
//		Set<String> itemNeed = wingNeed.keySet();
//		for (String item : itemNeed) {
//			if (GameRoot.getGameRefObjectManager().getManagedObject(item) == null) {
//				error(gameRefObject, "翅膀<wingUpgradeData>错误 , 翅膀升级所需道具RefID不存在：" + item);
//			}
//			int number = wingNeed.get(item);
//			if (number < 0) {
//				error(gameRefObject, "翅膀<wingUpgradeData>错误 , 翅膀升级所需道具数量错误 , number必须大于 0!!! 错误的number为" + number);
//			}
//		}
	}
}
