package newbee.morningGlory.mmorpg.player.activity.mining;

import java.util.HashMap;
import java.util.Map;

import sophia.mmorpg.player.Player;

public class MGPlayerMiningManager {
	private long lastMiningMills;
	
	private Map<Byte, Byte> collectedCount = new HashMap<Byte, Byte>();

	private Player owner;

	public MGPlayerMiningManager(Player owner) {
		this.owner = owner;
	}

	public Map<Byte, Byte> getCollectedCount() {
		return collectedCount;
	}

	public void setCollectedCount(Map<Byte, Byte> collectedCount) {
		this.collectedCount = collectedCount;
	}
	
	public byte totalCollectedCount() {
		byte totalCount = 0;
		for (Byte count : collectedCount.values()) {
			if (count != null) {
				totalCount += count;
			}
		}
		
		return totalCount;
	}

	public void collectedCountIncrement(Byte pluckType) {
		Byte pluckCount = collectedCount.get(pluckType);
		
		if (pluckCount != null) {
			pluckCount++;
		} else {
			pluckCount = 1;
		}
		
		collectedCount.put(pluckType, pluckCount);
	}
	
	public void resetCollectCount() {
		this.collectedCount.clear();
	}

//	public ComeFromScene getFromScene() {
//		return fromScene;
//	}
//
//	public void setFromScene(ComeFromScene fromScene) {
//		this.fromScene = fromScene;
//	}

	public Player getOwner() {
		return owner;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}

	public long getLastMiningMills() {
		return lastMiningMills;
	}

	public void setLastMiningMills(long lastMiningMills) {
		this.lastMiningMills = lastMiningMills;
	}

}
