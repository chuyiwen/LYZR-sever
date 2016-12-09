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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import sophia.stat.dao.StatLogDAO;
import sophia.stat.dao.StatRechargeDAO;

import com.google.common.util.concurrent.AbstractIdleService;

public abstract class StatLogService extends AbstractIdleService {
	private static Logger logger = Logger.getLogger(StatLogService.class);

	private StatLogDAO db = null;
	private StatRechargeDAO rdb = new StatRechargeDAO();;
	private List<StatLog> statLogs = null;
	private Set<StatRechargeData> rechargeOneLogs = Collections.newSetFromMap(new ConcurrentHashMap<StatRechargeData, Boolean>());
	private Set<StatRechargeData> rechargeTwoLogs = Collections.newSetFromMap(new ConcurrentHashMap<StatRechargeData, Boolean>());
	//private Timer timer = null;
	private int period = 60; // 1分钟
	private ScheduledFuture<?> beeperHandle;
	

	public <T extends StatLog> void save(final T statLog) {
		if (statLogs == null) {
			return;
		}

		synchronized (statLogs) {
			statLogs.add(statLog);
		}
	}

	public void save(StatRechargeData data) {
		if (rechargeOneLogs == null) {
			return;
		}
		rechargeOneLogs.add(data);
		rechargeLogSave();
	}

	@Override
	protected void startUp() throws Exception {
		statLogs = new ArrayList<StatLog>();
		db = new StatLogDAO();	
		//timer = new Timer();
		beeperHandle = Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {

				List<StatLog> logs = null;
				synchronized (statLogs) {
					logs = statLogs;
					statLogs = new ArrayList<StatLog>();

					if (logs != null && logs.size() > 0) {
						save(logs);

						for (StatLog log : logs) {
							log.recycle();
						}
					}

				}
			}
		}, period, period,TimeUnit.SECONDS);

		onStartup();
	}

	public abstract void onStartup();

	@Override
	protected void shutDown() throws Exception {
		//timer.cancel();
		beeperHandle.cancel(false);
		
		try {
			if (logger.isInfoEnabled()) {
				logger.info("waiting for stat log service future stop");
			}	
			beeperHandle.get();	
		} catch (Exception e) {
		}
		
		save(statLogs);

		onShutdown();

		if (logger.isInfoEnabled()) {
			logger.info("stat log service future was terminated");
		}
	}

	public abstract void onShutdown();

	private void save(List<StatLog> statLogs) {
		List<StatLogDBData> datas = new ArrayList<StatLogDBData>();
		for (StatLog log : statLogs) {
			datas.add(log.toDBData());
		}
		try {
			db.insert(datas);
		} catch (Exception ex) {
			logger.error(ex);
		}
	}

	private void rechargeLogSave() {
		rechargeTwoLogs.addAll(rechargeOneLogs);
		rechargeOneLogs.removeAll(rechargeTwoLogs);
		try {
			rdb.insert(rechargeTwoLogs);
		} catch (Exception ex) {
			logger.error(ex);
		}
		rechargeTwoLogs.clear();
	}

}
