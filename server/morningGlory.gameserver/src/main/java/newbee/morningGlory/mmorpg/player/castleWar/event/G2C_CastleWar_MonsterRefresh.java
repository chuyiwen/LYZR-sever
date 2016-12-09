package newbee.morningGlory.mmorpg.player.castleWar.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_CastleWar_MonsterRefresh extends ActionEventBase {
	
	private String monsterOwner;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer, monsterOwner);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		// TODO Auto-generated method stub
		
	}

	public String getMonsterOwner() {
		return monsterOwner;
	}

	public void setMonsterOwner(String monsterOwner) {
		this.monsterOwner = monsterOwner;
	}

}