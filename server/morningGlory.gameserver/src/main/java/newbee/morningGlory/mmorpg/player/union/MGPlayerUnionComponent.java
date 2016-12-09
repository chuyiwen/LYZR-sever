package newbee.morningGlory.mmorpg.player.union;

import newbee.morningGlory.MorningGloryContext;
import newbee.morningGlory.code.MGErrorCode;
import newbee.morningGlory.code.MGSuccessCode;
import newbee.morningGlory.mmorpg.player.union.actionEvent.C2G_Union_ApplyJoinUnion;
import newbee.morningGlory.mmorpg.player.union.actionEvent.C2G_Union_ApplyList;
import newbee.morningGlory.mmorpg.player.union.actionEvent.C2G_Union_AutoAgree;
import newbee.morningGlory.mmorpg.player.union.actionEvent.C2G_Union_CancelJoin;
import newbee.morningGlory.mmorpg.player.union.actionEvent.C2G_Union_Chat;
import newbee.morningGlory.mmorpg.player.union.actionEvent.C2G_Union_CreateUnion;
import newbee.morningGlory.mmorpg.player.union.actionEvent.C2G_Union_EditNotice;
import newbee.morningGlory.mmorpg.player.union.actionEvent.C2G_Union_Exit;
import newbee.morningGlory.mmorpg.player.union.actionEvent.C2G_Union_HandleApply;
import newbee.morningGlory.mmorpg.player.union.actionEvent.C2G_Union_Invite;
import newbee.morningGlory.mmorpg.player.union.actionEvent.C2G_Union_KickOutMember;
import newbee.morningGlory.mmorpg.player.union.actionEvent.C2G_Union_ReplyInvite;
import newbee.morningGlory.mmorpg.player.union.actionEvent.C2G_Union_UnionList;
import newbee.morningGlory.mmorpg.player.union.actionEvent.C2G_Union_UpgradeOffice;
import newbee.morningGlory.mmorpg.player.union.actionEvent.G2C_Union_ApplyJoinUnion;
import newbee.morningGlory.mmorpg.player.union.actionEvent.G2C_Union_ApplyList;
import newbee.morningGlory.mmorpg.player.union.actionEvent.G2C_Union_CancelJoin;
import newbee.morningGlory.mmorpg.player.union.actionEvent.G2C_Union_CreateUnion;
import newbee.morningGlory.mmorpg.player.union.actionEvent.G2C_Union_EditNotice;
import newbee.morningGlory.mmorpg.player.union.actionEvent.G2C_Union_Exit;
import newbee.morningGlory.mmorpg.player.union.actionEvent.G2C_Union_HandleApply;
import newbee.morningGlory.mmorpg.player.union.actionEvent.G2C_Union_Invite;
import newbee.morningGlory.mmorpg.player.union.actionEvent.G2C_Union_KickOutMember;
import newbee.morningGlory.mmorpg.player.union.actionEvent.G2C_Union_ReplyInvite;
import newbee.morningGlory.mmorpg.player.union.actionEvent.G2C_Union_UnionList;
import newbee.morningGlory.mmorpg.player.union.actionEvent.G2C_Union_UpgradeOffice;
import newbee.morningGlory.mmorpg.player.union.actionEvent.MGUnionEventDefines;
import newbee.morningGlory.mmorpg.sceneActivities.castleWar.CastleWarApplyMgr;
import newbee.morningGlory.mmorpg.union.MGUnion;
import newbee.morningGlory.mmorpg.union.MGUnionConstant;
import newbee.morningGlory.mmorpg.union.MGUnionHelper;
import newbee.morningGlory.mmorpg.union.MGUnionMember;
import newbee.morningGlory.mmorpg.union.MGUnionMgr;
import newbee.morningGlory.mmorpg.union.MGUnionSaver;
import newbee.morningGlory.mmorpg.vip.gameEvent.VipGE;
import newbee.morningGlory.stat.MGStatFunctions;
import newbee.morningGlory.stat.logs.StatUnion;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.authentication.Identity;
import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.code.CodeContext;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.player.chat.ChannelType;
import sophia.mmorpg.player.chat.PlayerChatComponent;
import sophia.mmorpg.player.chat.PlayerChatFacade;
import sophia.mmorpg.player.chat.event.ChatEventDefines;
import sophia.mmorpg.player.chat.event.G2C_Chat_System;
import sophia.mmorpg.player.gameEvent.EnterWorld_SceneReady_GE;
import sophia.mmorpg.player.gameEvent.LeaveWorld_GE;
import sophia.mmorpg.utils.StringValidChecker;

import com.google.common.base.Strings;

public class MGPlayerUnionComponent extends ConcreteComponent<Player> {
	private static Logger logger = Logger.getLogger(MGPlayerUnionComponent.class);

	private MGUnionMgr unionMgr = MorningGloryContext.getUnionSystemComponent().getUnionMgr();

	private PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();

	public static final String LeaveWorld_GE_ID = LeaveWorld_GE.class.getSimpleName();

	public static final String EnterWorld_SceneReady_ID = EnterWorld_SceneReady_GE.class.getSimpleName();

	public static final String VipGE_ID = VipGE.class.getSimpleName();
	
	public static final String Tag = "MGPlayerUnionComponent";

	private MGUnionSaver unionSaver = MGUnionSaver.getInstance();
	
	private final MGPlayerUnionMgr playerUnionMgr = new MGPlayerUnionMgr();
	
	public MGPlayerUnionMgr getPlayerUnionMgr() {
		return playerUnionMgr;
	}

	@Override
	public void ready() {
		addActionEventListener(MGUnionEventDefines.C2G_Union_UnionList);
		addActionEventListener(MGUnionEventDefines.C2G_Union_CreateUnion);
		addActionEventListener(MGUnionEventDefines.C2G_Union_JoinUnion);
		addActionEventListener(MGUnionEventDefines.C2G_Union_CancelJoin);
		addActionEventListener(MGUnionEventDefines.C2G_Union_Exit);
		addActionEventListener(MGUnionEventDefines.C2G_Union_HandleApply);
		addActionEventListener(MGUnionEventDefines.C2G_Union_ApplyList);
		addActionEventListener(MGUnionEventDefines.C2G_Union_KickOutMember);
		addActionEventListener(MGUnionEventDefines.C2G_Union_UpgradeOffice);
		addActionEventListener(MGUnionEventDefines.C2G_Union_EditNotice);
		addActionEventListener(MGUnionEventDefines.C2G_Union_AutoAgree);
		addActionEventListener(MGUnionEventDefines.C2G_Union_Invite);
		addActionEventListener(MGUnionEventDefines.C2G_Union_ReplyInvite);
		addActionEventListener(MGUnionEventDefines.C2G_Union_Chat);

		addInterGameEventListener(LeaveWorld_GE_ID);
		addInterGameEventListener(EnterWorld_SceneReady_ID);
		addInterGameEventListener(VipGE_ID);
	}

	@Override
	public void suspend() {
		removeActionEventListener(MGUnionEventDefines.C2G_Union_UnionList);
		removeActionEventListener(MGUnionEventDefines.C2G_Union_CreateUnion);
		removeActionEventListener(MGUnionEventDefines.C2G_Union_JoinUnion);
		removeActionEventListener(MGUnionEventDefines.C2G_Union_HandleApply);
		removeActionEventListener(MGUnionEventDefines.C2G_Union_Exit);
		removeActionEventListener(MGUnionEventDefines.C2G_Union_CancelJoin);
		removeActionEventListener(MGUnionEventDefines.C2G_Union_ApplyList);
		removeActionEventListener(MGUnionEventDefines.C2G_Union_KickOutMember);
		removeActionEventListener(MGUnionEventDefines.C2G_Union_UpgradeOffice);
		removeActionEventListener(MGUnionEventDefines.C2G_Union_EditNotice);
		removeActionEventListener(MGUnionEventDefines.C2G_Union_AutoAgree);
		removeActionEventListener(MGUnionEventDefines.C2G_Union_Invite);
		removeActionEventListener(MGUnionEventDefines.C2G_Union_ReplyInvite);
		removeActionEventListener(MGUnionEventDefines.C2G_Union_Chat);

		removeInterGameEventListener(LeaveWorld_GE_ID);
		removeInterGameEventListener(EnterWorld_SceneReady_ID);
		removeInterGameEventListener(VipGE_ID);
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {
		if (event.isId(LeaveWorld_GE_ID)) {
			Player player = getConcreteParent();
			getPlayerUnionMgr().getPlayerUnionInvitedMgr().clearInviteList();
			MGUnion union = MGUnionHelper.getUnion(player);
			if (union == null) {
				return;
			}
			
			MGUnionMember memberByPlayer = union.getMemberMgr().getMemberByPlayer(player);
			if (memberByPlayer == null) {
				return;
			}
			
			memberByPlayer.setFightValue(player.getFightPower());
			memberByPlayer.setLevel(player.getExpComponent().getLevel());
			memberByPlayer.setOnline(MGUnionConstant.NotOnline);
			memberByPlayer.setLastLogoutTime(System.currentTimeMillis());
			unionSaver.saveImmediateData(union);
		} else if (event.isId(VipGE_ID)) {
			Player player = getConcreteParent();
			MGUnion union = MGUnionHelper.getUnion(player);
			if (union != null) {
				MGUnionMember member = union.getMemberMgr().getMemberByPlayer(player);
				if (member != null) {
					VipGE ge = (VipGE) event.getData();
					byte vipType = ge.getVipType();
					member.setVipType(vipType);
				}
			}
		}
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
		short eventId = event.getActionEventId();
		if (logger.isDebugEnabled()) {
			logger.debug("公会事件Id:" + eventId);
		}

		switch (eventId) {
		case MGUnionEventDefines.C2G_Union_UnionList:
			handle_Union_UnionList((C2G_Union_UnionList) event);
			break;
		case MGUnionEventDefines.C2G_Union_CreateUnion:
			handle_Union_CreateUnion((C2G_Union_CreateUnion) event);
			break;
		case MGUnionEventDefines.C2G_Union_JoinUnion:
			handle_Union_ApplyJoinUnion((C2G_Union_ApplyJoinUnion) event);
			break;
		case MGUnionEventDefines.C2G_Union_HandleApply:
			handle_Union_HandleApply((C2G_Union_HandleApply) event);
			break;
		case MGUnionEventDefines.C2G_Union_Exit:
			handle_Union_Exit((C2G_Union_Exit) event);
			break;
		case MGUnionEventDefines.C2G_Union_CancelJoin:
			handle_Union_CancelJoin((C2G_Union_CancelJoin) event);
			break;
		case MGUnionEventDefines.C2G_Union_ApplyList:
			handle_Union_SelectApplyList((C2G_Union_ApplyList) event);
			break;
		case MGUnionEventDefines.C2G_Union_KickOutMember:
			handle_Union_KickOutMember((C2G_Union_KickOutMember) event);
			break;
		case MGUnionEventDefines.C2G_Union_UpgradeOffice:
			handle_Union_UpgradeOffice((C2G_Union_UpgradeOffice) event);
			break;
		case MGUnionEventDefines.C2G_Union_EditNotice:
			handle_Union_EditNotice((C2G_Union_EditNotice) event);
			break;
		case MGUnionEventDefines.C2G_Union_AutoAgree:
			handle_Union_SetAutoAgree((C2G_Union_AutoAgree) event);
			break;
		case MGUnionEventDefines.C2G_Union_Invite:
			handle_Union_Invite((C2G_Union_Invite) event);
			break;
		case MGUnionEventDefines.C2G_Union_ReplyInvite:
			handle_Union_ReplyInvite((C2G_Union_ReplyInvite) event);
			break;
		case MGUnionEventDefines.C2G_Union_Chat:
			handle_Union_Chat((C2G_Union_Chat) event);
			break;
		default:
			break;

		}
	}

	private void handle_Union_UnionList(C2G_Union_UnionList event) {
		byte kind = event.getKind();
		byte segment = event.getSegment();

		Player player = getConcreteParent();
		if (kind != UnionMacro.Type_Uncertain_UnionList && kind != UnionMacro.Type_All_UnionList) {
			logger.error("handle_Union_UnionList error, invalid kind=" + kind + ", player=" + player);
			return;
		}

		if (segment <= 0) {
			logger.error("handle_Union_UnionList error, invalid segment=" + segment + ", player=" + player);
			return;
		}

		G2C_Union_UnionList res = MessageFactory.getConcreteMessage(MGUnionEventDefines.G2C_Union_UnionList);
		MGUnion union = MGUnionHelper.getUnion(player);
		if (union != null && kind == UnionMacro.Type_Uncertain_UnionList) {
			res.setUnionListType(UnionMacro.Type_SelfUnionList);
			res.setByteArrayReadWriteBuffer(union.writeSelfUnionList(segment));
		} else {
			res.setUnionListType(UnionMacro.Type_AllUnionList);
			res.setByteArrayReadWriteBuffer(unionMgr.writeAllUnionList(segment, player));
		}

		GameRoot.sendMessage(event.getIdentity(), res);
	}

	private void handle_Union_CreateUnion(C2G_Union_CreateUnion event) {
		Identity identity = event.getIdentity();
		short actionEventId = event.getActionEventId();
		
		Player player = getConcreteParent();
		String unionName = MGUnionHelper.getUnionName(player);
		if (!StringUtils.isEmpty(unionName)) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_UNION_AlreadyInOneUnion);
			logger.error("handle_Union_CreateUnion error, invalid union name=" + unionName + ", player=" + player);
			return;
		}

		unionName = event.getUnionName();
		if (Strings.isNullOrEmpty(unionName)) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_UNION_UnionNameValid);
			logger.error("handle_Union_CreateUnion error, unionName=" + unionName + ", player=" + player);
			return;
		}
		
		int playerLevel = player.getExpComponent().getLevel();
		if (playerLevel < UnionMacro.Default_CreateUnion_Level) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_UNION_CrtPlayerLevelToLow);
			logger.error("handle_Union_CreateUnion error, player level=" + playerLevel + ", player=" + player);
			return;
		}
		
		int len = MGUnionHelper.calculateStringLength(unionName);
		if (len > UnionMacro.Default_CreateUnion_Name_MaxLen) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_UNION_UnionNameTooLong);
			logger.error("handle_Union_CreateUnion error, union name too long=" + unionName + ", player=" + player);
			return;
		}
		if (len < UnionMacro.Default_CreateUnion_Name_MinLen) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_UNION_UnionNameTooShort);
			logger.error("handle_Union_CreateUnion error, union name too short=" + unionName + ", player=" + player);
			return;
		}
		
		if (!StringValidChecker.isValid(unionName)) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_UNION_UnionNameValid);
			logger.error("handle_Union_CreateUnion error, invalid union name=" + unionName + ", player=" + player);
			return;
		}
		
		MGUnionMgr unionMgr = MGUnionMgr.getInstance();
		int code = unionMgr.createUnion(player, unionName);
		if (code != MGSuccessCode.CODE_SUCCESS) {
			ResultEvent.sendResult(identity, actionEventId, code);
			logger.error("handle_Union_CreateUnion error, " + CodeContext.description(code));
			return;
		}
		
		MGUnion union = unionMgr.getUnion(unionName);
		MGUnionHelper.sendUnionOperateEvent(player, union);
		MGStatFunctions.unionStat(player, StatUnion.create, unionName, union.getId(), MGUnionConstant.Chairman);
		MGUnionHelper.clearApplyUnion(player);
		MGUnionSaver.getInstance().insertData(union);
		MMORPGContext.getPlayerComponent().getSaveService().saveImmediateData(player);
		
		G2C_Union_CreateUnion res = MessageFactory.getConcreteMessage(MGUnionEventDefines.G2C_Union_CreateUnion);
		GameRoot.sendMessage(identity, res);
	}

	private void handle_Union_ApplyJoinUnion(C2G_Union_ApplyJoinUnion event) {
		Identity identity = event.getIdentity();
		short actionEventId = event.getActionEventId();
		String unionName = event.getUnionName();
		if (Strings.isNullOrEmpty(unionName)) {
			logger.error("handle_Union_ApplyJoinUnion error, unionName=" + unionName);
			return;
		}

		Player player = getConcreteParent();
		int level = player.getExpComponent().getLevel();
		if (level < 10) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_UNION_CrtPlayerLevelToLow);
			logger.error("handle_Union_ApplyJoinUnion error, level is too low=" + level + ", player=" + player);
			return;
		}
		
		MGUnion applyUnion = MGUnionHelper.getApplyUnion(player);
		if (applyUnion != null) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_UNION_AlreadyApplyOneUnion);
			logger.error("handle_Union_ApplyJoinUnion error, already applyed, player=" + player);
			return;
		}
		
		byte UIrefreshType = UnionMacro.Type_UI_Refresh;
		MGUnionMgr unionMgr = MGUnionMgr.getInstance();
		MGUnion union = unionMgr.getUnion(unionName); 
		if (union == null) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_UNION_UnionIsNotExist);
			logger.error("handle_Union_ApplyJoinUnion error, union not exist unionName=" + unionName + ", player=" + player);
			return;
		}
		
		int code = unionMgr.applyUnion(player, unionName);
		if (code != MGSuccessCode.CODE_SUCCESS) {
			ResultEvent.sendResult(identity, actionEventId, code);
			logger.error("handle_Union_ApplyJoinUnion error, " + CodeContext.description(code) + ", player=" + player);
			UIrefreshType = UnionMacro.Type_UI_Keep;
		}

		G2C_Union_ApplyJoinUnion res = MessageFactory.getConcreteMessage(MGUnionEventDefines.G2C_Union_JoinUnion);
		res.setUIrefreshType(UIrefreshType);
		GameRoot.sendMessage(identity, res);
		
		if (code == MGSuccessCode.CODE_SUCCESS) {
			MGUnionSaver.getInstance().saveImmediateData(union);
			if (union.getAutoState() == UnionMacro.Auto_Agree) {
				MGUnionHelper.joinUnionAndSave(player, union);
			}
		}
	}

	private void handle_Union_CancelJoin(C2G_Union_CancelJoin event) {
		Identity identity = event.getIdentity();
		short actionEventId = event.getActionEventId();
		Player player = getConcreteParent();
		
		String unionName = event.getUnionName();
		if (Strings.isNullOrEmpty(unionName)) {
			logger.error("handle_Union_CancelJoin error, invalid unionName=" + unionName + ", player=" + player);
			return;
		}

		MGUnionMgr unionMgr = MGUnionMgr.getInstance();
		MGUnion union = unionMgr.getUnion(unionName);
		if (union == null) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_UNION_UnionIsNotExist);
			logger.error("handle_Union_CancelJoin error, union not exist, unionName=" + unionName + ", player=" + player);
			return;
		}
		
		if (!union.getUnionApplyMgr().removeApply(player.getId())) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_UNION_ApplyAlreadyQuit);
			logger.error("handle_Union_CancelJoin error, union not exist this apply, unionName=" + unionName + ", player=" + player);
			return;
		}
		
		G2C_Union_CancelJoin res = MessageFactory.getConcreteMessage(MGUnionEventDefines.G2C_Union_CancelJoin);
		GameRoot.sendMessage(identity, res);
		
		MGUnionSaver.getInstance().saveImmediateData(union);
	}

	private void handle_Union_Exit(C2G_Union_Exit event) {
		Identity identity = event.getIdentity();
		short actionEventId = event.getActionEventId();
		Player player = getConcreteParent();
		
		String unionName = event.getUnionName();
		if (Strings.isNullOrEmpty(unionName)) {
			logger.error("handle_Union_Exit error, invalid unionName=" + unionName + ", player=" + player);
			return;
		}
		
		MGUnion union = MGUnionHelper.getUnion(player);
		if (union == null) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_UNION_UnionIsNotExist);
			logger.error("handle_Union_Exit error, union not exist, unionName=" + unionName + ", player=" + player);
			return;
		}
		
		MGUnionMgr unionMgr = MGUnionMgr.getInstance();
		if (MGUnionHelper.isUnionCreater(player)) {
			int code = unionMgr.closeUnion(player, union.getName());
			if (code != MGSuccessCode.CODE_SUCCESS) {
				ResultEvent.sendResult(identity, actionEventId, code);
				logger.error("handle_Union_Exit error, " + CodeContext.description(code) + ", player=" + player);
				return;
			}
			
			union.closeUnion();
			unionMgr.removeUnion(union.getName());
			MGUnionSaver.getInstance().deleteData(union);
			if (CastleWarApplyMgr.getInstance().isAlreadySignupWar(union.getName())) {
				CastleWarApplyMgr.getInstance().exitWar(union.getName());
			}
		} else {
			int code = MGUnionHelper.quitUnionAndSave(player, union, UnionMacro.Remove_Member);
			if (code != MGSuccessCode.CODE_SUCCESS) {
				ResultEvent.sendResult(identity, actionEventId, code);
				logger.error("handle_Union_Exit error, " + CodeContext.description(code) + ", player=" + player);
				return;
			}
			
			union.getMemberMgr().sortMember();
			MGUnionSaver.getInstance().saveImmediateData(union);
		}
		
		G2C_Union_Exit res = MessageFactory.getConcreteMessage(MGUnionEventDefines.G2C_Union_Exit);
		GameRoot.sendMessage(identity, res);
	}

	private void handle_Union_HandleApply(C2G_Union_HandleApply event) {
		Identity identity = event.getIdentity();
		short actionEventId = event.getActionEventId();
		String applyPlayerId = event.getApplyPlayerId();
		String unionName = event.getUnionName();
		byte vote = event.getVote();
		Player player = getConcreteParent();

		if (Strings.isNullOrEmpty(applyPlayerId)) {
			logger.error("handle_Union_HandleApply error, invalid argument! applyPlayerId=" + applyPlayerId + ", player=" + player);
			return;
		}

		if (Strings.isNullOrEmpty(unionName)) {
			logger.error("handle_Union_HandleApply error, invalid argument! unionName=" + unionName + ", player=" + player);
			return;
		}
		
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		Player target = playerManager.getPlayer(applyPlayerId);
		if (target == null) {
			logger.error("handle_Union_HandleApply error, can't find target=" + applyPlayerId + ", unionName=" + unionName + ", player=" + player);
			return;
		}
		
		if (MGUnionHelper.getUnion(target) != null) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_UNION_AlreadyInOneUnion);
			logger.error("handle_Union_HandleApply error, union not null, unionName=" + unionName + ", player=" + player);
			return;
		}
		
		MGUnionMgr unionMgr = MGUnionMgr.getInstance();
		MGUnion union = unionMgr.getUnion(unionName);
		if (union == null) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_UNION_UnionIsNotExist);
			logger.error("handle_Union_HandleApply error, union not exist, unionName=" + unionName + ", player=" + player);
			return;
		}
		
		if (!MGUnionHelper.isUnionCreater(player)) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_UNION_HasNOAuthority);
			logger.error("handle_Union_HandleApply error, has no authority, unionName=" + unionName + ", player=" + player);
			return;
		}
		
		MGUnion union2 = MGUnionHelper.getUnion(player);
		if (union != union2) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_UNION_HasNOAuthority);
			logger.error("handle_Union_HandleApply error, not the same union, unionName=" + unionName + ", player=" + player);
			return;
		}
		
		if (vote == UnionMacro.Agree) {
			boolean containsApplyId = union.getUnionApplyMgr().containsApplyId(target.getId());
			int code = 0;
			if (containsApplyId) {
				code = MGUnionHelper.joinUnionAndSave(target, union);
			} else {
				code = MGErrorCode.CODE_UNION_ApplyAlreadyQuit;
			}
			
			if (code != MGSuccessCode.CODE_SUCCESS) {
				ResultEvent.sendResult(identity, actionEventId, code);
				logger.error("handle_Union_HandleApply error, " + CodeContext.description(code) + ", player=" + player);
				return;
			}
		} else {
			union.getUnionApplyMgr().removeApply(applyPlayerId);
			MGUnionHelper.sendUnionMail(target, union, UnionMacro.Decline);
			MGUnionSaver.getInstance().saveImmediateData(union);
		}

		G2C_Union_HandleApply res = MessageFactory.getConcreteMessage(MGUnionEventDefines.G2C_Union_HandleApply);
		GameRoot.sendMessage(identity, res);
	}

	private void handle_Union_SelectApplyList(C2G_Union_ApplyList event) {
		short actionEventId = event.getActionEventId();
		Identity identity = event.getIdentity();
		
		Player player = getConcreteParent();
		MGUnion union = MGUnionHelper.getUnion(player);
		if (union == null) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_UNION_UnionIsNotExist);
			logger.error("handle_Union_HandleApply error, union not exist, player=" + player);
			return;
		}
		
		if (!MGUnionHelper.isUnionCreater(player)) {
			ResultEvent.sendResult(identity, event.getActionEventId(), MGErrorCode.CODE_UNION_HasNOAuthority);
			logger.error("handle_Union_HandleApply error, has no authority, unionName=" + union.getName() + ", player=" + player);
			return;
		}

		G2C_Union_ApplyList res = MessageFactory.getConcreteMessage(MGUnionEventDefines.G2C_Union_ApplyList);
		res.setUnion(union);
		GameRoot.sendMessage(identity, res);
	}

	private void handle_Union_KickOutMember(C2G_Union_KickOutMember event) {
		Identity identity = event.getIdentity();
		short actionEventId = event.getActionEventId();
		String beKickedPlayerId = event.getKickedPlayerId();
		String unionName = event.getUnionName();
		
		Player player = getConcreteParent();
		if (Strings.isNullOrEmpty(beKickedPlayerId)) {
			logger.error("handle_Union_KickOutMember error, invalid argument! beKickedPlayerId=" + beKickedPlayerId + ", player=" + player);
			return;
		}

		if (Strings.isNullOrEmpty(unionName)) {
			logger.error("handle_Union_KickOutMember error, invalid argument! unionName=" + unionName + ", player=" + player);
			return;
		}
		
		if (StringUtils.equals(beKickedPlayerId, player.getId())) {
			logger.error("handle_Union_KickOutMember error, can't kick slef, player=" + player);
			return;
		}
		
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		Player target = playerManager.getPlayer(beKickedPlayerId);
		if (target == null) {
			logger.error("handle_Union_KickOutMember error, can't find target=" + beKickedPlayerId + ", unionName=" + unionName + ", player=" + player);
			return;
		}
		
		MGUnion union = MGUnionHelper.getUnion(player);
		if (union == null) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_UNION_UnionIsNotExist);
			logger.error("handle_Union_HandleApply error, union not exist, unionName=" + unionName + ", player=" + player);
			return;
		}
		
		if (!MGUnionHelper.isUnionCreater(player)) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_UNION_HasNOAuthority);
			logger.error("handle_Union_KickOutMember error, has no authority, unionName=" + unionName + ", player=" + player);
			return;
		}
		
		int code = MGUnionHelper.quitUnionAndSave(target, union, UnionMacro.BeKickedOut);
		if (code != MGSuccessCode.CODE_SUCCESS) {
			ResultEvent.sendResult(identity, actionEventId, code);
			logger.error("handle_Union_KickOutMember error, " + CodeContext.description(code) + ", target=" + target + ", player=" + player);
			return;
		}

		union.getMemberMgr().sortMember();
		MGUnionSaver.getInstance().saveImmediateData(union);
		G2C_Union_KickOutMember res = MessageFactory.getConcreteMessage(MGUnionEventDefines.G2C_Union_KickOutMember);
		GameRoot.sendMessage(identity, res);
	}

	private void handle_Union_UpgradeOffice(C2G_Union_UpgradeOffice event) {
		Identity identity = event.getIdentity();
		short actionEventId = event.getActionEventId();
		String beUpgradePlayerId = event.getUpgradePlayerId();
		String unionName = event.getUnionName();
		byte officialId = event.getOfficialId();
		
		Player player = getConcreteParent();
		if (officialId < MGUnionConstant.Chairman || officialId > MGUnionConstant.Common) {
			logger.error("handle_Union_UpgradeOffice error, invalid argument, officialId=" + officialId + ", unionName=" + unionName + ", player=" + player);
			return;
		}
		
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		Player target = playerManager.getPlayer(beUpgradePlayerId);
		if (target == null) {
			logger.error("handle_Union_UpgradeOffice error, can't find target=" + beUpgradePlayerId + ", unionName=" + unionName + ", player=" + player);
			return;
		}
		
		MGUnion union = MGUnionHelper.getUnion(player);
		if (union == null) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_UNION_UnionIsNotExist);
			logger.error("handle_Union_UpgradeOffice error, union not exist, unionName=" + unionName + ", player=" + player);
			return;
		}
		
		if (!MGUnionHelper.isUnionCreater(player)) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_UNION_HasNOAuthority);
			logger.error("handle_Union_UpgradeOffice error, has no authority, unionName=" + unionName + ", player=" + player);
			return;
		}
		
		if (officialId == MGUnionConstant.Vice_Chairman) {
			if (union.getMemberMgr().getViceChairmanCount() >= UnionMacro.Default_ViceChairman_Count) {
				ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_UNION_ViceChairmanCountISFull);
				logger.error("handle_Union_UpgradeOffice error, vice chairman enough, unionName=" + unionName + ", player=" + player);
				return;
			}
		}
		
		if (!union.changeOfficialId(target, officialId)) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_UNION_ApplyAlreadyQuit);
			logger.error("handle_Union_UpgradeOffice error, already exit union, target=" + target + ", unionName=" + unionName + ", player=" + player);
			return;
		}
		
		if (officialId == MGUnionConstant.Chairman) {// 转让会长
			union.changeOfficialId(player, MGUnionConstant.Common);
			MMORPGContext.getPlayerComponent().getSaveService().saveImmediateData(player);
		}
		
		union.getMemberMgr().sortMember();

		// FIXME, 提升的目标？？？
		MGStatFunctions.unionStat(target, StatUnion.upgrade, union.getName(), union.getId(), officialId);
		
		MGUnionHelper.sendUnionUpdateMessage(union, UnionMacro.Upgrade_Member);
		
		MGUnionHelper.sendUnionSystemChatMessage(target, union, UnionMacro.Upgrade_Member);
		
		MMORPGContext.getPlayerComponent().getSaveService().saveImmediateData(target);
		
		MGUnionSaver.getInstance().saveImmediateData(union);

		G2C_Union_UpgradeOffice res = MessageFactory.getConcreteMessage(MGUnionEventDefines.G2C_Union_UpgradeOffice);
		GameRoot.sendMessage(identity, res);
	}

	private void handle_Union_EditNotice(C2G_Union_EditNotice event) {
		short actionEventId = event.getActionEventId();
		Identity identity = event.getIdentity();
		String unionName = event.getUnionName();
		String message = event.getMessage();

		Player player = getConcreteParent();
		if (MGUnionHelper.calculateStringLength(message) > 90) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_UNION_MessageTooLong);
			logger.error("handle_Union_EditNotice error, message too long, unionName=" + unionName + ", player=" + player);
			return;
		}
		
		MGUnion union = MGUnionHelper.getUnion(player);
		if (union == null) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_UNION_UnionIsNotExist);
			logger.error("handle_Union_EditNotice error, union not exist, unionName=" + unionName + ", player=" + player);
			return;
		}
		
		if (!MGUnionHelper.isUnionCreater(player)) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_UNION_HasNOAuthority);
			logger.error("handle_Union_EditNotice error, has no authority, unionName=" + unionName + ", player=" + player);
			return;
		}

		union.setMessage(message);
		MGUnionSaver.getInstance().saveImmediateData(union);

		G2C_Union_EditNotice res = MessageFactory.getConcreteMessage(MGUnionEventDefines.G2C_Union_EditNotice);
		GameRoot.sendMessage(identity, res);
	}

	private void handle_Union_SetAutoAgree(C2G_Union_AutoAgree event) {
		short actionEventId = event.getActionEventId();
		Identity identity = event.getIdentity();
		byte autoState = event.getAutoState();
		String unionName = event.getUnionName();

		Player player = getConcreteParent();
		if (autoState != 0 && autoState != 1) {
			logger.error("handle_Union_SetAutoAgree error, invalid argument, autoState=" + autoState + ", unionName=" + unionName + ", player=" + player);
			return;
		}
		
		MGUnion union = MGUnionHelper.getUnion(player);
		if (union == null) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_UNION_UnionIsNotExist);
			logger.error("handle_Union_SetAutoAgree error, union not exist, unionName=" + unionName + ", player=" + player);
			return;
		}
		
		if (!MGUnionHelper.isUnionCreater(player)) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_UNION_HasNOAuthority);
			logger.error("handle_Union_SetAutoAgree error, has no authority, unionName=" + unionName + ", player=" + player);
			return;
		}
		
		union.setAutoState(autoState);
		MGUnionSaver.getInstance().saveImmediateData(union);
	}

	private void handle_Union_Invite(C2G_Union_Invite event) {
		Identity identity = event.getIdentity();
		short actionEventId = event.getActionEventId();
		String unionName = event.getUnionName();
		String beInvitedPlayerId = event.getInvitedPlayerId();
		Player player = getConcreteParent();

		// 只能邀请在线玩家
		Player target = playerManager.getOnlinePlayer(beInvitedPlayerId);
		if (target == null) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_UNION_CrtPlayerNotOnLine);
			logger.error("handle_Union_Invite error, beInvitedPlayer not online, unionName=" + unionName + ", player=" + player);
			return;
		}

		int level = target.getExpComponent().getLevel();
		if (level < 10) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_UNION_CrtPlayerLevelToLow);
			logger.error("handle_Union_Invite error, beInvitedPlayer level too low, level=" + level + ", unionName=" + unionName + ", player=" + player);
			return;
		}
		
		MGUnion union = MGUnionHelper.getUnion(player);
		if (union == null) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_UNION_UnionIsNotExist);
			logger.error("handle_Union_Invite error, union not exist, unionName=" + unionName + ", player=" + player);
			return;
		}
		
		MGUnionMember memberByPlayer = union.getMemberMgr().getMemberByPlayer(player);
		if (!memberByPlayer.isChairman() && !memberByPlayer.isViceChairman()) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_UNION_HasNOAuthority);
			logger.error("handle_Union_Invite error,  has no authority, unionName=" + unionName + ", player=" + player);
			return;
		}

		MGUnionMgr unionMgr = MGUnionMgr.getInstance();
		int code = unionMgr.inviteUnion(target, union.getName());
		if (code == MGErrorCode.CODE_UNION_InviteNumberUpperLimit) {
			String chatContent = String.format("玩家[%1$s]正忙", target.getName());
			G2C_Chat_System res = (G2C_Chat_System) MessageFactory.getMessage(ChatEventDefines.G2C_Chat_System);
			res.setMsg(chatContent);
			res.setType((byte) 1);
			PlayerChatFacade.sendMessageToPlayer(player, res);
			return;
		}

		if (code != MGSuccessCode.CODE_SUCCESS) {
			ResultEvent.sendResult(identity, actionEventId, code);
			logger.error("handle_Union_Invite error, " + CodeContext.description(code) + ", target=" + target + ", unionName=" + unionName + ", player=" + player);
			return;
		}

		if (target.isOnline()) {
			G2C_Union_Invite res = MessageFactory.getConcreteMessage(MGUnionEventDefines.G2C_Union_Invite);
			res.setInvitePlayerId(player.getId());
			res.setInvitePlayerName(player.getName());
			res.setUnionName(union.getName());
			res.setLevel(player.getExpComponent().getLevel());
			GameRoot.sendMessage(target.getIdentity(), res);
		}
	}

	private void handle_Union_ReplyInvite(C2G_Union_ReplyInvite event) {
		Identity identity = event.getIdentity();
		short actionEventId = event.getActionEventId();
		String invitePlayerId = event.getInvitePlayerId();
		String unionName = event.getUnionName();
		byte reply = event.getReply(); 
		Player player = getConcreteParent();

		if (logger.isDebugEnabled()) {
			logger.debug("reply invite: reply = " + reply);
		}
		
		reply = (byte)(reply + 10);

		//FIXME, 为啥需要邀请者在线???
		Player invitePlayer = playerManager.getOnlinePlayer(invitePlayerId);
		if (invitePlayer == null) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_UNION_CrtPlayerNotOnLine);
			logger.error("handle_Union_ReplyInvite error, invitePlayer not online, unionName=" + unionName + ", player=" + player);
			return;
		}
		
		MGUnion union = MGUnionHelper.getUnion(invitePlayer);
		if (union == null) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_UNION_UnionIsNotExist);
			logger.error("handle_Union_ReplyInvite error, union not exist, unionName=" + unionName + ", player=" + player);
			return;
		}
		
		MGUnionMember memberByPlayer = union.getMemberMgr().getMemberByPlayer(invitePlayer);
		if (!memberByPlayer.isChairman() && !memberByPlayer.isViceChairman()) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_UNION_HasNOAuthority);
			logger.error("handle_Union_ReplyInvite error,  has no authority, unionName=" + unionName + ", player=" + player);
			return;
		}
		
		
		if (reply == UnionMacro.Reply_Agree) {
			int code = MGUnionHelper.joinUnionAndSave(player, union);
			if (code != MGSuccessCode.CODE_SUCCESS) {
				ResultEvent.sendResult(identity, actionEventId, code);
				logger.error("handle_Union_ReplyInvite error, " + CodeContext.description(code) + ", unionName=" + unionName + ", player=" + player);
				return;
			}
		} 

		// FIXME, 是不是应该无论加入公会是否成功，都应该移除被邀请，目前只是当成功加入，或者拒绝，才移除
		MGPlayerUnionInvitedMgr playerUnionInvitedMgr = getPlayerUnionMgr().getPlayerUnionInvitedMgr();
		playerUnionInvitedMgr.removeInviteUnionId(union.getId());
		
		MGUnionHelper.sendPersonalChatMessage(player, invitePlayer, reply);

		G2C_Union_ReplyInvite replyRes = (G2C_Union_ReplyInvite) MessageFactory.getMessage(MGUnionEventDefines.G2C_Union_ReplyInvite);
		replyRes.setPlayerName(player.getName());
		replyRes.setReply(reply);
		GameRoot.sendMessage(invitePlayer.getIdentity(), replyRes);
	}

	private void handle_Union_Chat(C2G_Union_Chat event) {
		Identity identity = event.getIdentity();
		short actionEventId = event.getActionEventId();
		Player player = getConcreteParent();
		
		PlayerChatComponent playerChatComponent = player.getPlayerChatComponent();
		if (playerChatComponent.checkCDTime(ChannelType.Sociaty)) {
			
			playerChatComponent.refreshCDTime(ChannelType.Sociaty);
			
			MGUnion union = MGUnionHelper.getUnion(player);
			if (union == null) {
				ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_UNION_UnionIsNotExist);
				logger.error("handle_Union_Chat error, union not exist, player=" + player);
				return;
			}

			MGUnionHelper.sendUnionPersonalChatMessage(player, union, event.getMsg());
			
			playerChatComponent.sendSuccessToClient(event);
		}
	}
}
