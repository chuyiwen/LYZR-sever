package newbee.morningGlory.checker.refObjectChecker.peerage;

import newbee.morningGlory.checker.BaseRefChecker;
import newbee.morningGlory.mmorpg.player.peerage.MGPeerageRef;
import sophia.foundation.property.PropertyDictionary;
import sophia.game.GameRoot;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class PeerageRefChecker extends BaseRefChecker<MGPeerageRef> {

	@Override
	public void check(GameRefObject gameRefObject) {
		MGPeerageRef ref = (MGPeerageRef) gameRefObject;

		if (!ref.getId().startsWith("knight_")) {
			error(gameRefObject, "爵位<refId>错误 , 请以knihgt_开头!!! 错误的refId为: " + ref.getId());
		}

		String refId = ref.getId();
		if (null == GameRoot.getGameRefObjectManager().getManagedObject(refId)) {
			error(gameRefObject, "爵位RefID不存在：" + refId);
		}

		checkKnightData(ref);
		checkProperty(ref);
	}

	private void checkKnightData(MGPeerageRef ref) {
		String refId = ref.getId();
		if (!"knight_10".equals(refId)) {
			if (ref.getMeritCondition() < 0) {
				error(ref, "爵位upgradeSrcConsume<merit>值小于0了");
			}
		}

	}

	private void checkProperty(MGPeerageRef ref) {
		PropertyDictionary pd = ref.getProperty();

		int knight = MGPropertyAccesser.getKnight(pd);
		if (0 > knight || 10 < knight) {
			error(ref, "爵位<knight>值小于0了");
		}

		int roleGrade = MGPropertyAccesser.getRoleGrade(ref.getProperty());
		if (roleGrade < 30) {
			error(ref, "爵位<roleGrade>错误,roleGrade值应该在30以上!!! 错误的roleGrade: " + roleGrade);
		}

		String knightPreRefId = MGPropertyAccesser.getKnightPreRefId(pd);
		String knightNextRefId = MGPropertyAccesser.getKnightNextRefId(pd);
		if (!"".equals(knightPreRefId) && !knightPreRefId.startsWith("knight_")) {
			error(ref, "爵位<knightPreRefId>错误 , 请以knihgt_开头!!! 错误的knightPreRefId为: " + knightPreRefId);
		}

		if (!"".equals(knightNextRefId) && !knightNextRefId.startsWith("knight_")) {
			error(ref, "爵位<knightNextRefId>错误 , 请以knihgt_开头!!! 错误的knightNextRefId为: " + knightNextRefId);
		}
	}

	@Override
	public String getDescription() {
		return "爵位";
	}

}
