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
package newbee.morningGlory.mmorpg.player.sectionQuest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import newbee.morningGlory.mmorpg.player.peerage.MGPlayerPeerageComponent;
import newbee.morningGlory.mmorpg.player.peerage.gameEvent.MGPeerageLevelUp_GE;
import newbee.morningGlory.mmorpg.player.sectionQuest.event.C2G_SectionQuest_Begin;
import newbee.morningGlory.mmorpg.player.sectionQuest.event.SectionQuestActionEventDefines;
import newbee.morningGlory.mmorpg.player.sectionQuest.event.mount.C2G_Mount_GetMountQuestReward;
import newbee.morningGlory.mmorpg.player.sectionQuest.event.mount.G2C_Mount_GetMountQuestReward;
import newbee.morningGlory.mmorpg.player.sectionQuest.event.mount.G2C_Mount_MountQuestResp;
import newbee.morningGlory.mmorpg.player.sectionQuest.event.mount.MountTask;
import newbee.morningGlory.mmorpg.player.sectionQuest.event.talisman.C2G_Talisman_GetQuestReward;
import newbee.morningGlory.mmorpg.player.sectionQuest.event.talisman.G2C_Talisman_GetQuestReward;
import newbee.morningGlory.mmorpg.player.sectionQuest.event.talisman.G2C_Talisman_QuestAccept;
import newbee.morningGlory.mmorpg.player.sectionQuest.event.talisman.G2C_Talisman_QuestResp;
import newbee.morningGlory.mmorpg.player.sectionQuest.event.wing.C2G_Wing_GetWingQuestReward;
import newbee.morningGlory.mmorpg.player.sectionQuest.event.wing.G2C_Wing_GetWingQuestReward;
import newbee.morningGlory.mmorpg.player.sectionQuest.event.wing.G2C_Wing_WingQuestAccept;
import newbee.morningGlory.mmorpg.player.sectionQuest.event.wing.G2C_Wing_WingQuestResp;
import newbee.morningGlory.mmorpg.player.talisman.MGPlayerTalismanComponent;
import newbee.morningGlory.mmorpg.player.talisman.MGTalismanState;
import newbee.morningGlory.mmorpg.player.wing.MGPlayerWing;
import newbee.morningGlory.mmorpg.player.wing.MGPlayerWingComponent;
import newbee.morningGlory.mmorpg.player.wing.MGPlayerWingRef;
import newbee.morningGlory.mmorpg.player.wing.MGWingEffectMgr;
import newbee.morningGlory.mmorpg.player.wing.actionEvent.MGWingLevelUp_GE;
import newbee.morningGlory.ref.loader.MGSectionQuestRefLoader;
import newbee.morningGlory.stat.MGStatFunctions;
import newbee.morningGlory.stat.logs.StatWing;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.authentication.Identity;
import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.foundation.property.PropertyDictionary;
import sophia.game.GameContext;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.GameObjectFactory;
import sophia.mmorpg.Mail.Mail;
import sophia.mmorpg.Mail.MailMgr;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.gameEvent.ChineseModeQuest_GE;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.player.mount.Mount;
import sophia.mmorpg.player.mount.MountEffectMgr;
import sophia.mmorpg.player.mount.MountManager;
import sophia.mmorpg.player.mount.MountRef;
import sophia.mmorpg.player.mount.PlayerMountComponent;
import sophia.mmorpg.player.mount.gameEvent.MGMountLevelUp_GE;
import sophia.mmorpg.player.quest.ref.order.QuestChineseOrderDefines;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.RuntimeResult;

import com.google.gson.Gson;

public final class MGSectionQuestComponent extends ConcreteComponent<Player> {

	private static MGSectionQuestComponent instance = new MGSectionQuestComponent();

	private static final Logger logger = Logger.getLogger(MGSectionQuestComponent.class);

	public static final String Tag = "MGSectionQuestComponent";

	private static final String MGPeerageLevelUp_GE_Id = MGPeerageLevelUp_GE.class.getSimpleName();

	private static final String MGWingLevelUp_GE_ID = MGWingLevelUp_GE.class.getSimpleName();

	private static final String MGMountLevelUp_GE_Id = MGMountLevelUp_GE.class.getSimpleName();
	
	private static final String ChineseModeQuest_GE_Id = ChineseModeQuest_GE.class.getSimpleName();

	private List<MGSectionQuestRef> questRefList = new ArrayList<>();

	private MGPlayerWingComponent wingComponent;

	private MGPlayerPeerageComponent peerageComponent;

	private MGPlayerTalismanComponent talismanComponent;

	private PlayerMountComponent mountComponent;

	public MGSectionQuestComponent() {
	}

	public static MGSectionQuestComponent getInstance() {
		return instance;
	}

	@Override
	public void ready() {
		/**
		addActionEventListener(SectionQuestActionEventDefines.C2G_SectionQuest_Begin);
		addActionEventListener(SectionQuestActionEventDefines.C2G_Wing_GetWingQuestReward);
		addActionEventListener(SectionQuestActionEventDefines.C2G_Talisman_GetQuestReward);
		addActionEventListener(SectionQuestActionEventDefines.C2G_Mount_GetMountQuestReward);
		addInterGameEventListener(MGPeerageLevelUp_GE_Id);
		addInterGameEventListener(MGWingLevelUp_GE_ID);
		 */
		addInterGameEventListener(MGMountLevelUp_GE_Id);
		super.ready();
	}

	@Override
	public void suspend() {
		/**
		removeActionEventListener(SectionQuestActionEventDefines.C2G_SectionQuest_Begin);
		removeActionEventListener(SectionQuestActionEventDefines.C2G_Wing_GetWingQuestReward);
		removeActionEventListener(SectionQuestActionEventDefines.C2G_Talisman_GetQuestReward);
		removeActionEventListener(SectionQuestActionEventDefines.C2G_Mount_GetMountQuestReward);
		removeInterGameEventListener(MGPeerageLevelUp_GE_Id);
		removeInterGameEventListener(MGWingLevelUp_GE_ID);
		 */
		removeInterGameEventListener(MGMountLevelUp_GE_Id);
		super.suspend();
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
		short actionEventId = event.getActionEventId();
		Identity identity = event.getIdentity();
		switch (actionEventId) {
		case SectionQuestActionEventDefines.C2G_SectionQuest_Begin:
			handle_SectionQuest_Begin((C2G_SectionQuest_Begin) event, actionEventId, identity);
			break;
		case SectionQuestActionEventDefines.C2G_Wing_GetWingQuestReward:
			handle_Wing_GetWingQuestReward((C2G_Wing_GetWingQuestReward) event, actionEventId, identity);
			break;
		case SectionQuestActionEventDefines.C2G_Talisman_GetQuestReward:
			handle_Wing_GetTalismanQuestReward((C2G_Talisman_GetQuestReward) event, actionEventId, identity);
			break;
		case SectionQuestActionEventDefines.C2G_Mount_GetMountQuestReward:
			handle_Mount_GetMountQuestReward((C2G_Mount_GetMountQuestReward) event, actionEventId, identity);
			break;
		}
		super.handleActionEvent(event);
	}

	private void sendWingRewardGameEventMessage() {
		MGPlayerWing playerWing = wingComponent.getPlayerWing();
		MGPlayerWingRef playerWingRef = playerWing.getPlayerWingRef();
		
		String wingRefId = wingComponent.getPlayerWing().getPlayerWingRef().getId();
		byte crtWingStageLevel = playerWingRef.getCrtWingStageLevel();
		byte crtWingStarLevel = playerWingRef.getCrtWingStarLevel();
		
		MGWingLevelUp_GE mgWingLevelUp_GE = new MGWingLevelUp_GE(wingRefId);
		GameEvent<MGWingLevelUp_GE> ge = (GameEvent<MGWingLevelUp_GE>) GameEvent.getInstance(MGWingLevelUp_GE_ID, mgWingLevelUp_GE);
		sendGameEvent(ge, getConcreteParent().getId());
		
		ChineseModeQuest_GE chineseModeQuest_GE = new ChineseModeQuest_GE();
		chineseModeQuest_GE.setType(ChineseModeQuest_GE.CourseType);
		chineseModeQuest_GE.setOrderEventId(QuestChineseOrderDefines.WingLevelUp);
		//chineseModeQuest_GE.setNumber(wingLevel);
		chineseModeQuest_GE.setNumber(crtWingStageLevel);
		chineseModeQuest_GE.setCount(crtWingStarLevel);
		GameEvent<ChineseModeQuest_GE> event = GameEvent.getInstance(ChineseModeQuest_GE_Id, chineseModeQuest_GE);
		sendGameEvent(event, getConcreteParent().getId());
		
		long exp = playerWing.getExp();
		MGStatFunctions.wingStat(getConcreteParent(), StatWing.Add, wingRefId, exp);
	}
	
	@Override
	public void handleGameEvent(GameEvent<?> event) {
		if (event.isId(MGPeerageLevelUp_GE_Id)) {
			if (wingComponent.getPlayerWing().getPlayerWingRef() != null) {
				return;
			}
			int peerageOrderLevel = 0;
			int peerageAcceptLevel = 0;
			for (MGSectionQuestRef questRef : questRefList) {
				if (questRef.getId().equals("wing_quest_1")) {
					peerageAcceptLevel = questRef.getAcceptCondition().get("knight");
					peerageOrderLevel = questRef.getOrderCondition().get("knight");
				}
			}
			MGPeerageLevelUp_GE peerage = (MGPeerageLevelUp_GE) event.getData();
			int peerageLevel = peerage.getPeerageLevel();
			if (peerageLevel == peerageAcceptLevel && wingComponent.getPlayerWing().getPlayerWingRef() == null) {
				G2C_Wing_WingQuestAccept questAccept = MessageFactory.getConcreteMessage(SectionQuestActionEventDefines.G2C_Wing_WingQuestAccept);
				GameRoot.sendMessage(getConcreteParent().getIdentity(), questAccept);
			}
			if (peerageLevel > peerageAcceptLevel && peerageLevel <= peerageOrderLevel) {
				G2C_Wing_WingQuestResp questResp = MessageFactory.getConcreteMessage(SectionQuestActionEventDefines.G2C_Wing_WingQuestResp);
				questResp.setPeerageLevel(peerageLevel);
				GameRoot.sendMessage(getConcreteParent().getIdentity(), questResp);
			}
		}
		if (event.isId(MGWingLevelUp_GE_ID)) {
			int wingOrderLevel = 0;
			int wingAcceptLevel = 0;
			for (MGSectionQuestRef questRef : questRefList) {
				if (questRef.getId().equals("talisman_quest_1")) {
					wingAcceptLevel = questRef.getAcceptCondition().get("wingLevel");
					wingOrderLevel = questRef.getOrderCondition().get("wingLevel");
				}
			}
			MGWingLevelUp_GE wing = (MGWingLevelUp_GE) event.getData();
			//int wingLevel = wing.getCrtStageLevel();
			String wingRefId = wing.getWingRefId();
			MGPlayerWingRef playerWingRef = (MGPlayerWingRef)GameRoot.getGameRefObjectManager().getManagedObject(wingRefId);
			byte crtStageLevel = playerWingRef.getCrtWingStageLevel();
			if (crtStageLevel == wingAcceptLevel) {
				G2C_Talisman_QuestAccept message = MessageFactory.getConcreteMessage(SectionQuestActionEventDefines.G2C_Talisman_QuestAccept);
				message.setType((byte) 1);
				GameRoot.sendMessage(getConcreteParent().getIdentity(), message);
			}
			if (crtStageLevel >= wingOrderLevel && !talismanComponent.getPlayerCitta().IsWingQuestCompleted()) {
				G2C_Talisman_QuestResp message = MessageFactory.getConcreteMessage(SectionQuestActionEventDefines.G2C_Talisman_QuestResp);
				message.setType((byte) 1);
				message.setLevel((byte) crtStageLevel);
				GameRoot.sendMessage(getConcreteParent().getIdentity(), message);
			}
		}
		if (event.isId(MGMountLevelUp_GE_Id)) {
			/**
			int mountAcceptLevel = 0;
			int mountOrderLevel = 0;
			for (MGSectionQuestRef questRef : questRefList) {
				if (questRef.getId().equals("talisman_quest_2")) {
					mountAcceptLevel = questRef.getAcceptCondition().get("stageLevel");
					mountOrderLevel = questRef.getOrderCondition().get("stageLevel");
				}
			}
			MGMountLevelUp_GE mountRef = (MGMountLevelUp_GE) event.getData();
			Mount mount = GameObjectFactory.getMount(mountRef.getMountRefId());
			int mountLevel = MGPropertyAccesser.getStageLevel(mount.getMountRef().getProperty());
			if (mountLevel == mountAcceptLevel) {
				G2C_Talisman_QuestAccept message = MessageFactory.getConcreteMessage(SectionQuestActionEventDefines.G2C_Talisman_QuestAccept);
				message.setType((byte) 2);
				GameRoot.sendMessage(getConcreteParent().getIdentity(), message);
			}
			if (mountLevel >= mountOrderLevel && !talismanComponent.getPlayerTalismanMgr().IsMountQuestCompleted()) {
				talismanComponent.getPlayerTalismanMgr().setMountQuestStatus(MGTalismanState.MountQuest_Complete);
				G2C_Talisman_QuestResp message = MessageFactory.getConcreteMessage(SectionQuestActionEventDefines.G2C_Talisman_QuestResp);
				message.setType((byte) 2);
				message.setLevel((byte) mountLevel);
				GameRoot.sendMessage(getConcreteParent().getIdentity(), message);
			} */
			MGMountLevelUp_GE even = (MGMountLevelUp_GE) event.getData();
			if (StringUtils.equals(even.getMountRefId(), "ride_1")) {
				G2C_Mount_GetMountQuestReward message = MessageFactory.getConcreteMessage(SectionQuestActionEventDefines.G2C_Mount_GetMountQuestReward);
				message.setResult(1);
				GameRoot.sendMessage(getConcreteParent().getIdentity(), message);
			}
		}
		super.handleGameEvent(event);
	}

	private void handle_Wing_GetWingQuestReward(C2G_Wing_GetWingQuestReward event, short actionEventId, Identity identity) {
		int peerageLevel = MGPropertyAccesser.getKnight(peerageComponent.getPeeragerefMgr().getCrtPeerageRef().getProperty());
		int peerageOrderLevel = 0;
		for (MGSectionQuestRef questRef : questRefList) {
			if (questRef.getId().equals("wing_quest_1")) {
				peerageOrderLevel = questRef.getOrderCondition().get("knight");
			}
		}
		if (peerageLevel >= peerageOrderLevel && wingComponent.getPlayerWing().getPlayerWingRef() == null) {
			GameRefObject wing_1 = GameRoot.getGameRefObjectManager().getManagedObject("wing_1");
			wingComponent.getPlayerWing().setPlayerWingRef((MGPlayerWingRef) wing_1);
			MGWingEffectMgr wingEffectMgr = new MGWingEffectMgr(getConcreteParent());
			wingEffectMgr.attach(wingComponent.getPlayerWing());
			wingComponent.getPlayerWing().broadcastWingModelProperty(getConcreteParent());
			G2C_Wing_GetWingQuestReward questReward = MessageFactory.getConcreteMessage(SectionQuestActionEventDefines.G2C_Wing_GetWingQuestReward);
			questReward.setResult(1);
			GameRoot.sendMessage(getConcreteParent().getIdentity(), questReward);
			sendWingRewardGameEventMessage();
		}
	}

	private void handle_Wing_GetTalismanQuestReward(C2G_Talisman_GetQuestReward event, short actionEventId, Identity identity) {
		int type = (int) event.getType();
		String questId = null;
		String condition = null;
		int Level = 0;

		MGSectionQuestRef Ref = null;
		if (type == 1) {
			questId = "talisman_quest_1";
			talismanComponent.getPlayerCitta().setWingQuestStatus(MGTalismanState.WingQuest_Complete);
			Level = MGPropertyAccesser.getWingLevel(wingComponent.getPlayerWing().getPlayerWingRef().getProperty());
			condition = "wingLevel";
		} else if (type == 2) {
			questId = "talisman_quest_2";
			Level = MGPropertyAccesser.getStageLevel(mountComponent.getMountManager().getCrtMount().getMountRef().getProperty());
			condition = "stageLevel";
		}
		for (MGSectionQuestRef questRef : questRefList) {
			if (questRef.getId().equals(questId)) {
				Ref = questRef;
			}

		}
		if (Ref != null && Level >= Ref.getOrderCondition().get(condition)) {
			List<ItemPair> reward = Ref.getRewardList();
			RuntimeResult runtimeResult = ItemFacade.addItem(getConcreteParent(), reward, ItemOptSource.SectionQuest);
			if (!runtimeResult.isOK()) {
				String content = new String("由于背包满，法宝任务奖励改为邮件发送。");
				String json = (new Gson()).toJson(reward);
				MailMgr.sendMailById(getConcreteParent().getId(), content, Mail.gonggao, json, 0, 0, 0);
			}
			G2C_Talisman_GetQuestReward message = MessageFactory.getConcreteMessage(SectionQuestActionEventDefines.G2C_Talisman_GetQuestReward);
			message.setType((byte) type);
			if (type == 1) {
				talismanComponent.getPlayerCitta().setWingQuestStatus(MGTalismanState.WingQuest_Complete);
			} else if (type == 2) {
				talismanComponent.getPlayerCitta().setMountQuestStatus(MGTalismanState.MountQuest_Complete);
			}
			GameRoot.sendMessage(getConcreteParent().getIdentity(), message);
		}
	}

	private void handle_SectionQuest_Begin(C2G_SectionQuest_Begin event, short actionEventId, Identity identity) {
		/**
		 * 翅膀任务
		 */
		int peerageOrderLevel = 0;
		int wingOrderLevel = 0;
		int wingAcceptLevel = 0;
		int mountStageOrderLevel = 0;
		int mountStartOrderLevel = 0;
		MGSectionQuestRefLoader loader = new MGSectionQuestRefLoader();
		Collection<MGSectionQuestRef> MGSectionQuestRefs = loader.loadAll();
		for (MGSectionQuestRef questRef : MGSectionQuestRefs) {
			if (questRef.getId().equals("wing_quest_1")) {
				questRefList.add(questRef);
				peerageOrderLevel = questRef.getOrderCondition().get("knight");
			}
			if (questRef.getId().equals("talisman_quest_1")) {
				questRefList.add(questRef);
				wingOrderLevel = questRef.getOrderCondition().get("wingLevel");
				wingAcceptLevel = questRef.getAcceptCondition().get("wingLevel");
			}
			if (questRef.getId().equals("talisman_quest_2")) {
				questRefList.add(questRef);
				mountStageOrderLevel = questRef.getOrderCondition().get("stageLevel");
				mountStartOrderLevel = questRef.getOrderCondition().get("startLevel");
			}
		}
		wingComponent = (MGPlayerWingComponent) getConcreteParent().getTagged(MGPlayerWingComponent.Tag);
		peerageComponent = (MGPlayerPeerageComponent) getConcreteParent().getTagged(MGPlayerPeerageComponent.Tag);
		if (peerageComponent.getPeeragerefMgr().getCrtPeerageRef() != null && wingComponent.getPlayerWing().getPlayerWingRef() == null) {
			int peerageLevel = MGPropertyAccesser.getKnight(peerageComponent.getPeeragerefMgr().getCrtPeerageRef().getProperty());

			if (peerageLevel < peerageOrderLevel) {
				G2C_Wing_WingQuestResp questResp = MessageFactory.getConcreteMessage(SectionQuestActionEventDefines.G2C_Wing_WingQuestResp);
				questResp.setPeerageLevel(peerageLevel);
				GameRoot.sendMessage(identity, questResp);
			}
			if (peerageLevel >= peerageOrderLevel) {
				G2C_Wing_WingQuestResp questResp = MessageFactory.getConcreteMessage(SectionQuestActionEventDefines.G2C_Wing_WingQuestResp);
				questResp.setPeerageLevel(peerageLevel);
				GameRoot.sendMessage(identity, questResp);
			}
		}
		/**
		 * 法宝任务
		 */
		talismanComponent = (MGPlayerTalismanComponent) getConcreteParent().getTagged(MGPlayerTalismanComponent.Tag);
		mountComponent = getConcreteParent().getPlayerMountComponent();
		if (!talismanComponent.getPlayerCitta().IsWingQuestCompleted()) {
			if (wingComponent.getPlayerWing().getPlayerWingRef() != null) {
				int wingLevel = MGPropertyAccesser.getWingLevel(wingComponent.getPlayerWing().getPlayerWingRef().getProperty());
				if (wingLevel >= wingAcceptLevel) {
					G2C_Talisman_QuestResp message = MessageFactory.getConcreteMessage(SectionQuestActionEventDefines.G2C_Talisman_QuestResp);
					message.setType((byte) 1);
					message.setLevel((byte) wingLevel);
					GameRoot.sendMessage(identity, message);
				}
			}
		}
		if (!talismanComponent.getPlayerCitta().IsMountQuestCompleted()) {
			if (mountComponent.getMountManager().getCrtMount() != null) {
				PropertyDictionary pd = mountComponent.getMountManager().getCrtMount().getMountRef().getProperty();
				int stageLevel = MGPropertyAccesser.getStageLevel(pd);
				if (stageLevel >= mountStartOrderLevel) {
					G2C_Talisman_QuestResp questResp = MessageFactory.getConcreteMessage(SectionQuestActionEventDefines.G2C_Talisman_QuestResp);
					questResp.setType((byte) 2);
					questResp.setLevel((byte) stageLevel);
					GameRoot.sendMessage(identity, questResp);
				}
			}
		}
		/**
		 * 坐骑任务
		 */
		if (mountComponent.getMountManager().getCrtMount() == null) {
			long onlineTime = MGPropertyAccesser.getOnlineTime(getConcreteParent().getProperty()) / 1000;
			final int time = 180; // 180s
			if (onlineTime < time) {// 这个180是 在线3分钟，写死
				int remainTime = (int) (time - onlineTime);
				G2C_Mount_MountQuestResp message = MessageFactory.getConcreteMessage(SectionQuestActionEventDefines.G2C_Mount_MountQuestResp);
				message.setTime(remainTime);
				GameRoot.sendMessage(identity, message);
				GameContext.getTaskManager().scheduleTask(new MountTask(getConcreteParent()), (long) remainTime * 1000);
				if (logger.isDebugEnabled()) {
					logger.debug("onlineTime=" + onlineTime + " ,remainTime=" + remainTime);
				}
			} else {
				G2C_Mount_MountQuestResp message = MessageFactory.getConcreteMessage(SectionQuestActionEventDefines.G2C_Mount_MountQuestResp);
				message.setTime(0);
				GameRoot.sendMessage(identity, message);
			}
		}
	}

	private void handle_Mount_GetMountQuestReward(C2G_Mount_GetMountQuestReward event, short actionEventId, Identity identity) {
		if (mountComponent.getMountManager().getCrtMount() == null) {
			mountTakeRewardTo(getConcreteParent());
			G2C_Mount_GetMountQuestReward message = MessageFactory.getConcreteMessage(SectionQuestActionEventDefines.G2C_Mount_GetMountQuestReward);
			message.setResult(1);
			GameRoot.sendMessage(identity, message);
		}
	}

	public void timeUp(Player player) {
		G2C_Mount_MountQuestResp message = MessageFactory.getConcreteMessage(SectionQuestActionEventDefines.G2C_Mount_MountQuestResp);
		message.setTime(0);
		GameRoot.sendMessage(player.getIdentity(), message);
	}

	public void mountTakeRewardTo(Player player) {
		String mountRefId = "ride_1";
		MountRef mountRef = (MountRef) GameRoot.getGameRefObjectManager().getManagedObject(mountRefId);
		Mount mount = GameObjectFactory.getMount();
		mount.setMountRef(mountRef);
		mount.setExp(0);
		mount.setId(UUID.randomUUID().toString());
		PlayerMountComponent mountComponent = player.getPlayerMountComponent();
		MountEffectMgr mountEffectMgr = new MountEffectMgr(player);
		mountComponent.setMountEffectMgr(mountEffectMgr);
		MountManager mountManager = mountComponent.getMountManager();
		mountManager.setCrtMount(mount);
		mountManager.setOwner(player);
		mountEffectMgr.firstGet(mount);

		MGMountLevelUp_GE MGMountLevelUp_GE = new MGMountLevelUp_GE(mountRefId);
		GameEvent<MGMountLevelUp_GE> ge = (GameEvent<MGMountLevelUp_GE>) GameEvent.getInstance(PlayerMountComponent.MGMountLevelUp_GE_ID, MGMountLevelUp_GE);
		player.handleGameEvent(ge);
		
		ChineseModeQuest_GE chineseModeQuest_GE = new ChineseModeQuest_GE();
		chineseModeQuest_GE.setType(ChineseModeQuest_GE.CourseType);
		chineseModeQuest_GE.setOrderEventId(QuestChineseOrderDefines.MountLevelUp);
		chineseModeQuest_GE.setNumber(mountRef.getStageLevel());
		GameEvent<ChineseModeQuest_GE> event = GameEvent.getInstance(ChineseModeQuest_GE_Id, chineseModeQuest_GE);
		player.handleGameEvent(event);
	}

}
