package newbee.morningGlory.mmorpg.sortboard;

import java.util.ArrayList;
import java.util.List;

public class SortboardData {
	private SortboardType type;
	private List<SortboardScoreData> scoreData;
	
	public SortboardData() {
	}

	public SortboardData(SortboardData sortboardData) {
		List<SortboardScoreData> newScoreData = new ArrayList<SortboardScoreData>();
		this.type = sortboardData.type;
		this.scoreData = newScoreData;
		
		for (SortboardScoreData sd : sortboardData.getScoreData()) {
			SortboardScoreData nsd = new SortboardScoreData(sd.getPlayerId(), sd.getName(), sd.getProfession(), sd.getScore());
			newScoreData.add(nsd);
		}
	}
	
	public SortboardData clone() {
		SortboardData sortboardData = this;
		List<SortboardScoreData> tempSocreData = new ArrayList<>();
		SortboardData data = new SortboardData();
		data.setType(sortboardData.getType());
		data.setScoreData(tempSocreData);
		
		for (SortboardScoreData temp : sortboardData.getScoreData()) {
			SortboardScoreData copySocreData = new SortboardScoreData(temp.getPlayerId(), temp.getName(), temp.getProfession(), temp.getScore());
			tempSocreData.add(copySocreData);
		}
		
		return data;
	}

	public SortboardType getType() {
		return type;
	}

	public void setType(SortboardType type) {
		this.type = type;
	}

	public List<SortboardScoreData> getScoreData() {
		return scoreData;
	}

	public void setScoreData(List<SortboardScoreData> scoreData) {
		this.scoreData = scoreData;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((scoreData == null) ? 0 : scoreData.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SortboardData other = (SortboardData) obj;
		if (scoreData == null) {
			if (other.scoreData != null)
				return false;
		} else if (!scoreData.equals(other.scoreData))
			return false;
		return true;
	}

	
}
