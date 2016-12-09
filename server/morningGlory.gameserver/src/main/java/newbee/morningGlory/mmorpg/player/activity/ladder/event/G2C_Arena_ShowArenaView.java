package newbee.morningGlory.mmorpg.player.activity.ladder.event;

import java.util.ArrayList;
import java.util.List;

import newbee.morningGlory.mmorpg.ladder.MGLadderMember;
import newbee.morningGlory.mmorpg.player.activity.ladder.MessageSender;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.player.Player;

public class G2C_Arena_ShowArenaView extends ActionEventBase{
	private Player owner;
	private MGLadderMember member;
	
	private List<Integer> rankList = new ArrayList<>();
	
	public G2C_Arena_ShowArenaView(){
		ziped =(byte)1;
	}
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		MessageSender.getInstance().writeAllListInfo(buffer, owner, member);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		// 公告区域
		byte noticeType = buffer.get();
		if (noticeType == 0) {
		} else if (noticeType == 1) {
			getString(buffer);
		} else if (noticeType == 2) {
			getString(buffer);
			getString(buffer);
		} else if (noticeType == 3) {
			getString(buffer);
			buffer.getInt();
		}
		
		// 挑战对象区域
		int size = buffer.getShort();
		for (int i = 0; i < size; i++) {
			buffer.get();
			buffer.get();
			rankList.add(buffer.getInt());
			getString(buffer);
			buffer.getInt();
			buffer.getInt();
		}
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}

	public void setMember(MGLadderMember member) {
		this.member = member;
	}

	public List<Integer> getRankList() {
		return rankList;
	}

	public void setRankList(List<Integer> rankList) {
		this.rankList = rankList;
	}

}
