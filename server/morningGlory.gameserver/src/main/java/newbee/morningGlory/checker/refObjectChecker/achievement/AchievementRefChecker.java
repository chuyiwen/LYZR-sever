package newbee.morningGlory.checker.refObjectChecker.achievement;

import newbee.morningGlory.checker.BaseRefChecker;
import newbee.morningGlory.mmorpg.player.achievement.MGPlayerAchievementRef;
import sophia.foundation.property.PropertyDictionary;
import sophia.game.GameRoot;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class AchievementRefChecker extends BaseRefChecker<MGPlayerAchievementRef> {

	@Override
	public void check(GameRefObject gameRefObject) {
		MGPlayerAchievementRef ref = (MGPlayerAchievementRef) gameRefObject;

		if (!ref.getId().startsWith("achieve_")) {
			error(gameRefObject, "成就<refId>错误 , 请以achieve_开头!!! 错误的refId为: " + ref.getId());
		}

		String refId = ref.getId();
		if (null == GameRoot.getGameRefObjectManager().getManagedObject(refId)) {
			error(gameRefObject, "成就refId不存在:" + refId);
		}

		checkAchieveReward(ref);
		checkProperty(ref);
	}

	private void checkAchieveReward(MGPlayerAchievementRef ref) {
		for (ItemPair itemPair : ref.getItemPairs()) {
			if (itemPair.getNumber() < 0) {
				error(ref, "奖励数量小于0");
			}
		}
	}

	private void checkProperty(MGPlayerAchievementRef ref) {
		PropertyDictionary pd = ref.getProperty();
		byte achieveType = MGPropertyAccesser.getAchieveType(pd);
		if (achieveType < 1 || achieveType > 7) {
			error(ref, "成就<achieveType>错误 , achieveType值范围应在1~7之间!!! 错误的achieveType为: " + achieveType);
		}

//		byte completeCondition = MGPropertyAccesser.getCompleteCondition(pd);
//		if (completeCondition < 1 || completeCondition > 12) {
//			error(ref, "成就<completeCondition>错误,completeCondition值范围应在1~12之间!!! 错误的completeCondition为:" + completeCondition);
//		}

		String nextAchieve = MGPropertyAccesser.getNextAchieve(pd);
		if (null != null && !nextAchieve.startsWith("achieve_")) {
			error(ref, "成就<nextAchieve>错误 , 请以achieve_开头!!! 错误的nextAchieve为: " + nextAchieve);
		}

		String targetID = MGPropertyAccesser.getTargetID(pd);
		if (null == targetID) {
			error(ref, "成就<targetID>错误 , targetID值为null了");
		}

		int targetNum = MGPropertyAccesser.getTargetNum(pd);
		if (0 > targetNum) {
			error(ref, "成就<targetNum>错误 , targetNum值小于0了: " + targetNum);
		}
	}

	@Override
	public String getDescription() {
		return "成就";
	}

}
