package sophia.mmorpg.player.scene.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_Npc_Transfer extends ActionEventBase {
	private String npcRefId;
	private String targetScene;
	private int transferInId;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		npcRefId = getString(buffer);
		targetScene = getString(buffer);
		transferInId = buffer.getInt();
	}

	public int getTransferInId() {
		return transferInId;
	}

	public void setTransferInId(int transferInId) {
		this.transferInId = transferInId;
	}

	public String getNpcRefId() {
		return npcRefId;
	}

	public void setNpcRefId(String npcRefId) {
		this.npcRefId = npcRefId;
	}

	public String getTargetScene() {
		return targetScene;
	}

	public void setTargetScene(String targetScene) {
		this.targetScene = targetScene;
	}

}
