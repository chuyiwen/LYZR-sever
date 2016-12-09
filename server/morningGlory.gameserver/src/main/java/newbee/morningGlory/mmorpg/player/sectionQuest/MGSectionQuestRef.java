package newbee.morningGlory.mmorpg.player.sectionQuest;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import sophia.game.ref.AbstractGameRefObjectBase;
import sophia.mmorpg.player.itemBag.ItemPair;

public class MGSectionQuestRef extends AbstractGameRefObjectBase {

	private static final long serialVersionUID = 1507484832235873076L;

	private Map<String, Integer> acceptCondition = new LinkedHashMap<>();
	
	private Map<String, Integer> orderCondition = new LinkedHashMap<>();
	
	private List<ItemPair> rewardList = new ArrayList<>();
	
	public MGSectionQuestRef(){
	}

	public Map<String, Integer> getAcceptCondition() {
		return acceptCondition;
	}

	public void setAcceptCondition(Map<String, Integer> acceptCondition) {
		this.acceptCondition = acceptCondition;
	}

	public Map<String, Integer> getOrderCondition() {
		return orderCondition;
	}

	public void setOrderCondition(Map<String, Integer> orderCondition) {
		this.orderCondition = orderCondition;
	}

	public List<ItemPair> getRewardList() {
		return rewardList;
	}

	public void addRewardList(ItemPair rewardList) {
		this.rewardList.add(rewardList);
	}
	
	

}
