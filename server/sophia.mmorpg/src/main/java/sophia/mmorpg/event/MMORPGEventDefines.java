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
package sophia.mmorpg.event;

import org.apache.log4j.Logger;

import sophia.foundation.communication.core.MessageFactory;
import sophia.mmorpg.Mail.event.MailEventDefines;
import sophia.mmorpg.equipmentSmith.smith.actionEvent.StrengEquipmentEventDefines;
import sophia.mmorpg.monster.event.MonsterEventDefines;
import sophia.mmorpg.player.equipment.event.EquipmentEventDefines;
import sophia.mmorpg.player.fightSkill.event.FightSkillDefines;
import sophia.mmorpg.player.itemBag.event.ItemBagEventDefines;
import sophia.mmorpg.player.mount.event.MountEventDefines;
import sophia.mmorpg.player.persistence.PlayerPeriodSaveActionEvent;
import sophia.mmorpg.player.property.event.PlayerEventDefines;
import sophia.mmorpg.player.quest.event.QuestActionEventDefines;
import sophia.mmorpg.player.scene.event.SceneEventDefines;
import sophia.mmorpg.player.team.actionEvent.TeamEventDefines;
import sophia.mmorpg.player.worldBossMsg.event.WorldBossDefines;

public final class MMORPGEventDefines {
	private static final Logger logger = Logger.getLogger(MMORPGEventDefines.class.getName());
	// System Player Period Save ActionEventId
	public static final short System_Player_PeriodSave_ActionEvent = 1;
	// ResultEvent的actionEventId
	public static final short G2C_Resultevent = 99;
	// for Authentication only
	public static final short User_Message_Begin = 100;
	// ////////////// MMORPG Message ID Range [300, 2000) ///////////////

	// 场景消息
	public static final short Scene_Message_Begin = 300;
	// 技能消息
	public static final short Skill_Message_Begin = 400;
	// 玩家屬性
	public static final short Player_Message_Begin = 500;
	// 任务消息初始值
	public static final short QST_Message_Begin = 600;
	// 背包起始消息值
	public static final short Bag_Message_Begin = 800;
	// 装备消息起始值
	public static final short Equip_Message_Begin = 900;
	// 聊天消息起始值
	public static final short Chat_Message_Begin = 700;
	// 好友消息起始值
	public static final short Friends_Message_Begin = 1000;
	// 坐骑消息起始值
	public static final short Mount_Message_Begin = 1100;
	// 邮件消息
	public static final short Mail_Message_Begin = 1400;
	// buff消息
	public static final short Buff_Message_Begin = 1500;
	// 组队消息起始值
	public static final short Team_Message_Begin = 1600;
	// 怪物
	public static final short Monster_Message_Begin = 1700;

	// 世界Boss 消息推送
	public static final short WorldBoss_Message_Begin = 1800;

	public static final short Friedn_Message_Begin = 1900;

	// 装备强化
	public static final short Forge_Message_Begin = 2200;

	public static final void registerActionEvents() {
		MessageFactory.addMessage(System_Player_PeriodSave_ActionEvent, PlayerPeriodSaveActionEvent.class);
		MessageFactory.addMessage(G2C_Resultevent, ResultEvent.class);

		SceneEventDefines.registerActionEvents();

		QuestActionEventDefines.registerActionEvents();

		ItemBagEventDefines.registerActionEvents();

		EquipmentEventDefines.registerActionEvents();

		FightSkillDefines.registerActionEvents();

		PlayerEventDefines.registerActionEvents();

		MountEventDefines.registerActionEvents();

		MailEventDefines.registerActionEvents();

		TeamEventDefines.registerActionEvents();

		MonsterEventDefines.registerActionEvents();

		WorldBossDefines.registerActionEvents();

		StrengEquipmentEventDefines.registerActionEvents();
	}
}
