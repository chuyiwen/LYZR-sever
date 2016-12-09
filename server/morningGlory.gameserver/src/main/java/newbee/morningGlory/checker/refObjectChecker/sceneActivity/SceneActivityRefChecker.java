package newbee.morningGlory.checker.refObjectChecker.sceneActivity;

import java.util.List;

import newbee.morningGlory.checker.BaseRefChecker;
import newbee.morningGlory.mmorpg.sceneActivities.SceneActivityRef;
import newbee.morningGlory.mmorpg.sceneActivities.castleWar.ref.CastleWarInstanceTransfer;
import newbee.morningGlory.mmorpg.sceneActivities.castleWar.ref.CastleWarOutSceneTransfer;
import newbee.morningGlory.mmorpg.sceneActivities.castleWar.ref.CastleWarRef;
import newbee.morningGlory.mmorpg.sceneActivities.castleWar.ref.CastleWarSceneTransfer;
import newbee.morningGlory.mmorpg.sceneActivities.mining.ref.MGMiningRefConfigRef;
import newbee.morningGlory.mmorpg.sceneActivities.monsterIntrusion.ref.MonsterInvasionRef;
import newbee.morningGlory.mmorpg.sceneActivities.payonPalace.ref.MGPayonPalaceActivityRef;

import org.apache.commons.lang3.StringUtils;

import sophia.game.GameRoot;
import sophia.game.ref.GameRefObject;

public class SceneActivityRefChecker extends BaseRefChecker<SceneActivityRef> {

	@Override
	public String getDescription() {
		return "场景活动";
	}

	@Override
	public void check(GameRefObject gameRefObject) {
		SceneActivityRef ref = (SceneActivityRef) gameRefObject;
		if (StringUtils.equals(ref.getId(), "castleWar")) {
			checkCastleWar(ref);
		} else if (StringUtils.equals(ref.getId(), "sa_1")) {
			checkMining(ref);
		} else if (StringUtils.equals(ref.getId(), "monsterIntrusion1")) {
			checkMonsterIntrusion(ref);
		} else if (StringUtils.equals(ref.getId(), "monsterIntrusion2")) {
			checkMonsterIntrusion(ref);
		} else if (StringUtils.equals(ref.getId(), "payonPalace_1")) {
			checkPayonPalace(ref);
		}
	}

	/**
	 * check怪物入侵
	 * 
	 * @param ref
	 */
	private void checkMonsterIntrusion(SceneActivityRef ref) {
		MonsterInvasionRef monsterIntrusionRef = ref.getComponentRef(MonsterInvasionRef.class);
		double expMultiple = monsterIntrusionRef.getExpMultiple();
		int level = monsterIntrusionRef.getLevel();
		if (expMultiple < 0) {
			error(ref, "怪物入侵活动经验倍数<expMultiple>非法，倍数小于零：" + expMultiple);
		}
		if (level <= 0) {
			error(ref, "怪物入侵活动参与等级<level>非法，等级小于等于零：" + level);
		}
		List<String> itemRefIdList = monsterIntrusionRef.getItemRefIdList();
		for (String itemRefId : itemRefIdList) {
			if (itemRefId.indexOf("item_") < 0) {
				error(ref, "怪物入侵活动道具refId<itemRefId>非法，id不包含指定字符：" + itemRefId);
			}
		}
	}

	/**
	 * check挖矿
	 * 
	 * @param ref
	 */
	private void checkMining(SceneActivityRef ref) {
		MGMiningRefConfigRef miningRef = ref.getComponentRef(MGMiningRefConfigRef.class);
		int miningCounts = miningRef.getLimitCount();
		int level = miningRef.getLevel();
		if (miningCounts < 0) {
			error(ref, "每次挖矿活动可挖次数<limitCount>非法，错误次数为：" + miningCounts);
		}
		if (level < 0) {
			error(ref, "挖矿活动可以限制进入等级<level>非法，错误级数为：" + level);
		}
	}

	private void checkCastleWar(SceneActivityRef ref) {
		CastleWarRef castleWarRef = ref.getComponentRef(CastleWarRef.class);
		String itemRefId = castleWarRef.getGiftRefID();
		if (GameRoot.getGameRefObjectManager().getManagedObject(itemRefId) == null) {
			error(ref, "攻城战<giftRefID>错误 , giftRefID不存在 !!! 错误的giftRefID为: " + itemRefId);
		}

		checkCastleTransferCheck(castleWarRef);

		String startApplyTime = castleWarRef.getStartApplyTime();
		String endApplyTime = castleWarRef.getEndApplyTime();
		String[] startTime = startApplyTime.split("\\|");
		String[] endTime = endApplyTime.split("\\|");

		int startDay = Integer.parseInt(startTime[0]);
		int endDay = Integer.parseInt(endTime[0]);
		if (startDay < 1 || startDay > 7) {
			error(ref, "攻城战<startTime>错误 , startApplyTime日期错误 !!! 错误的startApplyTime为: " + startApplyTime);
		}
		if (endDay < 1 || endDay > 7) {
			error(ref, "攻城战<startTime>错误 , endApplyTime日期错误 !!! 错误的endApplyTime为: " + endApplyTime);
		}

		if (!applyTimeChecker(startApplyTime)) {
			error(ref, "攻城战<startTime>错误 , startApplyTime时间错误 !!! 错误的startApplyTime为: " + startApplyTime);
		}
		if (!applyTimeChecker(endApplyTime)) {
			error(ref, "攻城战<startTime>错误 , endApplyTime时间错误 !!! 错误的endApplyTime为: " + endApplyTime);
		}

		int firstIntervalDays = castleWarRef.getFirstIntervalDays();
		int rangeIntervalDays = castleWarRef.getRangeIntervalDays();
		String openAndEndTime = castleWarRef.getOpenAndEndTime();

		if (firstIntervalDays < 0) {
			error(ref, "攻城战<firstIntervalDays>错误 , firstIntervalDays日期错误 !!! 错误的firstIntervalDays为: " + firstIntervalDays);
		}

		if (rangeIntervalDays < 0) {
			error(ref, "攻城战<rangeIntervalDays>错误 , rangeIntervalDays日期错误 !!! 错误的rangeIntervalDays为: " + rangeIntervalDays);
		}

		String[] days = openAndEndTime.split("\\|");
		for (String day : days) {
			if (!dailyTimeChecker(day)) {
				error(ref, "攻城战<openAndEndTime>错误 , openAndEndTime日期错误 !!! 错误的openAndEndTime为: " + openAndEndTime);
			}
		}
	}

	private boolean applyTimeChecker(String applyTime) {
		boolean ret = false;
		String[] day = applyTime.split("\\|");
		ret = dailyTimeChecker(day[1]);
		return ret;
	}

	private boolean dailyTimeChecker(String day) {
		boolean ret = false;
		String[] time = day.split(":");
		int hour = Integer.parseInt(time[0]);
		int minte = Integer.parseInt(time[1]);
		int second = Integer.parseInt(time[2]);
		if (hour < 0 || hour > 24) {
			ret = false;
		} else if (minte < 0 || minte > 60) {
			ret = false;
		} else if (second < 0 || second > 60) {
			ret = false;
		} else {
			ret = true;
		}
		return ret;
	}

	private void checkCastleTransferCheck(CastleWarRef castleWarRef) {
		CastleWarInstanceTransfer castleWarInstanceTransfer = castleWarRef.getCastleWarInstanceTransfer();
		String targetScene = castleWarInstanceTransfer.getTargetScene();
		if (StringUtils.isEmpty(targetScene)) {
			error(castleWarRef, "攻城战<instance>错误 , targetScene不存在 !!! 错误的targetScene为: " + targetScene);
		}
		int tranferInId = castleWarInstanceTransfer.getTranferInId();
		if (tranferInId < 0) {
			error(castleWarRef, "攻城战<instance>错误 , tranferInId错误 !!! 错误的tranferInId为: " + tranferInId);
		}

		CastleWarOutSceneTransfer castleWarOutSceneTransfer = castleWarRef.getCastleWarOutSceneTransfer();
		String OutScene = castleWarOutSceneTransfer.getTargetScene();
		if (StringUtils.isEmpty(OutScene)) {
			error(castleWarRef, "攻城战castleWarRefckOut>错误 , targetScene不存在 !!! 错误的targetScene为: " + OutScene);
		}
		int tranferOutId = castleWarOutSceneTransfer.getTranferInId();
		if (tranferOutId < 0) {
			error(castleWarRef, "攻城战<kickOut>错误 , tranferInId错误 !!! 错误的tranferInId为: " + tranferOutId);
		}

		CastleWarSceneTransfer castleWarSceneTransfer = castleWarRef.getCastleWarSceneTransfer();
		String warScene = castleWarSceneTransfer.getTargetScene();
		if (StringUtils.isEmpty(warScene)) {
			error(castleWarRef, "攻城战<warMap>错误 , targetScene不存在 !!! 错误的targetScene为: " + warScene);
		}
		int warTransfer = castleWarSceneTransfer.getTranferInId();
		if (warTransfer < 0) {
			error(castleWarRef, "攻城战<warMap>错误 , tranferInId错误 !!! 错误的tranferInId为: " + warTransfer);
		}
	}

	/**
	 * 
	 * 付费地宫检测
	 */

	private void checkPayonPalace(SceneActivityRef ref) {
		MGPayonPalaceActivityRef payonPalaceActivityRef = ref.getComponentRef(MGPayonPalaceActivityRef.class);
		String sceneRefId = ref.getSceneRefId();
		String[] sceneStrArray = sceneRefId.split("\\|");
		if (sceneStrArray.length != 2) {
			error(ref, "付费地宫场景配置非法!!!");
		}

		for (String itemRefId : payonPalaceActivityRef.getConsumptionItems().keySet()) {
			Integer number = payonPalaceActivityRef.getConsumptionItems().get(itemRefId);
			if (number < 0) {
				error(ref, "付费地宫物品值非法!!!");
			}
		}
	}

}
