package newbee.morningGlory.checker.refObjectChecker.sceneActivity;

import sophia.game.ref.GameRefObject;
import newbee.morningGlory.checker.BaseRefChecker;
import newbee.morningGlory.mmorpg.sceneActivities.monsterIntrusion.ref.MonsterInvasionScrollRef;

public class MonsterInvasionScrollRefChecker extends BaseRefChecker<MonsterInvasionScrollRef>{

	@Override
	public String getDescription() {
		return "怪物入侵玩家tip提示";
	}

	@Override
	public void check(GameRefObject gameRefObject) {
		MonsterInvasionScrollRef ref = (MonsterInvasionScrollRef)gameRefObject;
		String refId = ref.getId();
		//String range = ref.getRange();
		int stage= ref.getStage();
		String tips = ref.getTips();
		if(refId.indexOf("monsterInvasionScroll_")<0){
			error(ref,"怪物入侵活动玩家适配提示配置refId<refId>非法，id不包含指定字符："+refId);
		}
		if(stage<=0){
			error(ref,"怪物入侵活动玩家适配提示配置阶段类型<stage>非法，数值小于零："+stage);
		}
		if(tips == null || tips.equals("")){
			error(ref,"怪物入侵活动玩家适配提示文字<tips>非法，数值为空。");
		}
	}

}
