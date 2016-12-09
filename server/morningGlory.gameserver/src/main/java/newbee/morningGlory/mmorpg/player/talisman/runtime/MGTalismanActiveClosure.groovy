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
package newbee.morningGlory.mmorpg.player.talisman.runtime

import newbee.morningGlory.mmorpg.player.talisman.MGPlayerTalismanComponent
import newbee.morningGlory.mmorpg.player.talisman.MGTalisman
import newbee.morningGlory.mmorpg.player.talisman.MGTalismanStatistics

import org.apache.log4j.Logger

import sophia.foundation.property.PropertyDictionary
import sophia.foundation.task.PeriodicTaskHandle
import sophia.foundation.task.Task
import sophia.game.GameContext
import sophia.mmorpg.MMORPGContext
import sophia.mmorpg.Mail.MailMgr
import sophia.mmorpg.base.sprite.fightProperty.FightPropertyEffectFacade
import sophia.mmorpg.core.timer.SFTimeChimeListener
import sophia.mmorpg.core.timer.SFTimeUnit
import sophia.mmorpg.core.timer.SFTimer
import sophia.mmorpg.core.timer.SFTimerCreater
import sophia.mmorpg.player.Player
import sophia.mmorpg.player.chat.PlayerChatComponent
import sophia.mmorpg.player.itemBag.ItemFacade
import sophia.mmorpg.player.itemBag.ItemOptSource
import sophia.mmorpg.player.itemBag.ItemPair
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser
import sophia.mmorpg.ref.editorgen.MGPropertySymbolDefines
import sophia.mmorpg.utils.RuntimeResult

import com.google.gson.Gson

class MGTalismanActiveClosure {
	Map<String, Closure<RuntimeResult>> activeClosureMap = new HashMap<>();
	Map<String, Closure<RuntimeResult>> inactiveClosureMap = new HashMap<>();
	private static final Logger logger = Logger.getLogger(MGTalismanActiveClosure.class);
	MGTalismanActiveClosure() {
		activeClosureMap.put("talisman_1_default_active",talisman_1_default_active);
		activeClosureMap.put("talisman_2_default_active",talisman_2_default_active);
		activeClosureMap.put("talisman_3_default_active",talisman_3_default_active);
		activeClosureMap.put("talisman_4_default_active",talisman_4_default_active);
		activeClosureMap.put("talisman_9_default_active",talisman_9_default_active);
		activeClosureMap.put("talisman_10_default_active",talisman_10_default_active);

		inactiveClosureMap.put("talisman_1_default_unactive", talisman_1_default_unactive);
		inactiveClosureMap.put("talisman_2_default_unactive", talisman_2_default_unactive);
		inactiveClosureMap.put("talisman_3_default_unactive", talisman_3_default_unactive);
		inactiveClosureMap.put("talisman_4_default_unactive", talisman_4_default_unactive);
		inactiveClosureMap.put("talisman_9_default_unactive", talisman_9_default_unactive);
		inactiveClosureMap.put("talisman_10_default_unactive", talisman_10_default_unactive);
	}

	/**
	 * 点金-Active-缺省
	 */
	Closure<RuntimeResult> talisman_1_default_active = { Player owner,final MGTalisman talisman ->
		RuntimeResult result = RuntimeResult.OK();
		if(logger.isDebugEnabled()){
			logger.debug("我是小宝，帮你点金");
		}
		final MGPlayerTalismanComponent talismanCompoent = getTalismanCompoent(owner);
		final int gold = MGPropertyAccesser.getGold(talisman.getTalismanRef().getEffectData());
		final int handleTime = talisman.getHandleFirstTime();
		talismanCompoent.cancelHandle1();
		PeriodicTaskHandle periodicTaskHandle = GameContext.getTaskManager().schedulePeriodicTask(new Task(){
					void run() throws Exception {
						talismanCompoent.getPlayerCitta().addGoldReward(gold);
						talisman.updateLastHandleTime();
						talismanCompoent.sendRewardMessage();
					};
				},  handleTime ,MGTalisman.DEFAULT_HANDLE_TIME);
		talismanCompoent.setHandle1(periodicTaskHandle);
		return result;
	}

	/**
	 * 点金-Inactive-缺省
	 */
	Closure<RuntimeResult> talisman_1_default_unactive = {Player owner, MGTalisman talisman ->
		RuntimeResult result = RuntimeResult.OK();
		getTalismanCompoent(owner).cancelHandle1();
		return result;
	}

	/**
	 * 聚宝-Active-缺省
	 */
	Closure<RuntimeResult> talisman_2_default_active = {Player owner, MGTalisman talisman ->
		RuntimeResult result = RuntimeResult.OK();
		if(logger.isDebugEnabled()){
			logger.debug("我是小宝，帮你聚宝");
		}

		int number = MGPropertyAccesser.getNumber(talisman.getTalismanRef().getEffectData());
		String itemRefId = MGPropertyAccesser.getItemRefId(talisman.getTalismanRef().getEffectData());
		final Player player = owner;
		final ItemPair itemPair = new ItemPair(itemRefId, number, false);
		final MGPlayerTalismanComponent talismanCompoent = getTalismanCompoent(owner);
		getTalismanCompoent(owner).cancelHandle2();
		PeriodicTaskHandle periodicTaskHandle = GameContext.getTaskManager().scheduleDayPeriodicTask(new Task(){
					void run() throws Exception {
						talismanCompoent.getPlayerCitta().addBaoXiangs(itemPair);
						talisman.updateLastHandleTime();
						talismanCompoent.sendRewardMessage();
					};
				}, 24);
		talismanCompoent.setHandle2(periodicTaskHandle);

		return result;
	}

	/**
	 * 聚宝-Inactive-缺省
	 */
	Closure<RuntimeResult> talisman_2_default_unactive = {Player owner, MGTalisman talisman ->
		RuntimeResult result = RuntimeResult.OK();
		getTalismanCompoent(owner).cancelHandle2();
		return result;
	}

	/**
	 * 聚灵-Active-缺省
	 */
	Closure<RuntimeResult> talisman_3_default_active = {Player owner,final MGTalisman talisman ->
		RuntimeResult result = RuntimeResult.OK();
		if(logger.isDebugEnabled()){
			logger.debug("我是小宝，帮你聚灵");
		}
		final int number = MGPropertyAccesser.getNumber(talisman.getTalismanRef().getEffectData());
		String itemRefId = MGPropertyAccesser.getItemRefId(talisman.getTalismanRef().getEffectData());
		final Player player = owner;
		final int handleTime = talisman.getHandleFirstTime();
		final MGPlayerTalismanComponent talismanCompoent = getTalismanCompoent(owner);
		talismanCompoent.cancelHandle3();
		PeriodicTaskHandle periodicTaskHandle = GameContext.getTaskManager().schedulePeriodicTask(new Task(){
					void run() throws Exception {
						talismanCompoent.getPlayerCitta().addStoneReward(number);
						talisman.updateLastHandleTime();
						talismanCompoent.sendRewardMessage();
					};
				},  handleTime ,MGTalisman.DEFAULT_HANDLE_TIME);
		talismanCompoent.setHandle3(periodicTaskHandle);
		return result;
	}

	/**
	 * 聚灵-Inactive-缺省
	 */
	Closure<RuntimeResult> talisman_3_default_unactive = {Player owner, MGTalisman talisman ->
		RuntimeResult result = RuntimeResult.OK();
		getTalismanCompoent(owner).cancelHandle3();
		return result;
	}

	/**
	 * 千里传音-Active-缺省
	 */
	Closure<RuntimeResult> talisman_4_default_active = {Player owner, MGTalisman talisman ->
		RuntimeResult result = RuntimeResult.OK();
		int CDTime = MGPropertyAccesser.getCDTime(talisman.getTalismanRef().getEffectData()) * 1000;
		PlayerChatComponent chatComponent = owner.getPlayerChatComponent();
		chatComponent.setActiveChuanYinTalisman(true);
		chatComponent.setTalismanCDTime(CDTime);
		return result;
	}
	Closure<RuntimeResult> talisman_4_default_unactive = {Player owner, MGTalisman talisman ->
		RuntimeResult result = RuntimeResult.OK();
		int CDTime = 0;
		PlayerChatComponent chatComponent = owner.getPlayerChatComponent();
		chatComponent.setActiveChuanYinTalisman(false);
		chatComponent.setTalismanCDTime(CDTime);
		return result;
	}


	/**
	 * 移动速度-Active-缺省
	 */
	Closure<RuntimeResult> talisman_9_default_active = {Player owner, MGTalisman talisman ->
		// player property modify

		RuntimeResult result = RuntimeResult.OK();
		if(logger.isDebugEnabled()){
			logger.debug("我是小宝，增加移动速度");
		}

		int moveSpeedPer = MGPropertyAccesser.getMoveSpeedPer(talisman.getTalismanRef().getEffectData());

		PropertyDictionary pd = new PropertyDictionary(2);
		MGPropertyAccesser.setOrPutMoveSpeedPer(pd, moveSpeedPer);

		boolean isNotify = talisman.isIsNotify();
		if(isNotify){
			FightPropertyEffectFacade.attachAndNotify(owner, pd);
			if(owner.getCrtScene() != null){
				int crtMoveSpeedPer = owner.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotValueById(MGPropertySymbolDefines.MoveSpeedPer_Id);
				int crtMoveSpeed = owner.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotValueById(MGPropertySymbolDefines.MoveSpeed_Id);
				MGPropertyAccesser.setOrPutMoveSpeedPer(pd, crtMoveSpeedPer);
				MGPropertyAccesser.setOrPutMoveSpeed(pd, crtMoveSpeed);
				owner.getPathComponent().setMoveSpeed(crtMoveSpeed);
				owner.getAoiComponent().broadcastProperty(pd);
			}
			getTalismanCompoent(owner).getStatistics().addTalismanStatistics(MGTalismanStatistics.Total_MoveSpeedPer,moveSpeedPer);
		}else{
			FightPropertyEffectFacade.attachWithoutSnapshot(owner, pd);
			talisman.setIsNotify(true);
		}

		return result;
	}

	/**
	 * 移动速度-Inactive-缺省
	 */
	Closure<RuntimeResult> talisman_9_default_unactive = {Player owner, MGTalisman talisman ->
		RuntimeResult result = RuntimeResult.OK();
		if(logger.isDebugEnabled()){
			logger.debug("我是小宝，取消移动速度加成");
		}
		int moveSpeedPer = MGPropertyAccesser.getMoveSpeedPer(talisman.getTalismanRef().getEffectData());

		PropertyDictionary pd = new PropertyDictionary(2);
		MGPropertyAccesser.setOrPutMoveSpeedPer(pd, moveSpeedPer);
		boolean isNotify = talisman.isIsNotify();
		if(isNotify){
			FightPropertyEffectFacade.detachAndNotify(owner, pd);
			if(owner.getCrtScene() != null){
				int crtMoveSpeedPer = owner.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotValueById(MGPropertySymbolDefines.MoveSpeedPer_Id);
				int crtMoveSpeed = owner.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotValueById(MGPropertySymbolDefines.MoveSpeed_Id);
				MGPropertyAccesser.setOrPutMoveSpeedPer(pd, crtMoveSpeedPer);
				MGPropertyAccesser.setOrPutMoveSpeed(pd, crtMoveSpeed);
				owner.getPathComponent().setMoveSpeed(crtMoveSpeed);
				owner.getAoiComponent().broadcastProperty(pd);

			}

		}else{
			FightPropertyEffectFacade.detachAndSnapshot(owner, pd);
			talisman.setIsNotify(true);
		}
		getTalismanCompoent(owner).getStatistics().subTalismanStatistics(MGTalismanStatistics.Total_MoveSpeedPer,moveSpeedPer);
		return result;
	}
	/**
	 * 攻击速度-Active-缺省
	 */
	Closure<RuntimeResult> talisman_10_default_active = {Player owner, MGTalisman talisman ->
		RuntimeResult result = RuntimeResult.OK();
		if(logger.isDebugEnabled()){
			logger.debug("我是小宝，增加攻击速度");
		}
		int atkSpeedPer = MGPropertyAccesser.getAtkSpeedPer(talisman.getTalismanRef().getEffectData());
		PropertyDictionary pd = new PropertyDictionary(2);
		MGPropertyAccesser.setOrPutAtkSpeedPer(pd, atkSpeedPer);
		boolean isNotify = talisman.isIsNotify();
		if(isNotify){
			FightPropertyEffectFacade.attachAndNotify(owner, pd);
			if(owner.getCrtScene() != null){
				int crtAtkSpeedPer = owner.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotValueById(MGPropertySymbolDefines.AtkSpeedPer_Id);
				int crtAtkSpeed = owner.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotValueById(MGPropertySymbolDefines.AtkSpeed_Id);
				MGPropertyAccesser.setOrPutAtkSpeedPer(pd, crtAtkSpeedPer);
				MGPropertyAccesser.setOrPutAtkSpeed(pd, crtAtkSpeed);
				owner.getAoiComponent().broadcastProperty(pd);
			}
			getTalismanCompoent(owner).getStatistics().addTalismanStatistics(MGTalismanStatistics.Total_AtkSpeedPer,atkSpeedPer);
		}else{
			FightPropertyEffectFacade.attachWithoutSnapshot(owner, pd);
			talisman.setIsNotify(true);
		}

		return result;
	}

	/**
	 * 攻击速度-Inactive-缺省
	 */
	Closure<RuntimeResult> talisman_10_default_unactive = {Player owner, MGTalisman talisman ->
		RuntimeResult result = RuntimeResult.OK();
		if(logger.isDebugEnabled()){
			logger.debug("我是小宝，取消增加攻击速度");
		}
		int atkSpeedPer = MGPropertyAccesser.getAtkSpeedPer(talisman.getTalismanRef().getEffectData());

		PropertyDictionary pd = new PropertyDictionary(2);
		MGPropertyAccesser.setOrPutAtkSpeedPer(pd, atkSpeedPer);
		boolean isNotify = talisman.isIsNotify();
		if(isNotify){
			FightPropertyEffectFacade.detachAndNotify(owner, pd);
			if(owner.getCrtScene() != null){
				int crtAtkSpeedPer = owner.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotValueById(MGPropertySymbolDefines.AtkSpeedPer_Id);
				int crtAtkSpeed = owner.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotValueById(MGPropertySymbolDefines.AtkSpeed_Id);
				MGPropertyAccesser.setOrPutAtkSpeedPer(pd, crtAtkSpeedPer);
				MGPropertyAccesser.setOrPutAtkSpeed(pd, crtAtkSpeed);
				owner.getAoiComponent().broadcastProperty(pd);
			}
		}else{
			FightPropertyEffectFacade.detachAndSnapshot(owner, pd);
			talisman.setIsNotify(true);
		}
		getTalismanCompoent(owner).getStatistics().subTalismanStatistics(MGTalismanStatistics.Total_AtkSpeedPer,atkSpeedPer);
		return result;
	}


	MGPlayerTalismanComponent getTalismanCompoent(Player owner){
		return ((MGPlayerTalismanComponent) owner.getTagged(MGPlayerTalismanComponent.Tag));
	}


}
