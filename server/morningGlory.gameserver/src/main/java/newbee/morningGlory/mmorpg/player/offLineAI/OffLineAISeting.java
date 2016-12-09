package newbee.morningGlory.mmorpg.player.offLineAI;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sophia.foundation.property.PropertyDictionary;
import sophia.game.GameRoot;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.item.ref.ItemRef;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.player.itemBag.ItemType;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class OffLineAISeting {
	
	private int hp;//自动吃药的hp下限设置
	private int mp;//自动吃药的mp下限设置
	private int equipLv = -1;//拾取 指定等级以上的装备
	private Set<Byte> qualitySet = new HashSet<Byte>();//所有可以拾取的 装备品质
	private Set<Byte> professionIdSet = new HashSet<Byte>();//所有可以拾取的 装备职业
	
	OffLineAISeting(){}
	void clientOffLineAISeting(int hp, int mp, int equipLv,List<Byte> qualityList,List<Byte> professionIdList) {
		this.qualitySet.clear();
		this.professionIdSet.clear();
		this.hp = hp;
		this.mp = mp;
		this.equipLv = equipLv;
		int qualityListSize = qualityList == null ? 0 : qualityList.size();
		for (int i = 0; i < qualityListSize; i++) {
			this.qualitySet.add(qualityList.get(i));
		}
		int professionIdListSize = professionIdList == null ? 0 : professionIdList.size();
		for (int i = 0; i < professionIdListSize; i++) {
			this.professionIdSet.add(professionIdList.get(i));
		}
	}
	
	public int getHp() {
		return this.hp;
	}
	public int getMp() {
		return this.mp;
	}
	boolean isCanPick(Item item){
		return isCanPick(item.getItemRef().getProperty());
	}
	boolean isCanPick(ItemPair itemPair){
		ItemRef itemRef = (ItemRef)GameRoot.getGameRefObjectManager().getManagedObject(itemPair.getItemRefId());
		return isCanPick(itemRef.getProperty());
	}
	
	
	private boolean isCanPick(PropertyDictionary itemRefPd){
		if(MGPropertyAccesser.getItemType(itemRefPd) != ItemType.Equip){
			return true;
		}
		int _equipLv = MGPropertyAccesser.getEquipLevel(itemRefPd);
		byte _quality = MGPropertyAccesser.getQuality(itemRefPd);
		byte _professionId = MGPropertyAccesser.getProfessionId(itemRefPd);
		
		if(this.equipLv >= 0 && _equipLv < this.equipLv){
			return false;
		}
		if(!this.qualitySet.contains(_quality)){
			return false;
		}
		if(!this.professionIdSet.contains(_professionId)){
			return false;
		}
		return true;
	}
	
	
}
