package sophia.mmorpg.core;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * CDMgr manages a group of objects that differentiates itself by id. CDMgr
 * helps with public CD time between these objects.
 */
public class CDMgr {
	// id -> CDTimeManager
	private Map<String, CDTimeManager> cds = new HashMap<>();
	private long publicFightCDMillis;
	private long lastPublicFightTimeMillis;

	public CDMgr(long publicFightCDMillis) {
		super();
		this.publicFightCDMillis = publicFightCDMillis;
		this.lastPublicFightTimeMillis = 0;
	}

	public CDMgr(long publicFightCDMillis, long lastPublicFightTimeMillis) {
		super();
		this.publicFightCDMillis = publicFightCDMillis;
		this.lastPublicFightTimeMillis = lastPublicFightTimeMillis;
	}

	// You won't need to get accessed to this inner class. The public methods
	// of CDMgr will basically fulfill all your needs.
	private final class CDTimeManager {
		private String id;
		private long fightCDMillis;

		private long lastFightTimeMillis;

		public CDTimeManager(String id, long fightCDMillis) {
			super();
			this.setId(id);
			this.fightCDMillis = fightCDMillis;
			this.lastFightTimeMillis = 0;
		}

		public CDTimeManager(String skillRefId, long fightCDMillis, long lastFightTimeMillis) {
			super();
			this.setId(skillRefId);
			this.fightCDMillis = fightCDMillis;
			this.lastFightTimeMillis = lastFightTimeMillis;
		}

		public boolean isCDStarted() {
			return this.lastFightTimeMillis != 0;
		}

		public void update() {
			this.lastFightTimeMillis = System.currentTimeMillis();
			lastPublicFightTimeMillis = System.currentTimeMillis();
		}

		public void update(long now) {
			this.lastFightTimeMillis = now;
			lastPublicFightTimeMillis = now;
		}

		public boolean isOutOfCD() {
			long now = System.currentTimeMillis();
			boolean isOutOfThisCD = now - this.lastFightTimeMillis > this.fightCDMillis;
			boolean isOutOfPublicCD = now - lastPublicFightTimeMillis > publicFightCDMillis;
			return isOutOfThisCD && isOutOfPublicCD;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public long getFightCDMillis() {
			return fightCDMillis;
		}

		public void setFightCDMillis(long time) {
			this.fightCDMillis = time;
		}

		public long getLastFightTimeMillis() {
			return lastFightTimeMillis;
		}

		public void setLastFightTimeMillis(long time) {
			this.lastFightTimeMillis = time;
		}

		@Override
		public String toString() {
			return "CDTimeManager [id=" + id + ", fightCDMillis=" + fightCDMillis + ", lastFightTimeMillis=" + lastFightTimeMillis + "]";
		}
	}

	private void addCDManager(CDTimeManager cdManager) {
		this.cds.put(cdManager.getId(), cdManager);
	}

	private CDTimeManager getCDManager(String id) {
		return this.cds.get(id);
	}

	public void startCD(String id, long fightCDMillis) {
		CDTimeManager cdManager = getCDManager(id);
		if (cdManager == null) {
			addCDManager(this.new CDTimeManager(id, fightCDMillis));
		}
		checkNotNull(getCDManager(id));
	}

	public void startCD(String id, long fightCDMillis, long startFromWhen) {
		CDTimeManager cdManager = getCDManager(id);
		if (cdManager == null) {
			addCDManager(this.new CDTimeManager(id, fightCDMillis, startFromWhen));
		}
		checkNotNull(getCDManager(id));
	}

	public boolean isOutOfCD(String id) {
		CDTimeManager cdManager = getCDManager(id);
		checkArgument(cdManager != null, "CD of id " + id + " is not started.");
		return cdManager.isOutOfCD();
	}

	public void update(String id) {
		CDTimeManager cdManager = getCDManager(id);
		checkArgument(cdManager != null, "CD of id " + id + " is not started.");
		cdManager.update();
	}

	public void update(String id, long now) {
		CDTimeManager cdManager = getCDManager(id);
		checkArgument(cdManager != null, "CD of id " + id + " is not started.");
		cdManager.update(now);
	}

	public boolean isCDStarted(String id) {
		CDTimeManager cdManager = this.cds.get(id);
		return cdManager != null && cdManager.isCDStarted();
	}

	public long getPublicFightCDMillis() {
		return publicFightCDMillis;
	}

	public long getLastPublicFightTimeMillis() {
		return lastPublicFightTimeMillis;
	}

	public long getFightCDMillis(String id) {
		CDTimeManager cdManager = getCDManager(id);
		checkArgument(cdManager != null, "CD of id " + id + " is not started.");
		return cdManager.getFightCDMillis();
	}

	public void setFightCDMillis(String id, long time) {
		CDTimeManager cdManager = getCDManager(id);
		checkArgument(cdManager != null, "CD of id " + id + " is not started.");
		cdManager.setFightCDMillis(time);
	}

	public long getLastFightTimeMillis(String id) {
		CDTimeManager cdManager = getCDManager(id);
		checkArgument(cdManager != null, "CD of id " + id + " is not started.");
		return cdManager.getLastFightTimeMillis();
	}

	public void setLastFightTimeMillis(String id, long time) {
		CDTimeManager cdManager = getCDManager(id);
		checkArgument(cdManager != null, "CD of id " + id + " is not started.");
		cdManager.setLastFightTimeMillis(time);
	}

	@Override
	public String toString() {
		return "CDMgr [cds=" + cds + ", publicFightCDMillis=" + publicFightCDMillis + ", lastPublicFightTimeMillis=" + lastPublicFightTimeMillis + "]";
	}

}