package sophia.mmorpg.player.scene.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Scene_FightPower_NotEnought extends ActionEventBase {

	private int fightPower;
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.putInt(fightPower);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		// TODO Auto-generated method stub
	}

	public int getFightPower() {
		return fightPower;
	}

	public void setFightPower(int fightPower) {
		this.fightPower = fightPower;
	}

}