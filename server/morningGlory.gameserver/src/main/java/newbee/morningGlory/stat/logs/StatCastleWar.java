package newbee.morningGlory.stat.logs;

import sophia.mmorpg.stat.StatLogType;
import sophia.mmorpg.stat.logs.AbstractStatLog;
import sophia.mmorpg.utils.RecyclePool;

public class StatCastleWar extends AbstractStatLog {
	
	public static final byte Enter = 1;
	public static final byte Leave = 2;
	public static final byte Gift = 3;
	public static final byte Instance = 4;
	public static final byte JoinWar = 5;
	public static final byte Finish = 6;
	
	public static RecyclePool<StatCastleWar> Pool = new RecyclePool<StatCastleWar>() {

		@Override
		protected StatCastleWar instance() {
			return new StatCastleWar();
		}

		@Override
		protected void onRecycle(StatCastleWar obj) {
			obj.clear();
		}
	};
	
	@Override
	public void recycle() {
		Pool.recycle(this);
	}
	
	@Override
	public byte getStatLogType() {
		return StatLogType.CastleWar;
	}
	
	public void setOptType(byte type) {
		data.n1 = type;
	}
	
	public void setUnionName(String unionName) {
		data.s1 = unionName;
	}
	
	public void setOfficialId(byte OfficialId) {
		data.n2 = OfficialId;
	}
	
	public void setFightPower(int fightPower) {
		data.n3 = fightPower;
	}
}