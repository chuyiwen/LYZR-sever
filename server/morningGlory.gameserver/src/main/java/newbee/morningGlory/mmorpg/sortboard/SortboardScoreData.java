package newbee.morningGlory.mmorpg.sortboard;

public class SortboardScoreData implements Comparable<SortboardScoreData> {
	private String playerId;
	private String name;
	private int profession;
	private Integer score;

	public SortboardScoreData(String playerId, String name, int profession, int score) {
		this.setPlayerId(playerId);
		this.setName(name);
		this.setProfession(profession);
		this.setScore((int) score);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getProfession() {
		return profession;
	}

	public void setProfession(int profession) {
		this.profession = profession;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((playerId == null) ? 0 : playerId.hashCode());
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
		SortboardScoreData other = (SortboardScoreData) obj;
		if (playerId == null) {
			if (other.playerId != null)
				return false;
		} else if (!playerId.equals(other.playerId))
			return false;
		return true;
	}

	@Override
	public int compareTo(SortboardScoreData scoreData) {
		Integer score2 = scoreData.getScore();
		return score2.compareTo(score);
	}
}
