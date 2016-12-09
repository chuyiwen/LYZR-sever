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

public class StatLogDBData {
	public Date date;

	public Time time;

	public int qdCode1;

	public int qdCode2;

	public String playerId;

	public String identityName;

	public String playerName;

	public byte statType;

	public int n1 = Integer.MIN_VALUE;
	public long n2 = Long.MIN_VALUE;
	public long n3 = Long.MIN_VALUE;
	public long n4 = Long.MIN_VALUE;
	public long n5 = Long.MIN_VALUE;
	public long n6 = Long.MIN_VALUE;
	public long n7 = Long.MIN_VALUE;
	public long n8 = Long.MIN_VALUE;
	public long n9 = Long.MIN_VALUE;
	public long n10 = Long.MIN_VALUE;

	public String s1 = null;
	public String s2 = null;
	public String s3 = null;
	public String s4 = null;
	public String s5 = null;

	@Override
	public String toString() {
		return date + " " + time + " " + qdCode1 + " " + qdCode2 + " " + playerId + " " + identityName + " " + playerName + " " + statType + " " + n1 + " " + n2 + " " + n3 + " "
				+ n4 + " " + n5 + " " + n6 + " " + n7 + " " + n8 + " " + n9 + " " + n10 + " " + s1 + " " + s2 + " " + s3 + " " + s4 + " " + s5;
	}
}
