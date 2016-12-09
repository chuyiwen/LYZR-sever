package sophia.mmorpg.gameInstance;

public class ComeFromScene {

	private String comeFromSceneRefId;

	private int x;

	private int y;

	public ComeFromScene(String comeFromSceneRefId, int x, int y) {
		super();
		this.comeFromSceneRefId = comeFromSceneRefId;
		this.x = x;
		this.y = y;
	}

	public String getComeFromSceneRefId() {
		return comeFromSceneRefId;
	}

	public void setComeFromSceneRefId(String comeFromSceneRefId) {
		this.comeFromSceneRefId = comeFromSceneRefId;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

}
