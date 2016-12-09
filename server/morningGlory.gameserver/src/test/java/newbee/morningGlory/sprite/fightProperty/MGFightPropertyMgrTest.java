package newbee.morningGlory.sprite.fightProperty;

import java.util.Collection;

import newbee.morningGlory.mmorpg.sprite.MGFightPropertyMgr;
import newbee.morningGlory.ref.JSONDataManagerContext;
import newbee.morningGlory.ref.loader.PlayerProfessionRefLoader;
import newbee.morningGlory.ref.loader.SkillRefLoader;
import newbee.morningGlory.ref.symbol.PropertySymbolLoader;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import sophia.foundation.property.PropertyDictionary;
import sophia.mmorpg.base.sprite.fightProperty.FightPropertyEffectTransaction;
import sophia.mmorpg.base.sprite.fightProperty.FightPropertyMgr;
import sophia.mmorpg.core.PropertyDictionaryModifyPhase;
import sophia.mmorpg.player.fightSkill.ref.SkillLevelRef;
import sophia.mmorpg.player.fightSkill.ref.SkillRef;
import sophia.mmorpg.player.ref.PlayerProfessionLevelData;
import sophia.mmorpg.player.ref.PlayerProfessionRef;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class MGFightPropertyMgrTest {
	private static final Logger logger = Logger.getLogger(MGFightPropertyMgrTest.class);
	private static MGFightPropertyMgr fightPropertyMgr = new MGFightPropertyMgr();

	@Before
	public void setUp() throws Exception {
		JSONDataManagerContext.load();
		PropertySymbolLoader.load();
		
	}

	@Test
	public void testCrtModifyImpl() {
		PlayerProfessionRefLoader loader = new PlayerProfessionRefLoader();
		Collection<PlayerProfessionRef> professionRefs = loader.load("enchanter");
		PlayerProfessionRef professionRef = (PlayerProfessionRef) professionRefs.toArray()[0];
		
		PlayerProfessionLevelData playerProfessionLevelData = professionRef.getPlayerClassLevelData(1);
		PropertyDictionary levelPd = playerProfessionLevelData.getLevelProperties();
		int maxHP = MGPropertyAccesser.getMaxHP(levelPd);
		int maxMP = MGPropertyAccesser.getMaxMP(levelPd);
		
		PropertyDictionary fightPropertyDictionary = new PropertyDictionary();
		MGPropertyAccesser.setOrPutAtkSpeed(fightPropertyDictionary, MGPropertyAccesser.getAtkSpeed(levelPd));
		MGPropertyAccesser.setOrPutDodge(fightPropertyDictionary, MGPropertyAccesser.getDodge(levelPd));
		MGPropertyAccesser.setOrPutMaxPAtk(fightPropertyDictionary, MGPropertyAccesser.getMaxPAtk(levelPd));
		MGPropertyAccesser.setOrPutMaxMAtk(fightPropertyDictionary, MGPropertyAccesser.getMaxMAtk(levelPd));
		MGPropertyAccesser.setOrPutMaxTao(fightPropertyDictionary, MGPropertyAccesser.getMaxTao(levelPd));
		MGPropertyAccesser.setOrPutMaxPDef(fightPropertyDictionary, MGPropertyAccesser.getMaxPDef(levelPd));
		MGPropertyAccesser.setOrPutMaxMDef(fightPropertyDictionary, MGPropertyAccesser.getMaxMDef(levelPd));
		MGPropertyAccesser.setOrPutMinPAtk(fightPropertyDictionary, MGPropertyAccesser.getMinPAtk(levelPd));
		MGPropertyAccesser.setOrPutMinMAtk(fightPropertyDictionary, MGPropertyAccesser.getMinMAtk(levelPd));
		MGPropertyAccesser.setOrPutMinTao(fightPropertyDictionary, MGPropertyAccesser.getMinTao(levelPd));
		MGPropertyAccesser.setOrPutMinPDef(fightPropertyDictionary, MGPropertyAccesser.getMinPDef(levelPd));
		MGPropertyAccesser.setOrPutMinMDef(fightPropertyDictionary, MGPropertyAccesser.getMinMDef(levelPd));
		MGPropertyAccesser.setOrPutHit(fightPropertyDictionary, MGPropertyAccesser.getHit(levelPd));
		MGPropertyAccesser.setOrPutMoveSpeed(fightPropertyDictionary, MGPropertyAccesser.getMoveSpeed(levelPd));
		MGPropertyAccesser.setOrPutMaxHP(fightPropertyDictionary, maxHP);
		MGPropertyAccesser.setOrPutMaxMP(fightPropertyDictionary, maxMP);
		MGPropertyAccesser.setOrPutHealHP(fightPropertyDictionary, maxHP);
		MGPropertyAccesser.setOrPutHealMP(fightPropertyDictionary, maxMP);
		logger.debug("configFightProperty: fightPropertyDictionary: " + fightPropertyDictionary);
		fightPropertyMgr.setCrtPropertyDictionary(fightPropertyDictionary);
		
		SkillRefLoader skillLoader = new SkillRefLoader();
		Collection<SkillRef> skillRefs = skillLoader.load("skill_1");
		SkillRef skillRef = (SkillRef) skillRefs.toArray()[0];
		SkillLevelRef levelRef = skillRef.get(1);
		
		PropertyDictionaryModifyPhase appendPhase = FightPropertyMgr.getModifyPhaseFromPool();
		appendPhase.modify(levelRef.getRuntimeParameter());
		logger.debug("skillEffect: " + levelRef.getRuntimeParameter());
		logger.debug("appendPhase: " + appendPhase);

		FightPropertyEffectTransaction effectTransaction = fightPropertyMgr.getEffectTransaction();

		PropertyDictionaryModifyPhase appendPhase2 = effectTransaction.attach(appendPhase);
		
		logger.debug("appendPhase2: " + appendPhase2);
	}

}
