package sophia.mmorpg.player.mount.gameEvent;

public class MGMountLevelUp_GE {
	private String mountRefId;

	public MGMountLevelUp_GE(String mountRefId) {
		super();
		this.mountRefId = mountRefId;
	}

	public String getMountRefId() {
		return mountRefId;
	}

	public void setMountRefId(String mountRefId) {
		this.mountRefId = mountRefId;
	}

}
