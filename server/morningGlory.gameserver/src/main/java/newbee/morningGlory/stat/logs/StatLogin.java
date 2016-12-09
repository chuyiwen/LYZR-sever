package newbee.morningGlory.stat.logs;

import sophia.mmorpg.stat.StatLogType;
import sophia.mmorpg.stat.logs.AbstractStatLog;
import sophia.mmorpg.utils.RecyclePool;

public class StatLogin extends AbstractStatLog {
	

	public static RecyclePool<StatLogin> Pool = new RecyclePool<StatLogin>() {

		@Override
		protected StatLogin instance() {
			return new StatLogin();
		}

		@Override
		protected void onRecycle(StatLogin obj) {
			obj.clear();
		}
	};
	
	@Override
	public void recycle() {
		Pool.recycle(this);
	}
	
	@Override
	public byte getStatLogType() {
		return StatLogType.Login;
	}
	
	public void setLoginTimes(int times) {
		data.n1 = times;
	}
	
	public void setLastLoginTime(long lastLoginTime) {
		data.n3 = lastLoginTime;
	}

	public void setCurLoginDays(int loginDays) {
		data.n2 = loginDays;
	}
}
