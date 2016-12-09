package newbee.morningGlory.mmorpg.sceneActivities.teamBoss.ref;

import java.util.ArrayList;
import java.util.List;

import sophia.game.ref.AbstractGameRefObjectBase;

public class TeamBossRef extends AbstractGameRefObjectBase {

	private static final long serialVersionUID = 7327242777412389385L;
	
	private TeamBossTransferIn transferIn;
	private TeamBossTransferOut transferOut;
	
	private List<String> contentScene = new ArrayList<>();
	
	private byte type;

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public TeamBossTransferIn getTransferIn() {
		return transferIn;
	}

	public void setTransferIn(TeamBossTransferIn transferIn) {
		this.transferIn = transferIn;
	}

	public TeamBossTransferOut getTransferOut() {
		return transferOut;
	}

	public void setTransferOut(TeamBossTransferOut transferOut) {
		this.transferOut = transferOut;
	}
	
	public void addToContentScene(String sceneRefId) {
		contentScene.add(sceneRefId);
	}
	
	public List<String> getContentScene() {
		return contentScene;
	}
}
