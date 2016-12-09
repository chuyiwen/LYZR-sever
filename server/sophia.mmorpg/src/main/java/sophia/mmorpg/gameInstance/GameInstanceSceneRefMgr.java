package sophia.mmorpg.gameInstance;

import java.util.List;

/**
 * 副本Ref-副本层管理
 */
public class GameInstanceSceneRefMgr {

	List<GameInstanceSceneRef> instanceSceneList;
	
	public GameInstanceSceneRefMgr() {

	}

	public List<GameInstanceSceneRef> getInstanceSceneList() {
		return instanceSceneList;
	}

	public void setInstanceSceneList(List<GameInstanceSceneRef> instanceSceneList) {
		this.instanceSceneList = instanceSceneList;
	}

}
