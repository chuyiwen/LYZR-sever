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
package newbee.morningGlory.event;

import newbee.morningGlory.character.event.CharacterEventDefines;
import newbee.morningGlory.debug.DebugEventDefines;
import newbee.morningGlory.http.actionEvent.GameServerGmEventDefines;
import newbee.morningGlory.mmorpg.gameInstance.event.GameInstanceEventDefines;
import newbee.morningGlory.mmorpg.operatActivities.event.OperatActivityDefines;
import newbee.morningGlory.mmorpg.player.achievement.actionEvent.AchievementEventDefines;
import newbee.morningGlory.mmorpg.player.activity.QuickRecharge.event.QuickRechargeDefines;
import newbee.morningGlory.mmorpg.player.activity.digs.event.MGDigsEventDefines;
import newbee.morningGlory.mmorpg.player.activity.event.MGActivityEventDefines;
import newbee.morningGlory.mmorpg.player.activity.fund.event.FundDefines;
import newbee.morningGlory.mmorpg.player.activity.ladder.event.MGLadderDefines;
import newbee.morningGlory.mmorpg.player.activity.limitTimeRank.event.MGLimitTimeDefines;
import newbee.morningGlory.mmorpg.player.activity.mining.event.MGMiningEventDefines;
import newbee.morningGlory.mmorpg.player.activity.resDownload.event.ResDownloadDefines;
import newbee.morningGlory.mmorpg.player.auction.event.MGAuctionDefines;
import newbee.morningGlory.mmorpg.player.castleWar.event.CastleWarActionEventDefines;
import newbee.morningGlory.mmorpg.player.dailyQuest.event.DailyQuestActionEventDefines;
import newbee.morningGlory.mmorpg.player.depot.event.PlayerDepotEventDefines;
import newbee.morningGlory.mmorpg.player.funStep.event.FunStepActionEventDefines;
import newbee.morningGlory.mmorpg.player.offLineAI.event.OffLineAIEventDefines;
import newbee.morningGlory.mmorpg.player.peerage.actionEvent.PeerageEventDefines;
import newbee.morningGlory.mmorpg.player.pk.event.PkEventDefines;
import newbee.morningGlory.mmorpg.player.sectionQuest.event.SectionQuestActionEventDefines;
import newbee.morningGlory.mmorpg.player.sortboard.event.MGSortboardEventDefines;
import newbee.morningGlory.mmorpg.player.store.event.StoreEventDefines;
import newbee.morningGlory.mmorpg.player.talisman.actionEvent.TalisManEventDefines;
import newbee.morningGlory.mmorpg.player.union.actionEvent.MGUnionEventDefines;
import newbee.morningGlory.mmorpg.player.unionGameInstance.event.MGUnionGameInstanceDefines;
import newbee.morningGlory.mmorpg.player.wing.event.WingEventDefines;
import newbee.morningGlory.mmorpg.sceneActivities.event.SceneActivityEventDefines;
import newbee.morningGlory.mmorpg.sprite.buff.actionEvent.BuffEventDefines;
import newbee.morningGlory.mmorpg.vip.event.VipEventDefines;

import org.apache.log4j.Logger;

import sophia.foundation.communication.core.MessageFactory;
import sophia.mmorpg.auth.event.AuthEventDefines;
import sophia.mmorpg.player.chat.event.ChatEventDefines;
import sophia.mmorpg.player.friend.event.FriendEventDefines;

public final class MGEventDefines {

	private static final Logger logger = Logger.getLogger(MGEventDefines.class.getName());

	// 用户
	public static final short User_Message_Begin = 100;
	// 角色
	public static final short Character_Message_Begin = 200;
	// /////////////MorningGlory Message ID Range [2000,
	// Short.MAX_VALUE)////////
	public static final short Debug_Message_Begin = 2000;
	
	// 爵位起始消息
	public static final short Peerage_Message_Begin = 2300;

	// 成就起始消息
	public static final short Achievement_Message_Begin = 2400;

	// 副本起始消息
	public static final short Game_Instance_Message_Begin = 2500;

	// 翅膀消息起始
	public static final short Wing_Message_Begin = 2600;

	// 法宝消息起始值
	public static final short Talisman_Message_Begin = 2700;

	// 法宝消息起始值
	public static final short Shop_Message_Begin = 2800;

	// 公会消息起始值
	public static final short Union_Message_Begin = 2900;

	// PK消息起始值
	public static final short Pk_Message_Begin = 3000;

	// 游戏服务器起始消息
	public static final short Game_Serve_Message_Begin = 3100;

	// vip起始值
	public static final short Vip_Message_Begin = 3200;

	// 活动起始值
	public static final short Activity_Message_Begin = 3300;

	// 排行榜起始值
	public static final short SortBoard_Message_Begin = 3400;

	// 天梯起始值
	public static final short Ladder_Message_Begin = 3500;

	// 攻城战起始值
	public static final short CastleWar_Message_Begin = 3600;

	// 场景活动起始消息
	public static final short Scene_Activity_Message_Begin = 3700;

	// 离线挂机起始消息
	public static final short OffLineAISeting_Begin = 3800;

	// 分包下载奖励起始消息
	public static final short ResDownload_Message_Begin = 3900;

	// 拍卖行
	public static final short Auction_Message_Begin = 4000;

	// unionGameInstance
	public static final short UnionGameInstance_Message_Begin = 4100;

	// 新手引导记录
	public static final short FunStep_Message_Begin = 4200;

	// 快捷充值
	public static final short QuickRecharge_Message_Begin = 4300;

	// 挖矿
	public static final short Mining_Message_Begin = 4400;

	// 仓库
	public static final short Depot_Message_Begin = 4500;

	public static final void registerActionEvents() {
		DebugEventDefines.registerActionEvents();
		AuthEventDefines.registerActionEvents();
		CharacterEventDefines.registerActionEvents();
		DailyQuestActionEventDefines.registerActionEvents();
		WingEventDefines.registerActionEvents();
		PeerageEventDefines.registerActionEvents();// 爵位
		AchievementEventDefines.registerActionEvents();// 成就
		SectionQuestActionEventDefines.registerActionEvents();
		GameInstanceEventDefines.registerActionEvents();// 副本
		TalisManEventDefines.registerActionEvents();
		StoreEventDefines.registerActionEvents();
		BuffEventDefines.registerActionEvents();
		MGUnionEventDefines.registerActionEvents();
		PkEventDefines.registerActionEvents();
		GameServerGmEventDefines.registerActionEvents();
		VipEventDefines.registerActionEvents();
		ChatEventDefines.registerActionEvents();
		FriendEventDefines.registerActionEvents();
		MGActivityEventDefines.registerActionEvents();
		MGSortboardEventDefines.registerActionEvents();
		MGDigsEventDefines.registerActionEvents();
		MGLimitTimeDefines.registerActionEvents();
		MGMiningEventDefines.registerActionEvents();// 挖矿活动
		FundDefines.registerActionEvents();

		CastleWarActionEventDefines.registerActionEvents();
		OperatActivityDefines.registerActionEvents();
		SceneActivityEventDefines.registerActionEvents();
		OffLineAIEventDefines.registerActionEvents();
		MGLadderDefines.registerActionEvents();
		ResDownloadDefines.registerActionEvents();
		MGAuctionDefines.registerActionEvents();
		MGUnionGameInstanceDefines.registerActionEvents();
		FunStepActionEventDefines.registerActionEvents();
		QuickRechargeDefines.registerActionEvents();

		PlayerDepotEventDefines.registerActionEvents();
		
		if (logger.isDebugEnabled()) {
			logger.debug("registed message. number: " + MessageFactory.getMessageNumber());
		}
	}
}
