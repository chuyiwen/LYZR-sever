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
import java.util.List;

import newbee.morningGlory.code.MGSuccessCode;
import newbee.morningGlory.mmorpg.player.union.ExitUnion_GE;
import newbee.morningGlory.mmorpg.player.union.MGPlayerUnionComponent;
import newbee.morningGlory.mmorpg.player.union.UnionMacro;
import newbee.morningGlory.mmorpg.player.union.actionEvent.G2C_Union_Chat;
import newbee.morningGlory.mmorpg.player.union.actionEvent.G2C_Union_Update;
import newbee.morningGlory.mmorpg.player.union.actionEvent.MGUnionEventDefines;
import newbee.morningGlory.mmorpg.player.union.gameEvent.UnionOperateGE;
import newbee.morningGlory.mmorpg.player.union.gameEvent.UnionOperateType;
import newbee.morningGlory.stat.MGStatFunctions;
import newbee.morningGlory.stat.logs.StatUnion;

import org.apache.commons.lang3.StringUtils;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.foundation.property.PropertyDictionary;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.Mail.Mail;
import sophia.mmorpg.Mail.MailMgr;
import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.player.chat.PlayerChatFacade;
import sophia.mmorpg.player.chat.event.ChatEventDefines;
import sophia.mmorpg.player.chat.event.G2C_Chat_System;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.ref.editorgen.MGPropertySymbolDefines;

import com.google.common.base.Preconditions;

public final class MGUnionHelper {
	
	public static void changeUnionNameAndKingCityAndBroadcast(Player player, String unionName, byte kingCityType) {
		Preconditions.checkArgument(player != null, "player is null");
		Preconditions.checkArgument(kingCityType == MGUnionConstant.Is_KingCity || kingCityType == MGUnionConstant.Not_KingCity, "invalid kingCityType" + kingCityType);
		PropertyDictionary property = new PropertyDictionary();
		MGPropertyAccesser.setOrPutUnionName(player.getProperty(), unionName);
		MGPropertyAccesser.setOrPutUnionName(property, unionName);
		MGPropertyAccesser.setOrPutIsKingCity(player.getProperty(), kingCityType);
		MGPropertyAccesser.setOrPutIsKingCity(property, kingCityType);
		player.notifyPorperty(property);
		player.getAoiComponent().broadcastProperty(property);
	}

	public static void changeUnionOfficialIdAndNotify(Player player, byte unionOfficialId) {
		Preconditions.checkArgument(player != null, "player is null");
		Preconditions.checkArgument((unionOfficialId >= 1 && unionOfficialId <= 3) || unionOfficialId == -1, "invaid officialId=" + unionOfficialId);
		PropertyDictionary property = new PropertyDictionary();
		MGPropertyAccesser.setOrPutUnionOfficialId(player.getProperty(), unionOfficialId);
		MGPropertyAccesser.setOrPutUnionOfficialId(property, unionOfficialId);
		player.notifyPorperty(property);
	}
	
	public static void changeUnionKingCityTypeAndBroadcast(Player player, byte kingCityType) {
		Preconditions.checkArgument(player != null, "player is null");
		Preconditions.checkArgument(kingCityType == MGUnionConstant.Is_KingCity || kingCityType == MGUnionConstant.Not_KingCity, "invalid kingCityType" + kingCityType);
		PropertyDictionary property = new PropertyDictionary();
		MGPropertyAccesser.setOrPutIsKingCity(player.getProperty(), kingCityType);
		MGPropertyAccesser.setOrPutIsKingCity(property, kingCityType);
		player.notifyPorperty(property);
		player.getAoiComponent().broadcastProperty(property);
	}
	
	public static String getUnionName(Player player) {
		Preconditions.checkArgument(player != null, "player is null");
		if (player.getProperty().contains(MGPropertySymbolDefines.UnionName_Id)) {
			return MGPropertyAccesser.getUnionName(player.getProperty());
		}
		
		return "";
	}
	
	public static MGUnion getUnion(Player player) {
		Preconditions.checkArgument(player != null, "player is null");
		String unionName = getUnionName(player);
		if (StringUtils.isEmpty(unionName)) {
			return null;
		}
		
		MGUnionMgr unionMgr = MGUnionMgr.getInstance();
		return unionMgr.getUnion(unionName);
	}
	
	public static MGUnion getApplyUnion(Player player) {
		Preconditions.checkArgument(player != null, "player is null");
		MGPlayerUnionComponent unionComponent = (MGPlayerUnionComponent) player.getTagged(MGPlayerUnionComponent.Tag);
		String applyUnionName = unionComponent.getPlayerUnionMgr().getApplyUnionName();
		MGUnionMgr unionMgr = MGUnionMgr.getInstance();
		MGUnion union = null;
		if (!StringUtils.isEmpty(applyUnionName)) {
			union = unionMgr.getUnion(applyUnionName);
		}
		
		return union == null ? unionMgr.getApplyUnion(player.getId()) : union;
	}
	
	public static void clearApplyUnion(Player player) {
		MGUnion applyUnion = getApplyUnion(player);
		if (applyUnion != null) {
			applyUnion.getUnionApplyMgr().removeApply(player.getId());
		}
	}
	
	public static boolean isUnionCreater(Player player) {
		Preconditions.checkArgument(player != null, "player is null");
		String unionName = getUnionName(player);
		if (StringUtils.isEmpty(unionName)) {
			return false;
		}
		
		MGUnionMgr unionMgr = MGUnionMgr.getInstance();
		MGUnion union = unionMgr.getUnion(unionName);
		if (union == null) {
			return false;
		}
		
		if (!StringUtils.equals(union.getCreater().getPlayerName(), player.getName())) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * 判断两个玩家是否在同一个公会
	 */
	public static boolean isInTheSameUnion(FightSprite sprit, FightSprite target) {
		Preconditions.checkNotNull(target != null);
		Preconditions.checkNotNull(sprit != null);
		String unionName2 = MGPropertyAccesser.getUnionName(target.getProperty());
		String unionName1 = MGPropertyAccesser.getUnionName(sprit.getProperty());
		if (StringUtils.isEmpty(unionName1) || StringUtils.isEmpty(unionName2)) {
			return false;
		}
		if (!StringUtils.equals(unionName1, unionName2)) {
			return false;
		}

		return true;
	}
	
	public static boolean isKingCityUnionMember(Player player) {
		MGUnion union = getUnion(player);
		if (union == null) {
			return false;
		}
		
		return union.getKingCityType() == MGUnionConstant.Is_KingCity;
	}
	
	public static List<Player> getUnionAllPlayers(MGUnion union) {
		List<MGUnionMember> memberList = union.getMemberMgr().getMemberList();
		List<Player> playerList = new ArrayList<Player>();
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		for (MGUnionMember member : memberList) {
			Player player = playerManager.getPlayerByName(member.getPlayerName());
			if (player != null) {
				playerList.add(player);
			}
		}
		
		return playerList;
	}
	
	public static List<Player> getUnionOnlinePlayers(MGUnion union) {
		List<MGUnionMember> memberList = union.getMemberMgr().getMemberList();
		List<Player> playerList = new ArrayList<Player>();
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		for (MGUnionMember member : memberList) {
			Player player = playerManager.getOnlinePlayerByName(member.getPlayerName());
			if (player != null) {
				playerList.add(player);
			}
		}
		
		return playerList;
	}
	
	public static List<String> getUnionAllPlayerIds(MGUnion union) {
		List<String> playerIdList = new ArrayList<String>();
		List<MGUnionMember> memberList = union.getMemberMgr().getMemberList();
		for (MGUnionMember member : memberList) {
			playerIdList.add(member.getPlayerId());
		}
		
		return playerIdList;
	}
	
	public static Player getPlayerByMember(MGUnionMember member) {
		Preconditions.checkArgument(member != null, "member is null");
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		return playerManager.getPlayerByName(member.getPlayerName());
	}
	
	public static void changeKingCityUnionMember(MGUnion union, byte kingCityType) {
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		MGUnionMgr unionMgr = MGUnionMgr.getInstance();
		unionMgr.changeKingCityUnion(union, kingCityType);
		List<MGUnionMember> memberList = union.getMemberMgr().getMemberList();
		for (MGUnionMember member : memberList) {
			Player player = playerManager.getPlayerByName(member.getPlayerName());
			if (player != null) {
				MGUnionHelper.changeUnionKingCityTypeAndBroadcast(player, kingCityType);
				MMORPGContext.getPlayerComponent().getSaveService().saveImmediateData(player);
			} 
		}
		
		MGUnionSaver.getInstance().saveImmediateData(union);
	}
	
	public static void changeKingCityUnion(MGUnion union) {
		Preconditions.checkArgument(union != null, "union is null");
		MGUnionMgr unionMgr = MGUnionMgr.getInstance();
		MGUnion kingCityUnion = unionMgr.getKingCityUnion();
		if (kingCityUnion != null) {
			changeKingCityUnionMember(kingCityUnion, MGUnionConstant.Not_KingCity);
		}
		
		changeKingCityUnionMember(union, MGUnionConstant.Is_KingCity);
	}
	
	public static int joinUnionAndSave(Player player, MGUnion union) {
		Preconditions.checkArgument(player != null, "player is null");
		MGUnionMgr unionMgr = MGUnionMgr.getInstance();
		String unionName = union.getName();
		int code = unionMgr.joinUnion(player, unionName);
		if (code != MGSuccessCode.CODE_SUCCESS) {
			return code;
		}
		
		clearApplyUnion(player);
		
		sendUnionUpdateMessage(union, UnionMacro.Add_Member);
		
		MGStatFunctions.unionStat(player, StatUnion.Add, unionName, union.getId(), MGUnionConstant.Common);
		
		// 发送公会聊天消息
		sendUnionSystemChatMessage(player, union, UnionMacro.Add_Member);

		// 发送邮件
		sendUnionMail(player, union, UnionMacro.Add_Member);
		
		MGUnionSaver.getInstance().saveImmediateData(union);
		
		MMORPGContext.getPlayerComponent().getSaveService().saveImmediateData(player);
		
		return MGSuccessCode.CODE_SUCCESS;
	}
	
	public static int quitUnionAndSave(Player player, MGUnion union, byte quitType) {
		Preconditions.checkArgument(player != null, "player is null");
		String unionName = union.getName();
		String unionId = union.getId();
		
		
		MGUnionMgr unionMgr = MGUnionMgr.getInstance();
		int code = unionMgr.quitUnion(player, unionName);
		if (code != MGSuccessCode.CODE_SUCCESS) {
			return code;
		}

		byte optType = StatUnion.Exit;
		if (quitType == UnionMacro.BeKickedOut) {
			optType = StatUnion.kick;
		}
		
		sendUnionSystemChatMessage(player, union, quitType);
		
		MGStatFunctions.unionStat(player, optType, unionName, unionId, MGUnionConstant.NotUnionMember);
		
		sendUnionUpdateMessage(union, UnionMacro.Remove_Member);

		sendUnionMail(player, union, quitType);

		sendExitUnionGameEvent(union, player);
		
		MMORPGContext.getPlayerComponent().getSaveService().saveImmediateData(player);
		
		return MGSuccessCode.CODE_SUCCESS;
	}
	
	public static int calculateStringLength(String fromString) {
		int count = 0;
		String regex = "^[\u4e00-\u9fa5]{1}$";
		String[] names = fromString.split("");
		for (int i = 1; i < names.length; i++) {
			if (names[i].matches(regex)) {
				count++;
			}
		}

		return count * 2 + (fromString.length() - count);
	}
	
	public static void sendUnionMail(Player player, MGUnion union, byte optType) {
		Preconditions.checkArgument(player != null);
		Preconditions.checkArgument(union != null);
		String mailMessage = getMailMessage(optType, union);
		MailMgr.sendMailById(player.getId(), mailMessage, Mail.gonggao);
	}
	
	public static void sendUnionUpdateMessage(MGUnion union, byte optType) {
		Preconditions.checkArgument(union != null);
		G2C_Union_Update res = (G2C_Union_Update) MessageFactory.getMessage(MGUnionEventDefines.G2C_Union_Update);
		res.setType(optType);
		sendMessageToUnionOnlineMember(union, res);
	}
	
	public static void sendUnionSystemChatMessage(Player player, MGUnion union, byte optType) {
		String chatMessage = getChatMessage(optType, player);
		sendUnionChatMessage(player, union, chatMessage, UnionMacro.Type_System);
	}
	
	public static void sendUnionPersonalChatMessage(Player player, MGUnion union, String msg) {
		sendUnionChatMessage(player, union, msg, UnionMacro.Type_Personal);
	}
	
	public static void sendUnionSystemChatMessage(MGUnion union, String msg) {
		MGUnionMember creater = union.getCreater();
		Player player = MMORPGContext.getPlayerComponent().getPlayerManager().getPlayer(creater.getPlayerId());
		sendUnionChatMessage(player, union, msg, UnionMacro.Type_System);
	}
	
	public static void sendPersonalChatMessage(Player fromPlayer, Player toPlayer, byte optType) {
		String content = MGUnionHelper.getChatMessage(optType, fromPlayer);
		if (!StringUtils.isEmpty(content)) {
			G2C_Chat_System chatRes = (G2C_Chat_System) MessageFactory.getMessage(ChatEventDefines.G2C_Chat_System);
			chatRes.setMsg(content);
			chatRes.setType((byte) optType);
			PlayerChatFacade.sendMessageToPlayer(toPlayer, chatRes);
		}
	}
	
	private static void sendUnionChatMessage(Player player, MGUnion union, String msg, byte optType) {
		G2C_Union_Chat res = (G2C_Union_Chat) MessageFactory.getMessage(MGUnionEventDefines.G2C_Union_Chat);
		res.setType(optType);
		res.setMsg(msg);
		res.setSender(player);
		res.setTime(System.currentTimeMillis());
		sendMessageToUnionOnlineMember(union, res);
	}
	
	private static void sendMessageToUnionOnlineMember(MGUnion union, ActionEventBase res) {
		List<Player> playerList = getUnionOnlinePlayers(union);
		PlayerChatFacade.sendMessageToPlayers(playerList, res);
	}
	
	private static void sendExitUnionGameEvent(MGUnion union, Player player) {
		ExitUnion_GE exitUnion_GE = new ExitUnion_GE(union);
		String exitUnion_GE_Id = ExitUnion_GE.class.getSimpleName();
		GameEvent<ExitUnion_GE> event = (GameEvent<ExitUnion_GE>) GameEvent.getInstance(exitUnion_GE_Id, exitUnion_GE);
		player.handleGameEvent(event);
		GameEvent.pool(event);
	}
	
	/**
	 * 公会聊天消息
	 * 
	 * @param optType
	 * @param player
	 * @return
	 */
	private static String getChatMessage(byte optType, Player player) {
		Preconditions.checkArgument(player != null);

		String playerId = player.getId();
		String playerName = player.getName();
		byte gender = MGPropertyAccesser.getGender(player.getProperty());

		String chatMessage = null;
		if (optType == UnionMacro.Add_Member) {
			chatMessage = String.format("玩家{p=%1$s<%2$s><%3$s>}加入公会", playerName, playerId, gender);
		} else if (optType == UnionMacro.Remove_Member) {
			chatMessage = String.format("玩家{p=%1$s<%2$s><%3$s>}退出公会", playerName, playerId, gender);
		} else if (optType == UnionMacro.Upgrade_Member) {
			byte unionOfficialId = MGPropertyAccesser.getUnionOfficialId(player.getProperty());
			String officialName = MGUnionMember.nameMap.get(unionOfficialId);
			chatMessage = String.format("玩家{p=%1$s<%2$s><%3$s>}被任命为[%4$s]", playerName, playerId, gender, officialName);
		} else if (optType == UnionMacro.Reply_NoReply) {
			chatMessage = String.format("玩家{p=%1$s<%2$s><%3$s>}放弃选择", playerName, playerId, gender);
		} else if (optType == UnionMacro.Reply_Decline) {
			chatMessage = String.format("玩家{p=%1$s<%2$s><%3$s>}拒绝了你的邀请", playerName, playerId, gender);
		} else if (optType == UnionMacro.BeKickedOut) {
			chatMessage = String.format("玩家{p=%1$s<%2$s><%3$s>}被踢出公会", playerName, playerId, gender);
		} 

		return chatMessage;
	}

	/**
	 * 公会邮件
	 * 
	 * @return
	 */
	private static String getMailMessage(byte optType, MGUnion union) {
		Preconditions.checkArgument(union != null);

		String unionName = union.getName();
		String mailMessage = null;

		if (optType == UnionMacro.Dissolve) {
			mailMessage = String.format("成功解散公会[%1$s]", unionName);
		} else if (optType == UnionMacro.Remove_Member) {
			mailMessage = String.format("你成功退出了公会[%1$s]", unionName);
		} else if (optType == UnionMacro.Add_Member) {
			mailMessage = String.format("你已成功加入[%1$s]公会", unionName);
		} else if (optType == UnionMacro.BeKickedOut) {
			mailMessage = String.format("你被踢出公会[%1$s]", unionName);
		} else if (optType == UnionMacro.BeDissolved) {
			mailMessage = String.format("[%1$s]公会已经解散", unionName);
		} else if (optType == UnionMacro.Decline) {
			mailMessage = String.format("你对[%1$s]公会的申请被拒绝", unionName);
		}

		return mailMessage;

	}
	
	public static void sendUnionOperateEvent(Player player, MGUnion union) {
		MGUnionMemberMgr memberMgr = union.getMemberMgr();
		MGUnionMember memberByPlayer = memberMgr.getMemberByPlayer(player);
		if (isUnionCreater(player)) {
			sendUnionGameEvent(UnionOperateType.CreatUnion, player, true);
		} else {
			sendUnionGameEvent(UnionOperateType.AddUnion, player, isCharimanOrViceChairman(memberByPlayer));
		}

		List<MGUnionMember> members = memberMgr.getMemberList();
		if (memberMgr.isMemberFull()) { // 公会满员，会长和副会长额外添加成就类型
			for (MGUnionMember member : members) {
				boolean isOfficer = isCharimanOrViceChairman(member);
				Player playerByMember = getPlayerByMember(member);
				sendUnionGameEvent(UnionOperateType.FullUnion, playerByMember, isOfficer);
			}
		}
	}
	
	private static void sendUnionGameEvent(byte operateType, Player player, boolean isOfficer) {
		UnionOperateGE unionOperateGe = new UnionOperateGE(operateType, player, isOfficer);
		GameEvent<UnionOperateGE> event = GameEvent.getInstance(UnionOperateGE.class.getSimpleName(), unionOperateGe);
		player.handleGameEvent(event);
		GameEvent.pool(event);

	}
	
	private static boolean isCharimanOrViceChairman(MGUnionMember memberByPlayer) {
		return memberByPlayer.isChairman() || memberByPlayer.isViceChairman();
	}
	
}
