package sophia.mmorpg.player.state;

import org.apache.log4j.Logger;

import sophia.mmorpg.player.Player;

public final class PlayerStateMgr {
	
	private static final Logger logger = Logger.getLogger(PlayerStateMgr.class);
	
	// 一个上线的正常玩家的状态应该是 Online|Enabled|AllowTalk

	// 离线
	public static final byte OffLine = 0xE; // 1110
	// 在线
	public static final byte Online = 0x1; // 0001
	// 封号
	public static final byte Disabled = 0xD; // 1101
	// (正常)没有封号
	public static final byte Enabled = 0x2; // 0010
	// 禁言
	public static final byte DisallowTalk = 0xB; // 1011
	// (正常)没有禁言
	public static final byte AllowTalk = 0x4; // 0100
	
	// 玩家状态(封号/禁言)由GM控制
	private volatile byte states;
	
	private Player owner;
	
	public PlayerStateMgr(Player owner) {
		this.owner = owner;
		setState(Enabled);
		setState(AllowTalk);
	}

	public byte getStates() {
		return states;
	}

	public void setStates(byte states) {
		this.states = states;
	}

	public void setState(final byte state) {
		switch (state) {
		case OffLine:
			states &= OffLine;
			break;
		case Online:
			states |= Online;
			break;
		case Disabled:
			states &= Disabled;
			break;
		case Enabled:
			states |= Enabled;
			break;
		case DisallowTalk:
			states &= DisallowTalk;
			break;
		case AllowTalk:
			states |= AllowTalk;
			break;
		default:
			logger.error("setState error, invalid state=" + state);
			break;	
		}
	}

	public boolean hasState(final byte state) {
		return hasState(this.states, state);
	}
	
	/**
	 * 判定states里面是否存在state
	 * @param states
	 * @param state
	 * @return
	 */
	public boolean hasState(final byte states, final byte state) {
		switch (state) {
		case OffLine:
			return (Online & states) != Online;
		case Online:
			return (Online & states) == Online;
		case Disabled:
			return (Enabled & states) != Enabled;
		case Enabled:
			return (Enabled & states) == Enabled;
		case DisallowTalk:
			return (AllowTalk & states) != AllowTalk;
		case AllowTalk:
			return (AllowTalk & states) == AllowTalk;
		}
		return false;
	}
}
