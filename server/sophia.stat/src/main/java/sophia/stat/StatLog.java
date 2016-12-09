/**
 *   Copyright 2013-2015 Sophia
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package sophia.stat;

import java.sql.Date;
import java.sql.Time;

public abstract class StatLog {

	protected StatLogDBData data = new StatLogDBData();

	protected StatLog() {
	}

	public void setPlayerId(String playerId) {
		data.playerId = playerId;
	}

	public void setPlayerName(String playerName) {
		data.playerName = playerName;
	}

	public void setIdentityName(String identityName) {
		data.identityName = identityName;
	}

	public void setQdCode1(int qdCode1) {
		data.qdCode1 = qdCode1;
	}

	public void setQdCode2(int qdCode2) {
		data.qdCode2 = qdCode2;
	}

	public StatLogDBData toDBData() {
		return data;
	}

	protected void init(byte statType) {
		long datetime = System.currentTimeMillis();
		data.date = new Date(datetime);
		data.time = new Time(datetime);
		data.statType = statType;
	}

	protected void clear() {
		data.date = null;

		data.time = null;

		data.qdCode1 = -1;

		data.qdCode2 = -1;

		data.playerId = null;

		data.identityName = null;

		data.playerName = null;

		data.statType = 0;

		data.n1 = Integer.MIN_VALUE;
		data.n2 = Long.MIN_VALUE;
		data.n3 = Long.MIN_VALUE;
		data.n4 = Long.MIN_VALUE;
		data.n5 = Long.MIN_VALUE;
		data.n6 = Long.MIN_VALUE;
		data.n7 = Long.MIN_VALUE;
		data.n8 = Long.MIN_VALUE;
		data.n9 = Long.MIN_VALUE;
		data.n10 = Long.MIN_VALUE;

		data.s1 = null;
		data.s2 = null;
		data.s3 = null;
		data.s4 = null;
		data.s5 = null;
	}
	
	public abstract void recycle();

}
