package newbee.morningGlory.ref.loader.activity;

import newbee.morningGlory.mmorpg.player.activity.ladder.ArenaRewardRef;
import newbee.morningGlory.mmorpg.player.activity.ladder.LadderRewardUtil;
import newbee.morningGlory.ref.RefKey;
import newbee.morningGlory.ref.loader.AbstractGameRefObjectLoader;

import org.apache.log4j.Logger;

import com.google.gson.JsonObject;

public class ArenaRewardRefLoader extends AbstractGameRefObjectLoader<ArenaRewardRef>{
	private static Logger logger = Logger.getLogger(ArenaRewardRefLoader.class);
	
	@Override
	protected ArenaRewardRef create() {
		return new ArenaRewardRef();
	}

	public ArenaRewardRefLoader(){
		super(RefKey.arenaReward);
	}
	
	@Override
	protected void fillNonPropertyDictionary(ArenaRewardRef ref, JsonObject refData) {
		if(logger.isDebugEnabled()){
			logger.debug("load arena info");
		}
		
		String arenaRank = refData.get("arenaRank").getAsString();
		String goldReward = refData.get("goldReward").getAsString();
		String meritReward = refData.get("meritReward").getAsString();
		
		ref.setArenaRank(arenaRank);
		ref.setGoldReward(goldReward);
		ref.setMeritReward(meritReward);
		
		ref.setValue();
		
		LadderRewardUtil.refMaps.put(ref.getId(), arenaRank);
		super.fillNonPropertyDictionary(ref, refData);
	}

}
