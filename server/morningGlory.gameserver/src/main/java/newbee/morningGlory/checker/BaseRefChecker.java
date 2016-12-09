package newbee.morningGlory.checker;

import java.util.Collection;

import sophia.game.GameRoot;
import sophia.game.ref.GameRefObject;
import sophia.game.ref.GameRefObjectLoader;

public abstract class BaseRefChecker<T extends GameRefObject> implements RefChecker<T> {

	private RefCheckerManager refCheckerManager = new RefCheckerManager();

	public BaseRefChecker() {
	}

	@Override
	public Collection<T> delegateLoadAll(GameRefObjectLoader<T> loadSlaver) {
		try {
			Collection<T> all = loadSlaver.loadAll();
			refCheckerManager.getOutputCtrl().println("载入数据 @ " + loadSlaver.getClass().getSimpleName() + ".loadAll()...OK ...数量:" + all.size());
			return all;
		} catch (Throwable ex) {
			refCheckerManager.getOutputCtrl().error("载入数据 @ " + loadSlaver.getClass().getSimpleName() + ".loadAll()", ex);
			throw new Error(ex);
		}

	}

	protected void error(GameRefObject gameRefObject, String info) {
		Error error = new Error();
		System.out.println(refCheckerManager);
		refCheckerManager.getOutputCtrl().error(gameRefObject.getClass().getSimpleName() + "[" + gameRefObject.getId() + " # " + gameRefObject.toString() + "] @ " + info, error);
		throw error;
	}

	protected void error(GameRefObject gameRefObject, String info, Exception ex) {
		if (ex == null)
			ex = new Exception();
		error(gameRefObject, info);
		refCheckerManager.getOutputCtrl().error(info, ex);
		throw new Error(ex);
	}

	protected void checkEqualToTheNumber(GameRefObject gameRefObject, int fieldValue, int theNumber, String fieldName) {
		if (fieldValue != theNumber) {
			error(gameRefObject, getDescription() + fieldName + "必须等于" + theNumber + "，错误的" + fieldName + "为: " + fieldValue);
		}
	}

	protected void checkMoreThanOrEqualToTheNumber(GameRefObject gameRefObject, int fieldValue, int theNumber, String fieldName) {
		if (fieldValue < theNumber) {
			error(gameRefObject, getDescription() + fieldName + "必须大于等于" + theNumber + "，错误的" + fieldName + "为: " + fieldValue);
		}
	}

	protected void checkMoreThanOrEqualToTheNumber(GameRefObject gameRefObject, long fieldValue, long theNumber, String fieldName) {
		if (fieldValue < theNumber) {
			error(gameRefObject, getDescription() + fieldName + "必须大于等于" + theNumber + "，错误的" + fieldName + "为: " + fieldValue);
		}
	}

	protected void checkLessThanOrEqualToTheNumber(GameRefObject gameRefObject, int fieldValue, int theNumber, String fieldName) {
		if (fieldValue > theNumber) {
			error(gameRefObject, getDescription() + fieldName + "必须小于等于" + theNumber + "，错误的" + fieldName + "为: " + fieldValue);
		}
	}

	protected void checkBetweenTheNumber(GameRefObject gameRefObject, int fieldValue, int theMinNumber, int theMaxNumber, String fieldName) {
		if (!(fieldValue >= theMinNumber && fieldValue <= theMaxNumber)) {
			error(gameRefObject, getDescription() + fieldName + "必须大于等于" + theMinNumber + ",小于等于" + theMaxNumber + "，错误的" + fieldName + "为: " + fieldValue);
		}
	}

	protected void checkStartsWithTheString(GameRefObject gameRefObject, String fieldValue, String theString, String fieldName) {
		if (!fieldValue.startsWith(theString)) {
			error(gameRefObject, getDescription() + "请以 " + theString + " 开头!!! 错误的" + fieldName + "为: " + fieldValue);
		}
	}

	protected void checkInGameRefObjectManager(GameRefObject gameRefObject, String refId, String fieldName) {
		if (GameRoot.getGameRefObjectManager().getManagedObject(refId) == null) {
			error(gameRefObject, getDescription() + fieldName + "不存在 !!! 错误的" + fieldName + "为: " + refId);
		}
	}
}