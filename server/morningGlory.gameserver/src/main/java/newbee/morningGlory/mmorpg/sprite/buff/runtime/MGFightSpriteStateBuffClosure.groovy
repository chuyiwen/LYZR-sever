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
import newbee.morningGlory.mmorpg.sprite.buff.actionEvent.BuffEventDefines
import newbee.morningGlory.mmorpg.sprite.buff.actionEvent.G2C_Effect_Buff

import org.apache.log4j.Logger

import sophia.foundation.communication.core.MessageFactory
import sophia.foundation.property.PropertyDictionary
import sophia.foundation.util.Position
import sophia.mmorpg.base.scene.GameSceneHelper
import sophia.mmorpg.base.sprite.FightSprite
import sophia.mmorpg.base.sprite.fightProperty.FightPropertyEffectFacade
import sophia.mmorpg.base.sprite.state.adjunction.BleedState
import sophia.mmorpg.base.sprite.state.adjunction.BurningState
import sophia.mmorpg.base.sprite.state.adjunction.DizzinessState
import sophia.mmorpg.base.sprite.state.adjunction.DumbingState
import sophia.mmorpg.base.sprite.state.adjunction.InvincibleState
import sophia.mmorpg.base.sprite.state.adjunction.MagicImmunityState
import sophia.mmorpg.base.sprite.state.adjunction.ParalysisState
import sophia.mmorpg.base.sprite.state.adjunction.PhysicalImmunityState
import sophia.mmorpg.base.sprite.state.adjunction.PoisoningState
import sophia.mmorpg.base.sprite.state.adjunction.SlowDownState
import sophia.mmorpg.base.sprite.state.adjunction.StealthState
import sophia.mmorpg.monster.Monster
import sophia.mmorpg.monster.ai.MonsterPerceiveComponent
import sophia.mmorpg.player.Player
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser
import sophia.mmorpg.ref.editorgen.MGPropertySymbolDefines
import sophia.mmorpg.utils.RuntimeResult



class MGFightSpriteStateBuffClosure {
	private static final Logger logger = Logger.getLogger(MGFightSpriteStateBuffClosure.class);
	Map<String, Closure<RuntimeResult>> map = new HashMap<>();
	MGFightSpriteStateBuffClosure(){
		map.put("attach_wuDi_state",attach_wuDi_state);
		map.put("detach_wuDi_state",detach_wuDi_state);

		map.put("attach_wuMian_state",attach_wuMian_state);
		map.put("detach_wuMian_state",detach_wuMian_state);

		map.put("attach_moMian_state",attach_moMian_state);
		map.put("detach_moMian_state",detach_moMian_state);

		map.put("attach_huanSu_state",attach_huanSu_state);
		map.put("detach_huanSu_state",detach_huanSu_state);

		map.put("attach_yunXuan_state",attach_yunXuan_state);
		map.put("detach_yunXuan_state",detach_yunXuan_state);

		map.put("attach_maBi_state",attach_maBi_state);
		map.put("detach_maBi_state",detach_maBi_state);

		map.put("attach_chenMo_state",attach_chenMo_state);
		map.put("detach_chenMo_state",detach_chenMo_state);

		map.put("attach_yinSheng_state",attach_yinSheng_state);
		map.put("detach_yinSheng_state",detach_yinSheng_state);

		map.put("attach_liuXue_state",attach_liuXue_state);
		map.put("detach_liuXue_state",detach_liuXue_state);

		map.put("attach_zhongDu_state",attach_zhongDu_state);
		map.put("detach_zhongDu_state",detach_zhongDu_state);

		map.put("attach_zhuoSao_state",attach_zhuoSao_state);
		map.put("detach_zhuoSao_state",detach_zhuoSao_state);

		map.put("attach_zhongDu2_state",attach_zhongDu2_state);
		map.put("detach_zhongDu2_state",detach_zhongDu2_state);
	}

	public Map<String, Closure<RuntimeResult>> getClosures() {
		return map;
	}

	/**
	 * 流血
	 */
	Closure<RuntimeResult> attach_liuXue_state = { FightSprite from, FightSprite to, MGFightSpriteBuff buff ->
		if(logger.isDebugEnabled()){
			logger.debug("附加流血状态")
		}
		RuntimeResult result = RuntimeResult.OK();
		to.changeState(BleedState.BleedState_Id);
		buff.setAttachedState(BleedState.BleedState_Id);
		return result;
	}


	Closure<RuntimeResult> detach_liuXue_state = { FightSprite from, FightSprite to,MGFightSpriteBuff buff ->
		if(logger.isDebugEnabled()){
			logger.debug("取消流血状态")
		}
		RuntimeResult result = RuntimeResult.OK();

		if(!isExistSameStateBuff(to, buff)){
			to.cancelState(BleedState.BleedState_Id);
		}
		buff.resetAttachedState();

		return result;
	}

	/**
	 * 中毒
	 */
	Closure<RuntimeResult> attach_zhongDu_state = { FightSprite from, FightSprite to, MGFightSpriteBuff buff ->
		if(logger.isDebugEnabled()){
			logger.debug("附加中毒状态")
		}
		RuntimeResult result = RuntimeResult.OK();
		to.changeState(PoisoningState.PoisoningState_Id);
		buff.setAttachedState(PoisoningState.PoisoningState_Id);
		return result;
	}


	Closure<RuntimeResult> detach_zhongDu_state = { FightSprite from, FightSprite to,MGFightSpriteBuff buff ->
		if(logger.isDebugEnabled()){
			logger.debug("取消中毒状态")
		}
		RuntimeResult result = RuntimeResult.OK();
		if(!isExistSameStateBuff(to, buff)){
			to.cancelState(PoisoningState.PoisoningState_Id);
		}
		buff.resetAttachedState();
		return result;
	}
	/**
	 * 灼烧
	 */
	Closure<RuntimeResult> attach_zhuoSao_state = { FightSprite from, FightSprite to, MGFightSpriteBuff buff ->
		if(logger.isDebugEnabled()){
			logger.debug("附加灼烧状态")
		}
		RuntimeResult result = RuntimeResult.OK();
		to.changeState(BurningState.BurningState_Id);
		buff.setAttachedState(BurningState.BurningState_Id);
		return result;
	}


	Closure<RuntimeResult> detach_zhuoSao_state = { FightSprite from, FightSprite to,MGFightSpriteBuff buff ->
		if(logger.isDebugEnabled()){
			logger.debug("取消灼烧状态")
		}
		RuntimeResult result = RuntimeResult.OK();
		if(!isExistSameStateBuff(to, buff)){
			to.cancelState(BurningState.BurningState_Id);
		}
		buff.resetAttachedState();

		return result;
	}
	/**
	 * 中毒2
	 */
	Closure<RuntimeResult> attach_zhongDu2_state = { FightSprite from, FightSprite to, MGFightSpriteBuff buff ->
		RuntimeResult result = RuntimeResult.OK();
		double percent = MGPropertyAccesser.getSkillDamageRate(buff.getSpecialProperty()) /100.0
		if(percent < 0) {

			return;
		}
		percent = percent < 0 ? 0 : percent;
		PropertyDictionary playerPd = to.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotByNew().getPropertyDictionary()
		int maxMDef = MGPropertyAccesser.getMaxMDef(playerPd) ;
		int maxPDef = MGPropertyAccesser.getMaxPDef(playerPd) ;
		int subMaxMDef = maxMDef * percent;
		int subMaxPDef = maxPDef * percent;
		PropertyDictionary pd = new PropertyDictionary();
		MGPropertyAccesser.setOrPutMaxMDef(pd, subMaxMDef)
		MGPropertyAccesser.setOrPutMaxPDef(pd, subMaxPDef)
		MGPropertyAccesser.setOrPutMaxMDef(buff.getSpecialProperty(), subMaxMDef)
		MGPropertyAccesser.setOrPutMaxPDef(buff.getSpecialProperty(), subMaxPDef)
		FightPropertyEffectFacade.detachAndNotifySprite(to, pd);

		to.changeState(PoisoningState.PoisoningState_Id);
		buff.setAttachedState(PoisoningState.PoisoningState_Id);
		return result;
	}


	Closure<RuntimeResult> detach_zhongDu2_state = { FightSprite from, FightSprite to,MGFightSpriteBuff buff ->
		RuntimeResult result = RuntimeResult.OK();

		double percent = MGPropertyAccesser.getSkillDamageRate(buff.getSpecialProperty()) /100.0
		if(percent < 0) {
			return;
		}
		percent = percent < 0 ? 0 : percent;
		PropertyDictionary playerPd = to.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotByNew().getPropertyDictionary()
		int subMaxMDef = MGPropertyAccesser.getMaxMDef(buff.getSpecialProperty()) ;
		int subMaxPDef = MGPropertyAccesser.getMaxPDef(buff.getSpecialProperty()) ;
		subMaxMDef = subMaxMDef < 0 ? 0 : subMaxMDef;
		subMaxPDef = subMaxPDef < 0 ? 0 : subMaxPDef;
		PropertyDictionary pd = new PropertyDictionary();
		MGPropertyAccesser.setOrPutMaxMDef(pd, subMaxMDef)
		MGPropertyAccesser.setOrPutMaxPDef(pd, subMaxPDef)
		FightPropertyEffectFacade.attachAndNotifySprite(to, pd);
		if(!isExistSameStateBuff(to, buff)){
			to.cancelState(PoisoningState.PoisoningState_Id);
		}
		buff.resetAttachedState();
		return result;
	}
	/**
	 * 无敌
	 */
	Closure<RuntimeResult> attach_wuDi_state = { FightSprite from, FightSprite to, MGFightSpriteBuff buff ->
		if(logger.isDebugEnabled()){
			logger.debug("无敌状态")
		}
		RuntimeResult result = RuntimeResult.OK();
		to.changeState(InvincibleState.InvincibleState_Id);
		buff.setAttachedState(InvincibleState.InvincibleState_Id);
		return result;
	}


	Closure<RuntimeResult> detach_wuDi_state = { FightSprite from, FightSprite to,MGFightSpriteBuff buff ->
		if(logger.isDebugEnabled()){
			logger.debug("无敌状态")
		}
		RuntimeResult result = RuntimeResult.OK();
		if(!isExistSameStateBuff(to, buff)){
			to.cancelState(InvincibleState.InvincibleState_Id);
		}
		buff.resetAttachedState();
		return result;
	}


	/**
	 * 物免
	 */
	Closure<RuntimeResult> attach_wuMian_state = { FightSprite from, FightSprite to, MGFightSpriteBuff buff ->
		if(logger.isDebugEnabled()){
			logger.debug("物免状态")
		}
		RuntimeResult result = RuntimeResult.OK();
		to.changeState(PhysicalImmunityState.PhysicalImmunityState_Id);
		buff.setAttachedState(PhysicalImmunityState.PhysicalImmunityState_Id);
		return result;
	}


	Closure<RuntimeResult> detach_wuMian_state = { FightSprite from, FightSprite to,MGFightSpriteBuff buff ->
		if(logger.isDebugEnabled()){
			logger.debug("取消物免状态")
		}
		RuntimeResult result = RuntimeResult.OK();
		if(!isExistSameStateBuff(to, buff)){
			to.cancelState(PhysicalImmunityState.PhysicalImmunityState_Id)
		}
		buff.resetAttachedState();
		return result;
	}

	/**
	 *  魔免
	 */
	Closure<RuntimeResult> attach_moMian_state = { FightSprite from, FightSprite to, MGFightSpriteBuff buff ->
		if(logger.isDebugEnabled()){
			logger.debug("魔免状态")
		}
		RuntimeResult result = RuntimeResult.OK();
		to.changeState(MagicImmunityState.MagicImmunityState_Id);
		buff.setAttachedState(MagicImmunityState.MagicImmunityState_Id);
		return result;
	}


	Closure<RuntimeResult> detach_moMian_state = { FightSprite from, FightSprite to,MGFightSpriteBuff buff ->
		if(logger.isDebugEnabled()){
			logger.debug("取消魔免状态")
		}
		RuntimeResult result = RuntimeResult.OK();
		if(!isExistSameStateBuff(to, buff)){
			to.cancelState(MagicImmunityState.MagicImmunityState_Id);
		}
		buff.resetAttachedState();
		return result;
	}

	/**
	 *  缓速
	 */
	Closure<RuntimeResult> attach_huanSu_state = { FightSprite from, FightSprite to, MGFightSpriteBuff buff ->
		if(logger.isDebugEnabled()){
			logger.debug("缓速状态")
		}

		RuntimeResult result = RuntimeResult.OK();
		int moveSpeedPer =  MGPropertyAccesser.getMoveSpeedPer(buff.getSpecialProperty());
		int targetMoveSpeedPer = to.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotValueById(MGPropertySymbolDefines.MoveSpeedPer_Id);
		if(to instanceof Monster && targetMoveSpeedPer < 0){
			int defaultMoveSpeedPer = -90;
			int newMoveSpeedPer = targetMoveSpeedPer - defaultMoveSpeedPer;
			moveSpeedPer = newMoveSpeedPer > moveSpeedPer ? moveSpeedPer : newMoveSpeedPer;
			MGPropertyAccesser.setOrPutMoveSpeedPer(buff.getSpecialProperty(), moveSpeedPer);
		}
		PropertyDictionary pd = new PropertyDictionary(2);
		MGPropertyAccesser.setOrPutMoveSpeedPer(pd, moveSpeedPer);
		FightPropertyEffectFacade.detachAndNotifySprite(to, pd);
		pd.clear();
		if(to.getCrtScene() != null){
			int crtMoveSpeedPer = to.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotValueById(MGPropertySymbolDefines.MoveSpeedPer_Id);
			int crtMoveSpeed = to.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotValueById(MGPropertySymbolDefines.MoveSpeed_Id);
			MGPropertyAccesser.setOrPutMoveSpeedPer(pd, crtMoveSpeedPer);
			MGPropertyAccesser.setOrPutMoveSpeed(pd, crtMoveSpeed);
			to.getPathComponent().setMoveSpeed(crtMoveSpeed);
			to.getAoiComponent().broadcastProperty(pd);
			if(to instanceof Player){
				to = (Player) to;
				to.notifyPorperty(pd);
			}
		}
		to.changeState(SlowDownState.SlowDownState_Id);
		buff.setAttachedState(SlowDownState.SlowDownState_Id);
		return result;
	}


	Closure<RuntimeResult> detach_huanSu_state = { FightSprite from, FightSprite to,MGFightSpriteBuff buff ->
		if(logger.isDebugEnabled()){
			logger.debug("取消缓速状态")
		}
		RuntimeResult result = RuntimeResult.OK();

		int moveSpeedPer = MGPropertyAccesser.getMoveSpeedPer(buff.getSpecialProperty());

		PropertyDictionary pd = new PropertyDictionary(2);
		MGPropertyAccesser.setOrPutMoveSpeedPer(pd, moveSpeedPer);
		FightPropertyEffectFacade.attachAndNotifySprite(to, pd);
		pd.clear();

		if(to.getCrtScene() != null){
			int crtMoveSpeedPer = to.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotValueById(MGPropertySymbolDefines.MoveSpeedPer_Id);
			int crtMoveSpeed = to.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotValueById(MGPropertySymbolDefines.MoveSpeed_Id);
			MGPropertyAccesser.setOrPutMoveSpeedPer(pd, crtMoveSpeedPer);
			MGPropertyAccesser.setOrPutMoveSpeed(pd, crtMoveSpeed);
			to.getPathComponent().setMoveSpeed(crtMoveSpeed);
			to.getAoiComponent().broadcastProperty(pd);
			if(to instanceof Player){
				to = (Player) to;
				to.notifyPorperty(pd);
			}
		}
		if(!isExistSameStateBuff(to, buff)){
			to.cancelState(SlowDownState.SlowDownState_Id);
		}
		buff.resetAttachedState();
		return result;
	}


	/**
	 *  晕眩
	 */
	Closure<RuntimeResult> attach_yunXuan_state = { FightSprite from, FightSprite to, MGFightSpriteBuff buff ->
		if(logger.isDebugEnabled()) {
			logger.debug("晕眩状态, from=" + from + ", to=" + to);
		}
		RuntimeResult result = RuntimeResult.OK();

		Position pos = to.getCrtPosition();
		to.getPathComponent().stopMove(pos);

		G2C_Effect_Buff message = (G2C_Effect_Buff) MessageFactory.getMessage(BuffEventDefines.G2C_Effect_Buff);
		message.setAttacker(from);
		message.setTarget(to);
		message.setType(MGBuffEffectType.Move);
		message.setValue(0);
		message.setPositionX(pos.getX());
		message.setPositionY(pos.getY());
		GameSceneHelper.broadcastMessageToAOI(to,message);

		to.changeState(DizzinessState.DizzinessState_Id)
		buff.setAttachedState(DizzinessState.DizzinessState_Id);
		return result;
	}


	Closure<RuntimeResult> detach_yunXuan_state = { FightSprite from, FightSprite to,MGFightSpriteBuff buff ->
		if(logger.isDebugEnabled()){
			logger.debug("取消晕眩状态, from=" + from + ", to=" + to);
		}
		RuntimeResult result = RuntimeResult.OK();
		if(!isExistSameStateBuff(to, buff)){
			to.cancelState(DizzinessState.DizzinessState_Id);
		}
		buff.resetAttachedState();
		return result;
	}



	/**
	 *  麻痹
	 */
	Closure<RuntimeResult> attach_maBi_state = { FightSprite from, FightSprite to, MGFightSpriteBuff buff ->
		if(logger.isDebugEnabled()){
			logger.debug("麻痹状态, from=" + from + ", to=" + to);
		}
		RuntimeResult result = RuntimeResult.OK();
		Position pos = to.getCrtPosition();
		to.getPathComponent().stopMove(pos);

		G2C_Effect_Buff message = (G2C_Effect_Buff) MessageFactory.getMessage(BuffEventDefines.G2C_Effect_Buff);
		message.setAttacker(from);
		message.setTarget(to);
		message.setType(MGBuffEffectType.Move);
		message.setValue(0);
		message.setPositionX(pos.getX());
		message.setPositionY(pos.getY());
		GameSceneHelper.broadcastMessageToAOI(to,message);

		to.changeState(ParalysisState.ParalysisState_Id)
		buff.setAttachedState(ParalysisState.ParalysisState_Id);

		return result;
	}


	Closure<RuntimeResult> detach_maBi_state = { FightSprite from, FightSprite to,MGFightSpriteBuff buff ->
		if(logger.isDebugEnabled()){
			logger.debug("取消麻痹状态, from=" + from + ", to=" + to);
		}
		RuntimeResult result = RuntimeResult.OK();
		if(!isExistSameStateBuff(to, buff)){
			to.cancelState(ParalysisState.ParalysisState_Id);
		}
		buff.resetAttachedState();
		return result;
	}




	/**
	 *  沉默
	 */
	Closure<RuntimeResult> attach_chenMo_state = { FightSprite from, FightSprite to, MGFightSpriteBuff buff ->
		if(logger.isDebugEnabled()){
			logger.debug("沉默状态")
		}
		RuntimeResult result = RuntimeResult.OK();
		to.changeState(DumbingState.DumbingState_Id);
		buff.setAttachedState(DumbingState.DumbingState_Id)
		return result;
	}


	Closure<RuntimeResult> detach_chenMo_state = { FightSprite from, FightSprite to,MGFightSpriteBuff buff ->
		if(logger.isDebugEnabled()){
			logger.debug("取消沉默状态")
		}
		RuntimeResult result = RuntimeResult.OK();
		if(!isExistSameStateBuff(to, buff)){
			to.cancelState(DumbingState.DumbingState_Id);
		}
		buff.resetAttachedState();
		return result;
	}

	/**
	 *  隐身
	 */
	Closure<RuntimeResult> attach_yinSheng_state = { FightSprite from, FightSprite to, MGFightSpriteBuff buff ->
		if(logger.isDebugEnabled()){
			logger.debug("隐身状态")
		}
		RuntimeResult result = RuntimeResult.OK();
		Collection<FightSprite> fightSprites = GameSceneHelper.getAOIInterestedFightSprites(to);
		for(FightSprite fightSprite : fightSprites){
			if(fightSprite instanceof Monster){
				fightSprite = (Monster) fightSprite;
				if(fightSprite.getOwner() == null)
					((MonsterPerceiveComponent)fightSprite.getPerceiveComponent()).forsakeTarget();
			}
		}
		to.changeState(StealthState.StealthState_Id);
		buff.setAttachedState(StealthState.StealthState_Id);
		return result;
	}


	Closure<RuntimeResult> detach_yinSheng_state = { FightSprite from, FightSprite to,MGFightSpriteBuff buff ->
		if(logger.isDebugEnabled()){
			logger.debug("取消隐身状态")
		}
		RuntimeResult result = RuntimeResult.OK();
		if(!isExistSameStateBuff(to, buff)){
			to.cancelState(StealthState.StealthState_Id);
		}
		buff.resetAttachedState();
		return result;
	}


	boolean isExistSameStateBuff(FightSprite to,MGFightSpriteBuff buff){
		MGFightSpriteBuffComponent buffComponent = (MGFightSpriteBuffComponent)to.getTagged(MGFightSpriteBuffComponent.Tag);
		MGFightSpriteBuff sameStateBuff = buffComponent.getTheSameStateBuff(buff);
		if(sameStateBuff != null){
			return true;
		}
		return false;
	}
}
