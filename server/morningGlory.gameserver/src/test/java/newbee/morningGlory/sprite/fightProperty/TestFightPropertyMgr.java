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
package newbee.morningGlory.sprite.fightProperty;

import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import newbee.morningGlory.mmorpg.sprite.MGFightPropertyMgr;
import newbee.morningGlory.ref.JSONDataManagerContext;
import newbee.morningGlory.ref.symbol.PropertySymbolLoader;
import sophia.foundation.property.PropertyDictionary;
import sophia.mmorpg.base.sprite.fightProperty.FightPropertyEffectTransaction;
import sophia.mmorpg.base.sprite.fightProperty.FightPropertyMgr;
import sophia.mmorpg.base.sprite.fightProperty.FightPropertyModifyTransaction;
import sophia.mmorpg.core.PropertyDictionaryModifyPhase;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.ref.editorgen.MGPropertySymbolDefines;

public final class TestFightPropertyMgr {
	private static final Logger logger = Logger.getLogger(TestFightPropertyMgr.class.getName());
	private MGFightPropertyMgr fightPropertyMgr = new MGFightPropertyMgr();
	
	@Before
	public void setUp() throws Exception {
		JSONDataManagerContext.load();
		PropertySymbolLoader.load();
		
		fightPropertyMgr.setCrtPropertyDictionary(crtPropertyDictionary());
	}
	
	@Test
	public void testSetCrt() {
		PropertyDictionaryModifyPhase snapshotFromPool = fightPropertyMgr.getSnapshotFromPool();
		String string = snapshotFromPool.toString();
		logger.info(string);
		FightPropertyMgr.recycleSnapshotToPool(snapshotFromPool);
	}
	
	@Test
	public void testModifyHP() {
		FightPropertyModifyTransaction modifyTransaction = fightPropertyMgr.getModifyTransaction();
		int modifyHP = modifyTransaction.modifyHP(-20);
		logger.info("modifyHP: " + modifyHP);
		assertTrue(modifyHP == 80);
	}
	
	@Test
	public void testModifyMultiProperty() {
		FightPropertyModifyTransaction modifyTransaction = fightPropertyMgr.getModifyTransaction();
		PropertyDictionaryModifyPhase modifyPhaseFromPool = FightPropertyMgr.getModifyPhaseFromPool();
		modifyPhaseFromPool.modify(MGPropertySymbolDefines.HealHP_Id, 3);
		modifyPhaseFromPool.modify(MGPropertySymbolDefines.HealMP_Id, 83);
		modifyPhaseFromPool.modify(MGPropertySymbolDefines.MinMAtk_Id, -13);
		PropertyDictionaryModifyPhase modify = modifyTransaction.modify(modifyPhaseFromPool);
		
		logger.info("modify: " + modify);
	}
	
	@Test
	public void testDetachImpl() {
		FightPropertyEffectTransaction effectTransaction = fightPropertyMgr.getEffectTransaction();
		PropertyDictionaryModifyPhase modifyPhaseFromPool = FightPropertyMgr.getModifyPhaseFromPool();
		modifyPhaseFromPool.modify(MGPropertySymbolDefines.HP_Id, -20);
		effectTransaction.attach(modifyPhaseFromPool);
		int currentHP = MGPropertyAccesser.getHP(fightPropertyMgr.getSnapshotByNew().getPropertyDictionary());
		logger.info("currentHP after attach " + currentHP);
		assertTrue(currentHP == 80);
		PropertyDictionaryModifyPhase modifyPhaseFromPool2 = FightPropertyMgr.getModifyPhaseFromPool();
		modifyPhaseFromPool2.modify(MGPropertySymbolDefines.HP_Id, -20);
		effectTransaction.detach(modifyPhaseFromPool2);
		int currentHP2 = MGPropertyAccesser.getHP(fightPropertyMgr.getSnapshotByNew().getPropertyDictionary());
		logger.info("currentHP after detach " + currentHP2);
		assertTrue(currentHP2 == 100);
	}
	
	private PropertyDictionary crtPropertyDictionary() {
		PropertyDictionary snapshotPropertyDictionary = new PropertyDictionary();
		
		MGPropertyAccesser.setOrPutHealHP(snapshotPropertyDictionary, 100);
		MGPropertyAccesser.setOrPutHP(snapshotPropertyDictionary, 100);
		MGPropertyAccesser.setOrPutMaxHP(snapshotPropertyDictionary, 100);
		MGPropertyAccesser.setOrPutHealMP(snapshotPropertyDictionary, 100);
		MGPropertyAccesser.setOrPutMaxMP(snapshotPropertyDictionary, 100);
		MGPropertyAccesser.setOrPutMinPAtk(snapshotPropertyDictionary, 20);
		MGPropertyAccesser.setOrPutMaxPAtk(snapshotPropertyDictionary, 30);
		MGPropertyAccesser.setOrPutMinMAtk(snapshotPropertyDictionary, 40);
		MGPropertyAccesser.setOrPutMaxMAtk(snapshotPropertyDictionary, 50);
		MGPropertyAccesser.setOrPutMinTao(snapshotPropertyDictionary, 60);
		MGPropertyAccesser.setOrPutMaxTao(snapshotPropertyDictionary, 70);
		MGPropertyAccesser.setOrPutMinPDef(snapshotPropertyDictionary, 80);
		MGPropertyAccesser.setOrPutMaxPDef(snapshotPropertyDictionary, 90);
		MGPropertyAccesser.setOrPutMinMDef(snapshotPropertyDictionary, 100);
		MGPropertyAccesser.setOrPutMaxMDef(snapshotPropertyDictionary, 110);
		MGPropertyAccesser.setOrPutHit(snapshotPropertyDictionary, 120);
		MGPropertyAccesser.setOrPutDodge(snapshotPropertyDictionary, 130);
		MGPropertyAccesser.setOrPutMoveSpeed(snapshotPropertyDictionary, 9);
		MGPropertyAccesser.setOrPutAtkSpeed(snapshotPropertyDictionary, 8);
		MGPropertyAccesser.setOrPutPImmunityPer(snapshotPropertyDictionary, 160);
		MGPropertyAccesser.setOrPutMImmunityPer(snapshotPropertyDictionary, 161);
		MGPropertyAccesser.setOrPutIgnorePDef(snapshotPropertyDictionary, 120);
		MGPropertyAccesser.setOrPutIgnoreMDef(snapshotPropertyDictionary, 121);
		MGPropertyAccesser.setOrPutCrit(snapshotPropertyDictionary, 80);
		MGPropertyAccesser.setOrPutCritInjure(snapshotPropertyDictionary, 30);
		MGPropertyAccesser.setOrPutFortune(snapshotPropertyDictionary, 6);
		MGPropertyAccesser.setOrPutPDodgePer(snapshotPropertyDictionary, 50);
		MGPropertyAccesser.setOrPutMDodgePer(snapshotPropertyDictionary, 70);
		
		return snapshotPropertyDictionary;
	}
}
