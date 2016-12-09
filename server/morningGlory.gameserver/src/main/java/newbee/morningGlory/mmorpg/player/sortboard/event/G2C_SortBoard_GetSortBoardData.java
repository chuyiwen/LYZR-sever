package newbee.morningGlory.mmorpg.player.sortboard.event;

import java.util.List;

import newbee.morningGlory.mmorpg.sortboard.SortboardData;
import newbee.morningGlory.mmorpg.sortboard.SortboardScoreData;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_SortBoard_GetSortBoardData extends ActionEventBase {

	private SortboardData sortboardData;

	private int sortboardType;

	private int ranking;

	private int score;
	
	public G2C_SortBoard_GetSortBoardData(){
		ziped =(byte)1;
	}
	

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.put((byte)sortboardType);
		
		buffer.putInt(ranking);
		buffer.putLong(score);
		
		List<SortboardScoreData> scoreData = sortboardData.getScoreData();
		
		byte socreDataSize = scoreData.size() > 100? (byte)100 : (byte)scoreData.size();
		buffer.put(socreDataSize);

		int i = 0;
		for (SortboardScoreData data : scoreData) {
			if (i >= 100) {
				break;
			}
			putString(buffer, data.getPlayerId());
			putString(buffer, data.getName());
			buffer.put((byte)data.getProfession());
			buffer.putLong(data.getScore());
			i++;
		}
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		// TODO Auto-generated method stub

	}

	public SortboardData getSortboardData() {
		return sortboardData;
	}
	
	public void setSortboardData(SortboardData sortboardData) {
		this.sortboardData = sortboardData;
	}

	public int getSortboardType() {
		return sortboardType;
	}

	public void setSortboardType(int sortboardType) {
		this.sortboardType = sortboardType;
	}

	public int getRanking() {
		return ranking;
	}

	public void setRanking(int ranking) {
		this.ranking = ranking;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
}
