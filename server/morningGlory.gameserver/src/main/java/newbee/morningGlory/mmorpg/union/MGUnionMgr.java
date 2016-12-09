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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import newbee.morningGlory.code.MGErrorCode;
import newbee.morningGlory.code.MGSuccessCode;
import newbee.morningGlory.mmorpg.player.union.MGPlayerUnionComponent;
import newbee.morningGlory.mmorpg.player.union.MGPlayerUnionInvitedMgr;
import newbee.morningGlory.mmorpg.player.union.UnionMacro;
import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.money.PlayerMoneyComponent;

import com.google.common.base.Preconditions;

public final class MGUnionMgr {
	
	private Map<String, MGUnion> nameToUnionMap = new HashMap<>();
	
	private List<MGUnion> sortUnionList = new ArrayList<>();
	
	public static final MGUnionMgr instance = new MGUnionMgr();

	public static MGUnionMgr getInstance() {
		return instance;
	}
	
	private MGUnionMgr() {

	}
	
	private int checkCreateUnion(Player player, String unionName) {
		if (nameToUnionMap.containsKey(unionName)) {
			return MGErrorCode.CODE_UNION_UnionNameExist;
		}
		
		if (MGUnionHelper.getUnion(player) != null) {
			return MGErrorCode.CODE_UNION_AlreadyInOneUnion;
		}
		
		PlayerMoneyComponent playerMoneyComponent = player.getPlayerMoneyComponent();
		if (!playerMoneyComponent.subGold(UnionMacro.Default_CreateUnion_gold, ItemOptSource.Union)) {
			return MGErrorCode.CODE_UNION_CrtPlayerMoneyNotEnogh;
		}
		
		if (!ItemFacade.removeItem(player, UnionMacro.Union_Card_RefId, 1, false, ItemOptSource.Union)) {
			playerMoneyComponent.addGold(UnionMacro.Default_CreateUnion_gold, ItemOptSource.Union);
			return MGErrorCode.CODE_UNION_CrtPlayerItemNotEnough;
		}
		
		return MGSuccessCode.CODE_SUCCESS;
	}
	
	public synchronized int createUnion(Player player, String unionName) {
		int code = checkCreateUnion(player, unionName);
		if (code != MGSuccessCode.CODE_SUCCESS) {
			return code;
		}
		
		MGUnion union = new MGUnion();
		union.setName(unionName);
		code = union.joinUnion(player, MGUnionConstant.Chairman);
		if (code != MGSuccessCode.CODE_SUCCESS) {
			return code;
		}
		
		MGUnionMember member = union.getMemberMgr().getMemberByPlayer(player);
		int unionCount = getUnionCount();
		union.setCreater(member);
		union.setCreateTime(System.currentTimeMillis());
		union.setBecomeKingCityMills(0);
		union.setRank((short)(unionCount + 1));
		addUnion(union);
		
		return MGSuccessCode.CODE_SUCCESS;
	}
	
	public synchronized int closeUnion(Player player, String unionName) {
		MGUnion union = getUnion(unionName);
		if (union == null) {
			return MGErrorCode.CODE_UNION_UnionIsNotExist;
		}
		
		union.setClosed(true);
		
		return MGSuccessCode.CODE_SUCCESS;
	}
	
	public synchronized int applyUnion(Player player, String unionName) {
		MGUnion union = getUnion(unionName);
		if (union == null) {
			return MGErrorCode.CODE_UNION_UnionIsNotExist;
		}
		
		if (union.getMemberMgr().isMemberFull()) {
			return MGErrorCode.CODE_UNION_UnionIsFull;
		}
		
		if (MGUnionHelper.getUnion(player) != null) {
			return MGErrorCode.CODE_UNION_AlreadyInOneUnion;
		}
		
		if (!union.getUnionApplyMgr().addApply(player.getId())) {
			return MGErrorCode.CODE_UNION_ApplyNumberUpperLimit;
		}
		
		return MGSuccessCode.CODE_SUCCESS;
	}
	
	public synchronized int inviteUnion(Player player, String unionName) {
		MGUnion union = getUnion(unionName);
		if (union == null) {
			return MGErrorCode.CODE_UNION_UnionIsNotExist;
		}
		
		if (MGUnionHelper.getUnion(player) != null) {
			return MGErrorCode.CODE_UNION_AlreadyInOneUnion;
		}
		
		MGPlayerUnionComponent unionComponent = (MGPlayerUnionComponent) player.getTagged(MGPlayerUnionComponent.Tag);
		MGPlayerUnionInvitedMgr playerUnionInvitedMgr = unionComponent.getPlayerUnionMgr().getPlayerUnionInvitedMgr();
		if (playerUnionInvitedMgr.containsInviteUnionId(union.getId())) {
			return MGErrorCode.CODE_UNION_CANTRepeatInvite;
		}
		
		if (!playerUnionInvitedMgr.addInviteUnionId(union.getId())) {
			return MGErrorCode.CODE_UNION_InviteNumberUpperLimit;
		}
		
		return MGSuccessCode.CODE_SUCCESS;
	}
	
	public synchronized int joinUnion(Player player, String unionName) {
		MGUnion union = getUnion(unionName);
		if (union == null) {
			return MGErrorCode.CODE_UNION_UnionIsNotExist;
		}
		
		if (union.getMemberMgr().isMemberFull()) {
			return MGErrorCode.CODE_UNION_UnionIsFull;
		}
		
		if (MGUnionHelper.getUnion(player) != null) {
			return MGErrorCode.CODE_UNION_AlreadyInOneUnion;
		}
		
		return union.joinUnion(player, MGUnionConstant.Common);
	}
	
	public synchronized int quitUnion(Player player, String unionName) {
		MGUnion union = getUnion(unionName);
		if (union == null) {
			return MGErrorCode.CODE_UNION_UnionIsNotExist;
		}
		
		return union.quitUnion(player);
	}
	
	public synchronized void addUnion(MGUnion union) {
		nameToUnionMap.put(union.getName(), union);
		short rank = (short) (sortUnionList.size() + 1);
		union.setRank(rank);
		sortUnionList.add(union);
	}

	public synchronized void removeUnion(String unionName) {
		MGUnion union = nameToUnionMap.remove(unionName);
		if (union != null) {
			sortUnionList.remove(union);
			short rank = 1;
			for (MGUnion tmpUnion : sortUnionList) {
				tmpUnion.setRank(rank++);
			}
		}
	}

	public synchronized MGUnion getUnion(String unionName) {
		return nameToUnionMap.get(unionName);
	}
	
	public synchronized String getKingCityUnionName() {
		MGUnion kingCityUnion = getKingCityUnion();
		if (kingCityUnion != null) {
			return kingCityUnion.getName();
		}
		
		return "";
	}
	
	public synchronized MGUnion getKingCityUnion() {
		for (Entry<String, MGUnion> entry : nameToUnionMap.entrySet()) {
			MGUnion union = entry.getValue();
			if (union.getKingCityType() == MGUnionConstant.Is_KingCity) {
				return union; 
			}
		}
		
		return null;
	}
	
	public synchronized void changeKingCityUnion(MGUnion union, byte kingCityType) {
		union.changeKingCityUnion(kingCityType);
		sortUnion();
	}
	
	private synchronized int getUnionCount() {
		return nameToUnionMap.size();
	}
	
	public synchronized void sortUnion() {
		List<MGUnion> unions = new ArrayList<MGUnion>();
		for (Entry<String, MGUnion> entry : nameToUnionMap.entrySet()) {
			unions.add(entry.getValue());
		}

		Collections.sort(unions);
		
		short rank = 1;
		for (MGUnion union : unions) {
			union.setRank(rank++);
		}
		
		sortUnionList = unions;
	}
	
	private synchronized int getTotalSegments() {
		int size = getUnionCount();
		int moduloValue = size % MGUnionConstant.Union_List_CountPerPage;
		int dividValue = size / MGUnionConstant.Union_List_CountPerPage;
		return moduloValue == 0 ? dividValue : dividValue + 1;
	}
	
	/**
	 * 获取指定分段的公会列表
	 * 
	 * @param segment
	 * @return
	 */
	public synchronized List<MGUnion> getUnionsOfSegment(byte segment) {
		Preconditions.checkArgument(segment > 0);
		
		List<MGUnion> tempList = new ArrayList<MGUnion>();
		int begin = (segment - 1) * MGUnionConstant.Union_List_CountPerPage + 1;
		int end = segment * MGUnionConstant.Union_List_CountPerPage;
		
		int size = sortUnionList.size();
		if (begin > size) {
			return tempList;
		}
		
		for (int i = begin; i <= end; i++) {
			if (i > size) {
				break;
			}
			
			tempList.add(sortUnionList.get(i - 1));
		}
		
		return tempList;
	}
	
	public synchronized MGUnion getApplyUnion(String playerId) {
		for (MGUnion union : nameToUnionMap.values()) {
			if (union.getUnionApplyMgr().containsApplyId(playerId)) {
				return union;
			}
		}
		
		return null;
	}
	
	public ByteArrayReadWriteBuffer writeAllUnionList(byte segment, Player player) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();
		
		List<MGUnion> unions = getUnionsOfSegment(segment);
		
		byte count = (byte) unions.size();
		buffer.writeByte((byte) getTotalSegments());
		buffer.writeByte(count);

		for (MGUnion union : unions) {
			buffer.writeShort(union.getRank());
			buffer.writeString(union.getName());
			buffer.writeString(union.getCreater().getPlayerName());
			buffer.writeShort((short) union.getMemberMgr().getMemberCount());
		}
		
		MGUnion applyUnion = getApplyUnion(player.getId());
		String applyUnionName = applyUnion == null ? "" : applyUnion.getName();
		buffer.writeString(applyUnionName);
		
		return buffer;
	}
}
