package sophia.stat;

public class StatRechargeData {
	
	private String id;
	private String playerName;	
	private String identityName;
	private int qdCode1;
	private int qdCode2;
	
	public StatRechargeData(String id, String playerName, String identityName, int qdCode1, int qdCode2, int game_Gold, float pay_money, long pay_time) {
		super();
		this.id = id;
		this.playerName = playerName;
		this.identityName = identityName;
		this.qdCode1 = qdCode1;
		this.qdCode2 = qdCode2;
		this.game_Gold = game_Gold;
		this.pay_money = pay_money;
		this.pay_time = pay_time;
	}
	public String getPlayerName() {
		return playerName;
	}
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	public int getQdCode1() {
		return qdCode1;
	}
	public void setQdCode1(int qdCode1) {
		this.qdCode1 = qdCode1;
	}
	public int getQdCode2() {
		return qdCode2;
	}
	public void setQdCode2(int qdCode2) {
		this.qdCode2 = qdCode2;
	}
	public void setPay_money(float pay_money) {
		this.pay_money = pay_money;
	}
	private int game_Gold;
	private float pay_money;
	private long pay_time;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
		
	public String getIdentityName() {
		return identityName;
	}
	public void setIdentityName(String identityName) {
		this.identityName = identityName;
	}
	public int getGame_Gold() {
		return game_Gold;
	}
	public void setGame_Gold(int game_Gold) {
		this.game_Gold = game_Gold;
	}
	public float getPay_money() {
		return pay_money;
	}
	public void setPay_money(int pay_money) {
		this.pay_money = pay_money;
	}
	public long getPay_time() {
		return pay_time;
	}
	public void setPay_time(long pay_time) {
		this.pay_time = pay_time;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + game_Gold;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((identityName == null) ? 0 : identityName.hashCode());
		result = prime * result + Float.floatToIntBits(pay_money);
		result = prime * result + (int) (pay_time ^ (pay_time >>> 32));
		result = prime * result + ((playerName == null) ? 0 : playerName.hashCode());
		result = prime * result + qdCode1;
		result = prime * result + qdCode2;
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
		StatRechargeData other = (StatRechargeData) obj;
		if (game_Gold != other.game_Gold)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (identityName == null) {
			if (other.identityName != null)
				return false;
		} else if (!identityName.equals(other.identityName))
			return false;
		if (Float.floatToIntBits(pay_money) != Float.floatToIntBits(other.pay_money))
			return false;
		if (pay_time != other.pay_time)
			return false;
		if (playerName == null) {
			if (other.playerName != null)
				return false;
		} else if (!playerName.equals(other.playerName))
			return false;
		if (qdCode1 != other.qdCode1)
			return false;
		if (qdCode2 != other.qdCode2)
			return false;
		return true;
	}
	
	
	
	
}
