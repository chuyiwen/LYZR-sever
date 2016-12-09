package newbee.morningGlory.mmorpg.player.activity.ladder;

public class CombatRecord {
	private byte isAction;// 战斗表现:0-主动挑战，1-被动挑战
	private String name; // 主动挑战则name为被挑战者的名字，若为被动挑战，则name为挑战者的名字
	private byte result;// 0-战败 1-战胜
	private int rankChange;// >0：上升rankChange名 =0：不变 <0：下降rankChange名

	public static final byte Action_Initiative = 0;
	public static final byte Action_Passive = 1;
	
	public CombatRecord() {

	}

	public CombatRecord(byte isAction, String name, byte result, int rankChange) {
		this.isAction = isAction;
		this.name = name;
		this.result = result;
		this.rankChange = rankChange;
	}

	public byte getIsAction() {
		return isAction;
	}

	public void setIsAction(byte isAction) {
		this.isAction = isAction;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte getResult() {
		return result;
	}

	public void setResult(byte result) {
		this.result = result;
	}

	public int getRankChange() {
		return rankChange;
	}

	public void setRankChange(int rankChange) {
		this.rankChange = rankChange;
	}

}
