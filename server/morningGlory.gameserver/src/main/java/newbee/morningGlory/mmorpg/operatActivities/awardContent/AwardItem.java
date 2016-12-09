package newbee.morningGlory.mmorpg.operatActivities.awardContent;

import java.util.ArrayList;
import java.util.List;

import newbee.morningGlory.mmorpg.operatActivities.awardCond.CondType;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

/**
 * 奖励条目<br>
 */
public class AwardItem {
	private String id;
	private String itemDesc;
	private List<ItemPair> items;
	private String itemOtherData;

	private CondType condType;
	private String condValue;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public CondType getCondType() {
		return condType;
	}

	public void setCondType(CondType condType) {
		this.condType = condType;
	}

	public String getCondValue() {
		return condValue;
	}

	public void setCondValue(String condValue) {
		this.condValue = condValue;
	}

	public String getItemDesc() {
		return itemDesc;
	}

	public void setItemDesc(String itemDesc) {
		this.itemDesc = itemDesc;
	}

	public List<ItemPair> getItems() {
		return items;
	}

	public List<ItemPair> getItems(Player player) {
		List<ItemPair> list = new ArrayList<ItemPair>();
		byte gender = MGPropertyAccesser.getGender(player.getProperty());
		byte professionId = MGPropertyAccesser.getProfessionId(player.getProperty());
		byte professionAndGender = ItemPair.getProfessionId(gender, professionId);
		for(ItemPair itemPair : items){
			if(itemPair.getProfesssionAndGender() == ItemPair.DEFAULT_PROFESSION || itemPair.getProfesssionAndGender() == professionAndGender){
				list.add(itemPair);
			}
		}
		return list;
	}
	
	public void setItems(List<ItemPair> items) {
		this.items = items;
	}

	public String getItemOtherData() {
		return itemOtherData;
	}

	public void setItemOtherData(String itemOtherData) {
		this.itemOtherData = itemOtherData;
	}

}
