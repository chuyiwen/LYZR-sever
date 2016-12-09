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

import newbee.morningGlory.mmorpg.sprite.buff.MGBuffEffectType
import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuff
import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuffComponent
import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuffMgr
import newbee.morningGlory.mmorpg.sprite.buff.MGSpecialBuff;
import newbee.morningGlory.mmorpg.sprite.buff.actionEvent.BuffEventDefines
import newbee.morningGlory.mmorpg.sprite.buff.actionEvent.G2C_Effect_Buff

import org.apache.commons.lang3.StringUtils
import org.apache.log4j.Logger

import sophia.foundation.communication.core.MessageFactory
import sophia.foundation.property.PropertyDictionary
import sophia.mmorpg.base.scene.GameSceneHelper
import sophia.mmorpg.base.sprite.FightSprite
import sophia.mmorpg.player.Player
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser
import sophia.mmorpg.utils.RuntimeResult


class MGFightSpritePeriodAffectClosure_1 {

	private static final Logger logger = Logger.getLogger(MGFightSpritePeriodAffectClosure_1.class);
	Map<String, Closure<RuntimeResult>> map = new HashMap<>();
	MGFightSpritePeriodAffectClosure_1(){
		map.put("skill_addHp", skill_addHp);
		map.put("attach_BloodMagic_stone", attach_BloodMagic_stone);
		map.put("period_liuXue_state", period_liuXue_state);
		map.put("period_zhongDu_state", period_zhongDu_state);
		map.put("period_zhuoSao_state", period_zhuoSao_state);
		map.put("period_zhongDu2_state", period_zhongDu2_state);
	}
	public Map<String, Closure<RuntimeResult>> getClosures() {
		return map;
	}
	/**
	 * 治愈术buff
	 */
	Closure<RuntimeResult> skill_addHp = { FightSprite from, FightSprite to, MGFightSpriteBuff buff ->

		RuntimeResult result = RuntimeResult.OK();

		int hp = MGPropertyAccesser.getHP(buff.getSpecialProperty())
		String skillRefId = MGPropertyAccesser.getSkillRefId(buff.getSpecialProperty())
		int crtHP = to.getHP();
		int maxHP = to.getHPMax();
		if(crtHP >= maxHP){
			return result;
		}
		if(to.applyHP(to, hp)){
			G2C_Effect_Buff message = (G2C_Effect_Buff) MessageFactory.getMessage(BuffEventDefines.G2C_Effect_Buff);
			message.setAttacker(from);
			message.setTarget(to);
			message.setType(MGBuffEffectType.HP);
			message.setValue(hp);
			message.setCrtValue(to.getHP());
			message.setMaxValue(to.getHPMax());
			GameSceneHelper.broadcastMessageToAOI(to,message);
		}
		return result;
	}





	Closure<RuntimeResult> attach_BloodMagic_stone = {FightSprite from, FightSprite to ,MGFightSpriteBuff buff->

		to = (Player) to;
		RuntimeResult result = RuntimeResult.OK();
		if(to.isDead()){
			return result;
		}
		PropertyDictionary effectPd = buff.getSpecialProperty();
		int totalValue = MGPropertyAccesser.getTotalValue(effectPd);
		int mp = MGPropertyAccesser.getMP(effectPd);
		int hp = MGPropertyAccesser.getHP(effectPd);
		int curMaxHP = to.getHPMax();
		int curMaxMP = to.getMPMax();

		int curHP = to.getHP();
		int curMP = to.getMP();
		if(curMaxHP == curHP && curMaxMP == curMP){
			return;
		}
		int addHP = 0;
		int addMP = 0;
		if(curHP < curMaxHP) {
			if(curMaxHP - curHP > hp)
				addHP = hp;
			else
				addHP = curMaxHP - curHP;
		}
		if(curMP < curMaxMP){
			if(curMaxMP - curMP > mp)
				addMP = mp;
			else
				addMP = curMaxMP - curMP;
		}

		if(totalValue < addHP){
			addHP = totalValue;
			totalValue = 0;
		}
		else{
			totalValue -= addHP;
		}

		if(totalValue < addMP){
			addMP = totalValue;
			totalValue = 0;
		}
		else{
			totalValue -= addMP;
		}
		to.applyHP(to,addHP);
		to.applyMP(addMP);
		if(addHP > 0){
			G2C_Effect_Buff message = (G2C_Effect_Buff) MessageFactory.getMessage(BuffEventDefines.G2C_Effect_Buff);
			message.setAttacker(from);
			message.setTarget(to);
			message.setType(MGBuffEffectType.HP);
			message.setValue(addHP);
			message.setCrtValue(to.getHP());
			message.setMaxValue(to.getHPMax());
			GameSceneHelper.broadcastMessageToAOI(to,message);
		}
		if(addMP > 0){
			G2C_Effect_Buff message = (G2C_Effect_Buff) MessageFactory.getMessage(BuffEventDefines.G2C_Effect_Buff);
			message.setAttacker(from);
			message.setTarget(to);
			message.setType(MGBuffEffectType.MP);
			message.setValue(addMP);
			message.setCrtValue(to.getMP());
			message.setMaxValue(to.getMPMax());
			GameSceneHelper.broadcastMessageToAOI(to,message);
		}
		MGFightSpriteBuffComponent fightSpriteBuffComponent = (MGFightSpriteBuffComponent) to.getTagged(MGFightSpriteBuffComponent.Tag);
		MGPropertyAccesser.setOrPutTotalValue(buff.getSpecialProperty(),totalValue);
		if(totalValue <= 0){
			fightSpriteBuffComponent.getFightSpriteBuffMgr().removeBuff(buff);
		}

		return result;
	}
	/**
	 * 流血
	 */
	Closure<RuntimeResult> period_liuXue_state = {FightSprite from, FightSprite to ,MGFightSpriteBuff buff->
		RuntimeResult result = RuntimeResult.OK();
		sendDamageMessage(from, to, buff)
		return result;
	}
	/**
	 * 中毒
	 */
	Closure<RuntimeResult> period_zhongDu_state = {FightSprite from, FightSprite to ,MGFightSpriteBuff buff->

		RuntimeResult result = RuntimeResult.OK();
		sendDamageMessage(from, to, buff)
		return result;
	}
	/**
	 * 灼烧
	 */
	Closure<RuntimeResult> period_zhuoSao_state = {FightSprite from, FightSprite to ,MGFightSpriteBuff buff->

		RuntimeResult result = RuntimeResult.OK();
		sendDamageMessage(from, to, buff)
		return result;
	}

	/**
	 * 中毒2
	 */
	Closure<RuntimeResult> period_zhongDu2_state= {FightSprite from, FightSprite to ,MGFightSpriteBuff buff->
		RuntimeResult result = RuntimeResult.OK();
		sendDamageMessage(from, to, buff)
		return result;
	}

	/**
	 * 定时伤害的buff
	 * @param from
	 * @param to
	 * @param buff
	 */
	void sendDamageMessage(FightSprite from, FightSprite to ,MGFightSpriteBuff buff){
		MGFightSpriteBuffComponent fightSpriteBuffComponent = (MGFightSpriteBuffComponent) to.getTagged(MGFightSpriteBuffComponent.Tag);
		MGFightSpriteBuffMgr fightSpriteBuffMgr = fightSpriteBuffComponent.getFightSpriteBuffMgr();
		if(to instanceof Player && to.isInSafeRegion()){
			fightSpriteBuffMgr.removeBuff(buff);
			return;
		}
		String skillRefId = MGPropertyAccesser.getSkillRefId(buff.getSpecialProperty())
		int damage = MGPropertyAccesser.getHP(buff.getSpecialProperty())
		if(damage <= 0){
			return;
		}
		damage = damage < 0 ? 0 : damage;
		//魔法盾特殊处理
		for (MGFightSpriteBuff fightbuff : fightSpriteBuffMgr.getFightSpriteBuffList()) {
			if (MGSpecialBuff.isMagicShield(fightbuff.getFightSpriteBuffRef().getId())) {
				double skillDamageRate = MGPropertyAccesser.getSkillDamageRate(fightbuff.getSpecialProperty()) / 100.0;
				int crtHp = MGPropertyAccesser.getTotalValue(fightbuff.getSpecialProperty());
				int dxHp = (int) (damage * skillDamageRate);
				crtHp = crtHp - dxHp;
				damage = damage - dxHp;
				if (crtHp < 0) {
					fightSpriteBuffMgr.removeBuff(fightbuff);
				} else {
					MGPropertyAccesser.setOrPutHP(fightbuff.getSpecialProperty(), crtHp);
				}
			}
		}
		if(to.applyHP(from, -damage)){


			G2C_Effect_Buff message = (G2C_Effect_Buff) MessageFactory.getMessage(BuffEventDefines.G2C_Effect_Buff);
			message.setAttacker(from);
			message.setTarget(to);
			if(to.isDead()){
				message.setType(MGBuffEffectType.Die);
				message.setValue(-damage);
			}
			else{
				message.setType(MGBuffEffectType.HP);
				message.setValue(-damage);
				message.setCrtValue(to.getHP());
				message.setMaxValue(to.getHPMax());
			}
			GameSceneHelper.broadcastMessageToAOI(to,message);
		}
	}
}
