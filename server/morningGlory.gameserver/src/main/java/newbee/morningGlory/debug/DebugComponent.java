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
package newbee.morningGlory.debug;

import java.util.ArrayList;
import java.util.List;

import newbee.morningGlory.MorningGloryContext;
import newbee.morningGlory.debug.DebugCommand.CommandParameters;
import newbee.morningGlory.mmorpg.auction.AuctionItem;
import newbee.morningGlory.mmorpg.auction.AuctionMgr;
import newbee.morningGlory.mmorpg.auction.AuctionSystemComponent;
import newbee.morningGlory.mmorpg.operatActivities.OperatActivity;
import newbee.morningGlory.mmorpg.operatActivities.OperatActivityMgr;
import newbee.morningGlory.mmorpg.operatActivities.OperatActivityType;
import newbee.morningGlory.mmorpg.player.achievement.MGPlayerAchievementComponent;
import newbee.morningGlory.mmorpg.player.activity.QuickRecharge.MGPlayerQuickRechargeComponent;
import newbee.morningGlory.mmorpg.player.activity.fund.FundActivityComponet;
import newbee.morningGlory.mmorpg.player.activity.fund.FundType;
import newbee.morningGlory.mmorpg.player.auction.MGAuctionComponent;
import newbee.morningGlory.mmorpg.player.auction.event.C2G_Auction_DoSell;
import newbee.morningGlory.mmorpg.player.auction.event.MGAuctionDefines;
import newbee.morningGlory.mmorpg.player.dailyQuest.MGDailyQuest;
import newbee.morningGlory.mmorpg.player.dailyQuest.MGDailyQuestComponent;
import newbee.morningGlory.mmorpg.player.dailyQuest.event.DailyQuestActionEventDefines;
import newbee.morningGlory.mmorpg.player.dailyQuest.event.G2C_QST_DailyQuestUpdate;
import newbee.morningGlory.mmorpg.player.dailyQuest.event.G2C_QST_DailyStateUpdate;
import newbee.morningGlory.mmorpg.player.gameInstance.CountRecord;
import newbee.morningGlory.mmorpg.player.gameInstance.MGPlayerGameInstanceRecord;
import newbee.morningGlory.mmorpg.player.gameInstance.MGPlayerGameInstanceRecordMgr;
import newbee.morningGlory.mmorpg.player.peerage.MGPlayerPeerageComponent;
import newbee.morningGlory.mmorpg.sceneActivities.SceneActivityMgr;
import newbee.morningGlory.mmorpg.sceneActivities.castleWar.CastleWarApplyMgr;
import newbee.morningGlory.mmorpg.sceneActivities.castleWar.CastleWarMgr;
import newbee.morningGlory.mmorpg.sceneActivities.mining.MGMiningActivity;
import newbee.morningGlory.mmorpg.sceneActivities.monsterIntrusion.MonsterInvasionMgr;
import newbee.morningGlory.mmorpg.sceneActivities.teamBoss.TeamBossMgr;
import newbee.morningGlory.mmorpg.sortboard.SortboardMgr;
import newbee.morningGlory.mmorpg.sortboard.SortboardType;
import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuff;
import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuffComponent;
import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuffRef;
import newbee.morningGlory.mmorpg.union.MGUnion;
import newbee.morningGlory.mmorpg.union.MGUnionConstant;
import newbee.morningGlory.mmorpg.union.MGUnionHelper;
import newbee.morningGlory.mmorpg.union.MGUnionMember;
import newbee.morningGlory.mmorpg.union.MGUnionMgr;
import newbee.morningGlory.mmorpg.union.MGUnionSaver;
import newbee.morningGlory.mmorpg.vip.MGPlayerVipComponent;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.authentication.Identity;
import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.foundation.property.PropertyDictionary;
import sophia.foundation.util.DebugUtil;
import sophia.foundation.util.Position;
import sophia.foundation.util.PropertiesWrapper;
import sophia.game.GameRoot;
import sophia.mmorpg.GameObjectFactory;
import sophia.mmorpg.Mail.MailMgr;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.base.scene.GameSceneHelper;
import sophia.mmorpg.base.scene.grid.SceneGrid;
import sophia.mmorpg.base.scene.mgr.LootMgrComponent;
import sophia.mmorpg.base.scene.mgr.PluckMgrComponent;
import sophia.mmorpg.base.scene.ref.AbstractGameSceneRef;
import sophia.mmorpg.base.sprite.state.action.DeadState;
import sophia.mmorpg.base.sprite.state.action.IdleState;
import sophia.mmorpg.base.sprite.state.action.PluckingState;
import sophia.mmorpg.base.sprite.state.adjunction.BleedState;
import sophia.mmorpg.base.sprite.state.adjunction.BurningState;
import sophia.mmorpg.base.sprite.state.adjunction.DizzinessState;
import sophia.mmorpg.base.sprite.state.adjunction.DumbingState;
import sophia.mmorpg.base.sprite.state.adjunction.InvincibleState;
import sophia.mmorpg.base.sprite.state.adjunction.MagicImmunityState;
import sophia.mmorpg.base.sprite.state.adjunction.MagicShieldState;
import sophia.mmorpg.base.sprite.state.adjunction.ParalysisState;
import sophia.mmorpg.base.sprite.state.adjunction.PhysicalImmunityState;
import sophia.mmorpg.base.sprite.state.adjunction.PoisoningState;
import sophia.mmorpg.base.sprite.state.adjunction.SlowDownState;
import sophia.mmorpg.base.sprite.state.adjunction.StealthState;
import sophia.mmorpg.base.sprite.state.global.FightState;
import sophia.mmorpg.base.sprite.state.global.PKState;
import sophia.mmorpg.base.sprite.state.global.TeamState;
import sophia.mmorpg.base.sprite.state.movement.ChaseState;
import sophia.mmorpg.base.sprite.state.movement.MoveState;
import sophia.mmorpg.base.sprite.state.movement.PatrolState;
import sophia.mmorpg.base.sprite.state.movement.ReturnToBirthState;
import sophia.mmorpg.base.sprite.state.movement.StopState;
import sophia.mmorpg.base.sprite.state.posture.MountedState;
import sophia.mmorpg.base.sprite.state.posture.StandedState;
import sophia.mmorpg.base.sprite.state.posture.WalkState;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.loot.Loot;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.chat.sysytem.SystemPromptFacade;
import sophia.mmorpg.player.itemBag.ItemBag;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.player.itemBag.PlayerItemBagComponent;
import sophia.mmorpg.player.itemBag.event.G2C_Bag_Capacity;
import sophia.mmorpg.player.itemBag.event.ItemBagEventDefines;
import sophia.mmorpg.player.quest.PlayerQuestComponent;
import sophia.mmorpg.player.quest.Quest;
import sophia.mmorpg.player.quest.QuestState;
import sophia.mmorpg.player.quest.event.G2C_QST_QuestAcceptedList;
import sophia.mmorpg.player.quest.event.G2C_QST_QuestUpdate;
import sophia.mmorpg.player.quest.event.G2C_QST_QuestVisibleList;
import sophia.mmorpg.player.quest.event.G2C_QST_StateUpdate;
import sophia.mmorpg.player.quest.event.QuestActionEventDefines;
import sophia.mmorpg.player.quest.ref.QuestRef;
import sophia.mmorpg.pluck.Pluck;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.ref.editorgen.MGPropertySymbolDefines;
import sophia.mmorpg.utils.RuntimeResult;

import com.google.gson.Gson;

public class DebugComponent extends ConcreteComponent<Player> {

	private static final Logger logger = Logger.getLogger(DebugComponent.class);

	public static final String fail = "fail";

	public static boolean debug = true;

	public static final String Tag = "DebugComponent";

	static {
		PropertiesWrapper properties = MorningGloryContext.getProperties();
		debug = properties.getBooleanProperty("newbee.morningGlory.debug.DebugComponent.enable", false);
	}

	private static List<DebugCommand> debugCommands = new ArrayList<DebugCommand>();

	public static DebugCommand getDebugCommand(String commandStr) {
		for (DebugCommand d : debugCommands) {
			if (d.getCommandTemplate().toLowerCase().startsWith(commandStr.toLowerCase() + "")) {
				return d;
			}
		}
		return null;
	}

	public static void addDebugCommand(DebugCommand debugCommand) {
		debugCommands.add(debugCommand);
	}

	private static DebugCommand help = new DebugCommand("help", "帮助说明") {
		@Override
		public String exec(Player sender, CommandParameters parameters) {

			String result = "";
			for (DebugCommand d : debugCommands) {
				result += d.getCommandTemplate() + "\n";
				result += "    \\" + d.getDescription() + "\n";
				if (d.getExamples() != null) {
					for (String example : d.getExamples())
						result += "    \\" + example + "\n";
				}
			}

			logger.debug("help result: " + result);
			return result;
		}
	};

	@Override
	public void ready() {
		if (logger.isDebugEnabled()) {
			logger.debug("Debug Ready");
		}
		addActionEventListener(DebugEventDefines.C2G_Debug_Event);
		super.ready();
	}

	@Override
	public void suspend() {
		removeActionEventListener(DebugEventDefines.C2G_Debug_Event);
		super.suspend();
	}

	@Override
	public void handleActionEvent(ActionEventBase actionEvent) {
		if (debug == false) {
			if (logger.isDebugEnabled()) {
				logger.debug("debug is forbided");
			}
			return;
		}

		Identity identity = actionEvent.getIdentity();
		Player player = getConcreteParent();

		C2G_Debug_Event debugEvent = (C2G_Debug_Event) actionEvent;

		CommandParameters parameters = new CommandParameters(debugEvent.getCommandStr());

		String command = parameters.getString(0);

		logger.info(player + "-do debug cmd->" + command + ", parameters:" + parameters);

		DebugCommand debugCommand = DebugComponent.getDebugCommand(command);

		if (debugCommand == null) {
			logger.debug("Wrong Debug Commond");
			return;
		}
		String result = null;
		try {
			result = debugCommand.exec(player, parameters);
		} catch (Exception e) {
			result = DebugComponent.fail;
			logger.error("DebugComponent exec exception!!! " + DebugUtil.printStack(e));
		}

		G2C_Debug_Event g2c = MessageFactory.getConcreteMessage(DebugEventDefines.G2C_Debug_Event);
		g2c.setCommand(debugCommand.getCommandTemplate());
		g2c.setResult(result);
		GameRoot.sendMessage(identity, g2c);
		super.handleActionEvent(actionEvent);
	}

	public boolean execDebug(String commandStr) {
		Player player = getConcreteParent();
		CommandParameters parameters = new CommandParameters(commandStr);
		String command = parameters.getString(0);
		logger.info(player + "-execDebug cmd->" + command + ",parameters:" + parameters);
		DebugCommand debugCommand = DebugComponent.getDebugCommand(command);

		if (debugCommand == null) {
			logger.debug("Wrong Debug Commond");
			return false;
		}

		try {
			debugCommand.exec(player, parameters);
		} catch (Exception e) {
			logger.error("execDebug error, " + DebugUtil.printStack(e));
			return false;
		}

		return true;
	}

	private static DebugCommand addExp = new DebugCommand("addExp <数量>", "加经验", "示例1:addExp 1000") {

		@Override
		public String exec(Player player, CommandParameters parameters) {
			int exp = parameters.getInt(1);
			player.getExpComponent().addExp(exp);
			logger.debug("addExp " + exp);
			SystemPromptFacade.getExp(player, exp);
			return "addExp.. OK";
		}
	};
	/**
	 * 遮天基金减少天数
	 */
	private static DebugCommand mbt = new DebugCommand("mbt <类型> <天数>", "遮天基金减少天数", "示例1:mbt 1 1") {

		@Override
		public String exec(Player player, CommandParameters parameters) {
			int type = parameters.getInt(1);
			int day = parameters.getInt(2);
			FundActivityComponet fundActivityComponent = (FundActivityComponet) player.getTagged(FundActivityComponet.Tag);
			logger.debug("mbt " + type + " " + day);
			FundType funType = FundType.getFundType(type);
			fundActivityComponent.getFundDataByType(funType).setBuyFundTime(System.currentTimeMillis() - day * 24 * 3600 * 1000l);
			return "mbt.. OK";
		}
	};

	private static DebugCommand addHp = new DebugCommand("addHp <数量>", "加经验", "示例1:addHp 1000") {

		@Override
		public String exec(Player player, CommandParameters parameters) {
			int hp = parameters.getInt(1);
			PropertyDictionary pd = new PropertyDictionary();
			player.modifyHP(player, hp);
			int curHp = player.getHP();
			MGPropertyAccesser.setOrPutHP(pd, curHp);
			player.notifyPorperty(pd);
			logger.debug("addHp " + hp);
			return "addHp.. OK";
		}
	};

	private static DebugCommand addMp = new DebugCommand("addMp <数量>", "加经验", "示例1:addHp 1000") {

		@Override
		public String exec(Player player, CommandParameters parameters) {
			int mp = parameters.getInt(1);
			PropertyDictionary pd = new PropertyDictionary();
			player.modifyMP(mp);
			int curMp = player.getMP();
			MGPropertyAccesser.setOrPutMP(pd, curMp);
			player.notifyPorperty(pd);
			logger.debug("addMp " + mp);
			return "addMp.. OK";
		}
	};

	private static DebugCommand addItem = new DebugCommand("addItem <数量>", "加物品", "示例1:addItem item_2exp 10") {

		@Override
		public String exec(Player player, CommandParameters parameters) {
			String refId = parameters.getString(1);
			int number = parameters.getInt(2);

			ItemFacade.addItem(player, new ItemPair(refId, number, false), ItemOptSource.Debug);
			logger.debug("addItem " + refId + "数量:" + number);
			return "addItem.. OK";
		}
	};

	private static DebugCommand addAcution = new DebugCommand("addAuction <refId> <数量> <价格>", "加拍卖物品", "示例1:addAuction item_2exp 10 100") {

		@Override
		public String exec(Player player, CommandParameters parameters) {
			String refId = parameters.getString(1);
			int number = parameters.getInt(2);
			int price = parameters.getInt(2);

			Item aItem = GameObjectFactory.getItem(refId);
			if (aItem.isEquip()) {
				for (int i = 0; i < number; i++) {
					aItem = GameObjectFactory.getItem(refId);
					aItem.setNumber(1);
					ItemFacade.addItems(player, aItem, ItemOptSource.Debug);
					// long startTime = System.currentTimeMillis();
					// long endTime = startTime + 60 * 60 * 24 * 1000l;
					// AuctionSystemComponent auctionSystemComponent =
					// MorningGloryContext.getAuctionSystemComponent();
					// AuctionMgr auctionMgr =
					// auctionSystemComponent.getAuctionMgr();
					C2G_Auction_DoSell res = MessageFactory.getConcreteMessage(MGAuctionDefines.C2G_Auction_DoSell);
					res.setId(aItem.getId());
					res.setPrice(price);
					res.setNumber(1);
					MGAuctionComponent auctionComponent = (MGAuctionComponent) player.getTagged(MGAuctionComponent.Tag);
					auctionComponent.handle_Auction_DoSell(res);
					// AuctionItem auctionItem =
					// auctionMgr.createAuctionItem(player.getId(), aItem,
					// price, startTime, endTime);
					// auctionMgr.addAuctionItem(auctionItem);
				}
			} else {
				aItem.setNumber(number);
				ItemFacade.addItems(player, aItem, ItemOptSource.Debug);
				// long startTime = System.currentTimeMillis();
				// long endTime = startTime + 60 * 60 * 24 * 1000l;
				//
				// AuctionSystemComponent auctionSystemComponent =
				// MorningGloryContext.getAuctionSystemComponent();
				// AuctionMgr auctionMgr =
				// auctionSystemComponent.getAuctionMgr();
				// AuctionItem auctionItem =
				// auctionMgr.createAuctionItem(player.getId(), aItem, price,
				// startTime, endTime);
				// auctionMgr.addAuctionItem(auctionItem);

				C2G_Auction_DoSell res = MessageFactory.getConcreteMessage(MGAuctionDefines.C2G_Auction_DoSell);
				res.setId(aItem.getId());
				res.setPrice(price);
				res.setNumber(number);
				MGAuctionComponent auctionComponent = (MGAuctionComponent) player.getTagged(MGAuctionComponent.Tag);
				auctionComponent.handle_Auction_DoSell(res);
			}

			logger.debug("addAcution " + refId + "数量:" + number + ",价格：" + price);
			return "addAcution.. OK";
		}
	};

	private static DebugCommand addMoney = new DebugCommand("addMoney <数量>", "加货币 后面三个参数依次： 1为金币 2为绑定元宝 3为元宝", "示例1:addMoney 1000 1000 1000") {

		@Override
		public String exec(Player player, CommandParameters parameters) {
			int gold = parameters.getInt(1) < 0 ? 0 : parameters.getInt(1);
			int bindedGold = parameters.getInt(2) < 0 ? 0 : parameters.getInt(2);
			int unbindedGold = parameters.getInt(3) < 0 ? 0 : parameters.getInt(3);

			player.getPlayerMoneyComponent().addGold(gold, ItemOptSource.Debug);
			player.getPlayerMoneyComponent().addBindGold(bindedGold, ItemOptSource.Debug);
			player.getPlayerMoneyComponent().addUnbindGold(unbindedGold, ItemOptSource.Debug);
			logger.debug("addMoney 金币：" + gold + ",绑定元宝:" + bindedGold + ",元宝:" + unbindedGold);
			return "addMoney.. OK";
		}
	};
	private static DebugCommand sendMail = new DebugCommand("sendMail <content> <mailType> <gold> <bindGold> <coin>", "发邮件 后面四个参数依次：  1内容 2邮件类型3元宝4,绑定元宝 5铜钱 ",
			"示例1:addMoney 内容 0 23 24 45") {

		@Override
		public String exec(Player player, CommandParameters parameters) {
			String content = parameters.getString(1);
			byte mailType = parameters.getByte(2);
			int gold = parameters.getInt(3);
			int bindGold = parameters.getInt(4);
			int coin = parameters.getInt(5);
			List<ItemPair> list = new ArrayList<ItemPair>();
			ItemPair itemPair1 = new ItemPair("item_qianghuashi", 2, true);
			ItemPair itemPair2 = new ItemPair("item_qianghuajuan_1", 1, true);
			ItemPair itemPair3 = new ItemPair("item_exp_1", 4, true);
			list.add(itemPair1);
			list.add(itemPair2);
			list.add(itemPair3);
			String json = (new Gson()).toJson(list);
			logger.debug("Email json:" + json);
			MailMgr.sendMailById(player.getId(), content, mailType, json, gold, bindGold, coin);
			logger.debug("content:" + content + ",mailType:" + mailType);
			return "sendMail.. OK";
		}
	};

	private static DebugCommand addMerit = new DebugCommand("addMerit <数量>", "加功勋", "示例1:addMerit 1000") {

		@Override
		public String exec(Player player, CommandParameters parameters) {
			int merit = parameters.getInt(1);
			MGPlayerPeerageComponent playerPeerageComponent = (MGPlayerPeerageComponent) player.getTagged("MGPlayerPeerageComponent");
			playerPeerageComponent.getMeritManager().addMerit(merit);
			logger.debug("addMerit " + merit);
			return "addMerit.. OK";
		}
	};

	private static DebugCommand addAchieve = new DebugCommand("addAchieve <数量>", "加成就点", "示例1:addAchieve 1000") {

		@Override
		public String exec(Player player, CommandParameters parameters) {
			int achievement = parameters.getInt(1);
			MGPlayerAchievementComponent playerAchievementComponent = (MGPlayerAchievementComponent) player.getTagged("MGPlayerAchievementComponent");
			playerAchievementComponent.getAchievePointMgr().addAchievePoint(achievement);
			logger.debug("addAchieve " + achievement);
			return "addAchieve.. OK";
		}
	};
	private static DebugCommand addState = new DebugCommand("addState buffId", "示例1:addState buff_State_1") {

		@SuppressWarnings("rawtypes")
		@Override
		public String exec(Player player, CommandParameters parameters) {
			String buffRefId = parameters.getString(1);

			int duration = 10 * 1000;
			MGFightSpriteBuffComponent fightSpriteBuffComponent = (MGFightSpriteBuffComponent) player.getTagged(MGFightSpriteBuffComponent.Tag);
			MGFightSpriteBuffRef ref = (MGFightSpriteBuffRef) GameRoot.getGameRefObjectManager().getManagedObject(buffRefId);
			MGFightSpriteBuff buff = new MGFightSpriteBuff(ref, player, player, duration);
			MGPropertyAccesser.setOrPutDuration(buff.getSpecialProperty(), duration);
			MGPropertyAccesser.setOrPutAttachRepeatCount(buff.getSpecialProperty(), (byte) 3);
			MGPropertyAccesser.setOrPutSkillDamageRate(buff.getSpecialProperty(), 30);
			RuntimeResult result = fightSpriteBuffComponent.getFightSpriteBuffMgr().attachFightSpriteBuff(buff);

			return "buffState.. OK";
		}
	};
	private static DebugCommand acceptQuest = new DebugCommand("acceptQuest <任务名>", "修改当前主线任务", "示例1:acceptQuest quest_10") {

		@Override
		public String exec(Player player, CommandParameters parameters) {
			String refId = parameters.getString(1);
			PlayerQuestComponent questComponent = player.getPlayerQuestComponent();
			Quest quest = questComponent.createQuest(refId);
			questComponent.getQuestManager().setCrtQuest(quest);
			questComponent.chineseQuestGEAction(quest);
			G2C_QST_QuestAcceptedList accept = MessageFactory.getConcreteMessage(QuestActionEventDefines.G2C_QST_QuestAcceptedList);
			accept.setCrtQuest(quest);
			GameRoot.sendMessage(player.getIdentity(), accept);
			return "acceptQuest.. OK";
		}
	};
	private static DebugCommand completeQuest = new DebugCommand("completeQuest", "完成当前主线任务", "示例1:completeQuest") {

		@Override
		public String exec(Player player, CommandParameters parameters) {
			PlayerQuestComponent questComponent = player.getPlayerQuestComponent();
			Quest quest = questComponent.getQuestManager().getCrtQuest();
			questComponent.questStateUpdate(quest, QuestState.CompletedQuestState);

			QuestRef questRef = questComponent.getQuestManager().nextQuest().getQuestRef();
			if (questRef != null) {
				G2C_QST_QuestVisibleList visibleList = MessageFactory.getConcreteMessage(QuestActionEventDefines.G2C_QST_QuestVisibleList);
				visibleList.setNextQuestID(questComponent.getQuestManager().nextQuest().getQuestRef().getId());
				GameRoot.sendMessage(player.getIdentity(), visibleList);
				questComponent.checkIfHasAcceptNpc(quest);
			}
			return "acceptQuest.. OK";
		}
	};
	private static DebugCommand questCourse = new DebugCommand("questCourse <怪物名> <怪物数>", "完成当前主线任务", "示例1:questCourse monster_1 1") {

		@Override
		public String exec(Player player, CommandParameters parameters) {
			String refId = parameters.getString(1);
			int count = parameters.getInt(2);
			PlayerQuestComponent questComponent = player.getPlayerQuestComponent();
			Quest quest = questComponent.getQuestManager().getCrtQuest();
			if (quest.setQuestCourseNum(refId, count, player)) {
				if (quest.getQuestCourse().wasCompleted()) {
					quest.setQuestState(QuestState.SubmittableQuestState);
					G2C_QST_StateUpdate stateUpdate = MessageFactory.getConcreteMessage(QuestActionEventDefines.G2C_QST_StateUpdate);
					stateUpdate.setQuestId(quest.getQuestRef().getId());
					stateUpdate.setState(quest.getQuestState());
					GameRoot.sendMessage(player.getIdentity(), stateUpdate);
				} else {
					G2C_QST_QuestUpdate questUpdate = MessageFactory.getConcreteMessage(QuestActionEventDefines.G2C_QST_QuestUpdate);
					questUpdate.setQuest(quest);
					GameRoot.sendMessage(player.getIdentity(), questUpdate);
				}
			}

			MGDailyQuestComponent dailyQuestComponent = (MGDailyQuestComponent) player.getTagged(MGDailyQuestComponent.Tag);
			List<MGDailyQuest> questList = dailyQuestComponent.getDailyQuestManager().getCrtQuestList();
			int size = questList.size();
			for (int i = 0; i < size; i++) {
				MGDailyQuest dailyQuest = questList.get(i);
				if (dailyQuest.getQuestState() != QuestState.AcceptedQuestState) {
					continue;
				}
				if (dailyQuest.isAddToCourse(refId, count)) {
					if (dailyQuest.getDailyQuestCourse().wasCompleted()) {
						dailyQuest.setQuestState(QuestState.SubmittableQuestState);
						G2C_QST_DailyStateUpdate stateUpdate = MessageFactory.getConcreteMessage(DailyQuestActionEventDefines.G2C_QST_DailyStateUpdate);
						stateUpdate.setQuestId(dailyQuest.getDailyQuestRef().getId());
						stateUpdate.setState(dailyQuest.getQuestState());
						GameRoot.sendMessage(player.getIdentity(), stateUpdate);
					} else {
						G2C_QST_DailyQuestUpdate questUpdate = MessageFactory.getConcreteMessage(DailyQuestActionEventDefines.G2C_QST_DailyQuestUpdate);
						questUpdate.setQuest(dailyQuest);
						GameRoot.sendMessage(player.getIdentity(), questUpdate);
					}
				}
			}
			return "setQuest.. OK";
		}
	};
	private static DebugCommand openSlot = new DebugCommand("openslot <数量>", "打开锁定的背包格", "示例1:openslot 10") {

		@Override
		public String exec(Player player, CommandParameters parameters) {
			int number = parameters.getInt(1);
			PlayerItemBagComponent itemBagComponent = player.getItemBagComponent();
			ItemBag itemBag = itemBagComponent.getItemBag();
			if (number < 0) {
				number = 1;
			} else if (number > itemBag.getItemBagMaxCapacity() - itemBag.getItemBagCapacity()) {
				number = itemBag.getItemBagMaxCapacity() - itemBag.getItemBagCapacity();
			}
			itemBag.expendItemBagSlot(number);
			G2C_Bag_Capacity res = MessageFactory.getConcreteMessage(ItemBagEventDefines.G2C_Bag_Capacity);
			res.setItemBag(itemBag);
			GameRoot.sendMessage(player.getIdentity(), res);
			return "acceptQuest.. OK";
		}
	};
	private static DebugCommand upVip = new DebugCommand("upvip <等级>", "升级vip，等级分为 1 ,2 ,3", "示例1:upvip 1") {

		@Override
		public String exec(Player player, CommandParameters parameters) {
			int vipType = parameters.getInt(1);
			String vipRefId = "vip_1";
			switch (vipType) {
			case 1:
				vipRefId = "vip_1";
				break;
			case 2:
				vipRefId = "vip_2";
				break;
			case 3:
				vipRefId = "vip_3";
				break;
			default:
				vipType = 1;
				break;
			}
			MGPlayerVipComponent vipComponent = (MGPlayerVipComponent) player.getTagged(MGPlayerVipComponent.Tag);
			vipComponent.becameVip(vipRefId, (byte) vipType);
			return "acceptQuest.. OK";
		}
	};

	private static DebugCommand sortboard = new DebugCommand("sortboard <type>", "load type类型的排行榜数据，等级分为 1~7", "示例1:sortboard 1") {

		@Override
		public String exec(Player player, CommandParameters parameters) {
			int type = parameters.getInt(1);
			try {
				switch (type) {
				case 1:
					SortboardMgr.getInstance().newSortBoardDataByType(SortboardType.PlayerFightPower);
					break;
				case 2:
					SortboardMgr.getInstance().newSortBoardDataByType(SortboardType.PlayerLvl);
					break;
				case 3:
					SortboardMgr.getInstance().newSortBoardDataByType(SortboardType.PlayerMoney);
					break;
				case 4:
					SortboardMgr.getInstance().newSortBoardDataByType(SortboardType.PlayerMerit);
					break;
				case 5:
					SortboardMgr.getInstance().newSortBoardDataByType(SortboardType.PlayerWingLvl);
					break;
				case 6:
					SortboardMgr.getInstance().newSortBoardDataByType(SortboardType.MountLvl);
					break;
				case 7:
					SortboardMgr.getInstance().newSortBoardDataByType(SortboardType.TalismanLvl);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "load sortboard.. OK";
		}
	};

	private static DebugCommand chongzhi = new DebugCommand("chongzhi <数量>", "加货币 ", "示例1:chongzhi 1000 ") {

		@Override
		public String exec(Player player, CommandParameters parameters) {
			int gameUnBindedGold = parameters.getInt(1) < 0 ? 0 : parameters.getInt(1);
			OperatActivity a1 = OperatActivityMgr.simulateMap.get(OperatActivityType.FirstRechargeGift);
			OperatActivity a2 = OperatActivityMgr.simulateMap.get(OperatActivityType.TotalRechargeGift);
			OperatActivity a3 = OperatActivityMgr.simulateMap.get(OperatActivityType.EveryDayRechargeGift);
			MGPlayerQuickRechargeComponent quickRechargeComponent = (MGPlayerQuickRechargeComponent) player.getTagged(MGPlayerQuickRechargeComponent.Tag);
			int quickRechargeReward = quickRechargeComponent.getQuickRechargeReward(gameUnBindedGold);
			player.getPlayerMoneyComponent().addUnbindGold(gameUnBindedGold + quickRechargeReward, ItemOptSource.Debug);
			a1.modify(player);
			a2.modify(player, gameUnBindedGold);
			a3.modify(player);
			logger.debug("chongzhi 元宝:" + gameUnBindedGold);

			return "chongzhi.. OK";
		}
	};

	private static DebugCommand transfer = new DebugCommand("switch <场景Id> <X坐标> <Y坐标>", "传送到指定场景指定坐标", "示例1:switch S001 100 100") {

		@Override
		public String exec(Player player, CommandParameters parameters) {
			String sceneRefId = parameters.getString(1);
			int x = parameters.getInt(2);
			int y = parameters.getInt(3);
			AbstractGameSceneRef dstRef = (AbstractGameSceneRef) GameRoot.getGameRefObjectManager().getManagedObject(sceneRefId);
			if (dstRef == null) {
				return "transfer.. invalid sceneRefId";
			}

			// 非法的坐标
			if (!dstRef.getTerrainLayer().isInMatrixRange(x, y)) {
				return "transfer.. invalid coordinate";
			}

			// 阻挡点
			SceneGrid dstGrid = dstRef.getTerrainLayer().getSceneGrid(y, x);
			if (dstGrid.isBlocked()) {
				return "transfer.. blocked coordinate";
			}

			player.getPlayerSceneComponent().switchTo(sceneRefId, x, y);
			return "transfer.. OK";
		};

	};

	private static DebugCommand kingCityCreater = new DebugCommand("kingCityCreater", "变为王城公会会长", "示例1:kingCityCreater") {
		@Override
		public String exec(Player player, CommandParameters parameters) {
			String unionName = MGPropertyAccesser.getUnionName(player.getProperty());
			if (unionName == null) {
				if (logger.isDebugEnabled()) {
					logger.debug("debug命令错误，玩家非公会成员");
				}
				return null;
			}

			MGUnionMgr unionMgr = MorningGloryContext.getUnionSystemComponent().getUnionMgr();

			MGUnion union = unionMgr.getUnion(unionName);
			MGUnionMember creater = union.getCreater();

			if (!player.getName().equals(creater.getPlayerName())) {
				creater.setUnionOfficialId(MGUnionConstant.Common);
				MGUnionMember member = union.getMemberMgr().getMemberByPlayer(player);
				union.setCreater(member);
				member.setUnionOfficialId(MGUnionConstant.Chairman);

			}
			MGUnionHelper.changeKingCityUnion(union);
			MGUnionSaver.getInstance().saveImmediateData(union);
			return "kingcityChange Success";
		}

	};

	private static DebugCommand deleteKingCity = new DebugCommand("deleteKingCity", "将自己的公会从王城改变为普通公会", "示例1:deleteKingCity") {
		@Override
		public String exec(Player player, CommandParameters parameters) {
			MGUnionMgr unionMgr = MorningGloryContext.getUnionSystemComponent().getUnionMgr();
			String unionName = MGPropertyAccesser.getUnionName(player.getProperty());
			if (unionName == null) {
				if (logger.isDebugEnabled()) {
					logger.debug("debug命令错误，玩家非公会成员");
				}
				return null;
			}

			MGUnion union = unionMgr.getUnion(unionName);
			if (union.getKingCityType() == MGUnionConstant.Is_KingCity) {
				MGUnionHelper.changeKingCityUnionMember(union, MGUnionConstant.Not_KingCity);
			}
			return "deleteKingCity Success";
		}

	};

	private static DebugCommand clearUnionSignupList = new DebugCommand("clearUnionSignupList", "将报名参加公会战的公会列表清空", "示例1:clearUnionSignupList") {
		@Override
		public String exec(Player player, CommandParameters parameters) {
			CastleWarApplyMgr.getInstance().clearSignupWarUnions();
			return "deleteKingCity Success";
		}

	};

	private static DebugCommand castleWarPerStart = new DebugCommand("castleWarPerStart", "攻城战预开始", "示例1:castleWarPerStart") {
		@Override
		public String exec(Player player, CommandParameters parameters) {
			CastleWarMgr castleWarMgr = (CastleWarMgr) SceneActivityMgr.getInstance().getSceneAcitityBySceneRefId("S012");
			castleWarMgr.onPreStart();
			castleWarMgr.setPreActivityState((byte) 3);
			return "castleWarPerStart Success";
		}

	};

	private static DebugCommand castleWarStart = new DebugCommand("castleWarStart", "攻城战开始", "示例1:castleWarStart") {
		@Override
		public String exec(Player player, CommandParameters parameters) {
			CastleWarMgr castleWarMgr = (CastleWarMgr) SceneActivityMgr.getInstance().getSceneAcitityBySceneRefId("S012");
			castleWarMgr.onStart();
			castleWarMgr.setCrtActivityState((byte) 2);
			castleWarMgr.setPreActivityState((byte) 5);
			return "castleWarStart Success";
		}

	};

	private static DebugCommand castleWarEnd = new DebugCommand("castleWarEnd", "攻城战结束", "示例1:castleWarEnd") {
		@Override
		public String exec(Player player, CommandParameters parameters) {
			CastleWarMgr castleWarMgr = (CastleWarMgr) SceneActivityMgr.getInstance().getSceneAcitityBySceneRefId("S012");
			castleWarMgr.onEnd();
			castleWarMgr.setCrtActivityState((byte) 1);
			castleWarMgr.setPreActivityState((byte) 5);
			return "castleWarStart Success";
		}

	};

	private static DebugCommand teamBossPerStart = new DebugCommand("teamBossPerStart <地图refId>", "组队BOSS预开始", "示例1:teamBossPerStart <地图refId>") {
		@Override
		public String exec(Player player, CommandParameters parameters) {
			String sceneRefId = parameters.getString(1);
			TeamBossMgr teamBoss = (TeamBossMgr) SceneActivityMgr.getInstance().getSceneAcitityBySceneRefId(sceneRefId);
			teamBoss.onPreStart();
			teamBoss.setPreActivityState((byte) 3);
			return "teamBossPerStart Success";
		}

	};

	private static DebugCommand teamBossStart = new DebugCommand("teamBossStart <地图refId>", "组队BOSS开始", "示例1:teamBossStart <地图refId>") {
		@Override
		public String exec(Player player, CommandParameters parameters) {
			String sceneRefId = parameters.getString(1);
			TeamBossMgr teamBoss = (TeamBossMgr) SceneActivityMgr.getInstance().getSceneAcitityBySceneRefId(sceneRefId);
			teamBoss.onStart();
			teamBoss.setCrtActivityState((byte) 2);
			teamBoss.setPreActivityState((byte) 5);
			return "teamBossStart Success";
		}

	};

	private static DebugCommand teamBossEnd = new DebugCommand("teamBossEnd <地图refId>", "组队BOSS结束", "示例1:teamBossEnd <地图refId>") {
		@Override
		public String exec(Player player, CommandParameters parameters) {
			String sceneRefId = parameters.getString(1);
			TeamBossMgr teamBoss = (TeamBossMgr) SceneActivityMgr.getInstance().getSceneAcitityBySceneRefId(sceneRefId);
			teamBoss.onEnd();
			teamBoss.setCrtActivityState((byte) 1);
			teamBoss.setPreActivityState((byte) 5);
			return "teamBossEnd Success";
		}

	};

	// private static DebugCommand setPlayerWingLevel = new
	// DebugCommand("setPlayerWingLevel <翅膀等级>", "修改玩家翅膀等级",
	// "示例1:setPlayerWingLevel 1") {
	// @Override
	// public String exec(Player player, CommandParameters parameters) {
	// int wingLevel = parameters.getInt(1);
	// if (wingLevel <= 0 || wingLevel > 6) {
	// return "level : 0  ~  6 ";
	// }
	// String wingRefId = "wing_"+wingLevel;
	// MGPlayerWingComponent wingComponent = (MGPlayerWingComponent)
	// player.getTagged(MGPlayerWingComponent.Tag);
	// MGPlayerWing wing = wingComponent.getPlayerWing();
	// MGPlayerWingRef crtWingRef = wing.getPlayerWingRef();
	// MGWingEffectMgr wingEffectMgr = new MGWingEffectMgr(player);
	// if (crtWingRef == null) {
	// crtWingRef =
	// (MGPlayerWingRef)GameRoot.getGameRefObjectManager().getManagedObject("wing_1");
	// } else{
	// wingEffectMgr.detachAndSnapshot(wing);
	// wing.broadcastProperty(player);
	// }
	//
	// MGPlayerWingRef playerWingRef =
	// (MGPlayerWingRef)GameRoot.getGameRefObjectManager().getManagedObject(wingRefId);
	// if(playerWingRef == null){
	// wingEffectMgr.attach(wing);
	// wing.broadcastProperty(player);
	// return "setPlayerWingLevel succeed";
	// }
	// wing.setPlayerWingRef(playerWingRef);
	// wingEffectMgr.attach(wing);
	// wing.broadcastProperty(player);
	//
	// if (player.isOnline()) {
	// G2C_Wing_RequestNowWing requestNowWing =
	// MessageFactory.getConcreteMessage(WingEventDefines.G2C_Wing_RequestNowWing);
	// requestNowWing.setWing(wing.getPlayerWingRef().getId());
	// GameRoot.sendMessage(player.getIdentity(), requestNowWing);
	//
	// wingComponent.sendGameEventMessage(StatWing.LevelUp);
	// }
	//
	// return "setPlayerWingLevel succeed";
	// }
	//
	// };

	private static DebugCommand miningStart = new DebugCommand("miningStart", "挖矿活动开始", "示例1：miningStart") {

		@Override
		public String exec(Player player, CommandParameters parameters) {
			MGMiningActivity miningActivity = (MGMiningActivity) SceneActivityMgr.getInstance().getSceneAcitityBySceneRefId("S217");
			miningActivity.onStart();
			miningActivity.setCrtActivityState((byte) 2);
			miningActivity.setPreActivityState((byte) 5);
			return "miningStart Success";
		}
	};

	private static DebugCommand miningEnd = new DebugCommand("miningEnd", "挖矿活动结束", "示例1：miningEnd") {

		@Override
		public String exec(Player player, CommandParameters parameters) {
			MGMiningActivity miningActivity = (MGMiningActivity) SceneActivityMgr.getInstance().getSceneAcitityBySceneRefId("S217");
			miningActivity.onEnd();
			miningActivity.setCrtActivityState((byte) 1);
			miningActivity.setPreActivityState((byte) 5);
			return "miningEnd Success";
		}
	};

	private static DebugCommand monsterIntrusionStart = new DebugCommand("monsterIntrusionStart", "怪物入侵活动开始", "示例1：monsterIntrusionStart") {

		@Override
		public String exec(Player player, CommandParameters parameters) {
			MonsterInvasionMgr monsterIntrusionMgr = (MonsterInvasionMgr) SceneActivityMgr.getInstance().getSceneAcitityBySceneRefId("S218");
			monsterIntrusionMgr.onStart();
			monsterIntrusionMgr.setCrtActivityState((byte) 2);
			monsterIntrusionMgr.setPreActivityState((byte) 5);
			return "monsterIntrusionStart Success";
		}
	};

	private static DebugCommand monsterIntrusionEnd = new DebugCommand("monsterIntrusionEnd", "怪物入侵活动结束", "示例1：monsterIntrusionEnd") {

		@Override
		public String exec(Player player, CommandParameters parameters) {
			MonsterInvasionMgr monsterIntrusionMgr = (MonsterInvasionMgr) SceneActivityMgr.getInstance().getSceneAcitityBySceneRefId("S218");
			monsterIntrusionMgr.onEnd();
			monsterIntrusionMgr.setCrtActivityState((byte) 1);
			monsterIntrusionMgr.setPreActivityState((byte) 5);
			return "monsterIntrusionEnd Success";
		}
	};

	private static DebugCommand dropItem = new DebugCommand("dropItem", "掉落物品", "示例1：dropItem itemRefId number") {

		@Override
		public String exec(Player player, CommandParameters parameters) {
			String itemRefId = parameters.getString(1);
			int number = parameters.getInt(2);
			ItemPair itemPair = new ItemPair(itemRefId, number, false);
			GameScene crtScene = player.getCrtScene();
			Position crtPosition = player.getCrtPosition();
			List<SceneGrid> gridList = GameSceneHelper.getLootSceneGrids(crtScene, crtPosition, 1);
			LootMgrComponent lootManager = crtScene.getLootMgrComponent();
			Loot loot = lootManager.createLoot(itemPair);
			loot.setOwnerId(player.getId());
			SceneGrid sceneGrid = gridList.get(0);
			lootManager.enterWorld(loot, crtScene, sceneGrid.getColumn(), sceneGrid.getRow());
			return "dropItem Success";
		}
	};

	private static DebugCommand dailyQusetRing = new DebugCommand("dailyQusetRing <日常任务Id> <修改的环数>", "修改指定日常任务环数", "示例1:dailyQusetRing quest_daily_1 4") {

		@Override
		public String exec(Player player, CommandParameters parameters) {
			String questId = parameters.getString(1);
			int ringCount = parameters.getInt(2);
			ringCount = ringCount - 1 > 0 ? (ringCount - 1) : 0;
			MGDailyQuestComponent dailyQuestComponent = (MGDailyQuestComponent) player.getTagged(MGDailyQuestComponent.Tag);
			List<MGDailyQuest> questList = dailyQuestComponent.getDailyQuestManager().getCrtQuestList();
			int size = questList.size();
			for (int i = 0; i < size; i++) {
				MGDailyQuest dailyQuest = questList.get(i);
				String dailyQuestId = dailyQuest.getDailyQuestRef().getId();
				if (!StringUtils.equals(dailyQuestId, questId)) {
					continue;
				}
				dailyQuest.setNowTime(ringCount);
			}
			dailyQuestComponent.sendDailyQuestList();
			return "dailyQusetRing Success";
		}
	};

	private static DebugCommand applyplayeratt = new DebugCommand("applyplayeratt 对应属性  修改值", " 修改玩家战斗属性值", "示例1:applyplayeratt 1 5000") {
		@Override
		public String exec(Player player, CommandParameters parameters) {
			int property = parameters.getInt(1);
			int value = parameters.getInt(2);
			short id = 0;
			switch (property) {
			case 1:
				id = MGPropertySymbolDefines.MaxHP_Id;
				break;
			case 2:
				id = MGPropertySymbolDefines.MaxMP_Id;
				break;
			case 3:
				id = MGPropertySymbolDefines.MinPAtk_Id;
				break;
			case 4:
				id = MGPropertySymbolDefines.MaxPAtk_Id;
				break;
			case 5:
				id = MGPropertySymbolDefines.MinMAtk_Id;
				break;

			case 6:
				id = MGPropertySymbolDefines.MaxMAtk_Id;
				break;
			case 7:
				id = MGPropertySymbolDefines.MinTao_Id;
				break;
			case 8:
				id = MGPropertySymbolDefines.MaxTao_Id;
				break;
			case 9:
				id = MGPropertySymbolDefines.MinPDef_Id;
				break;
			case 10:
				id = MGPropertySymbolDefines.MaxPDef_Id;
				break;

			case 11:
				id = MGPropertySymbolDefines.MinMDef_Id;
				break;
			case 12:
				id = MGPropertySymbolDefines.MaxMDef_Id;
				break;
			case 13:
				id = MGPropertySymbolDefines.Fortune_Id;
				break;
			case 14:
				id = MGPropertySymbolDefines.Hit_Id;
				break;
			case 15:
				id = MGPropertySymbolDefines.Dodge_Id;
				break;

			case 16:
				id = MGPropertySymbolDefines.Crit_Id;
				break;
			case 17:
				id = MGPropertySymbolDefines.CritInjure_Id;
				break;
			case 18:
				id = MGPropertySymbolDefines.PImmunityPer_Id;
				break;
			case 19:
				id = MGPropertySymbolDefines.MImmunityPer_Id;
				break;
			case 20:
				id = MGPropertySymbolDefines.IgnorePDef_Id;
				break;
			case 21:
				id = MGPropertySymbolDefines.IgnoreMDef_Id;
				break;
			case 22:
				id = MGPropertySymbolDefines.AtkSpeed_Id;
				break;
			case 23:
				id = MGPropertySymbolDefines.MoveSpeed_Id;
				break;
			default:
				return "failed";
			}
			if (id == 0) {
				return "failed";
			}
			player.getFightPropertyMgrComponent().getFightPropertyMgr().setSnapshotValueById(id, value);
			PropertyDictionary pd = new PropertyDictionary(1);
			pd.setOrPutValue(id, value);
			player.notifyPorperty(pd);
			return "deleteKingCity Success";
		}

	};

	private static DebugCommand clearInstanceRecord = new DebugCommand("clearInstanceRecord InstanceRefId 值(1清除当天记录  2清除本周记录)", " 修改玩家副本次数", "clearInstanceRecord Ins_1 1") {
		@Override
		public String exec(Player player, CommandParameters parameters) {
			String gameInstanceRefId = parameters.getString(1);
			int type = parameters.getInt(2);

			MGPlayerGameInstanceRecordMgr r = MorningGloryContext.getGameInstanceSystemComponent().getGameInstanceMgr().getScheduleManager().getMyRecord(player);
			if (r != null) {
				MGPlayerGameInstanceRecord gameInstanceRecord = r.getRecord(gameInstanceRefId);
				if (gameInstanceRecord != null) {
					CountRecord countRecord = gameInstanceRecord.getCountRecord();
					if (countRecord != null) {
						if (type == 1) {
							countRecord.clearTimesInDayNoLimit();
						} else if (type == 2) {
							countRecord.clearTimesInThisWeekNoLimit();
						} else {
							return "failed";
						}
					}
				}
			}
			return "clearInstanceRecord Success";
		}
	};
	private static DebugCommand changeLevel = new DebugCommand("changeLevel level", " 修改玩家等级", "changelevel 50") {
		@Override
		public String exec(Player player, CommandParameters parameters) {
			int newlevel = parameters.getInt(1);
			player.getExpComponent().setLevelFromDebug(newlevel);
			return "clearInstanceRecord Success";
		}
	};

	private static DebugCommand kingCity = new DebugCommand("kingCity unionName", "设置该公会为王城公会", "kingCity hello") {
		@Override
		public String exec(Player player, CommandParameters parameters) {
			String unionName = parameters.getString(1);
			MGUnionMgr unionMgr = MorningGloryContext.getUnionSystemComponent().getUnionMgr();
			MGUnion union = unionMgr.getUnion(unionName);
			if (union == null) {
				return "kingCity fail";
			}

			MGUnionHelper.changeKingCityUnion(union);

			return "kingCity Success";
		}
	};

	private static DebugCommand pluck = new DebugCommand("pluck pluckId", "完成采集", "pluck pluckId") {
		@Override
		public String exec(Player player, CommandParameters parameters) {
			String pluckId = parameters.getString(1);

			PluckMgrComponent pluckMgrComponent = player.getCrtScene().getPluckMgrComponent();
			Pluck pluck = pluckMgrComponent.getPluck(pluckId);

			if (pluck == null) {
				if (logger.isDebugEnabled()) {
					logger.debug("pluckId = " + pluckId + " is not exist in Scene: sceneRefId = " + player.getCrtScene().getRef().getId());
				}
				return "pluck fail";
			}

			pluck.successPluck(player);

			return "pluck Success";
		}
	};

	private static DebugCommand applyPlayerState = new DebugCommand("applyPlayerState <玩家状态值>", "修改玩家到指定状态", "实例 1 : applyPlayerState 1 ") {

		@Override
		public String exec(Player player, CommandParameters parameters) {
			int state = parameters.getInt(1);
			short id = 0;
			switch (state) {
			case 1:
				id = FightState.FightState_Id;
				break;
			case 2:
				id = TeamState.TeamState_Id;
				break;
			case 8:
				id = PKState.PKState_Id;
				break;

			case 101:
				id = StopState.StopState_Id;
				break;
			case 102:
				id = MoveState.MoveState_Id;
				break;
			case 103:
				id = PatrolState.PatrolState_Id;
				break;
			case 104:
				id = ReturnToBirthState.ReturnToBirthState_Id;
				break;
			case 105:
				id = ChaseState.ChaseState_Id;
				break;

			case 201:
				id = StandedState.StandedState_Id;
				break;
			case 202:
				id = WalkState.WalkState_Id;
				break;
			case 251:
				id = MountedState.MountedState_Id;
				break;

			case 301:
				id = IdleState.IdleState_Id;
				break;
			case 306:
				id = PluckingState.PluckingState_Id;
				break;
			case 307:
				id = DeadState.DeadState_Id;
				break;

			case 501:
				id = BleedState.BleedState_Id;
				break;
			case 502:
				id = BurningState.BurningState_Id;
				break;
			case 503:
				id = DizzinessState.DizzinessState_Id;
				break;
			case 504:
				id = DumbingState.DumbingState_Id;
				break;
			case 505:
				id = InvincibleState.InvincibleState_Id;
				break;
			case 506:
				id = MagicImmunityState.MagicImmunityState_Id;
				break;
			case 507:
				id = ParalysisState.ParalysisState_Id;
				break;
			case 508:
				id = PhysicalImmunityState.PhysicalImmunityState_Id;
				break;
			case 509:
				id = PoisoningState.PoisoningState_Id;
				break;
			case 510:
				id = SlowDownState.SlowDownState_Id;
				break;
			case 511:
				id = StealthState.StealthState_Id;
				break;
			case 512:
				id = MagicShieldState.MagicShieldState_Id;
				break;
			default:
				return "failed";
			}

			if (id == 0) {
				return "failed";
			}
			if (id == 104) {
				player.changeState(ReturnToBirthState.ReturnToBirthState_Id);
				player.changeState(StandedState.StandedState_Id);
				player.changeState(IdleState.IdleState_Id);

				player.cancelState(FightState.FightState_Id);
				player.cancelState(TeamState.TeamState_Id);
				player.cancelState(PKState.PKState_Id);
				player.cancelState(StopState.StopState_Id);
				player.cancelState(MoveState.MoveState_Id);
				player.cancelState(PatrolState.PatrolState_Id);
				player.cancelState(ChaseState.ChaseState_Id);
				player.cancelState(WalkState.WalkState_Id);
				player.cancelState(MountedState.MountedState_Id);
				player.cancelState(DeadState.DeadState_Id);
				player.cancelState(PluckingState.PluckingState_Id);
				player.cancelState(BleedState.BleedState_Id);
				player.cancelState(BurningState.BurningState_Id);
				player.cancelState(DizzinessState.DizzinessState_Id);
				player.cancelState(DumbingState.DumbingState_Id);
				player.cancelState(InvincibleState.InvincibleState_Id);
				player.cancelState(MagicImmunityState.MagicImmunityState_Id);
				player.cancelState(ParalysisState.ParalysisState_Id);
				player.cancelState(PhysicalImmunityState.PhysicalImmunityState_Id);
				player.cancelState(PoisoningState.PoisoningState_Id);
				player.cancelState(SlowDownState.SlowDownState_Id);
				player.cancelState(StealthState.StealthState_Id);
				player.cancelState(MagicShieldState.MagicShieldState_Id);
				return "applyPlayerState Success";
			}

			if (player.changeState(id) == true) {
				return "applyPlayerState Success";
			} else {
				return "failed";
			}

		}
	};

	static {
		addDebugCommand(help);
		addDebugCommand(addExp);
		addDebugCommand(addHp);
		addDebugCommand(addMp);
		addDebugCommand(addItem);
		addDebugCommand(addMoney);
		addDebugCommand(sendMail);
		addDebugCommand(addMerit);
		addDebugCommand(addAchieve);
		addDebugCommand(addState);
		addDebugCommand(acceptQuest);
		addDebugCommand(questCourse);
		addDebugCommand(openSlot);
		addDebugCommand(upVip);
		addDebugCommand(transfer);
		addDebugCommand(sortboard);
		addDebugCommand(chongzhi);
		addDebugCommand(kingCityCreater);
		addDebugCommand(deleteKingCity);
		addDebugCommand(applyplayeratt);
		addDebugCommand(mbt);
		addDebugCommand(clearUnionSignupList);
		addDebugCommand(castleWarPerStart);
		addDebugCommand(castleWarStart);
		addDebugCommand(castleWarEnd);
		addDebugCommand(miningStart);
		// addDebugCommand(setPlayerWingLevel);
		addDebugCommand(miningEnd);
		addDebugCommand(monsterIntrusionStart);
		addDebugCommand(monsterIntrusionEnd);
		addDebugCommand(dropItem);
		addDebugCommand(clearInstanceRecord);
		addDebugCommand(dailyQusetRing);
		addDebugCommand(applyPlayerState);
		addDebugCommand(changeLevel);
		addDebugCommand(teamBossPerStart);
		addDebugCommand(teamBossStart);
		addDebugCommand(teamBossEnd);
		addDebugCommand(kingCity);
		addDebugCommand(pluck);
		addDebugCommand(completeQuest);
		addDebugCommand(addAcution);
	}

	public static void main(String[] args) {
		DebugCommand debugCommand = DebugComponent.getDebugCommand("help");
		System.out.println(debugCommand.exec(null, null));
	}
}
