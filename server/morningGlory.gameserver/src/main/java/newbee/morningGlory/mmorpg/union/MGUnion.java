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

import java.util.List;
import java.util.UUID;

import newbee.morningGlory.code.MGErrorCode;
import newbee.morningGlory.code.MGSuccessCode;
import newbee.morningGlory.mmorpg.player.union.UnionMacro;
import newbee.morningGlory.mmorpg.player.union.actionEvent.G2C_Union_UnionList;
import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.utils.DateTimeUtil;

public final class MGUnion implements Comparable<MGUnion> {
	
	private String id;

	private String name;

	private short rank;

	private byte autoState;

	private long createTime;

	private long becomeKingCityMills;

	private byte kingCityType = MGUnionConstant.Not_KingCity;

	private String message;

	private MGUnionMember create;

	private boolean isKilledUnionBoss = false;

	private boolean isSignup;

	private boolean isApplyGameInstance;
	
	// 公会解散标记
	private boolean closed = false;
	
	private final MGUnionMemberMgr memberMgr = new MGUnionMemberMgr();

	private final MGUnionApplyMgr applyMgr = new MGUnionApplyMgr();
	
	public MGUnion() {
		setId(UUID.randomUUID().toString());
		message = UnionMacro.Default_Message;
	}
	
	public MGUnionMemberMgr getMemberMgr() {
		return memberMgr;
	}
	
	public MGUnionApplyMgr getUnionApplyMgr() {
		return applyMgr;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MGUnionMember getCreater() {
		return create;
	}

	public void setCreater(MGUnionMember create) {
		this.create = create;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public byte getKingCityType() {
		return kingCityType;
	}

	public void setKingCityType(byte kingCityType) {
		this.kingCityType = kingCityType;
	}

	public short getRank() {
		return rank;
	}

	public void setRank(short rank) {
		this.rank = rank;
	}

	public byte getAutoState() {
		return autoState;
	}

	public void setAutoState(byte autoState) {
		this.autoState = autoState;
	}

	public boolean isAutoState() {
		return this.autoState == UnionMacro.Auto_Agree;
	}
	
	public boolean isSignup() {
		return isSignup;
	}

	public void setSignup(boolean isSignup) {
		this.isSignup = isSignup;
	}

	public boolean isKilledUnionBoss() {
		return isKilledUnionBoss;
	}

	public void setKilledUnionBoss(boolean isKilledUnionBoss) {
		this.isKilledUnionBoss = isKilledUnionBoss;
	}

	public boolean isApplyGameInstance() {
		return isApplyGameInstance;
	}

	public void setApplyGameInstance(boolean isApplyGameInstance) {
		this.isApplyGameInstance = isApplyGameInstance;
	}

	public long getBecomeKingCityMills() {
		return becomeKingCityMills;
	}

	public void setBecomeKingCityMills(long becomeKingCityMills) {
		this.becomeKingCityMills = becomeKingCityMills;
	}
	
	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	/**
	 * 公会排名规则：王城公会排第一，其他公会按创建时间排序
	 */
	@Override
	public int compareTo(MGUnion o) {
		if (getKingCityType() == MGUnionConstant.Is_KingCity) {
			return -1;
		}

		if (o.getKingCityType() == MGUnionConstant.Is_KingCity) {
			return 1;
		}

		return (int) (getCreateTime() - o.getCreateTime());
	}
	
	public void closeUnion() {
		List<Player> allPlayers = MGUnionHelper.getUnionAllPlayers(this);
		for (Player player : allPlayers) {
			if (MGUnionHelper.isUnionCreater(player)) {
				MGUnionHelper.quitUnionAndSave(player, this, UnionMacro.Dissolve);
			} else {
				MGUnionHelper.quitUnionAndSave(player, this, UnionMacro.Remove_Member);
			}
		}
	}
	
	public int quitUnion(Player player) {
		MGUnionMember member = memberMgr.getMemberByPlayer(player);
		if (!memberMgr.removeMember(member)) {
			return MGErrorCode.CODE_UNION_PlayerAlreadyQuit;
		}
		
		MGUnionHelper.changeUnionNameAndKingCityAndBroadcast(player, "", MGUnionConstant.Not_KingCity);
		MGUnionHelper.changeUnionOfficialIdAndNotify(player, MGUnionConstant.NotUnionMember);
		
		return MGSuccessCode.CODE_SUCCESS; 
	}
	
	public int joinUnion(Player player, byte officialId) {
		if (isClosed()) {
			return MGErrorCode.CODE_UNION_UnionAlreadyNotExist;
		}
		
		MGUnionMember member = memberMgr.createMember(player);
		if (!memberMgr.addMember(member)) {
			return MGErrorCode.CODE_UNION_UnionIsFull;
		}
		
		member.setUnionOfficialId(officialId);
		
		MGUnionHelper.changeUnionNameAndKingCityAndBroadcast(player, getName(), getKingCityType());
		MGUnionHelper.changeUnionOfficialIdAndNotify(player, officialId);
		MGUnionHelper.sendUnionOperateEvent(player, this);
		
		return MGSuccessCode.CODE_SUCCESS;
	}
	
	public void changeKingCityUnion(byte kingCityType) {
		long timeMills = 0;
		if (kingCityType == MGUnionConstant.Is_KingCity) {
			timeMills = System.currentTimeMillis();
		} 
		
		setKingCityType(kingCityType);
		setBecomeKingCityMills(timeMills);
	}
	
	public boolean changeOfficialId(Player player, byte officialId) {
		if (!memberMgr.changeOfficialId(player, officialId)) {
			return false;
		}
		
		// 转让会长
		if (officialId == MGUnionConstant.Chairman) {
			MGUnionMember member = memberMgr.getMemberByPlayer(player);
			setCreater(member);
		}
		
		return true;
	}

	public ByteArrayReadWriteBuffer writeSelfUnionList(byte segment) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();
		List<MGUnionMember> partMembers = memberMgr.getSegmentMembers(segment);

		byte totalSegments = (byte) memberMgr.getTotalSegments();
		String unionName = getName();
		String createrPlayerName = getCreater().getPlayerName();
		byte autoState = getAutoState();
		short memberNumber = (short) memberMgr.getMemberCount();
		String message = getMessage();
		byte size = (byte) partMembers.size();

		buffer.writeByte(totalSegments);
		buffer.writeString(unionName);
		buffer.writeString(createrPlayerName);
		buffer.writeByte(autoState);
		buffer.writeShort(memberNumber);
		buffer.writeString(message);
		buffer.writeByte(size);

		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		for (MGUnionMember member : partMembers) {
			String playerId = member.getPlayerId();
			byte unionOfficialId = member.getUnionOfficialId();
			String playerName = member.getPlayerName();
			byte professionId = member.getProfessionId();
			int level = member.getLevel();
			int fightPower = member.getFightValue();
			byte online = MGUnionConstant.NotOnline;
			long lasLogoutTime = member.getLastLogoutTime();
			byte lastLogoutType = getLastLogoutType(lasLogoutTime);
			byte vipType = member.getVipType();

			Player player = playerManager.getOnlinePlayer(playerId);
			if (player != null) {
				level = player.getExpComponent().getLevel();
				fightPower = player.getFightPower();
				online = MGUnionConstant.Online;
				lastLogoutType = G2C_Union_UnionList.Online;
			}

			buffer.writeString(playerId);
			buffer.writeString(playerName);
			buffer.writeByte(professionId);
			buffer.writeShort((short) level);
			buffer.writeInt(fightPower);
			buffer.writeByte(unionOfficialId);
			buffer.writeByte(online);
			buffer.writeByte(vipType);
			buffer.writeByte(lastLogoutType);
		}

		return buffer;
	}
	
	private byte getLastLogoutType(long lasLogoutTime) {
		long now = System.currentTimeMillis();
		long oneDayMills = 24 * 3600 * 1000l;
		boolean theSameDay = DateTimeUtil.isTheSameDay(now, lasLogoutTime);
		
		if (lasLogoutTime <= 0) {
			return G2C_Union_UnionList.Online;
		}
		
		if (theSameDay) {
			return G2C_Union_UnionList.Today;
		}
		
		now -= oneDayMills;
		theSameDay = DateTimeUtil.isTheSameDay(now, lasLogoutTime);
		if (theSameDay) {
			return G2C_Union_UnionList.Yesterday;
		}
		
		return G2C_Union_UnionList.ThreeDaysAgo;
		
	}
}
