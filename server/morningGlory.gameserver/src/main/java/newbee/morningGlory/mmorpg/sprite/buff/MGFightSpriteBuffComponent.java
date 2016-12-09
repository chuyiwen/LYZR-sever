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

import java.util.ArrayList;
import java.util.List;

import newbee.morningGlory.mmorpg.sprite.MGFightProcessComponent;
import newbee.morningGlory.mmorpg.sprite.buff.actionEvent.BuffEventDefines;
import newbee.morningGlory.mmorpg.sprite.buff.actionEvent.C2G_MoXueShi_Amount;
import newbee.morningGlory.mmorpg.sprite.buff.actionEvent.G2C_Buff_List;
import newbee.morningGlory.mmorpg.sprite.buff.actionEvent.G2C_MoXueShi_Amount;
import newbee.morningGlory.mmorpg.sprite.buff.actionEvent.G2C_State_Buff;
import newbee.morningGlory.mmorpg.sprite.buff.persistence.BuffPersistenceObject;

import org.apache.commons.lang3.StringUtils;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.game.GameRoot;
import sophia.game.component.GameObject;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.base.sprite.fightSkill.gameevent.AfterAttack_GE;
import sophia.mmorpg.base.sprite.state.adjunction.StealthState;
import sophia.mmorpg.base.sprite.state.gameEvent.StealthStateExit_GE;
import sophia.mmorpg.core.timer.SFTimeChimeListener;
import sophia.mmorpg.core.timer.SFTimer;
import sophia.mmorpg.core.timer.SFTimerCreater;
import sophia.mmorpg.monster.Monster;
import sophia.mmorpg.monster.gameEvent.MonsterDead_GE;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.gameEvent.ChineseModeQuest_GE;
import sophia.mmorpg.player.gameEvent.LeaveWorld_GE;
import sophia.mmorpg.player.gameEvent.PlayerDead_GE;
import sophia.mmorpg.player.mount.gameEvent.PlayerMountState_GE;
import sophia.mmorpg.player.persistence.immediatelySave.PlayerImmediateDaoFacade;
import sophia.mmorpg.player.quest.ref.order.QuestChineseOrderDefines;
import sophia.mmorpg.player.scene.PlayerSceneComponent;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class MGFightSpriteBuffComponent<T extends FightSprite> extends ConcreteComponent<T> {
	private final MGFightSpriteBuffMgr fightSpriteBuffMgr = new MGFightSpriteBuffMgr();
	public static final String Tag = "MGFightSpriteBuffComponent";
	private BuffPersistenceObject persistenceObject;
	private static final String MonsterDead_GE_Id = MonsterDead_GE.class.getSimpleName();
	private static final String PlayerDead_GE_Id = PlayerDead_GE.class.getSimpleName();
	private static final String AfterAttack_GE_Id = AfterAttack_GE.class.getSimpleName();
	private static final String PlayerExitStealth_GE_Id = StealthStateExit_GE.class.getSimpleName();
	private static final String LeaveWorld_GE_Id = LeaveWorld_GE.class.getSimpleName();
	private static final String PlayerMountState_GE_Id = PlayerMountState_GE.class.getSimpleName();
	private static final String ChineseModeQuest_GE_Id = ChineseModeQuest_GE.class.getSimpleName();
	private SFTimer timer;

	public MGFightSpriteBuffComponent() {

	}

	@Override
	public void ready() {
		addActionEventListener(BuffEventDefines.C2G_MoXueShi_Amount);
		addInterGameEventListener(MonsterDead_GE_Id);
		addInterGameEventListener(PlayerDead_GE_Id);
		addInterGameEventListener(AfterAttack_GE_Id);
		addInterGameEventListener(PlayerExitStealth_GE_Id);
		addInterGameEventListener(LeaveWorld_GE_Id);
		addInterGameEventListener(PlayerMountState_GE_Id);
		addInterGameEventListener(ChineseModeQuest_GE_Id);
		addInterGameEventListener(PlayerSceneComponent.EnterWorld_SceneReady_GE_Id);
		if (!(getParent() instanceof Player)) {
			tick();
		}
	}

	@Override
	public void suspend() {
		removeActionEventListener(BuffEventDefines.G2C_MoXueShi_Amount);
		removeInterGameEventListener(MonsterDead_GE_Id);
		removeInterGameEventListener(PlayerDead_GE_Id);
		removeInterGameEventListener(AfterAttack_GE_Id);
		removeInterGameEventListener(PlayerExitStealth_GE_Id);
		removeInterGameEventListener(LeaveWorld_GE_Id);
		removeInterGameEventListener(PlayerMountState_GE_Id);
		removeInterGameEventListener(ChineseModeQuest_GE_Id);
		removeInterGameEventListener(PlayerSceneComponent.EnterWorld_SceneReady_GE_Id);
		if (timer != null) {
			timer.cancel();
		}
	}

	@Override
	public void destroy() {
		if (timer != null) {
			timer.cancel();
		}
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {
		if (event.isId(PlayerSceneComponent.EnterWorld_SceneReady_GE_Id)) {
			handle_Buff_List();
			if (getParent() instanceof Player) {
				tick();
			}
		} else if (event.isId(MonsterDead_GE_Id)) {
			MonsterDead_GE monsterDead_GE = (MonsterDead_GE) event.getData();
			Monster monster = monsterDead_GE.getMonster();
			if (StringUtils.equals(getParent().getId(), monster.getId()))
				isClearOnDead();
		} else if (event.isId(PlayerDead_GE_Id)) {
			PlayerDead_GE playerDead_GE = (PlayerDead_GE) event.getData();
			Player player = playerDead_GE.getPlayer();
			if (StringUtils.equals(getParent().getId(), player.getId()))
				isClearOnDead();

		} else if (event.isId(PlayerExitStealth_GE_Id)) {
			FightSprite sprite = ((StealthStateExit_GE) event.getData()).getFightSprite();
			cancelStealthBuff(sprite);
		} else if (event.isId(LeaveWorld_GE_Id)) {
			isClearLoginOut();
		} else if (event.isId(AfterAttack_GE_Id)) {
			String buffRefId = "buff_skill_3";
			String buffRefId_ext = "buff_skill_3_1";
			FightSprite attacker = ((AfterAttack_GE) event.getData()).getAttacker();
			FightSprite target = ((AfterAttack_GE) event.getData()).getTarget();
			cancelStealthBuff(attacker);
			if (StringUtils.equals(getParent().getId(), target.getId())) {
				for (MGFightSpriteBuff buff : fightSpriteBuffMgr.getFightSpriteBuffList()) {
					if (StringUtils.equals(buff.getFightSpriteBuffRef().getId(), buffRefId) || StringUtils.equals(buff.getFightSpriteBuffRef().getId(), buffRefId_ext)) {
						MGFightProcessComponent<?> processComponent = (MGFightProcessComponent<?>) attacker.getTagged(MGFightProcessComponent.Tag);

						int damage = ((AfterAttack_GE) event.getData()).getDamge();
						double skillDamageRate = MGPropertyAccesser.getSkillDamageRate(buff.getSpecialProperty()) / 100.0;
						int crtHp = MGPropertyAccesser.getTotalValue(buff.getSpecialProperty());
						int subHp = (int) (damage * skillDamageRate);

						processComponent.setDamageFromOutSide(subHp);
						crtHp = crtHp - subHp;
						if (crtHp < 0) {
							this.fightSpriteBuffMgr.removeBuff(buff);
						} else {
							MGPropertyAccesser.setOrPutTotalValue(buff.getSpecialProperty(), crtHp);
						}
					}
				}
			}
		} else if (event.isId(ChineseModeQuest_GE_Id)) {
			ChineseModeQuest_GE chineseModeQuest_GE = (ChineseModeQuest_GE) event.getData();
			if (chineseModeQuest_GE.getType() == ChineseModeQuest_GE.AcceptType && chineseModeQuest_GE.getOrderEventId() == QuestChineseOrderDefines.HasBuffer) {
				for (MGFightSpriteBuff buff : fightSpriteBuffMgr.getFightSpriteBuffList()) {
					if (StringUtils.equals(buff.getFightSpriteBuffRef().getId(), chineseModeQuest_GE.getChineseModeTarget())) {
						chineseModeQuest_GE.setOrderEventId(QuestChineseOrderDefines.HasBuffer);
						chineseModeQuest_GE.setType(ChineseModeQuest_GE.CourseType);
						chineseModeQuest_GE.setChineseModeTarget(buff.getFightSpriteBuffRef().getId());
						GameEvent<ChineseModeQuest_GE> chinese = GameEvent.getInstance(ChineseModeQuest_GE_Id, chineseModeQuest_GE);
						sendGameEvent(chinese, getConcreteParent().getId());
					}
				}
			}
		}
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
		short actionEventId = event.getActionEventId();

		switch (actionEventId) {
		case BuffEventDefines.C2G_MoXueShi_Amount:
			handle_MoXueShi_Amount((C2G_MoXueShi_Amount) event);
			break;
		default:
			break;
		}
	}

	public void tick() {
		fightSpriteBuffMgr.setCanAttachBuff(true);
		if (timer != null) {
			timer.cancel();
		}
		SFTimerCreater timerCreater = MMORPGContext.getTimerCreater();
		timer = timerCreater.secondInterval(new SFTimeChimeListener() {
			@Override
			public void handleTimeChimeCancel() {

			}

			@Override
			public void handleTimeChime() {
				fightSpriteBuffMgr.tick();
			}

			@Override
			public void handleServiceShutdown() {

			}
		});
	}

	/**
	 * 显示魔血石当前容量
	 * 
	 * @param event
	 */
	private void handle_MoXueShi_Amount(C2G_MoXueShi_Amount event) {
		if (!(getParent() instanceof Player)) {
			return;
		}
		Player player = (Player) getParent();
		for (MGFightSpriteBuff buff : fightSpriteBuffMgr.getFightSpriteBuffList()) {
			if (MGSpecialBuff.buffStringList.contains(buff.getFightSpriteBuffRef().getId())) {
				int amount = MGPropertyAccesser.getTotalValue(buff.getSpecialProperty());
				G2C_MoXueShi_Amount res = MessageFactory.getConcreteMessage(BuffEventDefines.G2C_MoXueShi_Amount);
				res.setBuffRefId(buff.getFightSpriteBuffRef().getId());
				res.setCreateTime(buff.getCreateTime());
				res.setAmount(amount);
				GameRoot.sendMessage(player.getIdentity(), res);
				break;
			}
		}

	}

	private void handle_Buff_List() {

		if (!(getParent() instanceof Player)) {
			return;
		}
		loginReAddBuff();

		G2C_Buff_List res = (G2C_Buff_List) MessageFactory.getMessage(BuffEventDefines.G2C_Buff_List);
		List<MGFightSpriteBuff> buffList = new ArrayList<>();
		buffList.addAll(fightSpriteBuffMgr.getFightSpriteBuffList());
		Player player = (Player) getParent();

		if (!buffList.isEmpty()) {
			res.setFightSpriteBuffs(buffList);
			GameRoot.sendMessage(player.getIdentity(), res);
		}
	}

	private void cancelStealthBuff(FightSprite sprite) {
		MGFightSpriteBuff buff = getBuffFromList("buff_state_13");
		if (buff == null) {
			return;
		}
		if (sprite instanceof Player) {
			G2C_State_Buff res = (G2C_State_Buff) MessageFactory.getMessage(BuffEventDefines.G2C_State_Buff);
			res.setStateId(StealthState.StealthState_Id);
			res.setStateType((byte) 0);
			GameRoot.sendMessage(((Player) sprite).getIdentity(), res);
		}
		this.getFightSpriteBuffMgr().removeBuff(buff);

	}

	private MGFightSpriteBuff getBuffFromList(String buffRefId) {
		for (MGFightSpriteBuff buff : fightSpriteBuffMgr.getFightSpriteBuffList()) {
			if (StringUtils.equals(buff.getFightSpriteBuffRef().getId(), buffRefId)) {
				return buff;
			}
		}
		return null;
	}

	public MGFightSpriteBuff getTheSameStateBuff(MGFightSpriteBuff buff) {
		for (MGFightSpriteBuff spriteBuff : fightSpriteBuffMgr.getFightSpriteBuffList()) {
			if (!buff.equals(spriteBuff) && spriteBuff.getAttachedState() == buff.getAttachedState()) {
				return spriteBuff;
			}
		}
		return null;
	}

	/**
	 * 死亡是否清除
	 */
	private void isClearOnDead() {
		synchronized (fightSpriteBuffMgr) {
			for (MGFightSpriteBuff buff : fightSpriteBuffMgr.getFightSpriteBuffList()) {
				if (buff.getFightSpriteBuffRef().isClearOnDeadBuff()) {
					fightSpriteBuffMgr.removeBuff(buff);

				}
			}
		}

	}

	/**
	 * 退出登录是否要清除
	 */
	private void isClearLoginOut() {

		synchronized (fightSpriteBuffMgr) {
			fightSpriteBuffMgr.setCanAttachBuff(false);
			List<MGFightSpriteBuff> fightSpriteBuffList = new ArrayList<MGFightSpriteBuff>();
			fightSpriteBuffList.addAll(fightSpriteBuffMgr.getFightSpriteBuffList());
			for (MGFightSpriteBuff buff : fightSpriteBuffList) {
				if (!buff.getFightSpriteBuffRef().isNeedSaveBuff() || (buff.getFightSpriteBuffRef().isDependOnDurationTime() && buff.getCrtDuration() < 0)) {
					fightSpriteBuffMgr.removeBuffNoNotify(buff);
				} else {
					buff.reSetDuration();
					if (!buff.getFightSpriteBuffRef().isDependOnDurationTime()) {
						buff.setDuration(buff.getFightSpriteBuffRef().getDuration());
					}
				}

			}
			if (getConcreteParent() instanceof Player) {
				PlayerImmediateDaoFacade.update((Player) getParent());
			}
		}

	}

	/**
	 * 登录时重新加上buff
	 */
	private void loginReAddBuff() {

		for (MGFightSpriteBuff buff : fightSpriteBuffMgr.getFightSpriteBuffList()) {
			byte durationType = buff.getFightSpriteBuffRef().getDurationType();
			long lastEffectTime = buff.getLastAffectTime();
			long duration = buff.getDuration();
			if (MGFightSpriteBuffDurationType.Always_Duration_Tick == durationType) {
				buff.setExpiration(lastEffectTime + duration);
			} else {
				buff.setExpiration(System.currentTimeMillis() + duration);
			}
			buff.effectTo();
		}
	}

	public MGFightSpriteBuffMgr getFightSpriteBuffMgr() {
		return fightSpriteBuffMgr;
	}

	@Override
	public void setParent(GameObject owner) {
		super.setParent(owner);
		fightSpriteBuffMgr.setOwner((FightSprite) owner);

	}

	public BuffPersistenceObject getPersistenceObject() {
		return persistenceObject;
	}

	public void setPersistenceObject(BuffPersistenceObject persistenceObject) {
		this.persistenceObject = persistenceObject;
	}
}
