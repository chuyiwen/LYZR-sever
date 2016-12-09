package newbee.morningGlory.mmorpg.player.union.actionEvent;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Union_Update extends ActionEventBase {
	private byte type;
	private String playerId;
	private String playerName;
	private byte profession;
	private int level;
	private int fightValue;
	private byte officialId;
	private byte online;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.put(type);
//		putString(buffer, playerId);
//		putString(buffer, playerName);
//		buffer.put(profession);
//		buffer.putInt(level);
//		buffer.putInt(fightValue);
//		buffer.put(officialId);
//		buffer.put(online);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {

	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public byte getProfession() {
		return profession;
	}

	public void setProfession(byte profession) {
		this.profession = profession;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getFightValue() {
		return fightValue;
	}

	public void setFightValue(int fightValue) {
		this.fightValue = fightValue;
	}

	public byte getOfficialId() {
		return officialId;
	}

	public void setOfficialId(byte officialId) {
		this.officialId = officialId;
	}

	public byte getOnline() {
		return online;
	}

	public void setOnline(byte online) {
		this.online = online;
	}

}
