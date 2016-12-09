package sophia.mmorpg.base.scene.mgr;

import java.util.HashMap;
import java.util.Map;

import sophia.mmorpg.GameObjectFactory;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.npc.Npc;
import sophia.mmorpg.npc.ref.JobType;
import sophia.mmorpg.npc.ref.NpcRef;
import sophia.mmorpg.npc.ref.NpcShop;

public final class NpcMgrComponent extends ConcreteComponent<GameScene> {
	private Map<String, Npc> npcs = new HashMap<>();

	public Npc getNpc(String npcRefId) {
		return npcs.get(npcRefId);
	}

	public Npc removeNpc(String npcRefId) {
		return npcs.remove(npcRefId);
	}

	public Npc createNpc(String npcRefId) {
		return GameObjectFactory.getNpc(npcRefId);
	}

	public void enterWorld(Npc npc, GameScene gameScene, int x, int y) {
		addNpc(npc);
		npc.setCrtScene(gameScene);
		npc.getCrtPosition().setX(x);
		npc.getCrtPosition().setY(y);
	}

	public void addNpc(Npc npc) {
		npcs.put(npc.getNpcRef().getId(), npc);
	}

	public boolean isNpcHasShop(String refId, String shopId) {
		boolean temp = false;
		Npc npc = npcs.get(refId);
		NpcRef npcRef = npc.getNpcRef();
		NpcShop npcShop = (NpcShop) npcRef.getNpcJobManager().getNpcJob(JobType.Job_Type_Store);
		if (npcShop != null) {
			temp = npcShop.getShopList().contains(shopId);
		}
		return temp;
	}
}
