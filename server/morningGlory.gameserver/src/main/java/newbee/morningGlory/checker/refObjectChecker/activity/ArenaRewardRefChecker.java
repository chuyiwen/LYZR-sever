package newbee.morningGlory.checker.refObjectChecker.activity;

import org.apache.log4j.Logger;

import sophia.game.ref.GameRefObject;
import newbee.morningGlory.checker.BaseRefChecker;
import newbee.morningGlory.mmorpg.player.activity.ladder.ArenaRewardRef;

public class ArenaRewardRefChecker extends BaseRefChecker<ArenaRewardRef> {
	private static Logger logger = Logger.getLogger(ArenaRewardRefChecker.class);

	@Override
	public String getDescription() {
		return "竞技场";
	}

	@Override
	public void check(GameRefObject gameRefObject) {
		ArenaRewardRef ref = (ArenaRewardRef) gameRefObject;

		String goldReward = ref.getGoldReward();
		String meritReward = ref.getMeritReward();
		String arenaRank = ref.getArenaRank();

		if (!meritReward.matches("[0-9]+-\\w+\\*[0-9]+") && !meritReward.matches("[0-9]+")) {
			if (logger.isDebugEnabled()) {
				logger.debug(meritReward);
			}
			error(ref, "竞技场功勋值奖励非法");
		}

		if (!goldReward.matches("[0-9]+-\\w+\\*[0-9]+") && !goldReward.matches("[0-9]+")) {
			if (logger.isDebugEnabled()) {
				logger.debug(goldReward);
			}
			error(ref, "竞技奖励值名非法");
		}

		if (!arenaRank.matches("[0-9]+") && !arenaRank.matches("\\[[0-9]+,[0-9]+\\]") && !arenaRank.matches("\\[[0-9]+,\\+∞\\)")) {
			if (logger.isDebugEnabled()) {
				logger.debug(arenaRank);
			}
			error(ref, "竞技场排名奖励非法");
		}

	}
}
