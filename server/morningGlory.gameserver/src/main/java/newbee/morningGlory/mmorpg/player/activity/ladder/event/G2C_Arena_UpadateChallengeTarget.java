package newbee.morningGlory.mmorpg.player.activity.ladder.event;

import newbee.morningGlory.mmorpg.ladder.MGLadderMember;
import newbee.morningGlory.mmorpg.player.activity.ladder.MessageSender;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.player.Player;

public class G2C_Arena_UpadateChallengeTarget extends ActionEventBase {
	private MGLadderMember member;
	private Player player;

	public G2C_Arena_UpadateChallengeTarget() {
		ziped = (byte) 1;
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		MessageSender.getInstance().writeEnemyInfo(buffer, player, member);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {

	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void setMember(MGLadderMember member) {
		this.member = member;
	}

}
