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
package newbee.morningGlory.mmorpg.operatActivities.utils;

import java.util.Map;

import net.minidev.json.JSONValue;
import newbee.morningGlory.mmorpg.operatActivities.utils.HttpConnection.CallbackListener;

import org.apache.log4j.Logger;

import sophia.mmorpg.utils.Type;

public class LoadOpenTimeCallBack implements CallbackListener{
	
	private static Logger logger = Logger.getLogger(LoadCallBack.class);
	private boolean callback = false;
	private Throwable throwable;
	private long serverOpenTime;
	@Override
	public void callBack(int responseCode, String result) {
		if (org.apache.http.HttpStatus.SC_OK == responseCode) {
			try {
				if (result != null && result.length() > 0) {
					if (logger.isDebugEnabled()) {
						logger.debug(result);
					}
					Map<?, ?> jsondata = (Map<?, ?>) JSONValue.parse(result);
					serverOpenTime = Type.getLong(jsondata.get("serverOpenTime"),0);
				}
			} catch (Throwable e) {
				logger.error("请求开服时间失败，请检查GM后台", e);
				setThrowable(e);
			}
		}else{
			logger.error("请求开服时间失败，请检查GM后台");
//			throw new RuntimeException("请求开服时间失败，请检查GM后台");
		}
		setCallback(true);
	}
	public Throwable getThrowable() {
		return throwable;
	}
	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}
	public boolean isCallback() {
		return callback;
	}
	public void setCallback(boolean callback) {
		this.callback = callback;
	}
	public long getServerOpenTime() {
		return serverOpenTime;
	}
	public void setServerOpenTime(long serverOpenTime) {
		this.serverOpenTime = serverOpenTime;
	}
}
