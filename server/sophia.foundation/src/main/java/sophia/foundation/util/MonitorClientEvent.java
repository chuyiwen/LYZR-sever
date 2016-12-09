/**
 * 
 */
package sophia.foundation.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import sophia.foundation.authentication.Identity;
import sophia.foundation.communication.core.ActionEventBase;

public class MonitorClientEvent {

	public static final String Enable_Property = "sophia.foundation.util.MonitorClientEvent.enable";
	public static final String Packet_Property = "sophia.foundation.util.MonitorClientEvent.message.length";

	private boolean enabled = false;
	private int messageLengthLimit;

	private Map<Identity, List<byte[]>> eventMap = new ConcurrentHashMap<Identity, List<byte[]>>();
	private long lastTime;
	private static final MonitorClientEvent instance = new MonitorClientEvent();

	private MonitorClientEvent() {
	}

	public static MonitorClientEvent getInstance() {
		return instance;
	}

	public Map<Identity, List<byte[]>> getEventMap() {
		return eventMap;
	}

	public void addEvent(ActionEventBase message, byte[] data) {
		Identity identity = message.getIdentity();

		if (identity == null) {
			return;
		}

		List<byte[]> eventList = eventMap.get(identity);
		if (eventList == null) {
			eventList = new ArrayList<byte[]>();
			eventMap.put(identity, eventList);
		}
		eventList.add(data);
	}

	public List<byte[]> getEventList(Identity identity) {

		List<byte[]> eventList = eventMap.get(identity);

		return eventList;
	}

	public long getLastTime() {
		return lastTime;
	}

	public void setLastTime(long lastTime) {
		this.lastTime = lastTime;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getMessageLengthLimit() {
		return messageLengthLimit;
	}

	public void setMessageLengthLimit(int messageLengthLimit) {
		this.messageLengthLimit = messageLengthLimit;
	}

}
