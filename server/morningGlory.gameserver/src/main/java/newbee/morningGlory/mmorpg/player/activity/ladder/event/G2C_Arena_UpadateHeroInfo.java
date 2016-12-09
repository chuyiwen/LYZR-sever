package newbee.morningGlory.mmorpg.player.activity.ladder.event;

import newbee.morningGlory.mmorpg.ladder.MGLadderMember;
import newbee.morningGlory.mmorpg.player.activity.ladder.MessageSender;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Arena_UpadateHeroInfo extends ActionEventBase {
	private MGLadderMember member;
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		MessageSender.getInstance().writePersonalInfo(buffer, member);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {

	}

	public void setMember(MGLadderMember member) {
		this.member = member;
	}
}
