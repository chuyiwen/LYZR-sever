/**
 * 
 */
package newbee.morningGlory.mmorpg.sceneActivities.gameEvent;


/**
 * @author yinxinglin
 * 
 */
public final class SceneActivityOpen_GE {

	private String sceneRefId;
	private int preStartTime;
	private int preEndTime;

	public SceneActivityOpen_GE(String sceneRefId, int preStartTime, int preEndTime) {
		this.sceneRefId = sceneRefId;
		this.preStartTime = preStartTime;
		this.preEndTime = preEndTime;
	}

	
	public String getSceneRefId() {
		return sceneRefId;
	}


	public void setSceneRefId(String sceneRefId) {
		this.sceneRefId = sceneRefId;
	}


	public int getPreStartTime() {
		return preStartTime;
	}

	public void setPreStartTime(int preStartTime) {
		this.preStartTime = preStartTime;
	}

	public int getPreEndTime() {
		return preEndTime;
	}

	public void setPreEndTime(int preEndTime) {
		this.preEndTime = preEndTime;
	}

}
