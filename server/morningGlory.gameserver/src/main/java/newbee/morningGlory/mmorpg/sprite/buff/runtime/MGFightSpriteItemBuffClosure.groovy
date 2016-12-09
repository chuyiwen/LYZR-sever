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
package newbee.morningGlory.mmorpg.sprite.buff.runtime

import java.text.DecimalFormat

import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuff
import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuffRef
import newbee.morningGlory.mmorpg.sprite.buff.actionEvent.BuffEventDefines
import newbee.morningGlory.mmorpg.sprite.buff.actionEvent.G2C_State_Buff

import org.apache.log4j.Logger

import sophia.foundation.communication.core.MessageFactory
import sophia.foundation.property.PropertyDictionary
import sophia.game.GameRoot
import sophia.mmorpg.base.sprite.FightSprite
import sophia.mmorpg.base.sprite.fightProperty.FightPropertyEffectFacade;
import sophia.mmorpg.base.sprite.state.adjunction.ParalysisState
import sophia.mmorpg.core.PropertyDictionaryModifyPhase
import sophia.mmorpg.player.Player
import sophia.mmorpg.player.itemBag.ItemFacade
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser
import sophia.mmorpg.ref.editorgen.MGPropertySymbolDefines
import sophia.mmorpg.utils.RuntimeResult


class MGFightSpriteItemBuffClosure {
	private static final Logger logger = Logger.getLogger(MGFightSpriteItemBuffClosure.class);
	Map<String, Closure<RuntimeResult>> map = new HashMap<>();
	MGFightSpriteItemBuffClosure(){
		map.put("attach_Player_MaxPatk", attach_Player_MaxPatk);
		map.put("detach_Player_MaxPatk", detach_Player_MaxPatk);
		map.put("attach_Player_MaxMAtk", attach_Player_MaxMAtk);
		map.put("detach_Player_MaxMAtk", detach_Player_MaxMAtk);
		map.put("attach_Player_MaxTao", attach_Player_MaxTao);
		map.put("detach_Player_MaxTao", detach_Player_MaxTao);
		map.put("attach_Player_MaxPDef", attach_Player_MaxPDef);
		map.put("detach_Player_MaxPDef", detach_Player_MaxPDef);
		map.put("attach_Player_MaxMDef", attach_Player_MaxMDef);
		map.put("detach_Player_MaxMDef", detach_Player_MaxMDef);
		
		map.put("attach_killMonster_exp", attach_killMonster_exp);
		map.put("detach_killMonster_exp", detach_killMonster_exp);
		map.put("attach_benumb_effect", attach_benumb_effect);
		map.put("detach_benumb_effect", detach_benumb_effect);
		map.put("attach_gift_buff", attach_gift_buff);
		map.put("detach_gift_buff", detach_gift_buff);
		map.put("attach_gift_buff_2", attach_gift_buff_2);
		map.put("detach_gift_buff_2", detach_gift_buff_2);
	}

	public Map<String, Closure<RuntimeResult>> getClosures() {
		return map;
	}
	/**
	 * 武力丹
	 */
	Closure<RuntimeResult> attach_Player_MaxPatk = {FightSprite from, FightSprite to ,MGFightSpriteBuff buff->

		RuntimeResult result = RuntimeResult.OK();
		to = (Player) to;
		MGFightSpriteBuffRef buffRef = buff.getFightSpriteBuffRef();
		int maxPAtk = MGPropertyAccesser.getMaxPAtk(buffRef.getEffectProperty());		
		PropertyDictionary pd = new PropertyDictionary();
		MGPropertyAccesser.setOrPutMaxPAtk(pd, maxPAtk);
		MGPropertyAccesser.setOrPutMaxPAtk(buff.getSpecialProperty(), maxPAtk);
		if(buff.isNotify){
			FightPropertyEffectFacade.attachAndNotify(to, pd);
		}else{
			FightPropertyEffectFacade.attachWithoutSnapshot(to, pd);
		}
		
		return result;
	}
	Closure<RuntimeResult> detach_Player_MaxPatk = {FightSprite from, FightSprite to ,MGFightSpriteBuff buff->
		
		RuntimeResult result = RuntimeResult.OK();
		to = (Player) to;
		MGFightSpriteBuffRef buffRef = buff.getFightSpriteBuffRef();
		int maxPAtk = MGPropertyAccesser.getMaxPAtk(buffRef.getEffectProperty());
		PropertyDictionary pd = new PropertyDictionary();
		MGPropertyAccesser.setOrPutMaxPAtk(pd, maxPAtk);
		FightPropertyEffectFacade.detachAndNotify(to, pd);

		return result;
	}
	/**
	 * 魔力丹
	 */
	Closure<RuntimeResult> attach_Player_MaxMAtk = {FightSprite from, FightSprite to ,MGFightSpriteBuff buff->

		RuntimeResult result = RuntimeResult.OK();
		to = (Player) to;
		MGFightSpriteBuffRef buffRef = buff.getFightSpriteBuffRef();
		int maxMAtk = MGPropertyAccesser.getMaxMAtk(buffRef.getEffectProperty());	
		PropertyDictionary pd = new PropertyDictionary();
		MGPropertyAccesser.setOrPutMaxMAtk(pd, maxMAtk);
		MGPropertyAccesser.setOrPutMaxMAtk(buff.getSpecialProperty(), maxMAtk);
		if(buff.isNotify){
			FightPropertyEffectFacade.attachAndNotify(to, pd);
		}else{
			FightPropertyEffectFacade.attachWithoutSnapshot(to, pd);
		}
		return result;
	}
	Closure<RuntimeResult> detach_Player_MaxMAtk = {FightSprite from, FightSprite to ,MGFightSpriteBuff buff->
		
		
		RuntimeResult result = RuntimeResult.OK();
		to = (Player) to;
		MGFightSpriteBuffRef buffRef = buff.getFightSpriteBuffRef();
		int maxMAtk = MGPropertyAccesser.getMaxMAtk(buffRef.getEffectProperty());
		PropertyDictionary pd = new PropertyDictionary();
		MGPropertyAccesser.setOrPutMaxMAtk(pd, maxMAtk);
		FightPropertyEffectFacade.detachAndNotify(to, pd);
		return result;
	}
	/**
	 * 道力丹
	 */
	Closure<RuntimeResult> attach_Player_MaxTao = {FightSprite from, FightSprite to ,MGFightSpriteBuff buff->

		RuntimeResult result = RuntimeResult.OK();
		to = (Player) to;
		MGFightSpriteBuffRef buffRef = buff.getFightSpriteBuffRef();
		int maxTao = MGPropertyAccesser.getMaxTao(buffRef.getEffectProperty());
		
		PropertyDictionary pd = new PropertyDictionary();
		MGPropertyAccesser.setOrPutMaxTao(pd, maxTao);
		MGPropertyAccesser.setOrPutMaxTao(buff.getSpecialProperty(), maxTao);
		if(buff.isNotify){
			FightPropertyEffectFacade.attachAndNotify(to, pd);
		}else{
			FightPropertyEffectFacade.attachWithoutSnapshot(to, pd);
		}
		
		return result;
	}
	Closure<RuntimeResult> detach_Player_MaxTao = {FightSprite from, FightSprite to ,MGFightSpriteBuff buff->
		
		RuntimeResult result = RuntimeResult.OK();
		to = (Player) to;
		MGFightSpriteBuffRef buffRef = buff.getFightSpriteBuffRef();
		int maxTao = MGPropertyAccesser.getMaxTao(buffRef.getEffectProperty());
		PropertyDictionary pd = new PropertyDictionary();
		MGPropertyAccesser.setOrPutMaxTao(pd, maxTao);
		FightPropertyEffectFacade.detachAndNotify(to, pd);
		return result;
	}
	/**
	 * 金钟丹
	 */
	Closure<RuntimeResult> attach_Player_MaxPDef = {FightSprite from, FightSprite to ,MGFightSpriteBuff buff->

		RuntimeResult result = RuntimeResult.OK();
		to = (Player) to;
		MGFightSpriteBuffRef buffRef = buff.getFightSpriteBuffRef();
		int maxPDef = MGPropertyAccesser.getMaxPDef(buffRef.getEffectProperty());
		
		PropertyDictionary pd = new PropertyDictionary();
		MGPropertyAccesser.setOrPutMaxPDef(pd, maxPDef);
		MGPropertyAccesser.setOrPutMaxPDef(buff.getSpecialProperty(), maxPDef);
		if(buff.isNotify){
			FightPropertyEffectFacade.attachAndNotify(to, pd);
		}else{
			FightPropertyEffectFacade.attachWithoutSnapshot(to, pd);
		}
		return result;
	}
	Closure<RuntimeResult> detach_Player_MaxPDef = {FightSprite from, FightSprite to ,MGFightSpriteBuff buff->
		
		RuntimeResult result = RuntimeResult.OK();
		to = (Player) to;
		MGFightSpriteBuffRef buffRef = buff.getFightSpriteBuffRef();
		int maxPDef = MGPropertyAccesser.getMaxPDef(buffRef.getEffectProperty());
		
		PropertyDictionary pd = new PropertyDictionary();
		MGPropertyAccesser.setOrPutMaxPDef(pd, maxPDef);
		FightPropertyEffectFacade.detachAndNotify(to, pd);
		return result;
	}
	/**
	 * 铁布丹
	 */
	Closure<RuntimeResult> attach_Player_MaxMDef = {FightSprite from, FightSprite to ,MGFightSpriteBuff buff->

		RuntimeResult result = RuntimeResult.OK();
		to = (Player) to;
		MGFightSpriteBuffRef buffRef = buff.getFightSpriteBuffRef();
		int maxMDef = MGPropertyAccesser.getMaxMDef(buffRef.getEffectProperty());
		
		PropertyDictionary pd = new PropertyDictionary();
		MGPropertyAccesser.setOrPutMaxMDef(pd, maxMDef);
		MGPropertyAccesser.setOrPutMaxMDef(buff.getSpecialProperty(), maxMDef);
		if(buff.isNotify){
			FightPropertyEffectFacade.attachAndNotify(to, pd);
		}else{
			FightPropertyEffectFacade.attachWithoutSnapshot(to, pd);
		}
		return result;
	}
	Closure<RuntimeResult> detach_Player_MaxMDef = {FightSprite from, FightSprite to ,MGFightSpriteBuff buff->
		
		RuntimeResult result = RuntimeResult.OK();
		to = (Player) to;
		MGFightSpriteBuffRef buffRef = buff.getFightSpriteBuffRef();
		int maxMDef = MGPropertyAccesser.getMaxMDef(buffRef.getEffectProperty());
	
		PropertyDictionary pd = new PropertyDictionary();
		MGPropertyAccesser.setOrPutMaxMDef(pd, maxMDef);
		
		FightPropertyEffectFacade.detachAndNotify(to, pd);
		return result;
	}
	/**
	 * 经验宝典，增加杀怪经验倍数
	 */
	Closure<RuntimeResult> attach_killMonster_exp = {FightSprite from, FightSprite to ,MGFightSpriteBuff buff->
		
		RuntimeResult result = RuntimeResult.OK();
		float expMultiple = MGPropertyAccesser.getExpMultiple(buff.getFightSpriteBuffRef().getEffectProperty());
		if(to instanceof Player){
			Player player = (Player) to;
			player.getExpComponent().addExpMultiple(expMultiple);
		}

		return result;
	}
	
	Closure<RuntimeResult> detach_killMonster_exp = {FightSprite from, FightSprite to ,MGFightSpriteBuff buff->
		
		RuntimeResult result = RuntimeResult.OK();
		float expMultiple = MGPropertyAccesser.getExpMultiple(buff.getFightSpriteBuffRef().getEffectProperty());
		if(to instanceof Player){
			Player player = (Player) to;
			player.getExpComponent().subExpMultiple(expMultiple);
		}

		return result;
	}
	/**
	 * 限时礼包（时长）
	 */
	Closure<RuntimeResult> attach_gift_buff = {FightSprite from, FightSprite to ,MGFightSpriteBuff buff->
		RuntimeResult result = RuntimeResult.OK();
		return result;
	}
	
	Closure<RuntimeResult> detach_gift_buff = {FightSprite from, FightSprite to ,MGFightSpriteBuff buff->
		
		RuntimeResult result = RuntimeResult.OK();
		to = (Player) to;
		String id = MGPropertyAccesser.getItemId(buff.getSpecialProperty());
		ItemFacade.removeItemById(to, id, 1);
		
		return result;
	}
	/**
	 * 限时礼包（日期）
	 */
	Closure<RuntimeResult> attach_gift_buff_2 = {FightSprite from, FightSprite to ,MGFightSpriteBuff buff->
		
		RuntimeResult result = RuntimeResult.OK();
		
		return result;
	}
	
	Closure<RuntimeResult> detach_gift_buff_2 = {FightSprite from, FightSprite to ,MGFightSpriteBuff buff->
		
		RuntimeResult result = RuntimeResult.OK();
		to = (Player) to;
		String id = MGPropertyAccesser.getItemId(buff.getSpecialProperty());
		ItemFacade.removeItemById(to, id, 1);
		
		return result;
	}
	
	/**
	 * 附加麻痹效果（?麻痹效果是不能动吗?）
	 */
	Closure<RuntimeResult> attach_benumb_effect = {FightSprite from, FightSprite to ,MGFightSpriteBuff buff->
		
		RuntimeResult result = RuntimeResult.OK();
		to.changeState(ParalysisState.ParalysisState_Id);
		return result;
	}
	Closure<RuntimeResult> detach_benumb_effect = {FightSprite from, FightSprite to ,MGFightSpriteBuff buff->
		
		RuntimeResult result = RuntimeResult.OK();
		to.cancelState(ParalysisState.ParalysisState_Id);
		return result;
	}
	
	
}
