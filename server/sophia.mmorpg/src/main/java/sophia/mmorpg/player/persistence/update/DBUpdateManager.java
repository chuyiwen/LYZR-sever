package sophia.mmorpg.player.persistence.update;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Copyright (c) 2011 by 游爱.
 * 
 * @author XieEEE 2013-10-8 下午5:34:30
 */

public class DBUpdateManager {
	private final static Logger logger = Logger.getLogger(DBUpdateManager.class);
	private List<IPatch> patchList = new ArrayList<IPatch>();

	public DBUpdateManager() {
		//patchList.add();
	}

	private static String errMsg(int state) {
		switch (state) {
		case 0:
			return "skip";
		case 1:
			return "success";
		}
		return "fail";
	}

	public void processAll() {
		StringBuffer sb = new StringBuffer("Check ").append(" patches(").append(patchList.size()).append("), there will be some minutes...");
		for (IPatch patch : patchList) {
			sb = new StringBuffer("The ").append(patch.getPatchName()).append(": ").append("\"").append(patch.getName()).append("\" on [").append(patch.getPublishDate())
					.append("] - ").append(patch.getDescription());

			logger.info(sb.toString());
			patch.upgrade();
			logger.info(errMsg(patch.getState()) + ".");
			if (patch.getState() == 2) {
				logger.info(patch.getErrMsg());
			}
		}
		logger.info("Check upgrade patches finished.");
	}
}
