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

import sophia.stat.StatLogService;
import sophia.stat.StatOnlineTicker;

public class StatService extends StatLogService {
	private static StatService statService = new StatService();
	private StatOnlineTickerImpl statOnlineTickerImpl = new StatOnlineTickerImpl();

	private StatService() {
	}

	public static StatService getInstance() {
		return statService;
	}
	
	public StatOnlineTicker getStatOnlineTicker(){
		return statOnlineTickerImpl;
	}
	
	@Override
	public void onStartup() {

		statOnlineTickerImpl.startup();
	}

	@Override
	public void onShutdown() {

		statOnlineTickerImpl.shutdown();
	}

}
