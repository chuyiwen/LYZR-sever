package newbee.morningGlory.checker.refObjectChecker.achievement;

import java.util.Map;

import newbee.morningGlory.checker.BaseRefChecker;
import newbee.morningGlory.mmorpg.player.achievement.medal.MGMedalConfig;
import newbee.morningGlory.mmorpg.player.achievement.medal.MGMedalDataRef;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class MedalRefChecker extends BaseRefChecker<MGMedalConfig> {

	@Override
	public void check(GameRefObject gameRefObject) {
		MGMedalConfig medalConfig = (MGMedalConfig) gameRefObject;
		Map<String, MGMedalDataRef> map = medalConfig.getMedalDataRefMap();

		for (Map.Entry<String, MGMedalDataRef> entry : map.entrySet()) {
			String crtRefId = entry.getKey();
			MGMedalDataRef ref = entry.getValue();
			if (!crtRefId.startsWith("equip_")) {
				error(medalConfig, "勋章<refId>错误:" + crtRefId);
			}

			if (null == ref) {
				error(medalConfig, "勋章refId不存在:" + crtRefId);
			}
			checkeMedalDataRef(ref, medalConfig);
		}

	}

	private void checkeMedalDataRef(MGMedalDataRef ref, MGMedalConfig medalConfig) {
		String nextMedal = MGPropertyAccesser.getNextMedal(ref.getProperty());
		if (null == nextMedal) {
			error(medalConfig, "勋章<nextMedal>错误:nextMedal值为null");
		}

		if (!"".equals(nextMedal) && !nextMedal.startsWith("equip_")) {
			error(medalConfig, "勋章<nextMedal>错误:" + nextMedal);
		}

		int needAchieve = MGPropertyAccesser.getNeedAchieve(ref.getProperty());
		if (needAchieve < 0) {
			error(medalConfig, "勋章<needAchieve>值小于0了:" + needAchieve);
		}
	}

	@Override
	public String getDescription() {
		return "勋章";
	}

}
