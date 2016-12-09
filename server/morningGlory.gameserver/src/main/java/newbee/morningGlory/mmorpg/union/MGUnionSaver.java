package newbee.morningGlory.mmorpg.union;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import newbee.morningGlory.mmorpg.union.persistence.MGUnionDAO;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.util.DebugUtil;

public class MGUnionSaver {
	private static Logger logger = Logger.getLogger(MGUnionSaver.class);

	private static MGUnionSaver instance = new MGUnionSaver();

	private MGUnionSaver() {
	}

	public static MGUnionSaver getInstance() {
		return instance;
	}

	private static Set<MGUnion> saveImmediateSetPrimary = Collections.newSetFromMap(new ConcurrentHashMap<MGUnion, Boolean>());
	private static Set<MGUnion> saveImmediateSetSecondary = Collections.newSetFromMap(new ConcurrentHashMap<MGUnion, Boolean>());

	public static Set<MGUnion> getSaveImmediateSetPrimary() {
		return saveImmediateSetPrimary;
	}

	public static Set<MGUnion> getSaveImmediateSetSecondary() {
		return saveImmediateSetSecondary;
	}

	public void save() {
		if (logger.isDebugEnabled()) {
			logger.debug("公会数据update");
		}
		if (saveImmediateSetPrimary.isEmpty()) {
			return;
		}
		saveImmediateSetSecondary.addAll(saveImmediateSetPrimary);
		saveImmediateSetPrimary.removeAll(saveImmediateSetSecondary);
		for (MGUnion union : saveImmediateSetSecondary) {
			try {
				updateUnion(union);
			} catch (Exception e) {
				saveImmediateSetPrimary.add(union);
				logger.error("save error, unionName=" + union.getName());
				logger.error("save error, " + DebugUtil.printStack(e));
			}
		}
		saveImmediateSetSecondary.clear();
	}

	public void shutDownSave() throws Exception {
		save();
		if (!saveImmediateSetPrimary.isEmpty()) {
			throw new RuntimeException("MGUnionSaver Save Data error!!");
		}
	}

	public void updateUnion(MGUnion union) throws Exception {
		MGUnionDAO.getInstance().updateMemberData(union);
		MGUnionDAO.getInstance().updateApplyData(union);
		MGUnionDAO.getInstance().updateUnionData(union);
	}

	public void saveImmediateData(MGUnion union) {
		saveImmediateSetPrimary.add(union);
	}

	public void insertData(MGUnion union) {
		try {
			MGUnionDAO.getInstance().insertData(union);
		} catch (Exception e) {
			logger.error("insertData error, unionName=" + union.getName());
			logger.error("insertData error, " + DebugUtil.printStack(e));
		}
	}

	public void deleteData(MGUnion union) {
		try {
			MGUnionDAO.getInstance().deleteData(union);
		} catch (Exception e) {
			logger.error("deleteData error, unionName=" + union.getName());
			logger.error("deleteData error, " + DebugUtil.printStack(e));
		}
	}

	public MGUnion getUnionByName(String unionName) {
		for (MGUnion union : saveImmediateSetPrimary) {
			if (StringUtils.equals(unionName, union.getName())) {
				return union;
			}
		}
		return null;
	}
}
