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
package newbee.morningGlory.mmorpg.sprite.buff;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import newbee.morningGlory.code.MGErrorCode;
import newbee.morningGlory.mmorpg.sprite.MGFightProcessComponent;
import newbee.morningGlory.mmorpg.sprite.MGFightProcessHelper;
import newbee.morningGlory.mmorpg.sprite.buff.actionEvent.BuffEventDefines;
import newbee.morningGlory.mmorpg.sprite.buff.actionEvent.G2C_Attach_Buff;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.communication.core.MessageFactory;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.base.sprite.fightSkill.gameevent.AfterAttack_GE;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.property.PlayerConfig;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.RuntimeResult;

public class MGFightSpriteBuffMgr {
	private static final Logger logger = Logger.getLogger(MGFightSpriteBuffMgr.class);
	private final CopyOnWriteArrayList<MGFightSpriteBuff> fightSpriteBuffList = new CopyOnWriteArrayList<>();
	private static final int ApplicationResult = 10000;
	private static final int BuffCountLimit = 100;
	private volatile boolean canAttachBuff = true;
	private FightSprite owner;

	public MGFightSpriteBuffMgr() {

	}

	public void tick() {
		if (fightSpriteBuffList.isEmpty()) {
			return;
		}

		for (MGFightSpriteBuff buff : fightSpriteBuffList) {
			buff.tick();
			if (buff.isFinished()) {
				removeBuff(buff);
			}
		}
	}

	public List<MGFightSpriteBuff> getFightSpriteBuffList() {
		return fightSpriteBuffList;
	}

	public RuntimeResult attachFightSpriteBuff(MGFightSpriteBuff buff) {
		RuntimeResult ret = RuntimeResult.OK();
		if (!canAttachBuff) {
			if (logger.isDebugEnabled()) {
				logger.debug("attachFightSpriteBuff canAttachBuff is false, owner=" + owner);
			}
			return RuntimeResult.ParameterError();
		}

		int count = fightSpriteBuffList.size();
		if (count >= BuffCountLimit) {
			if (logger.isDebugEnabled()) {
				logger.debug("attachFightSpriteBuff bufflist count=" + count + ", owner=" + owner);
			}
		}

		RuntimeResult addBuff = addBuff(buff);
		if (!addBuff.isOK()) {
			return addBuff;
		}
		sendAfterAttackGameEvent(buff);
		return ret;
	}

	public synchronized RuntimeResult valid(MGFightSpriteBuff buff) {
		if (buff.getAttachFightSprite().isDead() && buff.getFightSpriteBuffRef().isClearOnDeadBuff()) {
			return RuntimeResult.ParameterError();
		}

		RuntimeResult pkModelValid = pkModelValid(buff);
		if (!pkModelValid.isOK()) {
			return pkModelValid;
		}

		MGFightSpriteBuffRef buffRef = buff.getFightSpriteBuffRef();
		for (MGFightSpriteBuff spriteBuff : this.fightSpriteBuffList) {
			if (StringUtils.equals(spriteBuff.getFightSpriteBuffRef().getId(), buffRef.getId())) { // 如果是同一个buff效果
				if (buffRef.getAttachRepeatRuleType() == MGFightSpriteBuffAttachRepeatRuleType.OnlySingle_Buff) { // 效果不叠加，取最后一个

					removeBuff(spriteBuff);
					return RuntimeResult.OK();

				} else if (buffRef.getAttachRepeatRuleType() == MGFightSpriteBuffAttachRepeatRuleType.RepeatTime_Buff) { // 时间累加

					long duration = buff.getDuration();
					long expiration = spriteBuff.getExpiration();
					expiration = expiration + duration;
					spriteBuff.setExpiration(expiration);
					spriteBuff.addDuration(duration);
					sendActionEvent(spriteBuff.getFightSpriteBuffRef().getId(), MGBuffEffectType.Detach, spriteBuff);
					sendActionEvent(spriteBuff.getFightSpriteBuffRef().getId(), MGBuffEffectType.Attach, spriteBuff);
					return RuntimeResult.RuntimeApplicationError(ApplicationResult);

				} else if (buffRef.getAttachRepeatRuleType() == MGFightSpriteBuffAttachRepeatRuleType.Repeat_Buff) { // 效果value叠加

					byte attachRepeatCount = MGPropertyAccesser.getAttachRepeatCount(buff.getSpecialProperty());
					attachRepeatCount = attachRepeatCount < 0 ? MGPropertyAccesser.getAttachRepeatCount(buff.getFightSpriteBuffRef().getProperty()) : attachRepeatCount;
					if (attachRepeatCount == 0) {
						removeBuff(spriteBuff);
						return RuntimeResult.OK();
					}
					String from = MGPropertyAccesser.getBuffSourece(buff.getSpecialProperty());
					int count = 0;
					for (MGFightSpriteBuff bf : this.fightSpriteBuffList) {
						String f1 = MGPropertyAccesser.getBuffSourece(bf.getSpecialProperty());
						if (StringUtils.equals(bf.getFightSpriteBuffRef().getId(), buffRef.getId()) && StringUtils.equals(from, f1)) {
							count++;

						}
					}
					if (count < attachRepeatCount) {
						return RuntimeResult.OK();
					}
					return RuntimeResult.ParameterError();

				} else if (buffRef.getAttachRepeatRuleType() == MGFightSpriteBuffAttachRepeatRuleType.RepeatValue_Buff) {

					int crtValue = MGPropertyAccesser.getTotalValue(spriteBuff.getSpecialProperty());
					int initValue = MGPropertyAccesser.getTotalValue(buffRef.getEffectProperty());
					int initRepeatCount = MGPropertyAccesser.getAttachRepeatCount(buffRef.getProperty());
					int attachRepeatCount = crtValue % initValue == 0 ? crtValue / initValue : crtValue / initValue + 1;
					if (attachRepeatCount >= initRepeatCount) {
						return RuntimeResult.ParameterError();
					}
					MGPropertyAccesser.setOrPutTotalValue(spriteBuff.getSpecialProperty(), crtValue + initValue);
					return RuntimeResult.RuntimeApplicationError(ApplicationResult);

				}

			}

		}

		for (MGFightSpriteBuff spriteBuff : this.fightSpriteBuffList) {
			if (spriteBuff.getFightSpriteBuffRef().getGroupId() == buffRef.getGroupId()) { // 如果是同组的
				if (spriteBuff.getFightSpriteBuffRef().getAttachGorupRuleType() == MGFightSpriteBuffAttachGroupRuleType.OnlySingle_Buff) {
					if (spriteBuff.getFightSpriteBuffRef().getWeightOfGroup() <= buffRef.getWeightOfGroup()) {
						removeBuff(spriteBuff);
						return RuntimeResult.OK();
					}
					return RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_BUFF_BETTER);
				}
			}
		}
		return RuntimeResult.OK();
	}

	private synchronized RuntimeResult pkModelValid(MGFightSpriteBuff buff) {
		if (buff.getFightSpriteBuffRef().isPkModelCheckBuff()) { // pk模式判定
			FightSprite owner = buff.getOwner();
			FightSprite attcker = buff.getAttachFightSprite();
			MGFightProcessComponent<?> fightProcessComponent = (MGFightProcessComponent<?>) owner.getTagged(MGFightProcessComponent.Tag);
			byte attackType = MGFightProcessHelper.P_ATTACK;
			if (attcker instanceof Player) {
				Player attacker = (Player) attcker;
				if (PlayerConfig.isWarrior(attacker.getProfession())) {
					attackType = MGFightProcessHelper.P_ATTACK;
				} else if (PlayerConfig.isEnchanter(attacker.getProfession())) {
					attackType = MGFightProcessHelper.M_ATTACK;
				} else {
					attackType = MGFightProcessHelper.D_ATTACK;
				}
			}

			if (!fightProcessComponent.isValidAttackState(buff.getAttachFightSprite(), owner, attackType)) {
				return RuntimeResult.ParameterError();
			}
		}

		return RuntimeResult.OK();
	}

	private void sendAfterAttackGameEvent(MGFightSpriteBuff buff) {

		if (!buff.getFightSpriteBuffRef().isPositiveBuff() && buff.isSendAttackEvent()) {
			FightSprite attacker = buff.getOwner();
			FightSprite target = buff.getAttachFightSprite();
			int damage = MGPropertyAccesser.getHP(buff.getSpecialProperty());
			damage = damage < 0 ? 10 : damage;
			AfterAttack_GE after = new AfterAttack_GE(attacker, target, damage);
			sendGameEvent(AfterAttack_GE.class.getSimpleName(), after, attacker);
			sendGameEvent(AfterAttack_GE.class.getSimpleName(), after, target);
		}

	}

	private void sendGameEvent(String id, Object data, FightSprite target) {
		GameEvent<Object> event = GameEvent.getInstance(id, data);
		target.handleGameEvent(event);
		GameEvent.pool(event);
	}

	public synchronized void removeAll() {
		this.fightSpriteBuffList.clear();
	}

	public synchronized RuntimeResult addBuff(MGFightSpriteBuff buff) {
		RuntimeResult ret = this.valid(buff);
		if (ret.getApplicationCode() == ApplicationResult) {
			return RuntimeResult.OK();
		}

		if (!ret.isOK()) {
			return ret;
		}

		sendActionEvent(buff.getFightSpriteBuffRef().getId(), MGBuffEffectType.Attach, buff);
		fightSpriteBuffList.add(buff);

		if (buff.getFightSpriteBuffRef().getAttachClosure() != null) {
			ret = buff.attach();
			if (!ret.isOK()) {
				removeBuff(buff);
				return RuntimeResult.ParameterError();
			}
		}

		buff.effectTo();

		return RuntimeResult.OK();
	}

	public synchronized void removeBuff(MGFightSpriteBuff buff) {

		if (this.fightSpriteBuffList.remove(buff)) {
			buff.detach();
			sendActionEvent(buff.getFightSpriteBuffRef().getId(), MGBuffEffectType.Detach, buff);
		}
	}

	public synchronized void removeBuffNoNotify(MGFightSpriteBuff buff) {

		if (this.fightSpriteBuffList.remove(buff)) {
			buff.detach();
		}
	}

	public FightSprite getOwner() {
		return owner;
	}

	public void setOwner(FightSprite owner) {
		this.owner = owner;
	}

	public void sendActionEvent(String buffRefId, byte type, MGFightSpriteBuff buff) {
		G2C_Attach_Buff message = (G2C_Attach_Buff) MessageFactory.getMessage(BuffEventDefines.G2C_Attach_Buff);
		message.setAttacker(buff.getOwner());
		message.setTarget(buff.getAttachFightSprite());
		message.setBuffRefId(buffRefId);
		message.setType(type);
		message.setCreateTime(buff.getCreateTime());
		message.setDuration(buff.getCrtDuration());
		message.setAbsoluteDuration(buff.getAbsoluteDuration());
		byte isPositiveBuff = MGPropertyAccesser.getIsPositiveBuff(buff.getFightSpriteBuffRef().getProperty());
		if (this.owner instanceof Player && ((isPositiveBuff == 1 || isPositiveBuff == 0))) {
			GameRoot.sendMessage(((Player) this.owner).getIdentity(), message);
		}

	}

	public boolean isCanAttachBuff() {
		return canAttachBuff;
	}

	public void setCanAttachBuff(boolean canAttachBuff) {
		this.canAttachBuff = canAttachBuff;
	}

}
