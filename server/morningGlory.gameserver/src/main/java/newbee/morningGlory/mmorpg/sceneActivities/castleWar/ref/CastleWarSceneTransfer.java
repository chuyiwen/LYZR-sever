package newbee.morningGlory.mmorpg.sceneActivities.castleWar.ref;

public class CastleWarSceneTransfer implements CastleWarTransfer {
	private String targetScene;
	private int tranferInId;

	@Override
	public String getTargetScene() {
		return targetScene;
	}

	@Override
	public void setTargetScene(String targetScene) {
		this.targetScene = targetScene;
	}

	@Override
	public int getTranferInId() {
		return tranferInId;
	}

	@Override
	public void setTranferInId(int tranferInId) {
		this.tranferInId = tranferInId;
	}
}
