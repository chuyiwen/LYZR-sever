package sophia.mmorpg.npc.ref;

public final class SingleTransfer{
	private int id;// 传送功能编号
	private String name;// 用于显示的名称
	private String targetScene;// 目标场景
	private int targetTransIn;// 目标场景传入点编号
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTargetScene() {
		return targetScene;
	}

	public void setTargetScene(String targetScene) {
		this.targetScene = targetScene;
	}

	public int getTargetTransIn() {
		return targetTransIn;
	}

	public void setTargetTransIn(int targetTransIn) {
		this.targetTransIn = targetTransIn;
	}

}
