package sophia.mmorpg.npc.ref;

import java.util.ArrayList;
import java.util.List;

import sophia.foundation.communication.core.ActionEventBase;

public class NpcShop implements NpcJob {

	private List<String> shopList = new ArrayList<>();

	@Override
	public int compareTo(NpcJob o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Short getJobType() {
		return JobType.Job_Type_Store;
	}

	@Override
	public int priority() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void doJob(ActionEventBase actionEvent) {
		// TODO Auto-generated method stub

	}

	public List<String> getShopList() {
		return shopList;
	}

	public void setShopList(List<String> shopList) {
		this.shopList = shopList;
	}

}
