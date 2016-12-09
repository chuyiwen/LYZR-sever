package newbee.morningGlory.checker.refObjectChecker.monster;

import newbee.morningGlory.checker.BaseRefChecker;
import sophia.foundation.property.PropertyDictionary;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.monster.ref.MonsterRef;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

import com.google.common.base.Strings;

public class MonsterRefChecker extends BaseRefChecker<MonsterRef> {

	@Override
	public String getDescription() {
		return "怪物";
	}

	@Override
	public void check(GameRefObject gameRefObject) {
		MonsterRef ref = (MonsterRef) gameRefObject;
		PropertyDictionary pd = ref.getProperty();
		if (Strings.isNullOrEmpty(MGPropertyAccesser.getName(pd))) {
			error(gameRefObject, "monsterRefId = " + ref.getId() + " 名字为空");
		}
		
		byte kind = MGPropertyAccesser.getKind(pd);
		if (kind < 1 || kind > 7) {
			error(gameRefObject, "monsterRefId = " + ref.getId() + " 非法的kind = " + kind);
		}
		
		byte quality = MGPropertyAccesser.getQuality(pd);
		if (quality < 1 || quality > 3) {
			error(gameRefObject, "monsterRefId = " + ref.getId() + " 非法的quality = " + quality);
		}
		
		int level = MGPropertyAccesser.getLevel(pd);
		if (level < 1 || level > 100) {
			error(gameRefObject, "monsterRefId = " + ref.getId() + " 非法的level = " + level);
		}
		
		byte attackType = MGPropertyAccesser.getAttackType(pd);
		if (attackType < 1 || attackType > 2) {
			error(gameRefObject, "monsterRefId = " + ref.getId() + " 非法的attackType = " + attackType);
		}
		
		// 非技能怪，必须有技能
		if (ref.getFightSkillList().size() == 0 && kind != 6) {
			error(gameRefObject, "monsterRefId = " + ref.getId() + " 没有技能skill=null");
		}
	}

}
