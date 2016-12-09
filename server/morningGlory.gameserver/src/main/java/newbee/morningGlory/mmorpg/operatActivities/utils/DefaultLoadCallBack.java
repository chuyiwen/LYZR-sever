package newbee.morningGlory.mmorpg.operatActivities.utils;

import newbee.morningGlory.mmorpg.operatActivities.utils.HttpConnection.CallbackListener;

import org.apache.log4j.Logger;

public class DefaultLoadCallBack implements CallbackListener {
	private static Logger logger = Logger.getLogger(DefaultLoadCallBack.class);
	private boolean success = false;
	private boolean callback = false;
	private Throwable throwable;

	@Override
	public void callBack(int responseCode, String result) {
		if (org.apache.http.HttpStatus.SC_OK == responseCode) {
			try {
				if (result != null && result.length() > 0) {
					if (logger.isDebugEnabled()) {
						logger.debug(result);
					}
					success = true;
				}
			} catch (Throwable e) {
				logger.error("http request failed!!!defaultLoadCallBack", e);
				throwable = e;
			}
		}
		callback = true;
	}

	public boolean isSuccess() {
		return success;
	}

	public boolean isCallback() {
		return callback;
	}

	public Throwable getThrowable() {
		return throwable;
	}

	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}

}
