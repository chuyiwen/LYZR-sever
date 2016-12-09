package newbee.morningGlory.mmorpg.operatActivities.utils;

import java.util.Map;

import net.minidev.json.JSONValue;
import newbee.morningGlory.mmorpg.operatActivities.OperatActivityMgr;
import newbee.morningGlory.mmorpg.operatActivities.OperatActivityRef;
import newbee.morningGlory.mmorpg.operatActivities.utils.HttpConnection.CallbackListener;

import org.apache.log4j.Logger;

public class LoadCallBack implements CallbackListener {
	private static Logger logger = Logger.getLogger(LoadCallBack.class);
	private boolean success = false;
	private boolean callback = false;
	private OperatActivityRef ref = null;
	private Throwable throwable;

	@Override
	public void callBack(int responseCode, String result) {
		if (org.apache.http.HttpStatus.SC_OK == responseCode) {
			try {
				if (result != null && result.length() > 0) {
					if (logger.isDebugEnabled()) {
						logger.debug(result);
					}
					Map<?, ?> jsondata = (Map<?, ?>) JSONValue.parse(result);
					ref = new OperatActivityRef();
					ref.getData().getData().putAll(jsondata);
					ref.sourceData = result;
					success = OperatActivityMgr.getInstance().onRefInit(ref);
				}
			} catch (Throwable e) {
				logger.error("拉取活动数据失败！请检查活动数据！", e);
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

	public OperatActivityRef getRef() {
		return ref;
	}

	public Throwable getThrowable() {
		return throwable;
	}

	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}

}
