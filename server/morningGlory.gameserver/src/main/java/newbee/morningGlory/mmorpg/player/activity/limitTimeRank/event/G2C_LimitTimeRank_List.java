package newbee.morningGlory.mmorpg.player.activity.limitTimeRank.event;

import newbee.morningGlory.mmorpg.player.activity.limitTimeRank.LimitTimeRankMgr;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_LimitTimeRank_List extends ActionEventBase{
	private LimitTimeRankMgr rankMgr;
	private byte sortBoardType;
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		rankMgr.writeRankListInfoToClient(sortBoardType, buffer);
		return buffer;
	}

	public G2C_LimitTimeRank_List(){
		ziped =(byte)1;
	}
	
	@Override
	public void unpackBody(IoBuffer arg0) {
		
	}

	public LimitTimeRankMgr getRankMgr() {
		return rankMgr;
	}

	public void setRankMgr(LimitTimeRankMgr rankMgr) {
		this.rankMgr = rankMgr;
	}

	public byte getSortBoardType() {
		return sortBoardType;
	}

	public void setSortBoardType(byte sortBoardType) {
		this.sortBoardType = sortBoardType;
	}

}
