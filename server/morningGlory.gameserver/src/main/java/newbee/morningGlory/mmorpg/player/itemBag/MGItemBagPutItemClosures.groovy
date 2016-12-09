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

package newbee.morningGlory.mmorpg.player.itemBag

import java.text.ParseException
import java.text.SimpleDateFormat

import newbee.morningGlory.mmorpg.player.achievement.MGPlayerAchievementComponent
import newbee.morningGlory.mmorpg.player.peerage.MGPlayerPeerageComponent
import newbee.morningGlory.mmorpg.sceneActivities.monsterIntrusion.MonsterInvasionComponent
import newbee.morningGlory.mmorpg.sceneActivities.monsterIntrusion.ref.MonsterInvasionScrollRef;
import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuff
import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuffComponent
import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuffRef

import org.apache.log4j.Logger

import sophia.game.GameRoot
import sophia.mmorpg.item.Item
import sophia.mmorpg.player.Player
import sophia.mmorpg.player.itemBag.ItemFacade
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser
import sophia.mmorpg.utils.RuntimeResult
import org.apache.commons.lang3.StringUtils;


class MGItemBagPutItemClosures {
	private static final Logger logger = Logger.getLogger(MGItemBagPutItemClosures.class);
	Map<String, Closure<RuntimeResult>> map = new HashMap<>();
	MGItemBagPutItemClosures(){
		map.put("putItemClosure", putItemClosure);
		map.put("putGiftBuffClosure_1", putGiftBuffClosure_1);
		map.put("putGiftBuffClosure_2", putGiftBuffClosure_2);
		map.put("putLazyGiftClosure", putLazyGiftClosure);
		map.put("addPlayerMerit", addPlayerMerit);
		map.put("addPlayerAchievement", addPlayerAchievement);
		map.put("putMonsterInvasion_Scroll", putMonsterInvasion_Scroll);
	}


	/**
	 * 放入物品
	 */
	Closure<RuntimeResult> putItemClosure = { Player player, Item item, int index ->
		RuntimeResult result = RuntimeResult.OK();

		return result;
	}
	/**
	 * 限时礼包
	 */
	Closure<RuntimeResult> putGiftBuffClosure_1 = { Player player, Item item, int index ->
		RuntimeResult result = RuntimeResult.OK();
		String buffRefId = MGPropertyAccesser.getBuffRefId(item.getItemRef().getEffectProperty());
		MGFightSpriteBuffComponent fightSpriteBuffComponent = (MGFightSpriteBuffComponent) player.getTagged(MGFightSpriteBuffComponent.Tag);
		MGFightSpriteBuffRef buffRef = (MGFightSpriteBuffRef) GameRoot.getGameRefObjectManager().getManagedObject(buffRefId);
		MGFightSpriteBuff buff = new MGFightSpriteBuff(buffRef,player,player)
		MGPropertyAccesser.setOrPutItemId(buff.getSpecialProperty(), item.getId());
		result =fightSpriteBuffComponent.getFightSpriteBuffMgr().attachFightSpriteBuff(buff);
		return result;
	}
	/**
	 * 限时礼包（指定日期）
	 */
	Closure<RuntimeResult> putGiftBuffClosure_2 = { Player player, Item item, int index ->
		RuntimeResult result = RuntimeResult.OK();
		String buffRefId = MGPropertyAccesser.getBuffRefId(item.getItemRef().getEffectProperty());
		String removeTime = "2013-12-25 17:48:30";
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(removeTime));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long expireTime = c.getTimeInMillis();

		MGFightSpriteBuffComponent fightSpriteBuffComponent = (MGFightSpriteBuffComponent) player.getTagged(MGFightSpriteBuffComponent.Tag);
		MGFightSpriteBuffRef buffRef = (MGFightSpriteBuffRef) GameRoot.getGameRefObjectManager().getManagedObject(buffRefId);

		MGFightSpriteBuff buff = new MGFightSpriteBuff(buffRef,player,player);
		MGPropertyAccesser.setOrPutItemId(buff.getSpecialProperty(), item.getId());
		buff.setExpiration(expireTime);
		result = fightSpriteBuffComponent.getFightSpriteBuffMgr().attachFightSpriteBuff(buff);
		return result;
	}
	/**
	 * 延时礼包
	 */
	Closure<RuntimeResult> putLazyGiftClosure = { Player player, Item item, int index ->
		RuntimeResult result = RuntimeResult.OK();
		long duration = MGPropertyAccesser.getDuration(item.getItemRef().getEffectProperty()) * 60 * 1000;

		long expireTime = System.currentTimeMillis() + duration;

		MGPropertyAccesser.setOrPutExpiredTime(item.getProperty(), expireTime+"");

		MGPropertyAccesser.setOrPutIsNonPropertyItem(item.getItemRef().getProperty(), (byte)1);
		MGPropertyAccesser.setOrPutIsNonPropertyItem(item.getProperty(), (byte)1);
		return result;
	}
	/**
	 * 加功勋点
	 */
	Closure<RuntimeResult> addPlayerMerit = {Player player, int number ->

		MGPlayerPeerageComponent meritComponent = (MGPlayerPeerageComponent)player.getTagged(MGPlayerPeerageComponent.Tag);
		meritComponent.getMeritManager().addMerit(number);
	}
	/**
	 * 加成就点
	 */
	Closure<RuntimeResult> addPlayerAchievement = {Player player, int number ->

		MGPlayerAchievementComponent achievementComponent = (MGPlayerAchievementComponent)player.getTagged(MGPlayerAchievementComponent.Tag);
		achievementComponent.getAchievePointMgr().addAchievePoint(number);
	}
	/** 活动兑换卷放入背包回调 */
	Closure<RuntimeResult> putMonsterInvasion_Scroll = {Player player, Item item,int index ->
		MonsterInvasionComponent monsterIntrusionComponent = (MonsterInvasionComponent)player.getTagged(MonsterInvasionComponent.Tag);
		monsterIntrusionComponent.refreshSelfFont();
	}
}
