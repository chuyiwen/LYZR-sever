package newbee.morningGlory.ref.loader.activity;

import newbee.morningGlory.mmorpg.player.activity.limitTimeRank.LimitTimeActivityMgr;
import newbee.morningGlory.mmorpg.player.activity.limitTimeRank.ref.RankBeginEndTimeRef;
import newbee.morningGlory.ref.RefKey;
import newbee.morningGlory.ref.loader.AbstractGameRefObjectLoader;

import org.apache.log4j.Logger;

import com.google.gson.JsonObject;

public class RankBeginEndTimeRefLoader extends AbstractGameRefObjectLoader<RankBeginEndTimeRef> {
	private static Logger logger = Logger.getLogger(RankBeginEndTimeRefLoader.class);

	@Override
	protected RankBeginEndTimeRef create() {
		return new RankBeginEndTimeRef();
	}

	public RankBeginEndTimeRefLoader() {
		super(RefKey.rankBeginEndTime);
	}

	@Override
	protected void fillNonPropertyDictionary(RankBeginEndTimeRef ref, JsonObject refData) {
		if (logger.isDebugEnabled()) {
			logger.debug("load timeBeginEnd info");
		}

		String beginTime = refData.get("beginTime").getAsString();
		String endTime = refData.get("endTime").getAsString();
		byte rankType = refData.get("rankType").getAsByte();

		ref.setRankBeginTime(beginTime);
		ref.setRankEndTime(endTime);
		ref.setRankType(rankType);
		
		LimitTimeActivityMgr.getBeginEndTimeMaps().put(rankType, ref.getId());
		super.fillNonPropertyDictionary(ref, refData);
	}

}
