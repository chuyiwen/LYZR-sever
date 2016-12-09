package newbee.morningGlory.checker.refObjectChecker.npc;

import java.util.List;

import newbee.morningGlory.checker.BaseRefChecker;

import org.apache.commons.lang3.StringUtils;

import sophia.foundation.property.PropertyDictionary;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.npc.ref.JobType;
import sophia.mmorpg.npc.ref.NpcJob;
import sophia.mmorpg.npc.ref.NpcJobManager;
import sophia.mmorpg.npc.ref.NpcRef;
import sophia.mmorpg.npc.ref.NpcShop;
import sophia.mmorpg.npc.ref.NpcTransfers;
import sophia.mmorpg.npc.ref.SingleTransfer;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class NpcRefChecker extends BaseRefChecker<NpcRef> {

	@Override
	public String getDescription() {
		return "NPC";
	}

	@Override
	public void check(GameRefObject gameRefObject) {
		NpcRef info = (NpcRef) gameRefObject;
		if (!info.getId().startsWith("npc_")) {
			error(gameRefObject, "NPC<refId>错误 , 请以npc_开头!!! 错误的refId为: " + info.getId());
		}

		PropertyDictionary pd = info.getProperty();
		String name = MGPropertyAccesser.getName(pd);
		if (StringUtils.isEmpty(name)) {
			error(gameRefObject, "NPC<name>错误 , name不能为空!!!");
		}

		checkJobManager(gameRefObject);
	}

	public void checkJobManager(GameRefObject gameRefObject) {
		NpcRef ref = (NpcRef) gameRefObject;
		NpcJobManager npcJobManager = ref.getNpcJobManager();
		List<NpcJob> jobList = npcJobManager.getNpcJobList();
		for (NpcJob job : jobList) {
			short type = job.getJobType();
			if (type == JobType.Job_Type_Transfer) {
				checkTransferNpc(gameRefObject, job);
			} else if (type == JobType.Job_Type_Store) {
				checkStoreNpc(gameRefObject, job);
			}
		}
	}

	public void checkTransferNpc(GameRefObject gameRefObject, NpcJob job) {
		NpcTransfers npcTransfer = (NpcTransfers) job;
		List<SingleTransfer> transferList = npcTransfer.getTransferData();
		for (SingleTransfer single : transferList) {
			String name = single.getName();
			if (StringUtils.isEmpty(name)) {
				error(gameRefObject, "NPC<job>错误 , tranfer name 不能为空 !!!");
			}

			int transferIn = single.getTargetTransIn();
			if (transferIn < 0) {
				error(gameRefObject, "NPC<job>错误 , transferIn 不能小于0 !!!");
			}

			String targerScene = single.getTargetScene();
			if (!targerScene.startsWith("S")) {
				error(gameRefObject, "NPC<job>错误 , targetScene 请以S 开头 !!!  错误的targetScene为: " + targerScene);
			}
		}
	}

	public void checkStoreNpc(GameRefObject gameRefObject, NpcJob job) {
		NpcShop npcShop = (NpcShop) job;
		List<String> shopList = npcShop.getShopList();
		for (String shopId : shopList) {
			if (!shopId.startsWith("shop_")) {
				error(gameRefObject, "NPC<job>错误 , shopId 请以shopId开头 !!!  错误的shopId为: " + shopId);
			}
		}
	}
}
