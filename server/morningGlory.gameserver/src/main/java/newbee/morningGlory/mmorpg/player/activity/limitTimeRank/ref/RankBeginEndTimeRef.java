package newbee.morningGlory.mmorpg.player.activity.limitTimeRank.ref;

import java.util.List;

import newbee.morningGlory.mmorpg.sceneActivities.chime.Chime;
import sophia.game.ref.AbstractGameRefObjectBase;

public class RankBeginEndTimeRef extends AbstractGameRefObjectBase {
	private static final long serialVersionUID = 8548882267632757500L;

	private String rankBeginTime;
	private String rankEndTime;
	private byte rankType;
	private List<Chime> dateChimeList;

	public RankBeginEndTimeRef() {

	}

	public String getRankBeginTime() {
		return rankBeginTime;
	}

	public void setRankBeginTime(String rankBeginTime) {
		this.rankBeginTime = rankBeginTime;
	}

	public String getRankEndTime() {
		return rankEndTime;
	}

	public void setRankEndTime(String rankEndTime) {
		this.rankEndTime = rankEndTime;
	}

	public byte getRankType() {
		return rankType;
	}

	public void setRankType(byte rankType) {
		this.rankType = rankType;
	}

	public List<Chime> getDateChimeList() {
		return dateChimeList;
	}

	public void setDateChimeList(List<Chime> dateChimeList) {
		this.dateChimeList = dateChimeList;
	}

}
