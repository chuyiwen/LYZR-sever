package sophia.mmorpg.communication;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

/**
 * This class uses heart beat packets to determine the packet receiving rate of
 * a client. So it is still vulnerable to the condition that a malicious client
 * sending too fast packets without heart beat.
 * 
 * @author Xiao Hua
 * 
 */
public class PacketThrottler {
	private static final Logger logger = Logger.getLogger(PacketThrottler.class);

	// These variables are to be configured
	private int maxTolerantPacketLimit;
	private int twoHeartbeatInterval;
	private double defaultTolerantRate;

	// playerId -> lastHeartbeatTime(in seconds)
	private Map<String, Long> lastHeartbeatTimes = new ConcurrentHashMap<>();
	// playerId -> current tolerant times
	private Map<String, Integer> currentTolerantCount = new ConcurrentHashMap<>();

	public PacketThrottler() {
		super();
		// set default values of configured variables
		this.maxTolerantPacketLimit = 10;
		this.twoHeartbeatInterval = 5;
		this.defaultTolerantRate = 1;
	}

	public PacketThrottler(int maxTolerantPacketLimit, int twoHeartbeatInterval, double defaultTolerantRate) {
		super();
		this.maxTolerantPacketLimit = maxTolerantPacketLimit;
		this.twoHeartbeatInterval = twoHeartbeatInterval;
		this.defaultTolerantRate = defaultTolerantRate;
	}

	public void addTolerantCount(String playerId) {
		if (currentTolerantCount.containsKey(playerId)) {
			Integer curTimes = currentTolerantCount.get(playerId);
			currentTolerantCount.put(playerId, curTimes + 1);
		} else {
			currentTolerantCount.put(playerId, 1);
		}
	}
	
	public void removeTolerantCount(String playerId) {
		currentTolerantCount.remove(playerId);
	}

	private boolean isOverrateHeartbeatInterval(Long last, Long now) {
		if (last == null || now == null) {
			return false;
		}
		long diff = now - last;
		boolean isOverrate = diff < getTwoHeartbeatInterval() * getDefaultTolerantRate();
		if (logger.isDebugEnabled()) {
			logger.debug("isOverrateHeartbeatInterval last " + last + " now " + now + " diff " + diff + " twoHeartbeatInterval " + getTwoHeartbeatInterval()
					+ " defaultTolerantRate " + getDefaultTolerantRate());
		}
		return isOverrate;
	}

	public void updateLastHeartbeatTime(String playerId, long now) {
		Long lastHeartbeatTime = lastHeartbeatTimes.get(playerId);
		boolean isRegular = !isOverrateHeartbeatInterval(lastHeartbeatTime, now);
		if (isRegular) {
			currentTolerantCount.remove(playerId);
		}
		lastHeartbeatTimes.put(playerId, now);
	}

	public boolean isOverrate(String playerId, long currentHeartbeatTime) {
		if (!lastHeartbeatTimes.containsKey(playerId)) {
			return false;
		}
		Long lastHeartbeatTime = lastHeartbeatTimes.get(playerId);
		boolean isOverrate = isOverrateHeartbeatInterval(lastHeartbeatTime, currentHeartbeatTime);
		if (logger.isDebugEnabled()) {
			logger.debug("isOverrate playerId " + playerId + " isOverrate " + isOverrate);
		}
		return isOverrate;
	}

	public boolean isTolerable(String playerId) {
		Integer curTolerantCount = currentTolerantCount.get(playerId);
		if (logger.isDebugEnabled()) {
			logger.debug("isTolerable curTolerantCount " + curTolerantCount);
		}
		if (curTolerantCount == null) {
			return true;
		}
		boolean tolerable = curTolerantCount <= getMaxTolerantPacketLimit();
		if (!tolerable) {
			currentTolerantCount.remove(playerId);
		}
		return tolerable;
	}

	public int getMaxTolerantPacketLimit() {
		return maxTolerantPacketLimit;
	}

	public void setMaxTolerantPacketLimit(int maxTolerantPacketLimit) {
		this.maxTolerantPacketLimit = maxTolerantPacketLimit;
	}

	public int getTwoHeartbeatInterval() {
		return twoHeartbeatInterval;
	}

	public void setTwoHeartbeatInterval(int twoHeartbeatInterval) {
		this.twoHeartbeatInterval = twoHeartbeatInterval;
	}

	public double getDefaultTolerantRate() {
		return defaultTolerantRate;
	}

	public void setDefaultTolerantRate(double defaultTolerantRate) {
		this.defaultTolerantRate = defaultTolerantRate;
	}

	@Override
	public String toString() {
		return "PacketThrottler [lastHeartbeatTimes=" + lastHeartbeatTimes + ", currentTolerantCount=" + currentTolerantCount + "]";
	}

}
