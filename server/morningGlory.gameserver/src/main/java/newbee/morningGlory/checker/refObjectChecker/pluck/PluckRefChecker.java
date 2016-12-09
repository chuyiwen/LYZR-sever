package newbee.morningGlory.checker.refObjectChecker.pluck;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import newbee.morningGlory.checker.BaseRefChecker;
import sophia.foundation.property.PropertyDictionary;
import sophia.game.GameRoot;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.pluck.PluckRef;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

import com.google.common.base.Strings;

public class PluckRefChecker extends BaseRefChecker<PluckRef> {

	@Override
	public void check(GameRefObject gameRefObject) {
		PluckRef ref = (PluckRef) gameRefObject;

		if (!ref.getId().startsWith("npc_collect_")) {
			error(gameRefObject, "采集<refId>错误 , 请以npc_collect_开头!!! 错误的refId为: " + ref.getId());
		}

		String refId = ref.getId();
		if (null == GameRoot.getGameRefObjectManager().getManagedObject(refId)) {
			error(gameRefObject, "采集refId不存在:" + refId);
		}

		checkItemReward(ref);
		checkProperty(ref);
	}

	private void checkItemReward(PluckRef ref) {
		Map<String, HashMap<Integer, Float>> itemRewardMapping = ref.getItemRewardMapping();
		Map<String, Integer> propertyRewardMapping = ref.getPropertyRewardMapping();

		for (Entry<String, HashMap<Integer, Float>> entry : itemRewardMapping.entrySet()) {
			String itemRefId = entry.getKey();
			if (Strings.isNullOrEmpty(itemRefId)) {
				error(ref, "采集itemRefId值为空");
			}

			Map<Integer, Float> itemcountMapping = entry.getValue();
			float probability = 0.0f;
			for (Entry<Integer, Float> tmpEntry : itemcountMapping.entrySet()) {
				if (tmpEntry.getValue() < 0.0f) {
					error(ref, "采集物品数量概率probability值小于0了");
				}
				probability = probability + tmpEntry.getValue();
			}
			if (probability != 1.0f) {
				error(ref, "采集物品数量概率probability总和不为1");
			}
		}

		for (Entry<String, Integer> entry : propertyRewardMapping.entrySet()) {
			String itemRefId = entry.getKey();
			int count = entry.getValue();
			if (Strings.isNullOrEmpty(itemRefId)) {
				error(ref, "采集itemRefId值为空");
			}

			if (count < 0) {
				error(ref, "采集count指小于0");
			}
		}

	}

	private void checkProperty(PluckRef ref) {
		PropertyDictionary pd = ref.getProperty();

		byte pluckBehavior = MGPropertyAccesser.getPluckBehavior(pd);
		byte pluckItemAdscription = MGPropertyAccesser.getPluckItemAdscription(pd);
		int pluckLevel = MGPropertyAccesser.getPluckLevel(pd);
		int pluckRefreshTime = MGPropertyAccesser.getPluckRefreshTime(pd);
		int pluckTime = MGPropertyAccesser.getPluckTime(pd);

		if (pluckBehavior < 0)
			error(ref, printInfo(pluckBehavior));

		if (pluckItemAdscription < 0)
			error(ref, printInfo(pluckItemAdscription));

		if (pluckLevel < 0)
			error(ref, printInfo(pluckLevel));

		if (pluckRefreshTime < 0)
			error(ref, printInfo(pluckRefreshTime));

		if (pluckTime < 0)
			error(ref, printInfo(pluckTime));
	}

	private String printInfo(int field) {
		return "采集<" + field + ">值小于0了" + field;
	}

	@Override
	public String getDescription() {
		return "采集物";
	}

}
