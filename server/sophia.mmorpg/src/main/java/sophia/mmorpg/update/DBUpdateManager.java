package sophia.mmorpg.update;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import sophia.mmorpg.update.v0_4.VerDBUpdate0_4;

/**
 * 数据库升级为顺序执行，不用考虑多线程的问题 Copyright (c) 2014 by 游爱.
 * 
 */

public class DBUpdateManager {
	private final static Logger logger = Logger.getLogger(DBUpdateManager.class);
	private List<VerDBUpdate> verList = new ArrayList<VerDBUpdate>();

	private Comparator<VerDBUpdate> verdb_sort_compar = new Comparator<VerDBUpdate>() {
		@Override
		public int compare(VerDBUpdate ver, VerDBUpdate ver2) {
			int tmp = ver.getMajorVersion() - ver2.getMajorVersion();
			if (tmp > 0)
				return 1;
			else if (tmp < 0)
				return -1;
			else
				return ver.getMinorVersion() - ver2.getMinorVersion();
		}
	};

	public DBUpdateManager() {
		// 0.4
		verList.add(new VerDBUpdate0_4());
		
		// 0.5
		
		// 0.6
		
		Collections.sort(verList, verdb_sort_compar);
		
	}

	public boolean processAll() throws Exception {
		StringBuffer sb = new StringBuffer("Check ").append(" patches(").append(verList.size()).append("), there will be some minutes...");
		logger.info(sb.toString());

		for (VerDBUpdate ver : verList) {
			if (!ver.upgrade()) {
				logger.info("Upgrade " + ver.getMajorVersion() + "." + ver.getMinorVersion() + "." + ver.getFractionalVersion() + " failure!");
				return false;
			}
		}

		logger.info("Check upgrade patches finished.");
		return true;
	}
}
