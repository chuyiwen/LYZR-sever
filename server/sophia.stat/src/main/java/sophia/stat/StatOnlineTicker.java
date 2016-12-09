/**
 *   Copyright 2013-2015 Sophia
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package sophia.stat;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import sophia.foundation.util.DebugUtil;
import sophia.stat.dao.StatOnlineDAO;

public abstract class StatOnlineTicker {
	private static final Logger logger = Logger.getLogger(StatOnlineTicker.class);
	private StatOnlineDAO db = null;
	private Timer timer = null;
	private int period = 1000 * 60; // 60秒
	private int lastMinute = -1;

	public void startup() {
		db = new StatOnlineDAO();

		timer = new Timer();

		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				save();
			}

		}, 5000, period);

		if (logger.isInfoEnabled()) {
			logger.info("StatOnlineTickerService was started.");
		}

	}

	public void shutdown() {
		timer.cancel();
		if (logger.isInfoEnabled()) {
			logger.info("StatOnlineTickerService was terminated.");
		}
	}

	public abstract void selectTotalNum();
	
	public abstract int getTotalNum();
	
	public abstract void addTotalNum(int addNum);
	
	public abstract void setTotalNum(int totalNum);
	
	public abstract int getTotalIncNum();
	
	public abstract int getOnlineNum();
	
	public abstract int getConnectedNum();
	
	public abstract int getLoggedNum();
	
	public abstract int getConnectedIpNum();
	
	public abstract int getLoggedUidNum();
	
	public abstract int getEnteredNum();
	
	public abstract int getEnteredUidNum();
	
	public abstract void onEntered(String identityId);
	
	public abstract void onLogged(String identityId);
	
	public abstract void onConnected(String ip);
	
	public abstract void onTickSave();

	@SuppressWarnings({ "deprecation" })
	private void save() {
//		Date date = new Date();
		Calendar crtCalenda = Calendar.getInstance();
		int year = crtCalenda.get(Calendar.YEAR);
//		int month = date.getMonth() + 1;
//		int day = date.getDate();
//		int hour = date.getHours();
//		int minute = date.getMinutes();
		
		int month = crtCalenda.get(Calendar.MONTH) + 1;
		int day = crtCalenda.get(Calendar.DAY_OF_MONTH);
		int hour = crtCalenda.get(Calendar.HOUR_OF_DAY);
		int minute = crtCalenda.get(Calendar.MINUTE);
		int total = getTotalNum();
		int online = getOnlineNum();
		int total_inc = getTotalIncNum();
		int connected = getConnectedNum();
		int connected_ips = getConnectedIpNum();
		int logged = getLoggedNum();
		int logged_uids = getLoggedUidNum();
		int entered = getEnteredNum();
		int entered_uids = getEnteredUidNum();
		if (lastMinute != minute) {
			try {
				db.insert(year, month, day, hour, minute, total,total_inc, online,connected,connected_ips,logged,logged_uids,entered,entered_uids);
				lastMinute = minute;
				onTickSave();
			} catch (Exception ex) {
				logger.error(DebugUtil.printStack(ex));
			}
		}	
	}
}
