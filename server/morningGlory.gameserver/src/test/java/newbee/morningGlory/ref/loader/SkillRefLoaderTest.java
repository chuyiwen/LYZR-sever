package newbee.morningGlory.ref.loader;

import static org.junit.Assert.assertTrue;

import java.util.Collection;

import newbee.morningGlory.ref.JSONDataManagerContext;
import newbee.morningGlory.ref.symbol.PropertySymbolLoader;

import org.junit.Before;
import org.junit.Test;

import sophia.foundation.property.PropertyDictionary;
import sophia.mmorpg.player.fightSkill.ref.SkillRef;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class SkillRefLoaderTest {

	@Before
	public void setUp() throws Exception {
		JSONDataManagerContext.load();
		PropertySymbolLoader.load();
	}

	@Test
	public void testLoadAll() {
		SkillRefLoader loader = new SkillRefLoader();
		Collection<SkillRef> skillRefs = loader.loadAll();
	}

	@Test
	public void testLoad() {
		SkillRefLoader loader = new SkillRefLoader();
		Collection<SkillRef> skillRefs = loader.load("skill_0", "skill_zs_1");
		assertTrue(skillRefs.size() == 2);
		
		SkillRef ref0 = (SkillRef) skillRefs.toArray()[0];
		PropertyDictionary ref0PD = ref0.getProperty();
		byte professionId = MGPropertyAccesser.getProfessionId(ref0PD);
		assertTrue(professionId == 0);
		byte skillAtkType = MGPropertyAccesser.getSkillAtkType(ref0PD);
		assertTrue(skillAtkType == 1);
		String skillFunction = MGPropertyAccesser.getSkillFunction(ref0PD);
		assertTrue(skillFunction.equals("player_0_0_closure"));
		
		SkillRef ref1 = (SkillRef) skillRefs.toArray()[1];
		PropertyDictionary ref1PD = ref1.getProperty();
		byte professionId2 = MGPropertyAccesser.getProfessionId(ref1PD);
		assertTrue(professionId2 == 1);
	}
}
