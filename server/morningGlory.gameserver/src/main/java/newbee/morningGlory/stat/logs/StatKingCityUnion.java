package newbee.morningGlory.stat.logs;

import sophia.mmorpg.stat.StatLogType;
import sophia.mmorpg.stat.logs.AbstractStatLog;
import sophia.mmorpg.utils.RecyclePool;

public class StatKingCityUnion extends AbstractStatLog{

	public static RecyclePool<StatKingCityUnion> Pool = new RecyclePool<StatKingCityUnion>() {

		@Override
		protected StatKingCityUnion instance() {
			return new StatKingCityUnion();
		}

		@Override
		protected void onRecycle(StatKingCityUnion obj) {
			obj.clear();
		}
	};
	
	@Override
	public byte getStatLogType() {
		return StatLogType.kingCity;
	}

	@Override
	public void recycle() {
		Pool.recycle(this);
	}
	
	public void setUnionName(String unionName) {
		data.s1 = unionName;
	}
	
	public void setCreaterPlayerName(String createPlayerName) {
		data.s2 = createPlayerName;
	}
	
	
	
	public void setBecomeKingCityMillis(long millis) {
		data.n2 = millis;
	}
	
	/** 以下是单个公会成员信息*/
	
	public void setMemberName(String memberName) {
		data.s3 = memberName;
	}
	
	public void setProfessionId(byte professionId) {
		data.n3 = professionId;
	}
	
	public void setLevel(int level) {
		data.n4 = level;
	}
	
	public void setFightValue(int fightValue) {
		data.n5 = fightValue;
	}
	
	public void setUnionOfficialId(byte unionOfficialId) {
		data.n6 = unionOfficialId;
	}
	
	public void setEnterTime(long enterTime) {
		data.n7 = enterTime;
	}
	
}
