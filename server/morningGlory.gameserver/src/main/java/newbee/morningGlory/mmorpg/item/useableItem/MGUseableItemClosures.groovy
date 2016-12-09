


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
package newbee.morningGlory.mmorpg.item.useableItem


import java.text.ParseException
import java.text.SimpleDateFormat

import newbee.morningGlory.mmorpg.operatActivities.OperatActivityComponent
import newbee.morningGlory.mmorpg.operatActivities.OperatActivityRecord
import newbee.morningGlory.mmorpg.operatActivities.impl.FirstRechargeGift
import newbee.morningGlory.mmorpg.player.achievement.MGPlayerAchievementComponent
import newbee.morningGlory.mmorpg.player.itemBag.gift.MGGiftBagConfig
import newbee.morningGlory.mmorpg.player.itemBag.gift.MGGiftRef
import newbee.morningGlory.mmorpg.player.peerage.MGPlayerPeerageComponent
import newbee.morningGlory.mmorpg.player.pk.MGPlayerPKComponent
import newbee.morningGlory.mmorpg.player.wing.MGPlayerWingComponent
import newbee.morningGlory.mmorpg.player.wing.wingModule.WingManager
import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuff
import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuffComponent
import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuffRef
import newbee.morningGlory.mmorpg.vip.MGPlayerVipComponent
import newbee.morningGlory.stat.MGStatFunctions
import newbee.morningGlory.stat.logs.AchievePointStat
import newbee.morningGlory.stat.logs.MeritPointStat

import org.apache.commons.lang3.StringUtils
import org.apache.log4j.Logger

import sophia.foundation.communication.core.MessageFactory
import sophia.foundation.property.PropertyDictionary
import sophia.game.GameRoot
import sophia.game.component.communication.GameEvent
import sophia.game.ref.GameRefObject
import sophia.mmorpg.base.scene.GameSceneHelper
import sophia.mmorpg.base.scene.grid.SceneGrid
import sophia.mmorpg.base.sprite.fightProperty.FightEffectProperty
import sophia.mmorpg.base.sprite.fightSkill.FightSkill
import sophia.mmorpg.code.MMORPGErrorCode
import sophia.mmorpg.core.CDMgr
import sophia.mmorpg.item.Item
import sophia.mmorpg.item.ref.ItemRef
import sophia.mmorpg.player.Player
import sophia.mmorpg.player.chat.sysytem.SpecialEffectsType
import sophia.mmorpg.player.chat.sysytem.SystemPromptFacade
import sophia.mmorpg.player.chat.sysytem.SystemPromptPosition
import sophia.mmorpg.player.equipment.PlayerEquipBodyArea
import sophia.mmorpg.player.equipment.event.EquipmentEventDefines
import sophia.mmorpg.player.equipment.event.G2C_Equip_Update
import sophia.mmorpg.player.fightSkill.PlayerFightSkillTree
import sophia.mmorpg.player.fightSkill.event.G2C_AddSkillExp
import sophia.mmorpg.player.fightSkill.ref.SkillRef
import sophia.mmorpg.player.gameEvent.ChineseModeQuest_GE
import sophia.mmorpg.player.itemBag.ItemCode
import sophia.mmorpg.player.itemBag.ItemFacade
import sophia.mmorpg.player.itemBag.ItemLuckDataPair
import sophia.mmorpg.player.itemBag.ItemOptSource
import sophia.mmorpg.player.itemBag.ItemPair
import sophia.mmorpg.player.itemBag.ItemSomeConfigData
import sophia.mmorpg.player.mount.PlayerMountFacade
import sophia.mmorpg.player.quest.Quest
import sophia.mmorpg.player.quest.ref.order.ChineseModeStringQuestRefOrderItem
import sophia.mmorpg.player.quest.ref.order.QuestChineseOrderDefines
import sophia.mmorpg.player.quest.ref.order.QuestRefOrderItem
import sophia.mmorpg.player.quest.ref.order.QuestRefOrderType
import sophia.mmorpg.player.ref.PlayerProfessionLevelData
import sophia.mmorpg.player.ref.PlayerProfessionRef
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser
import sophia.mmorpg.utils.RuntimeResult
import sophia.mmorpg.utils.SFRandomUtils


class MGUseableItemClosures {

	private static final Logger logger = Logger.getLogger(MGUseableItemClosures.class);
	private static final String ChineseModeQuest_GE_Id = ChineseModeQuest_GE.class.getSimpleName();
	CDMgr redCDMgr = new CDMgr(500);
	MGUseableItemClosures(){
		map.put("modify_Player_HpOrMp", modify_Player_HpOrMp);
		map.put("add_Player_merit", add_Player_merit);
		map.put("add_Player_achievement", add_Player_achievement);
		map.put("modify_Weapon_Property", modify_Weapon_Property);
		map.put("modify_Player_PK", modify_Player_PK);
		map.put("add_Player_money", add_Player_money);
		map.put("add_Player_Exp", add_Player_Exp);
		map.put("add_Player_MaxExp", add_Player_MaxExp);
		map.put("add_Player_ExpBuff", add_Player_ExpBuff);
		map.put("add_Fight_buffer", add_Fight_buffer);
		map.put("add_Player_MpOrHpBuff", add_Player_MpOrHpBuff);
		map.put("learn_skill", learn_skill);
		map.put("transfer_Player_position", transfer_Player_position);
		map.put("open_gift_closure", open_gift_closure);
		map.put("add_Mount_Exp", add_Mount_Exp);
		map.put("add_Wing_Exp", add_Wing_Exp);
		map.put("lazy_gift1_closure", lazy_gift1_closure);
		map.put("lazy_gift2_closure", lazy_gift2_closure);
		map.put("first_gift_closure", first_gift_closure);
		map.put("shengji_vip_closure", shengji_vip_closure);
		map.put("common_Closure", common_Closure);
		map.put("add_skill_exp", add_skill_exp);
	}
	Map<String, Closure<RuntimeResult>> map = new HashMap<>();

	/**
	 * 加血加蓝
	 */
	Closure<RuntimeResult> modify_Player_HpOrMp = { Player player, Item item,int number ->
		if(logger.isDebugEnabled()){
			logger.debug("加蓝加血");
		}

		RuntimeResult result = RuntimeResult.OK();
		if(player.isDead()){
			return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_ITEM_PLAYER_DEAD)
		}
		int hp = MGPropertyAccesser.getHP(item.getItemRef().getEffectProperty())*number;
		int mp = MGPropertyAccesser.getMP(item.getItemRef().getEffectProperty())*number;
		int crtHP = player.getHP();
		int crtMP = player.getMP();
		int maxHP = player.getHPMax();
		int maxMP = player.getMPMax();
		if(hp > 0 && mp > 0){

			if(crtHP >= maxHP && crtMP >= maxMP) {
				return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_ITEM_USE_MPHP_FULL);
			}
			player.applyHP(player, hp);
			player.applyMP(mp);
		}else if(hp > 0 && mp <= 0){

			if(crtHP == maxHP){
				return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_ITEM_USE_HP_FULL);
			}
			if(!player.applyHP(player, hp)){
				result = RuntimeResult.RuntimeError();
			}
		}else if(mp > 0 && hp <= 0){
			if(crtMP == maxMP){
				return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_ITEM_USE_MP_FULL);
			}
			if(!player.applyMP(mp)){
				result = RuntimeResult.RuntimeError();
			}
		}else{
			result = RuntimeResult.RuntimeError();
		}
		return result;
	}

	/**
	 * 增加功勋
	 */
	Closure<RuntimeResult> add_Player_merit = { Player player, Item item,int number ->
		RuntimeResult result = RuntimeResult.OK();
		if(logger.isDebugEnabled()){
			logger.debug("增加成就，功勋，修为，荣誉");
		}
		PropertyDictionary effectProperty = item.getItemRef().getEffectProperty();

		int merit = MGPropertyAccesser.getMerit(effectProperty) * number;
		MGPlayerPeerageComponent peerageComponent = (MGPlayerPeerageComponent) player.getTagged(MGPlayerPeerageComponent.Tag);
		peerageComponent.getMeritManager().addMerit(merit);
		MGStatFunctions.meritPointStat(player, MeritPointStat.Add, MeritPointStat.MeritToken, merit);
		return result;
	}
	/**
	 * 增加成就
	 */
	Closure<RuntimeResult> add_Player_achievement = { Player player, Item item,int number ->
		RuntimeResult result = RuntimeResult.OK();
		if(logger.isDebugEnabled()){
			logger.debug("增加成就，功勋，修为，荣誉");
		}
		PropertyDictionary effectProperty = item.getItemRef().getEffectProperty();
		int achievement = MGPropertyAccesser.getAchievement(effectProperty) * number ;
		MGPlayerAchievementComponent achievementComponent = (MGPlayerAchievementComponent)player.getTagged(MGPlayerAchievementComponent.Tag);
		achievementComponent.getAchievePointMgr().addAchievePoint(achievement);
		MGStatFunctions.achievePointStat(player, AchievePointStat.Add, AchievePointStat.AchieveToken, achievement);
		return result;
	}

	/**
	 * 增加荣誉
	 */
	Closure<RuntimeResult> add_Player_honor = { Player player, Item item,int number ->
		RuntimeResult result = RuntimeResult.OK();
		if(logger.isDebugEnabled()){
			logger.debug("增加成就，功勋，修为，荣誉");
		}


		return result;
	}


	/**
	 * 增加修为
	 */
	Closure<RuntimeResult> add_Player_cultivation = { Player player, Item item,int number ->
		RuntimeResult result = RuntimeResult.OK();
		if(logger.isDebugEnabled()){
			logger.debug("增加成就，功勋，修为，荣誉");
		}

		return result;
	}

	/**
	 * 增加武器幸运
	 */
	Closure<RuntimeResult> modify_Weapon_Property = { Player player, Item item,int number ->
		RuntimeResult result =  RuntimeResult.OK();
		if(logger.isDebugEnabled()){
			logger.debug("增加武器幸运");
		}
		int fortune = 0;
		Item weapon = player.getPlayerEquipBodyConponent().getPlayerBody().getBodyArea(PlayerEquipBodyArea.weaponBodyId).getEquipment();
		if(weapon == null){
			return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_ITEM_WEAPON_EMPTY);
		}
		int luckFortune = MGPropertyAccesser.getFortune(weapon.getNewAttachPropertyIfNull());	//祝福油所加总幸运
		int weaponFortune = MGPropertyAccesser.getFortune(weapon.getProperty());		//武器总幸运
		int luckAddedFuntune = 0; // 本次祝福油增加的幸运
		luckFortune = luckFortune < 0 ? 0 :luckFortune;
		weaponFortune = weaponFortune < 0 ? 0 : weaponFortune;
		if(luckFortune > weaponFortune){
			MGPropertyAccesser.setOrPutFortune(weapon.getProperty(),luckFortune);
		}
		if(luckFortune >= 7 || weaponFortune >=9){
			return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_ITEM_LUCK_FULL);
		}
		PropertyDictionary propertyDictionary = null;
		String itemRefId = item.getItemRefId();
		if(StringUtils.equals("item_luck",itemRefId)){
			//普通祝福油
			int addFortune = 0 ;
			if(luckFortune < 7){
				ItemLuckDataPair pair = ItemSomeConfigData.itemLuckDataMap.get(luckFortune);
				int upRate = pair.getSprobability();
				int stayRate = pair.getNprobability();
				int downRate = pair.getFprobability();
				int sRate = upRate + stayRate;
				int random = SFRandomUtils.random100();
				if(random <= upRate){
					addFortune = addFortune + 1;
				}else if(random > sRate){
					addFortune = addFortune - 1;
				}
			}
			fortune = addFortune;
		}else{									//超级祝福油
			fortune = MGPropertyAccesser.getFortune(item.getItemRef().getEffectProperty());
			fortune = fortune < 0 ? 0 :fortune;
		}
		player.getPlayerEquipBodyConponent().getEquipEffectMgr().detachAndSnapshot(weapon);

		if(weapon.isNonPropertyItem()){
			int baseFortune = MGPropertyAccesser.getFortune(weapon.getItemRef().getEffectProperty()) < 0 ? 0 :MGPropertyAccesser.getFortune(weapon.getItemRef().getEffectProperty());
			baseFortune += fortune;
			weaponFortune = baseFortune > 9 ? 9 :baseFortune;
			luckFortune = fortune > 7 ? 7 : fortune;
			luckAddedFuntune = luckFortune;
			weapon.changePropertyItem();
			MGPropertyAccesser.setOrPutFortune(weapon.getProperty(),weaponFortune);
			MGPropertyAccesser.setOrPutFortune(weapon.getNewAttachPropertyIfNull(),luckFortune);

		}else{
			int realLuckFortune = luckFortune + fortune;
			realLuckFortune = realLuckFortune > 7 ? 7 :realLuckFortune;
			luckAddedFuntune = realLuckFortune - luckFortune;
			weaponFortune = weaponFortune + luckAddedFuntune;
			weaponFortune = weaponFortune > 9 ? 9 :weaponFortune;
			MGPropertyAccesser.setOrPutFortune(weapon.getProperty(),weaponFortune);
			MGPropertyAccesser.setOrPutFortune(weapon.getNewAttachPropertyIfNull(),realLuckFortune);
		}

		int crtFortune = MGPropertyAccesser.getFortune(weapon.getProperty());
		String tips = "当前武器幸运="+crtFortune;
		SystemPromptFacade.sendMsgSpecialEffects(player, tips, SystemPromptPosition.POSITION_RIGHT_1, SpecialEffectsType.SPECIAL_EFFECTS_TYPE_GREEN);

		if(luckAddedFuntune == 0){
			tips = "武器幸运不变";
			SystemPromptFacade.sendMsgSpecialEffects(player, tips, SystemPromptPosition.POSITION_RIGHT_1, SpecialEffectsType.SPECIAL_EFFECTS_TYPE_GREEN);
			player.getPlayerEquipBodyConponent().getEquipEffectMgr().attach(weapon);

			return result;
		}else if(luckAddedFuntune > 0){
			int attachFortune = MGPropertyAccesser.getFortune(weapon.getNewAttachPropertyIfNull());
			if(attachFortune >= 7){
				tips = "通过祝福油可附加的幸运已达上限7";
				SystemPromptFacade.sendMsgSpecialEffects(player, tips, SystemPromptPosition.POSITION_RIGHT_1, SpecialEffectsType.SPECIAL_EFFECTS_TYPE_GREEN);
			}

		}


		if(item.binded()){
			weapon.setBindStatus(Item.Binded);
		}
		// 发送结果
		int fightValue = player.getFightPower(weapon.getProperty());
		MGPropertyAccesser.setOrPutFightValue(weapon.getProperty(), fightValue);
		G2C_Equip_Update update = MessageFactory.getConcreteMessage(EquipmentEventDefines.G2C_Equip_Update);
		update.setEventType(ItemCode.MODIFY_UPDATE);
		update.setBodyId(PlayerEquipBodyArea.weaponBodyId);
		update.setCount((short)1);
		update.setPosition((byte)0);
		update.setItem(weapon);
		update.setPlayer(player);
		GameRoot.sendMessage(player.getIdentity(), update);
		player.getPlayerEquipBodyConponent().getEquipEffectMgr().attach(weapon);

		return result;

	}

	/**
	 * 改变PK 为0
	 */
	Closure<RuntimeResult> modify_Player_PK = { Player player, Item item,int number ->
		RuntimeResult result =  RuntimeResult.OK();
		if(logger.isDebugEnabled()){
			logger.debug("改变PK 为0");
		}
		MGPropertyAccesser.setOrPutPkValue(player.getProperty(), 0);
		PropertyDictionary pd = new PropertyDictionary();
		MGPropertyAccesser.setOrPutPkValue(pd, 0);
		player.notifyPorperty(pd);
		MGPlayerPKComponent pkComponent = (MGPlayerPKComponent) player.getTagged(MGPlayerPKComponent.Tag);
		pkComponent.sendPKNameColorEvent();
		pkComponent.changeNameColorBroadcast();
		return result;
	}

	/**
	 * 增加金钱
	 */
	Closure<RuntimeResult> add_Player_money = { Player player, Item item,int number ->
		RuntimeResult result =  RuntimeResult.OK();
		if(logger.isDebugEnabled()){
			logger.debug("增加金钱");
		}
		PropertyDictionary effectData = item.getItemRef().effectProperty;
		byte saleCurrency = MGPropertyAccesser.getSaleCurrency(effectData);
		if(saleCurrency == 1)
			player.getPlayerMoneyComponent().addGold(MGPropertyAccesser.getGold(effectData) * number,ItemOptSource.ItemBagUse);
		else if(saleCurrency == 2)
			player.getPlayerMoneyComponent().addUnbindGold(MGPropertyAccesser.getUnbindedGold(effectData) * number,ItemOptSource.ItemBagUse);
		else if(saleCurrency == 3)
			player.getPlayerMoneyComponent().addBindGold(MGPropertyAccesser.getBindedGold(effectData) * number,ItemOptSource.ItemBagUse);
		return result;
	}

	/**
	 * 增加经验
	 */
	Closure<RuntimeResult> add_Player_Exp = { Player player, Item item,int number ->
		RuntimeResult result = RuntimeResult.OK();
		if(logger.isDebugEnabled()){
			logger.debug("增加经验");
		}
		if(player.isDead()){
			return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_ITEM_PLAYER_DEAD)
		}
		int exp = MGPropertyAccesser.getExp(item.getItemRef().getEffectProperty())*number;
		if(exp != 0){
			player.getExpComponent().addExp(exp);
		}
		return result;
	}

	/**
	 * 增加当前等级经验最大值
	 */
	Closure<RuntimeResult> add_Player_MaxExp = { Player player, Item item,int number ->
		RuntimeResult result = RuntimeResult.OK();
		if(logger.isDebugEnabled()){
			logger.debug("增加当前等级经验升级所需经验");
		}
		if(player.isDead()){
			return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_ITEM_PLAYER_DEAD)
		}
		PropertyDictionary pd = player.getProperty();
		PlayerProfessionRef professionRef = player.getPlayerProfessionRef();
		PlayerProfessionLevelData playerProfessionLevelData = professionRef.getPlayerClassLevelData(MGPropertyAccesser.getLevel(pd));
		PropertyDictionary levelPd = playerProfessionLevelData.getLevelProperties();
		int maxExp = MGPropertyAccesser.getMaxExp(levelPd);
		int curExp = player.getExpComponent().getExp();
		int addExp = maxExp - curExp;
		player.getExpComponent().addExp(addExp);
		return result;
	}

	/**
	 * 附加经验buff
	 */
	Closure<RuntimeResult> add_Player_ExpBuff = { Player player, Item item,int number ->
		RuntimeResult result = null;
		if(logger.isDebugEnabled()){
			logger.debug("附加经验buff");
		}
		MGFightSpriteBuffComponent fightSpriteBuffComponent = (MGFightSpriteBuffComponent) player.getTagged(MGFightSpriteBuffComponent.Tag);
		float expMultiple = MGPropertyAccesser.getExpMultiple(player.getProperty());
		String buffRefId = MGPropertyAccesser.getBuffRefId(item.getItemRef().getEffectProperty());
		long duration = MGPropertyAccesser.getDuration(item.getItemRef().getEffectProperty());
		MGFightSpriteBuffRef buffRef = (MGFightSpriteBuffRef) GameRoot.getGameRefObjectManager().getManagedObject(buffRefId);
		MGFightSpriteBuff buff = new MGFightSpriteBuff(buffRef,player,player,duration);
		result = fightSpriteBuffComponent.getFightSpriteBuffMgr().attachFightSpriteBuff(buff);
		return result;
	}
	/**
	 * 附加战斗buff
	 */
	Closure<RuntimeResult> add_Fight_buffer = { Player player, Item item,int number ->
		RuntimeResult result = RuntimeResult.OK();
		if(logger.isDebugEnabled()){
			logger.debug("附加战斗buff");
		}

		MGFightSpriteBuffComponent fightSpriteBuffComponent = (MGFightSpriteBuffComponent) player.getTagged(MGFightSpriteBuffComponent.Tag);
		String buffRefId = MGPropertyAccesser.getBuffRefId(item.getItemRef().getEffectProperty());
		MGFightSpriteBuffRef buffRef = (MGFightSpriteBuffRef) GameRoot.getGameRefObjectManager().getManagedObject(buffRefId);
		result = fightSpriteBuffComponent.getFightSpriteBuffMgr().attachFightSpriteBuff(new MGFightSpriteBuff(buffRef,player,player));
		return result;
	}

	/**
	 * 附加蓝红buff(魔血石)
	 */
	Closure<RuntimeResult> add_Player_MpOrHpBuff = { Player player, Item item,int number ->
		RuntimeResult result = null;
		if(logger.isDebugEnabled()){
			logger.debug("附加蓝红buff");
		}
		if(player.isDead()){
			return RuntimeResult.RuntimeError();
		}
		MGFightSpriteBuffComponent fightSpriteBuffComponent = (MGFightSpriteBuffComponent) player.getTagged(MGFightSpriteBuffComponent.Tag);
		String buffRefId = MGPropertyAccesser.getBuffRefId(item.getItemRef().getEffectProperty());
		MGFightSpriteBuffRef buffRef = (MGFightSpriteBuffRef) GameRoot.getGameRefObjectManager().getManagedObject(buffRefId);
		MGFightSpriteBuff buff = new MGFightSpriteBuff(buffRef,player,player);
		PropertyDictionary specialProperty = buff.getSpecialProperty();
		specialProperty.copyFrom(buffRef.getEffectProperty());
		//		byte[] array = buffRef.getEffectProperty().toByteArray();
		//		specialProperty.loadDictionary(array);
		Quest quest = player.getPlayerQuestComponent().getQuestManager().getCrtQuest();
		for (QuestRefOrderItem orderItem : quest.getQuestRef().getOrder().getQuestRefOrder(player)) {
			if (orderItem.getOrderType() == QuestRefOrderType.ChineseMode_String_Value_Order_Type) {
				ChineseModeStringQuestRefOrderItem chineseOrderItem = (ChineseModeStringQuestRefOrderItem) orderItem;
				Short eventId = chineseOrderItem.getOrderEventId();
				String target = chineseOrderItem.getChineseModeTarget();
				if (eventId == QuestChineseOrderDefines.HasBuffer && StringUtils.equals(target, buffRefId)) {
					ChineseModeQuest_GE chineseModeQuest_GE = new ChineseModeQuest_GE();
					chineseModeQuest_GE.setOrderEventId(QuestChineseOrderDefines.HasBuffer);
					chineseModeQuest_GE.setType(ChineseModeQuest_GE.CourseType);
					chineseModeQuest_GE.setChineseModeTarget(buffRefId);
					GameEvent<ChineseModeQuest_GE> chinese = GameEvent.getInstance(ChineseModeQuest_GE_Id, chineseModeQuest_GE);
					player.handleGameEvent(chinese);
				}
			}
		}
		result = fightSpriteBuffComponent.getFightSpriteBuffMgr().attachFightSpriteBuff(buff);
		return result;
	}

	/**
	 * 学习技能
	 */
	Closure<RuntimeResult> learn_skill = { Player player, Item item,int number ->
		RuntimeResult result = RuntimeResult.OK();
		if(logger.isDebugEnabled()){
			logger.debug("学习技能");
		}

		PlayerFightSkillTree playerFightSkillTree = player.getPlayerFightSkillComponent().getPlayerFightSkillTree();
		SkillRef ref = (SkillRef) GameRoot.getGameRefObjectManager().getManagedObject("skill_ds_10");
		playerFightSkillTree.learn(new FightSkill("skill_ds_10", ref));
		return result;
	}





	/**
	 * 位置传送
	 */
	Closure<RuntimeResult> transfer_Player_position = { Player player, Item item,int number ->
		RuntimeResult result = RuntimeResult.OK();
		if(logger.isDebugEnabled()){
			logger.debug("位置传送");
		}
		if(player.isDead()){
			return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_ITEM_PLAYER_DEAD)
		}
		if(StringUtils.equals(item.getItemRefId(),"item_moveto_1") ){
			SceneGrid grid = GameSceneHelper.getRandomWalkableGrid(player.getCrtScene())
			player.getPlayerSceneComponent().switchTo(player.getCrtScene().getRef().getId(), grid.getColumn(), grid.getRow());
		}else if(StringUtils.equals(item.getItemRefId(),"item_moveto_2")){
			player.goHome();
		}else if(StringUtils.equals(item.getItemRefId(),"item_moveto_3")){

			return result;
		}

		return result;
	}



	/**
	 * 打开礼包 
	 */
	Closure<RuntimeResult> open_gift_closure = { Player player, Item item,int number ->
		RuntimeResult result = RuntimeResult.OK();
		if(logger.isDebugEnabled()){
			logger.debug("打开礼包");
		}
		String giftRefId = item.getItemRefId();
		MGGiftBagConfig giftConfig = (MGGiftBagConfig) GameRoot.gameRefObjectManager.getManagedObject(MGGiftBagConfig.Gift_Data_Id);
		List<MGGiftRef> itemList = giftConfig.getGiftConfigMap().get(giftRefId);
		if(itemList == null){
			result = RuntimeResult.ParameterError();
			return result;
		}

		List<ItemPair> items = new ArrayList<ItemPair>();
		int count = 0;
		int totalSlot = 0 ;
		for(MGGiftRef giftRef : itemList){
			String itemRefId = giftRef.getItemRefId();
			int itemNumber = giftRef.getNumber();
			int probability = giftRef.getProbability();
			byte bindStatus = giftRef.getBindStatus();
			int random = SFRandomUtils.random100w();
			if(random <= probability){
				GameRefObject gameRef = GameRoot.getGameRefObjectManager().getManagedObject(itemRefId);
				if(gameRef instanceof ItemRef){
					ItemRef itemRef = (ItemRef) gameRef;
					int maxStackNumber = MGPropertyAccesser.getMaxStackNumber(itemRef.getProperty());
					totalSlot += itemNumber % maxStackNumber == 0 ?itemNumber / maxStackNumber : itemNumber / maxStackNumber +1;
				}else{
					count++;
				}
				ItemPair itemPair = new ItemPair(itemRefId,itemNumber,bindStatus);
				items.add(itemPair);
			}


		}
		result = ItemFacade.addItemCompareSlot(player,items,ItemOptSource.OpenGift);
		if(!result.isOK()){
			SystemPromptFacade.itemBagForGift(player, totalSlot-count);
			return RuntimeResult.ParameterError();
		}


		return result
	}

	/**
	 * 延时礼包2
	 */
	Closure<RuntimeResult> lazy_gift2_closure = { Player player, Item item,int number ->
		RuntimeResult result = RuntimeResult.OK();
		if(logger.isDebugEnabled()){
			logger.debug("延时礼包1");
		}
		String time = "2013-12-28 18:00:00";
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long openTime = c.getTimeInMillis();
		long crtTime = System.currentTimeMillis();
		if(crtTime < openTime ){
			result = RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_ITEM_GIFT_TIME_NOT);
			logger.debug("礼包还未到开启时间");
		}else
			result = getOpen_gift_closure().call(player, item, number);
		return result;
	}
	/**
	 * 延时礼包1
	 */
	Closure<RuntimeResult> lazy_gift1_closure = { Player player, Item item,int number ->
		RuntimeResult result = RuntimeResult.OK();
		if(logger.isDebugEnabled()){
			logger.debug("延时礼包1");
		}
		String expireTime = MGPropertyAccesser.getExpiredTime(item.getProperty());
		long canUseTime = 0l;
		if(expireTime != null){
			Long.parseLong(expireTime);
		}
		long crtTime = System.currentTimeMillis();
		if(crtTime < canUseTime ){
			result = RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_ITEM_GIFT_TIME_NOT);
			logger.debug("礼包还未到开启时间");
		}else
			result = getOpen_gift_closure().call(player, item, number);
		return result;
	}
	/**
	 * 首冲礼包
	 */
	Closure<RuntimeResult> first_gift_closure = { Player player, Item item,int number ->
		RuntimeResult result = RuntimeResult.OK();
		if(logger.isDebugEnabled()){
			logger.debug("首冲礼包");
		}
		boolean flag = true;
		OperatActivityComponent operatActivityComponent = (OperatActivityComponent)player.getTagged(OperatActivityComponent.Tag);
		OperatActivityRecord record = operatActivityComponent.getOperatActivityRecord();
		if(record.getIsFirstRecharge() == FirstRechargeGift.RechargeAndReceive || FirstRechargeGift.RechargeButNotReceive ==record.getIsFirstRecharge() ){

			result = getOpen_gift_closure().call(player, item, number);
		}else{
			result = RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_ITEM_GIFT_NOT_RECHARGE);
		}
		return result;
	}


	/**
	 * 增加坐骑经验
	 */
	Closure<RuntimeResult> add_Mount_Exp = { Player player, Item item,int number ->

		if(logger.isDebugEnabled()){
			logger.debug("增加坐骑经验");
		}
		int baoJiRate = PlayerMountFacade.baoJiRate();
		int exp = MGPropertyAccesser.getExp(item.getItemRef().getEffectProperty())*number;
		int totalExp = exp * baoJiRate;
		if(exp != 0){
			player.getPlayerMountComponent().getMountManager().rewardExp(totalExp);
		}
		RuntimeResult result = new RuntimeResult(RuntimeResult.OKResult, String.valueOf(baoJiRate));
		return result;
	}

	/**
	 * 增加翅膀经验
	 */
	Closure<RuntimeResult> add_Wing_Exp = { Player player, Item item,int number ->

		if(logger.isDebugEnabled()){
			logger.debug("增加翅膀经验");
		}

		byte critMultipleType = WingManager.getCritMultipleType();
		int exp = MGPropertyAccesser.getExp(item.getItemRef().getEffectProperty())*number;
		int totalExp = exp * critMultipleType;

		if(totalExp != 0){
			MGPlayerWingComponent playerWingComponent = (MGPlayerWingComponent)player.getTagged(MGPlayerWingComponent.Tag);
			playerWingComponent.getWingManager().rewardExp(totalExp);
		}
		RuntimeResult result = new RuntimeResult(RuntimeResult.OKResult, String.valueOf(critMultipleType));
		return result;
	}

	/**
	 * 升级VIP
	 */
	Closure<RuntimeResult> shengji_vip_closure = { Player player, Item item,int number ->
		RuntimeResult result = RuntimeResult.OK();
		if(logger.isDebugEnabled()){
			logger.debug("升级VIP");
		}
		String vipRefId = MGPropertyAccesser.getItemRefId(item.getItemRef().getEffectProperty());
		byte  vipType = MGPropertyAccesser.getVipType(item.getItemRef().getEffectProperty());
		MGPlayerVipComponent vipComponent = (MGPlayerVipComponent) player.getTagged(MGPlayerVipComponent.Tag);
		boolean isSuccessed = vipComponent.becameVip(vipRefId, vipType);
		if(!isSuccessed){
			result = RuntimeResult.ParameterError();
		}
		return result;
	}

	/**
	 * 通用闭包
	 */
	Closure<RuntimeResult> common_Closure = { Player player, Item item,int number ->
		RuntimeResult result = RuntimeResult.OK();

		return result;
	}

	/**
	 * 增加技能熟练度
	 */
	Closure<RuntimeResult> add_skill_exp = { Player player, Item item, int skillRefId ->
		int skillExpToAdd = MGPropertyAccesser.getSkillExp(item.getItemRef().getEffectProperty())
		FightSkill skill = player.getPlayerFightSkillComponent().getPlayerFightSkillTree().get(skillRefId)
		if(skill != null) {
			int currentSkillExp = skill.addSkillExp(skillExpToAdd)
			G2C_AddSkillExp response = new G2C_AddSkillExp(skill.getRefId(), currentSkillExp)
			GameRoot.sendMessage(player.getIdentity(), response)
			return RuntimeResult.OK()
		}

		return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_SKILL_NOT_LEARNED)
	}
}
