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
package sophia.mmorpg.stat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.player.persistence.PlayerDAO;

public class StatOnlineTickerImpl extends sophia.stat.StatOnlineTicker {
	
	private AtomicInteger connectedNum = new AtomicInteger(0);
	private List<String> connectedIps = new ArrayList<String>();
	private AtomicInteger loggedNum = new AtomicInteger(0);
	private List<String> loggedUids = new ArrayList<String>();
	private AtomicInteger enteredNum = new AtomicInteger(0);
	private List<String> enteredUids = new ArrayList<String>();
	private int last_players =-1;
	private int totalNum = 0;
	
	@Override
	public void selectTotalNum() {
		setTotalNum(PlayerDAO.getInstance().getSelectPlayerTotal());
	}
	@Override
	public int getTotalNum() {
		return  totalNum;
	
	}
	@Override
	public void addTotalNum(int addNum) {
		totalNum += addNum;
	}

	@Override
	public void setTotalNum(int totalNum) {
		this.totalNum = totalNum;
	}
	@Override
	public int getOnlineNum() {
		return PlayerManager.getOnlineTotalCount();
	}

	@Override
	public int getTotalIncNum() {
		if(last_players == -1){
			return 0;
		}
		return getTotalNum() - last_players;
	}

	@Override
	public int getConnectedNum() {
		return connectedNum.get();
	}

	@Override
	public int getConnectedIpNum() {
		synchronized (connectedIps) {
			return connectedIps.size();
		}
	}

	@Override
	public int getLoggedNum() {
		return loggedNum.get();
	}

	@Override
	public int getLoggedUidNum() {
		synchronized (loggedUids) {
			return loggedUids.size();
		}
	}

	@Override
	public int getEnteredNum() {
		return enteredNum.get();
	}

	@Override
	public int getEnteredUidNum() {
		synchronized (enteredUids) {
			return enteredUids.size();
		}
	}

	@Override
	public void onTickSave() {

		last_players = getTotalNum();

		connectedNum.set(0);
		loggedNum.set(0);
		enteredNum.set(0);

		synchronized (connectedIps) {
			connectedIps.clear();
		}

		synchronized (loggedUids) {
			loggedUids.clear();
		}

		synchronized (enteredUids) {
			enteredUids.clear();
		}
	}

	@Override
	public void onConnected(String ip) {

		connectedNum.addAndGet(1);

		synchronized (connectedIps) {
			if (!connectedIps.contains(ip))
				connectedIps.add(ip);
		}
	}

	@Override
	public void onLogged(String identityId) {

		if (identityId == null)
			identityId = "";

		loggedNum.addAndGet(1);

		synchronized (loggedUids) {
			if (!loggedUids.contains(identityId))
				loggedUids.add(identityId);
		}
	}

	@Override
	public void onEntered(String identityId) {

		enteredNum.addAndGet(1);

		synchronized (enteredUids) {
			if (!enteredUids.contains(identityId))
				enteredUids.add(identityId);
		}
	}

	

}
