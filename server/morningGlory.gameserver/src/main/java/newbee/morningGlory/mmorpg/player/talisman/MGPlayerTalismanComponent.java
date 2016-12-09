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
package newbee.morningGlory.mmorpg.player.talisman;

import newbee.morningGlory.mmorpg.player.sectionQuest.event.SectionQuestActionEventDefines;
import newbee.morningGlory.mmorpg.player.sectionQuest.event.talisman.G2C_Talisman_GetQuestReward;
import newbee.morningGlory.mmorpg.player.talisman.actionEvent.C2G_Citta_LevelUp;
import newbee.morningGlory.mmorpg.player.talisman.actionEvent.C2G_Talisman_GetReward;
import newbee.morningGlory.mmorpg.player.talisman.actionEvent.C2G_Talisman_List;
import newbee.morningGlory.mmorpg.player.talisman.actionEvent.C2G_Talisman_Operation;
import newbee.morningGlory.mmorpg.player.talisman.actionEvent.C2G_Talisman_Reward;
import newbee.morningGlory.mmorpg.player.talisman.actionEvent.C2G_Talisman_Statistics;
import newbee.morningGlory.mmorpg.player.talisman.actionEvent.G2C_Citta_LevelUp;
import newbee.morningGlory.mmorpg.player.talisman.actionEvent.G2C_Talisman_GetReward;
import newbee.morningGlory.mmorpg.player.talisman.actionEvent.G2C_Talisman_List;
import newbee.morningGlory.mmorpg.player.talisman.actionEvent.G2C_Talisman_Operation;
import newbee.morningGlory.mmorpg.player.talisman.actionEvent.G2C_Talisman_Reward;
import newbee.morningGlory.mmorpg.player.talisman.actionEvent.G2C_Talisman_Statistics;
import newbee.morningGlory.mmorpg.player.talisman.actionEvent.TalisManEventDefines;
import newbee.morningGlory.mmorpg.player.talisman.gameEvent.HeartLevelUP_GE;
import newbee.morningGlory.mmorpg.player.talisman.gameEvent.TalismanAcquire_GE;
import newbee.morningGlory.mmorpg.player.talisman.gameEvent.TalismanLevelUp;
import newbee.morningGlory.mmorpg.sprite.MGFightProcessComponent;
import newbee.morningGlory.mmorpg.sprite.buff.MGBuffEffectType;
import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuff;
import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuffComponent;
import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuffRef;
import newbee.morningGlory.mmorpg.sprite.buff.actionEvent.BuffEventDefines;
import newbee.morningGlory.mmorpg.sprite.buff.actionEvent.G2C_Effect_Buff;
import newbee.morningGlory.stat.MGStatFunctions;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.authentication.Identity;
import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.foundation.data.PersistenceObject;
import sophia.foundation.task.PeriodicTaskHandle;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.base.scene.GameSceneHelper;
import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.base.sprite.fightSkill.gameevent.AfterAttack_GE;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.core.CDMgr;
import sophia.mmorpg.core.timer.SFTimer;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.monster.Monster;
import sophia.mmorpg.monster.gameEvent.MonsterDead_GE;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.chat.gameEvent.PlayerUseChuanYinTalisman_GE;
import sophia.mmorpg.player.chat.sysytem.SystemPromptFacade;
import sophia.mmorpg.player.exp.gameevent.PlayerLevelUp_GE;
import sophia.mmorpg.player.gameEvent.EnterWorld_SceneReady_GE;
import sophia.mmorpg.player.gameEvent.PlayerDead_GE;
import sophia.mmorpg.player.persistence.immediatelySave.PlayerImmediateDaoFacade;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.RuntimeResult;
import sophia.mmorpg.utils.SFRandomUtils;

import com.google.common.base.Preconditions;

/**
 * 玩家-法宝组件
 */
public final class MGPlayerTalismanComponent extends ConcreteComponent<Player> {
	private static final Logger logger = Logger.getLogger(MGPlayerTalismanComponent.class);
	public static final String Tag = "MGPlayerTalismanComponent";
	private CDMgr reviveCDMgr = new CDMgr(0);
	private static final String AfterAttack_GE_Id = AfterAttack_GE.class.getSimpleName();
	private static final String TalismanLevleUp_GE_Id = TalismanLevelUp.class.getSimpleName();
	private static final String EnterWorld_SceneReady_ID = EnterWorld_SceneReady_GE.class.getSimpleName();
	private static final String PlayerUseChuanYinTalisman_GE_Id = PlayerUseChuanYinTalisman_GE.class.getSimpleName();
	private static final String MonsterDead_GE_Id = MonsterDead_GE.class.getSimpleName();
	private static final String PlayerLevelUp_GE_Id = PlayerLevelUp_GE.class.getSimpleName();
	private MGTalismanEffectMgr talismanEffectMgr;
	private PersistenceObject persistenceObject;
	private MGPlayerCitta playerCitta = new MGPlayerCitta();
	private MGTalismanStatistics statistics = new MGTalismanStatistics();
	private SFTimer timer;
	private PeriodicTaskHandle handle1, handle2, handle3;
	private static final String Talisman = "talisman";
	private CDMgr redCDMgr = new CDMgr(0);

	public MGPlayerTalismanComponent() {

	}

	@Override
	public void ready() {
		addActionEventListener(TalisManEventDefines.C2G_Talisman_List);
		addActionEventListener(TalisManEventDefines.C2G_Talisman_Operation);
		addActionEventListener(TalisManEventDefines.C2G_Talisman_Statistics);
		addActionEventListener(TalisManEventDefines.C2G_Citta_LevelUp);
		addActionEventListener(TalisManEventDefines.C2G_Talisman_Reward);
		addActionEventListener(TalisManEventDefines.C2G_Talisman_GetReward);
		addInterGameEventListener(AfterAttack_GE_Id);
		addInterGameEventListener(EnterWorld_SceneReady_ID);
		addInterGameEventListener(PlayerUseChuanYinTalisman_GE_Id);
		addInterGameEventListener(MonsterDead_GE_Id);
		addInterGameEventListener(Player.PlayerDead_GE_Id);
		addInterGameEventListener(PlayerLevelUp_GE_Id);
		Player player = (Player) getConcreteParent();
		for (MGTalismanContains talismanContain : this.playerCitta.getTalismanList()) {
			MGTalisman talisman = talismanContain.getTalisman();
			if (talisman != null && talisman.isActive()) {
				setHandleFirstTime(talisman);
				talisman.active(player);
				playerCitta.speciaTalismanHandle(talisman, player);
			}
		}
	}

	@Override
	public void suspend() {
		removeActionEventListener(TalisManEventDefines.C2G_Talisman_List);
		removeActionEventListener(TalisManEventDefines.C2G_Talisman_Operation);
		removeActionEventListener(TalisManEventDefines.C2G_Talisman_Statistics);
		removeActionEventListener(TalisManEventDefines.C2G_Citta_LevelUp);
		removeActionEventListener(TalisManEventDefines.C2G_Talisman_Reward);
		removeActionEventListener(TalisManEventDefines.C2G_Talisman_GetReward);
		removeInterGameEventListener(AfterAttack_GE_Id);
		removeInterGameEventListener(EnterWorld_SceneReady_ID);
		removeInterGameEventListener(PlayerUseChuanYinTalisman_GE_Id);
		removeInterGameEventListener(MonsterDead_GE_Id);
		removeInterGameEventListener(Player.PlayerDead_GE_Id);
		removeInterGameEventListener(PlayerLevelUp_GE_Id);
		for (MGTalismanContains talismanContain : this.playerCitta.getTalismanList()) {
			MGTalisman talisman = talismanContain.getTalisman();
			if (talisman != null && talisman.isActive()) {
				talisman.unactive(getConcreteParent());
				talisman.setState(MGTalismanState.Active_State);
			}
		}
		cancelHandle();
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {

		if (event.isId(PlayerLevelUp_GE_Id)) {
			if(checkTalismanOpenState()){
				sendOpenTailsmanSystem((byte) this.getPlayerCitta().getTalismanSystemActiveState());
				sendTalismanList();
			}
		}else if (event.isId(AfterAttack_GE_Id)) {
			MGTalisman talisman = playerCitta.getTalisman("title_7");
			if (!talisman.isActive()) {
				return;
			}
			FightSprite target = ((AfterAttack_GE) event.getData()).getTarget();
			FightSprite attacker = ((AfterAttack_GE) event.getData()).getAttacker();

			if (StringUtils.equals(getConcreteParent().getId(), attacker.getId())) {
				String buffRefId = "buff_state_9";
				double rate = MGPropertyAccesser.getSkillDamageRate(talisman.getTalismanRef().getEffectData());
				int duration = 2 * 1000;
				int random = SFRandomUtils.random100();
				if (random < rate) {
					MGFightSpriteBuffComponent<?> fightSpriteBuffComponent = (MGFightSpriteBuffComponent<?>) target.getTagged(MGFightSpriteBuffComponent.Tag);
					MGFightSpriteBuffRef ref = (MGFightSpriteBuffRef) GameRoot.getGameRefObjectManager().getManagedObject(buffRefId);
					MGFightSpriteBuff buff = new MGFightSpriteBuff(ref, attacker, target, duration);
					buff.setSendAttackEvent(false);
					fightSpriteBuffComponent.getFightSpriteBuffMgr().attachFightSpriteBuff(buff);
					statistics.addTalismanStatistics(MGTalismanStatistics.Total_Benumb, 1);
				}
			}
			
			talisman = playerCitta.getTalisman("title_8");
			if (!talisman.isActive()) {
				return;
			}
			if (StringUtils.equals(getConcreteParent().getId(), target.getId())) {
				MGFightProcessComponent<?> processComponent = (MGFightProcessComponent<?>) attacker.getTagged(MGFightProcessComponent.Tag);
				int damage = ((AfterAttack_GE) event.getData()).getDamge();
				double rate = MGPropertyAccesser.getSkillDamageRate(talisman.getTalismanRef().getEffectData()) / 100.0;
				int curMp = target.getMP();
				if (curMp <= 0) {
					return;
				}
				int subHp = (int) (rate * damage);

				processComponent.setDamageFromOutSide(subHp);
				if (target.applyMP(-subHp)) {
					G2C_Effect_Buff message = (G2C_Effect_Buff) MessageFactory.getMessage(BuffEventDefines.G2C_Effect_Buff);
					message.setAttacker(attacker);
					message.setTarget(target);
					message.setType(MGBuffEffectType.MP);
					message.setValue(-subHp);
					GameSceneHelper.broadcastMessageToAOI(target, message);
					statistics.addTalismanStatistics(MGTalismanStatistics.Total_Hurt, subHp);
				}

			}
		} else if (event.isId(Player.PlayerDead_GE_Id)) {
			MGTalisman talisman = playerCitta.getTalisman("title_9");
			if (!talisman.isActive()) {
				return;
			}
			PlayerDead_GE playerDead_GE = ((PlayerDead_GE) event.getData());

			Player player = playerDead_GE.getPlayer();
			if (!player.equals(getConcreteParent())) {
				return;
			}

			String refId = talisman.getTalismanRef().getId();
			int CDTime = MGPropertyAccesser.getCDTime(talisman.getTalismanRef().getEffectData());
			if (!reviveCDMgr.isCDStarted(refId)) {
				reviveCDMgr.startCD(refId, (long) CDTime * 1000);
			}

			if (!reviveCDMgr.isOutOfCD(refId)) {
				RuntimeResult ret = RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_ACTIVE_IN_CD);
				return;
			}
			reviveCDMgr.update(refId);
			getConcreteParent().revive();
			statistics.addTalismanStatistics(MGTalismanStatistics.Total_Revive, 1);
		} else if (event.isId(MonsterDead_GE_Id)) {
			MGTalisman talisman = playerCitta.getTalisman("title_4");
			if (!talisman.isActive()) {
				return;
			}
			Monster monster = ((MonsterDead_GE) event.getData()).getMonster();
			float expMultiple = MGPropertyAccesser.getExpMultiple(talisman.getTalismanRef().getEffectData());
			int exp = (int) MGPropertyAccesser.getExp(monster.getMonsterRef().getProperty());
			int totalExp = (int) (exp * expMultiple);
			this.playerCitta.addExpReward(totalExp);
			sendRewardMessage();
		} else if (event.isId(PlayerUseChuanYinTalisman_GE_Id)) {
			statistics.addTalismanStatistics(MGTalismanStatistics.Total_Message, 1);
		}

	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
		short actionEventId = event.getActionEventId();
		Identity identity = event.getIdentity();

		switch (actionEventId) {
		case TalisManEventDefines.C2G_Talisman_List:
			handle_Talisman_List((C2G_Talisman_List) event, actionEventId, identity);
			break;
		case TalisManEventDefines.C2G_Talisman_Operation:
			handle_Talisman_Operation((C2G_Talisman_Operation) event, actionEventId, identity);
			break;
		case TalisManEventDefines.C2G_Talisman_Statistics:
			handle_Talisman_Statistics((C2G_Talisman_Statistics) event, actionEventId, identity);
			break;
		case TalisManEventDefines.C2G_Citta_LevelUp:
			handle_Citta_LevelUp((C2G_Citta_LevelUp) event, actionEventId, identity);
			break;
		case TalisManEventDefines.C2G_Talisman_Reward:
			handle_Talisman_Reward((C2G_Talisman_Reward) event, actionEventId, identity);
			break;
		case TalisManEventDefines.C2G_Talisman_GetReward:
			handle_Talisman_GetReward((C2G_Talisman_GetReward) event, actionEventId, identity);
			break;
		default:
			return;
		}

	}
	
	private boolean checkTalismanOpenState(){
		Player player = getConcreteParent();
		MGCittaRef cittaRef = (MGCittaRef) GameRoot.getGameRefObjectManager().getManagedObject("citta_1");
		int roleGrade = MGPropertyAccesser.getRoleGrade(cittaRef.getProperty());
		if (player.getLevel() >= roleGrade && this.getPlayerCitta().getTalismanSystemActiveState() == 0) {
			this.getPlayerCitta().setCittaRef(cittaRef);
			this.talismanEffectMgr.attach(cittaRef);
			return true;
		}
		return false;
	}
	
	private void handle_Talisman_GetReward(C2G_Talisman_GetReward event, short actionEventId, Identity identity) {

		sendRewardMessage();
	}

	public void sendRewardMessage() {
		G2C_Talisman_GetReward res = MessageFactory.getConcreteMessage(TalisManEventDefines.G2C_Talisman_GetReward);
		res.setTotalBaoXiang(statistics.getStatistics().get(MGTalismanStatistics.Total_BaoXiang).intValue());
		res.setTotalGold(statistics.getStatistics().get(MGTalismanStatistics.Total_Gold).intValue());
		res.setTotalExp(statistics.getStatistics().get(MGTalismanStatistics.Total_Exp).intValue());
		res.setTotalStone(statistics.getStatistics().get(MGTalismanStatistics.Total_ShenQiExp).intValue());

		res.setBaoXiang(this.playerCitta.getBaoXiangsCount());
		res.setExp(this.playerCitta.getExpReward());
		res.setGold(this.playerCitta.getGoldReward());
		res.setStone(this.playerCitta.getStoneReward());
		GameRoot.sendMessage(getConcreteParent().getIdentity(), res);
	}

	private void handle_Talisman_Reward(C2G_Talisman_Reward event, short actionEventId, Identity identity) {
		RuntimeResult result = reward();
		G2C_Talisman_Reward res = MessageFactory.getConcreteMessage(TalisManEventDefines.G2C_Talisman_Reward);
		byte ret = 1;
		if (!result.isOK()) {
			ret = 0;
			ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), result.getApplicationCode());
		}
		res.setResult(ret);
		GameRoot.sendMessage(event.getIdentity(), res);
		sendRewardMessage();
		if (result.isOK()) {
			PlayerImmediateDaoFacade.update(getConcreteParent());
		}

	}

	private void handle_Citta_LevelUp(C2G_Citta_LevelUp event, short actionEventId, Identity identity) {
		RuntimeResult result = levelUp();
		G2C_Citta_LevelUp res = MessageFactory.getConcreteMessage(TalisManEventDefines.G2C_Citta_LevelUp);
		if (!result.isOK()) {
			ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), result.getApplicationCode());
		}
		int level = this.playerCitta.getLevel();
		
		sendHeartLevelUP(level);
		res.setResult((byte) level);
		GameRoot.sendMessage(event.getIdentity(), res);
		PlayerImmediateDaoFacade.update(getConcreteParent());
	}

	private void handle_Talisman_Operation(C2G_Talisman_Operation event, short actionEventId, Identity identity) {

		byte type = event.getType();
		short index = event.getIndex();
		byte ret = 0;
		MGTalisman talisman = playerCitta.getTalisman(index);
		Preconditions.checkArgument(talisman != null, "获取法宝对象为空，索引位置：" + index);
		RuntimeResult result = RuntimeResult.RuntimeError();

		switch (type) {
		case MGTalismanRequestType.Acquire:
			result = aquire(talisman);
			sendGameEvent();
			sendAquireGameEvent();
			break;
		case MGTalismanRequestType.Active:
			result = active(talisman);
			break;
		case MGTalismanRequestType.Unactive:
			result = unactive(talisman);
			break;
		default:
			return;
		}
		sendTalismanStatistics();
		MGStatFunctions.talismanStat(getConcreteParent(), type, talisman.getTalismanRef().getId(), talisman.getLevel(), talisman.getState());
		if (result.isOK()) {
			ret = 1;
			G2C_Talisman_Operation message = MessageFactory.getConcreteMessage(TalisManEventDefines.G2C_Talisman_Operation);
			message.setType(type);
			message.setIndex(index);
			message.setLevel(talisman.getLevel());
			message.setRet(ret);
			GameRoot.sendMessage(identity, message);
		} else if (result.getCode() == 3) {
			ret = 0;
			G2C_Talisman_Operation message = MessageFactory.getConcreteMessage(TalisManEventDefines.G2C_Talisman_Operation);
			message.setType(type);
			message.setIndex(index);
			message.setLevel(talisman.getLevel());
			message.setRet(ret);
			GameRoot.sendMessage(identity, message);
		} else {
			ResultEvent.sendResult(identity, actionEventId, result.getApplicationCode());
		}
		PlayerImmediateDaoFacade.update(getConcreteParent());
	}

	private void handle_Talisman_List(C2G_Talisman_List event, short actionEventId, Identity identity) {
		if (logger.isDebugEnabled()) {
			logger.debug("请求法宝列表");
		}
		checkTalismanOpenState();
		sendTalismanList();

	}

	private void handle_Talisman_Statistics(C2G_Talisman_Statistics event, short actionEventId, Identity identity) {
		sendTalismanStatistics();
	}

	private void sendTalismanStatistics() {
		G2C_Talisman_Statistics message = MessageFactory.getConcreteMessage(TalisManEventDefines.G2C_Talisman_Statistics);

		message.setMap(statistics.getStatistics());

		GameRoot.sendMessage(getConcreteParent().getIdentity(), message);
	}

	private void sendTalismanList() {

		G2C_Talisman_List message = MessageFactory.getConcreteMessage(TalisManEventDefines.G2C_Talisman_List);

		message.setTalismanMgr(getPlayerCitta());

		GameRoot.sendMessage(getConcreteParent().getIdentity(), message);

	}

	private void sendOpenTailsmanSystem(byte state) {
		Player player = getConcreteParent();
		G2C_Talisman_GetQuestReward res = MessageFactory.getConcreteMessage(SectionQuestActionEventDefines.G2C_Talisman_GetQuestReward);
		res.setType(state);
		GameRoot.sendMessage(player.getIdentity(), res);
	}

	/**
	 * 领取被动法宝的奖励
	 * 
	 * @return
	 */
	private RuntimeResult reward() {
		return this.playerCitta.reward(getConcreteParent());
	}

	/**
	 * 获取法宝
	 * 
	 * @param talisman
	 * @return
	 */
	private RuntimeResult aquire(MGTalisman talisman) {
		RuntimeResult result = RuntimeResult.OK();
		result = talisman.acquire(getConcreteParent());
		if(result.isOK()){
			SystemPromptFacade.broadGetTalisman(getConcreteParent().getName(),getConcreteParent().getId(),talisman.getTalismanRef().getName(), Talisman);
		}
		return result;
	}

	/**
	 * 激活法宝
	 * 
	 * @param talisman
	 * @return
	 */
	private RuntimeResult active(MGTalisman talisman) {
		RuntimeResult result = RuntimeResult.OK();
		if (talisman.isNotAcquire()) {
			if (logger.isDebugEnabled()) {
				logger.debug("player:" + getConcreteParent().getName() + ",法宝:" + MGPropertyAccesser.getName(talisman.getTalismanRef().getProperty()) + ",状态:"
						+ talisman.getState());
			}
			return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_ACTIVE_UNACQUIRE_STATE);
		}

		if (talisman.isActive()) {
			if (logger.isDebugEnabled()) {
				logger.debug("player:" + getConcreteParent().getName() + ",法宝:" + MGPropertyAccesser.getName(talisman.getTalismanRef().getProperty()) + ",状态:"
						+ talisman.getState());
			}
			return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_ACTIVE_ACTIVE_STATE);
		}

		String refId = "title";
		if (!redCDMgr.isCDStarted(refId)) {
			redCDMgr.startCD(refId, (long) 10 * 1000); // 30 miao
		}

		if (!redCDMgr.isOutOfCD(refId)) {
			result = RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_ACTIVE_IN_CD);
			return result;
		}

		redCDMgr.update(refId);

		if (playerCitta.getCrtActiveTalisman() != null) {
			if (playerCitta.getCrtActiveTalisman().isActive() && !playerCitta.getCrtActiveTalisman().isPassiveTalisman()) {
				playerCitta.getCrtActiveTalisman().unactive(getConcreteParent());
				playerCitta.setCrtActiveTalisman(null);
			}
		}
		playerCitta.setCrtActiveTalisman(talisman);
		result = talisman.active(getConcreteParent());
		if (!result.isOK()) {
			playerCitta.setCrtActiveTalisman(null);
		}

		return result;
	}

	/**
	 * 取消激活
	 * 
	 * @param talisman
	 * @return
	 */
	private RuntimeResult unactive(MGTalisman talisman) {
		RuntimeResult result = RuntimeResult.OK();

		MGTalisman crtActiveTalisman = playerCitta.getCrtActiveTalisman();
		if (crtActiveTalisman != null) {
			if (crtActiveTalisman.isNotActive()) {
				return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_ACTIVE_UNACTIVE_STATE);
			}

			result = crtActiveTalisman.unactive(getConcreteParent());
			playerCitta.emptyCrtActiveTalisman();
		}

		return result;
	}

	/**
	 * 心法升级
	 * 
	 * @param talisman
	 * @return
	 */
	private RuntimeResult levelUp() {
		MGCittaRef ref = this.playerCitta.getCittaRef();
		if (ref == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("未获取心法系统");
			}
			return RuntimeResult.ParameterError();
		}

		return this.playerCitta.levelUp(getConcreteParent(), getTalismanEffectMgr());
	}

	private void sendGameEvent() {
		TalismanLevelUp talismanLevleUp_GE = new TalismanLevelUp();
		for (MGTalismanContains contains : playerCitta.getTalismanList()) {
			int level = contains.getTalisman().getLevel();
			if (talismanLevleUp_GE.getTalisMap().get(level) == null) {
				talismanLevleUp_GE.getTalisMap().put(level, 1);
			} else {
				int count = talismanLevleUp_GE.getTalisMap().get(level);
				talismanLevleUp_GE.getTalisMap().put(level, count + 1);
			}
		}
		GameEvent<TalismanLevelUp> ge = (GameEvent<TalismanLevelUp>) GameEvent.getInstance(TalismanLevleUp_GE_Id, talismanLevleUp_GE);
		sendGameEvent(ge, getConcreteParent().getId());
	}
	
	private void sendAquireGameEvent() {
		int totalCount = playerCitta.getTotalTalismanCount();
		TalismanAcquire_GE talismanAcquireGE = new TalismanAcquire_GE(totalCount);
		GameEvent<TalismanAcquire_GE> ge = (GameEvent<TalismanAcquire_GE>) GameEvent.getInstance(TalismanAcquire_GE.class.getSimpleName(), talismanAcquireGE);
		sendGameEvent(ge, getConcreteParent().getId());
	}
	
	private void sendHeartLevelUP(int heartLevel) {
		HeartLevelUP_GE heartLevelup_GE = new HeartLevelUP_GE(heartLevel);
		GameEvent<HeartLevelUP_GE> ge = (GameEvent<HeartLevelUP_GE>) GameEvent.getInstance(HeartLevelUP_GE.class.getSimpleName(), heartLevelup_GE);
		sendGameEvent(ge, getConcreteParent().getId());
	}

	public void cancelHandle1() {
		if (handle1 != null) {
			handle1.cancel();
			handle1 = null;
		}
	}

	public void cancelHandle2() {

		if (handle2 != null) {
			handle2.cancel();
			handle2 = null;
		}
	}

	public void cancelHandle3() {

		if (handle3 != null) {
			handle3.cancel();
			handle3 = null;
		}
	}

	public void cancelHandle() {
		cancelHandle1();
		cancelHandle2();
		cancelHandle3();
	}

	public void setHandleFirstTime(MGTalisman talisman) {
		long lastHandleTime = talisman.getLastHandleTime();
		long crttime = System.currentTimeMillis();
		long subTime = MGTalisman.DEFAULT_HANDLE_TIME - (crttime - lastHandleTime) % MGTalisman.DEFAULT_HANDLE_TIME;
		subTime = subTime < 0 ? 0 : subTime;
		talisman.setHandleFirstTime((int) subTime);
	}

	public MGPlayerCitta getPlayerCitta() {
		return playerCitta;
	}

	public void setPlayerCitta(MGPlayerCitta playerCitta) {
		this.playerCitta = playerCitta;
	}

	public PersistenceObject getPersistenceObject() {
		return persistenceObject;
	}

	public void setPersistenceObject(PersistenceObject persistenceObject) {
		this.persistenceObject = persistenceObject;
	}

	public MGTalismanEffectMgr getTalismanEffectMgr() {
		return talismanEffectMgr;
	}

	public void setTalismanEffectMgr(MGTalismanEffectMgr talismanEffectMgr) {
		this.talismanEffectMgr = talismanEffectMgr;
	}

	public SFTimer getTimer() {
		return timer;
	}

	public void setTimer(SFTimer timer) {
		this.timer = timer;
	}

	public PeriodicTaskHandle getHandle1() {
		return handle1;
	}

	public void setHandle1(PeriodicTaskHandle handle1) {
		this.handle1 = handle1;
	}

	public PeriodicTaskHandle getHandle2() {
		return handle2;
	}

	public void setHandle2(PeriodicTaskHandle handle2) {
		this.handle2 = handle2;
	}

	public PeriodicTaskHandle getHandle3() {
		return handle3;
	}

	public void setHandle3(PeriodicTaskHandle handle3) {
		this.handle3 = handle3;
	}

	public MGTalismanStatistics getStatistics() {
		return statistics;
	}

	public void setStatistics(MGTalismanStatistics statistics) {
		this.statistics = statistics;
	}

}
