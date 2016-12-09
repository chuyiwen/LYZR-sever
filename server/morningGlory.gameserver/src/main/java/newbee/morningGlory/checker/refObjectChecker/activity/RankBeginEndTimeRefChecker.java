package newbee.morningGlory.checker.refObjectChecker.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

import newbee.morningGlory.checker.BaseRefChecker;
import newbee.morningGlory.mmorpg.player.activity.limitTimeRank.ref.RankBeginEndTimeRef;
import sophia.game.ref.GameRefObject;

public class RankBeginEndTimeRefChecker extends BaseRefChecker<RankBeginEndTimeRef> {
	private static Logger logger = Logger.getLogger(RankBeginEndTimeRefChecker.class);

	@Override
	public String getDescription() {
		return "限时冲榜时间表";
	}

	@Override
	public void check(GameRefObject gameRefObject) {
		RankBeginEndTimeRef ref = (RankBeginEndTimeRef) gameRefObject;

		String rankBeginTime = ref.getRankBeginTime();
		String rankEndTime = ref.getRankEndTime();
		byte rankType = ref.getRankType();

		if (rankType < 0) {
			if (logger.isDebugEnabled()) {
				logger.debug(rankType);
			}
			error(ref, "排名类型非法");
		}
		
//		String defualtTimeFormat = "yyyyMMddHHmmss";
//		if (!checkTime(rankBeginTime, defualtTimeFormat)) {
//			if (logger.isDebugEnabled()) {
//				logger.debug(rankBeginTime);
//			}
//			error(ref, "开始时间非法");
//		}
//		
//		if (!checkTime(rankEndTime, defualtTimeFormat)) {
//			if (logger.isDebugEnabled()) {
//				logger.debug(rankEndTime);
//			}
//			error(ref, "结束时间非法");
//		}
	}
	
	private boolean checkTime(String time, String format) {
		boolean flag = false;
		try {
			new SimpleDateFormat(format).parse(time);
			flag = true;
		} catch (ParseException e) {
			flag = false;
		}
		return flag;
	}

}
