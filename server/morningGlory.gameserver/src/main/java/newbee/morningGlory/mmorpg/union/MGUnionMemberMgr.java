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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

import com.google.common.base.Preconditions;

public final class MGUnionMemberMgr {
	
	private List<MGUnionMember> members = new ArrayList<MGUnionMember>();
	
	public MGUnionMember createMember(Player player) {
		Preconditions.checkArgument(player != null);
		byte vipType = player.getVipType();
		//long lastLogoutTime = MGPropertyAccesser.getLastLogoutTime(player.getProperty());

		MGUnionMember member = new MGUnionMember();
		member.setPlayerId(player.getId());
		member.setPlayerName(player.getName());
		member.setProfessionId(player.getProfession());
		member.setUnionOfficialId(MGUnionConstant.Common);
		member.setLevel(player.getExpComponent().getLevel());
		member.setFightValue(player.getFightPower());
		member.setEnterTime(System.currentTimeMillis());
		member.setVipType(vipType);
		member.setLastLogoutTime(0);
		byte onlineType = player.isOnline() ? MGUnionConstant.Online : MGUnionConstant.NotOnline;
		member.setOnline(onlineType);

		return member;
	}
	
	public synchronized int getMemberCount() {
		return members.size();
	}
	
	public synchronized boolean addMember(MGUnionMember member) {
		if (getMemberCount() >= MGUnionConstant.Member_Number_UpperLimit) {
			return false;
		}

		members.add(member);
		return true;
	}
	
	public synchronized boolean removeMember(MGUnionMember member) {
		return members.remove(member);
	}
	
	public synchronized void sortMember() {
		Collections.sort(members);
	}
	
	public synchronized boolean isMemberFull() {
		return getMemberCount() >= MGUnionConstant.Member_Number_UpperLimit;
	}
	
	public synchronized int getTotalSegments() {
		int size = getMemberCount();
		int moduloValue = size % MGUnionConstant.Member_List_CountPerPage;
		int dividValue = size / MGUnionConstant.Member_List_CountPerPage;
		return moduloValue == 0 ? dividValue : dividValue + 1;
	}
	
	public synchronized List<MGUnionMember> getSegmentMembers(byte segment) {
		Preconditions.checkArgument(segment > 0);
		
		List<MGUnionMember> tempList = new ArrayList<MGUnionMember>();
		int begin = (segment - 1) * MGUnionConstant.Member_List_CountPerPage + 1;
		int end = segment * MGUnionConstant.Member_List_CountPerPage;
		for (int i = begin; i <= end && i <= getMemberCount(); i++) {
			tempList.add(members.get(i - 1));
		}
		
		return tempList;
	}

	public synchronized int getViceChairmanCount() {
		int count = 0;
		for (MGUnionMember member : members) {
			if (member.isViceChairman()) {
				count++;
			}
		}
		
		return count;
	}
	
	public synchronized List<MGUnionMember> getMemberList() {
		List<MGUnionMember> list = new ArrayList<>();
		list.addAll(members);
		return list;
	}
	
	public synchronized MGUnionMember getMemberByPlayer(Player player) {
		for (MGUnionMember member : members) {
			if (StringUtils.equals(member.getPlayerName(), player.getName())) {
				return member;
			}
		}
		
		return null;
	}
	
	public synchronized void changeKingCityProperty(byte kingCityType) {
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		for (MGUnionMember member : members) {
			Player player = playerManager.getPlayerByName(member.getPlayerName());
			if (player != null) {
				MGUnionHelper.changeUnionKingCityTypeAndBroadcast(player, kingCityType);
			} 
		}
	}
	
	public synchronized boolean changeOfficialId(Player player, byte officialId) {
		MGUnionMember findMember = getMemberByPlayer(player);
		if (findMember == null) {
			return false;
		}
		
		findMember.setUnionOfficialId(officialId);
		
		MGUnionHelper.changeUnionOfficialIdAndNotify(player, officialId);

		return true;
	}
	
	public synchronized ByteArrayReadWriteBuffer memberInfoToBytes(ByteArrayReadWriteBuffer buffer) {
		buffer.writeInt(getMemberCount());
		for (MGUnionMember member : members) {
			buffer.writeString(member.getPlayerId());
			buffer.writeString(member.getPlayerName());
			buffer.writeByte(member.getProfessionId());
			buffer.writeInt(member.getLevel());
			buffer.writeInt(member.getFightValue());
			buffer.writeByte(member.getUnionOfficialId());
			buffer.writeByte(member.getOnline());
			buffer.writeLong(member.getEnterTime());
		}
		
		return buffer;
	}
}
