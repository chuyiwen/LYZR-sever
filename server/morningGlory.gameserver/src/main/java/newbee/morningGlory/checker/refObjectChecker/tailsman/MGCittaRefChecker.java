/**
 * 
 */
package newbee.morningGlory.checker.refObjectChecker.tailsman;

import org.apache.commons.lang3.StringUtils;

import sophia.game.GameRoot;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import newbee.morningGlory.checker.BaseRefChecker;
import newbee.morningGlory.mmorpg.player.talisman.MGCittaRef;

/**
 * @author Administrator2014-6-25
 * 
 */
public class MGCittaRefChecker extends BaseRefChecker<MGCittaRef> {

	@Override
	public String getDescription() {
		return "心法等级";
	}

	@Override
	public void check(GameRefObject gameRefObject) {
		MGCittaRef ref = (MGCittaRef) gameRefObject;
		byte talisManLevel = MGPropertyAccesser.getTalisManLevel(ref.getProperty());
		if (talisManLevel < 0 || talisManLevel > 60) {
			error(gameRefObject, "心法等级出错,等级：" + talisManLevel);
		}
		int levelUpUseMaterialCount = ref.getLevelUpUseMaterialCount();
		if (levelUpUseMaterialCount < 0) {
			error(gameRefObject, "心法升级所使用的材料数小于0");
		}
		String nextRefId = ref.getNextRefId();
		String preRefId = ref.getPreRefId();
		
		if(!StringUtils.equals(ref.getId(), "citta_1") ){
			MGCittaRef pre = (MGCittaRef) GameRoot.getGameRefObjectManager().getManagedObject(preRefId);
			String nextRefId2 = pre.getNextRefId();
			if (!StringUtils.equals(nextRefId2, ref.getId())) {
				error(gameRefObject, "心法升级前一个等级填写错误,当前:" + ref.getId() + ",前一个:" + preRefId);
			}
		}
		
		if (!StringUtils.equals(ref.getId(), "citta_60")) {
			MGCittaRef next = (MGCittaRef) GameRoot.getGameRefObjectManager().getManagedObject(nextRefId);
			String preRefId2 = next.getPreRefId();
			if (!StringUtils.equals(preRefId2, ref.getId())) {
				error(gameRefObject, "心法升级后一个等级填写错误,当前:" + ref.getId() + ",后一个:" + nextRefId);
			}
		}
	}

}
