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
package newbee.morningGlory.mmorpg.union;

import java.util.HashMap;
import java.util.Map;

/**
 * 行会成员，这个是从数据库初始的对象，我们尽量让它小一点
 */
public final class MGUnionMember implements Comparable<MGUnionMember>{
	public static Map<Byte, String> nameMap = new HashMap<Byte, String>();

	private String playerId;

	private String playerName;

	private int fightValue;

	private byte unionOfficialId;

	private byte online;

	private byte professionId;

	private int level;

	private long enterTime;
	
	private byte vipType;
	
	private long lastLogoutTime;

	public MGUnionMember() {

	}

	static {
		nameMap.put(MGUnionConstant.Chairman, "会长");
		nameMap.put(MGUnionConstant.Vice_Chairman, "副会长");
		nameMap.put(MGUnionConstant.Common, "帮众");
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public int getFightValue() {
		return fightValue;
	}

	public void setFightValue(int fightValue) {
		this.fightValue = fightValue;
	}

	public byte getUnionOfficialId() {
		return unionOfficialId;
	}

	public void setUnionOfficialId(byte unionOfficialId) {
		this.unionOfficialId = unionOfficialId;
	}

	public byte getOnline() {
		return online;
	}

	public void setOnline(byte online) {
		this.online = online;
	}
	
	public boolean isOnline() {
		return this.online == MGUnionConstant.Online;
	}

	public byte getProfessionId() {
		return professionId;
	}

	public void setProfessionId(byte professionId) {
		this.professionId = professionId;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public long getEnterTime() {
		return enterTime;
	}

	public void setEnterTime(long enterTime) {
		this.enterTime = enterTime;
	}
	
	public byte getVipType() {
		return vipType;
	}

	public void setVipType(byte vipType) {
		this.vipType = vipType;
	}

	public long getLastLogoutTime() {
		return lastLogoutTime;
	}

	public void setLastLogoutTime(long lastLogoutTime) {
		this.lastLogoutTime = lastLogoutTime;
	}

	/**
	 * 判断是否是会长
	 */
	public boolean isChairman() {
		return this.unionOfficialId == MGUnionConstant.Chairman;
	}

	/**
	 * 判断是否是副会长
	 */
	public boolean isViceChairman() {
		return this.unionOfficialId == MGUnionConstant.Vice_Chairman;
	}

	/**
	 * 成员排名规则：职位优先级高于加入公会时间；
	 * 不同职位：会长优先级高于副会长，副会长高于普通成员
	 * 相同职位：按加入公会先后的时间顺序
	 */
	@Override
	public int compareTo(MGUnionMember o) {
		long compareResult = getUnionOfficialId() - o.getUnionOfficialId();
		if (compareResult == 0) {
			compareResult = getEnterTime() - o.getEnterTime();
		}
		
		return (int)compareResult;
	}

}
