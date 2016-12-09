package sophia.mmorpg.base.sprite.fightProperty;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import sophia.foundation.property.PropertyDictionary;
import sophia.mmorpg.core.PropertyDictionaryModifyPhase;
import sophia.mmorpg.core.PropertyPool;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class FightPropertySnapshotPoolTest {
	private final static Logger logger = Logger.getLogger(FightPropertySnapshotPoolTest.class);
	private PropertyPool Snapshot_Pool = new PropertyPool();

	@Before
	public void setUp() throws Exception {
		PropertyDictionary snapshotPropertyDictionary = new PropertyDictionary();
		MGPropertyAccesser.setOrPutHealHP(snapshotPropertyDictionary, 0);
		MGPropertyAccesser.setOrPutMaxHP(snapshotPropertyDictionary, 0);
		MGPropertyAccesser.setOrPutHealMP(snapshotPropertyDictionary, 0);
		MGPropertyAccesser.setOrPutMaxMP(snapshotPropertyDictionary, 0);
		MGPropertyAccesser.setOrPutMinPAtk(snapshotPropertyDictionary, 0);
		MGPropertyAccesser.setOrPutMaxPAtk(snapshotPropertyDictionary, 0);
		MGPropertyAccesser.setOrPutMinMAtk(snapshotPropertyDictionary, 0);
		MGPropertyAccesser.setOrPutMaxMAtk(snapshotPropertyDictionary, 0);
		MGPropertyAccesser.setOrPutMinTao(snapshotPropertyDictionary, 0);
		MGPropertyAccesser.setOrPutMaxTao(snapshotPropertyDictionary, 0);
		MGPropertyAccesser.setOrPutMinPDef(snapshotPropertyDictionary, 0);
		MGPropertyAccesser.setOrPutMaxPDef(snapshotPropertyDictionary, 0);
		MGPropertyAccesser.setOrPutMinMDef(snapshotPropertyDictionary, 0);
		MGPropertyAccesser.setOrPutMaxMDef(snapshotPropertyDictionary, 0);
		MGPropertyAccesser.setOrPutHit(snapshotPropertyDictionary, 0);
		MGPropertyAccesser.setOrPutDodge(snapshotPropertyDictionary, 0);
		MGPropertyAccesser.setOrPutMoveSpeed(snapshotPropertyDictionary, 0);
		MGPropertyAccesser.setOrPutAtkSpeed(snapshotPropertyDictionary, 0);
		MGPropertyAccesser.setOrPutPImmunityPer(snapshotPropertyDictionary, 0);
		MGPropertyAccesser.setOrPutMImmunityPer(snapshotPropertyDictionary, 0);
		MGPropertyAccesser.setOrPutIgnorePDef(snapshotPropertyDictionary, 0);
		MGPropertyAccesser.setOrPutIgnoreMDef(snapshotPropertyDictionary, 0);
		MGPropertyAccesser.setOrPutCrit(snapshotPropertyDictionary, 0);
		MGPropertyAccesser.setOrPutCritInjure(snapshotPropertyDictionary, 0);
		MGPropertyAccesser.setOrPutFortune(snapshotPropertyDictionary, 0);
		MGPropertyAccesser.setOrPutPDodgePer(snapshotPropertyDictionary, 0);
		MGPropertyAccesser.setOrPutMDodgePer(snapshotPropertyDictionary, 0);
		// FIXME: 黄晓源，还需要加状态属性.比如：移动

		Snapshot_Pool.setPropertyDictionary(snapshotPropertyDictionary);
	}

	@Test
	public void testObtain() {
		PropertyDictionaryModifyPhase newSnapshot = Snapshot_Pool.obtain();
		logger.debug(newSnapshot);
	}

}
