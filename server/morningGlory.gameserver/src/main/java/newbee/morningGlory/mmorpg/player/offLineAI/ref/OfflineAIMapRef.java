package newbee.morningGlory.mmorpg.player.offLineAI.ref;

import sophia.game.ref.AbstractGameRefObjectBase;

public class OfflineAIMapRef extends AbstractGameRefObjectBase {

	private static final long serialVersionUID = -2424457360224220806L;

	
	private int minLevelId;
	private int maxLevelId;
	private String mapId;
	
	
	public int getMinLevelId() {
		return minLevelId;
	}
	public void setMinLevelId(int minLevelId) {
		this.minLevelId = minLevelId;
	}
	public int getMaxLevelId() {
		return maxLevelId;
	}
	public void setMaxLevelId(int maxLevelId) {
		this.maxLevelId = maxLevelId;
	}
	public String getMapId() {
		return mapId;
	}
	public void setMapId(String mapId) {
		this.mapId = mapId;
	}

	
	
	
}
