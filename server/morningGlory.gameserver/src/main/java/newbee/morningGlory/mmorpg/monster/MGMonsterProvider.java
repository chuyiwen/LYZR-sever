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
package newbee.morningGlory.mmorpg.monster;

import newbee.morningGlory.mmorpg.sprite.MGFightProcessComponent;
import newbee.morningGlory.mmorpg.sprite.MGFightPropertyMgr;
import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuffComponent;
import newbee.morningGlory.mmorpg.sprite.player.fightSkill.MGFightSkillRuntime;
import sophia.foundation.property.PropertyDictionary;
import sophia.game.GameRoot;
import sophia.mmorpg.GameObjectProvider;
import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.base.sprite.aoi.SpriteAOIComponent;
import sophia.mmorpg.base.sprite.fightSkill.FightSkillRuntimeComponent;
import sophia.mmorpg.base.sprite.state.FightSpriteStateMgr;
import sophia.mmorpg.base.sprite.state.action.IdleState;
import sophia.mmorpg.base.sprite.state.movement.StopState;
import sophia.mmorpg.base.sprite.state.posture.StandedState;
import sophia.mmorpg.monster.Monster;
import sophia.mmorpg.monster.MonsterPathComponent;
import sophia.mmorpg.monster.ref.MonsterRef;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public final class MGMonsterProvider implements GameObjectProvider<Monster> {
	private static final MGMonsterProvider instance = new MGMonsterProvider();
	
	private MGMonsterProvider() {
		
	}
	
	public static MGMonsterProvider getInstance() {
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Monster get(Class<Monster> type) {
		Monster monster = new Monster(); 
		monster.setAoiComponent((SpriteAOIComponent<Monster>) monster.createComponent(SpriteAOIComponent.class));
		monster.setPathComponent((MonsterPathComponent) monster.createComponent(MonsterPathComponent.class));
		return monster;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Monster get(Class<Monster> type, Object... args) {
		Monster monster = new Monster(); 
		monster.setAoiComponent((SpriteAOIComponent<Monster>) monster.createComponent(SpriteAOIComponent.class));
		monster.setPathComponent((MonsterPathComponent) monster.createComponent(MonsterPathComponent.class));
		
		String monsterRefId = (String) args[0];
		MonsterRef monsterRef = (MonsterRef) GameRoot.getGameRefObjectManager().getManagedObject(monsterRefId);
		monster.setMonsterRef(monsterRef);
		
		PropertyDictionary refPd = monsterRef.getProperty();
		PropertyDictionary pd = monster.getProperty();
		String name = MGPropertyAccesser.getName(refPd);
		MGPropertyAccesser.setOrPutName(pd, name);
		monster.setName(name);
		MGPropertyAccesser.setOrPutLevel(pd, MGPropertyAccesser.getLevel(refPd));
		int maxHP = MGPropertyAccesser.getMaxHP(refPd);
		MGPropertyAccesser.setOrPutHP(pd, maxHP);
		MGPropertyAccesser.setOrPutMaxHP(pd, maxHP);
		
		FightSpriteStateMgr fightSpriteStateMgr = monster.getFightSpriteStateMgr();
		fightSpriteStateMgr.setDefaultMovementState(StopState.StopState_Id);
		fightSpriteStateMgr.setDefaultPostureState(StandedState.StandedState_Id);
		fightSpriteStateMgr.setDefaultActionState(IdleState.IdleState_Id);
		
		MGFightSpriteBuffComponent fightSpriteBuffComponentmonster =(MGFightSpriteBuffComponent) monster.createComponent(MGFightSpriteBuffComponent.class, MGFightSpriteBuffComponent.Tag);
		fightSpriteBuffComponentmonster.setParent(monster);
		MGMonsterFightProcessComponent processComponent = (MGMonsterFightProcessComponent) monster.createComponent(MGMonsterFightProcessComponent.class, MGFightProcessComponent.Tag);
		processComponent.setOwner(monster);
		monster.getFightPropertyMgrComponent().setFightPropertyMgr(new MGFightPropertyMgr());
		FightSkillRuntimeComponent<? extends FightSprite> fightSkillRuntimeComponent = monster.getFightSkillRuntimeComponent();
		MGFightSkillRuntime skillRuntime = new MGFightSkillRuntime();
		fightSkillRuntimeComponent.setFightSkillRuntime(skillRuntime);
		
		return monster;
	}
}
