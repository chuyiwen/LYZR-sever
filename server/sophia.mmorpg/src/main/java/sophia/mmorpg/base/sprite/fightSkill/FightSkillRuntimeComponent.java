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
package sophia.mmorpg.base.sprite.fightSkill;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.property.PropertyDictionary;
import sophia.foundation.util.Position;
import sophia.game.GameRoot;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.code.CodeContext;
import sophia.mmorpg.code.MMORPGSuccessCode;
import sophia.mmorpg.core.CDMgr;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.fightSkill.event.C2G_UseSkill;
import sophia.mmorpg.player.fightSkill.event.FightSkillDefines;
import sophia.mmorpg.player.fightSkill.gameevent.SkillUseSkill_GE;
import sophia.mmorpg.player.fightSkill.ref.SkillLevelRef;
import sophia.mmorpg.player.property.PlayerConfig;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.RuntimeResult;

public final class FightSkillRuntimeComponent<T extends FightSprite> extends ConcreteComponent<T> {
	private static final Logger logger = Logger.getLogger(FightSkillRuntimeComponent.class);
	public static final int basicCD = 600;
	private static final int skillCD = 1000;
	private static final int warriorSkillCD = 700;
	private static final int enchanterSkillCD = 1200;
	private static final int warlockSkillCD = 1200;
	private final CDMgr basicCDMgr = new CDMgr(basicCD);
	private final CDMgr skillCDMgr = new CDMgr(skillCD);
	private final Map<Byte, CDMgr> playerCDMgrs = new HashMap<>(3);

	private FightSkillRuntime fightSkillRuntime;

	public FightSkillRuntimeComponent() {
		this.playerCDMgrs.put(PlayerConfig.WARRIOR, new CDMgr(warriorSkillCD));
		this.playerCDMgrs.put(PlayerConfig.ENCHANTER, new CDMgr(enchanterSkillCD));
		this.playerCDMgrs.put(PlayerConfig.WARLOCK, new CDMgr(warlockSkillCD));
	}

	@Override
	public void ready() {
		super.ready();
		addActionEventListener(FightSkillDefines.C2G_UseSkill);
	}

	@Override
	public void suspend() {
		super.suspend();
		removeActionEventListener(FightSkillDefines.C2G_UseSkill);
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
		short eventId = event.getActionEventId();
		logger.debug("useSkill: " + event);
		switch (eventId) {
		case FightSkillDefines.C2G_UseSkill:
			handle_C2G_UseSkill((C2G_UseSkill) event);
			break;
		default:
			break;
		}
		super.handleActionEvent(event);
	}

	private void costSkillMp(FightSprite caster, FightSkill skill) {
		checkArgument(caster != null);
		checkArgument(skill != null);
		SkillLevelRef levelRef = skill.getLevelRef();
		if (levelRef != null) {
			PropertyDictionary property = levelRef.getProperty();
			checkArgument(property != null);
			int mpRequired = MGPropertyAccesser.getMP(property);
			int currentMp = caster.getMP();
			if (currentMp >= mpRequired) {
				caster.modifyMP(-mpRequired);
				if (caster instanceof Player) {
					PropertyDictionary pd = new PropertyDictionary();
					int newMp = caster.getMP();
					MGPropertyAccesser.setOrPutMP(pd, newMp);
					Player player = (Player) caster;
					player.notifyPorperty(pd);
				}
			}
		}

	}

	private void sendUseSkillGameEvent(String skillRefId) {
		// fire SkillUseSkill_GE game event
		SkillUseSkill_GE ge = new SkillUseSkill_GE(skillRefId);
		sendGameEvent(SkillUseSkill_GE.class.getSimpleName(), ge);
	}

	private void handle_C2G_UseSkill(C2G_UseSkill event) {
		if (event.getDestType() == 0) { // target sprite
			Player player = (Player) getConcreteParent();
			String targetId = event.getTargetId();
			String skillRefId = event.getSkillRefId();
			FightSkill fightSkill = player.getPlayerFightSkillComponent().getPlayerFightSkillTree().getFightSkill(skillRefId);
			if(fightSkill==null){
				logger.error("handle_C2G_UseSkill  destType=0 skillRefId = " + skillRefId +" is not exist");
				return;
			}
			
			FightSprite fightSprite = (FightSprite) GameRoot.getGameObjectManager().getObjectForId(targetId);

			int code = FightSkillRuntimeHelper.canCastSkill(player, fightSkill, fightSprite);
			if (code != MMORPGSuccessCode.CODE_SKILL_SUCCESS) {
				ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), code);
				logger.debug("handle_C2G_UseSkill player: " + player.getId() + " can not cast skill: " + skillRefId + " error code: " + CodeContext.description(code));
				return;
			}

			RuntimeResult result = castingSkill(fightSkill, fightSprite);

			costSkillMp(player, fightSkill);
			FightSkillRuntimeHelper.startOrUpdateCDTime(player, fightSkill);
			sendUseSkillGameEvent(skillRefId);

		} else if (event.getDestType() == 1) { // target grid
			Player player = (Player) getConcreteParent();
			String skillRefId = event.getSkillRefId();
			int targetX = event.getTargetX();
			int targetY = event.getTargetY();
			Position targetGrid = new Position(targetX, targetY);
			FightSkill fightSkill = player.getPlayerFightSkillComponent().getPlayerFightSkillTree().getFightSkill(skillRefId);
			if(fightSkill==null){
				logger.error("handle_C2G_UseSkill  destType=1 skillRefId = " + skillRefId +" is not exist");
				return;
			}
			
			int code = FightSkillRuntimeHelper.canCastSkill(player, fightSkill, targetGrid);
			if (code != MMORPGSuccessCode.CODE_SKILL_SUCCESS) {
				ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), code);
				logger.debug("handle_C2G_UseSkill player: " + player.getId() + " can not cast skill: " + skillRefId + " error code: " + code);
				return;
			}

			RuntimeResult result = castingSkill(fightSkill, targetGrid);

			costSkillMp(player, fightSkill);
			FightSkillRuntimeHelper.startOrUpdateCDTime(player, fightSkill);
			sendUseSkillGameEvent(skillRefId);

		} else if (event.getDestType() == 2) { // target direction
			Player player = (Player) getConcreteParent();
			String skillRefId = event.getSkillRefId();
			byte direction = event.getDirection();
			FightSkill fightSkill = player.getPlayerFightSkillComponent().getPlayerFightSkillTree().getFightSkill(skillRefId);
			if(fightSkill==null){
				logger.error("handle_C2G_UseSkill  destType=2  skillRefId = " + skillRefId +" is not exist");
				return;
			}

			int code = FightSkillRuntimeHelper.canCastSkill(player, fightSkill, direction);
			if (code != MMORPGSuccessCode.CODE_SKILL_SUCCESS) {
				ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), code);
				logger.debug("handle_C2G_UseSkill player: " + player.getId() + " can not cast skill: " + skillRefId + " error code: " + code);
				return;
			}

			RuntimeResult result = castingSkill(fightSkill, direction);

			costSkillMp(player, fightSkill);
			FightSkillRuntimeHelper.startOrUpdateCDTime(player, fightSkill);
			sendUseSkillGameEvent(skillRefId);

		}
	}

	public RuntimeResult castingSkill(FightSkill fightSkill, FightSprite targetFightSprite) {
		return fightSkillRuntime.castingSkill(fightSkill, getConcreteParent(), targetFightSprite);
	}

	public RuntimeResult castingSkill(FightSkill fightSkill, Position targetGrid) {
		return fightSkillRuntime.castingSkill(fightSkill, getConcreteParent(), targetGrid);
	}

	public RuntimeResult castingSkill(FightSkill fightSkill, byte direction) {
		return fightSkillRuntime.castingSkill(fightSkill, getConcreteParent(), direction);
	}

	public void setFightSkillRuntime(FightSkillRuntime fightSkillRuntime) {
		this.fightSkillRuntime = fightSkillRuntime;
	}

	public FightSkillRuntime getFightSkillRuntime() {
		return fightSkillRuntime;
	}

	public CDMgr getSkillCDMgr() {
		FightSprite owner = getConcreteParent();
		if (owner instanceof Player) {
			Player player = (Player) owner;
			byte profession = player.getProfession();
			return this.playerCDMgrs.get(profession);
		}
		return skillCDMgr;
	}

	public CDMgr getBasicCDMgr() {
		return basicCDMgr;
	}
}
