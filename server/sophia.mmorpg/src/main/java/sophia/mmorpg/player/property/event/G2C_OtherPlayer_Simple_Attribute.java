package sophia.mmorpg.player.property.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_OtherPlayer_Simple_Attribute extends ActionEventBase {
	private String charId;
	private int hp;
	private int maxHP;

	@Override
	public void unpackBody(IoBuffer buffer) {
		
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer, getCharId());
		buffer.putInt(getHp());
		buffer.putInt(getMaxHP());
		return buffer;
	}

	public String getCharId() {
		return charId;
	}

	public void setCharId(String charId) {
		this.charId = charId;
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public int getMaxHP() {
		return maxHP;
	}

	public void setMaxHP(int maxHP) {
		this.maxHP = maxHP;
	}
}
