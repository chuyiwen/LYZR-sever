package newbee.morningGlory.mmorpg.player.activity.event;

import newbee.morningGlory.mmorpg.player.activity.mgr.LevelUpMgr;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_LevelUp_List extends ActionEventBase {
	private LevelUpMgr levelUpMgr;

	public G2C_LevelUp_List(){
		ziped = (byte)1;
	}
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		levelUpMgr.writeInfoToClient(buffer);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {

	}

	public LevelUpMgr getLevelUpMgr() {
		return levelUpMgr;
	}

	public void setLevelUpMgr(LevelUpMgr levelUpMgr) {
		this.levelUpMgr = levelUpMgr;
	}

}
