package newbee.morningGlory.mmorpg.sceneActivities.monsterIntrusion.ref;

import java.util.ArrayList;
import java.util.List;

import sophia.game.ref.AbstractGameRefObjectBase;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class MonsterInvasionRef extends AbstractGameRefObjectBase {

	private static final long serialVersionUID = -7444829658300534137L;
	
	private List<String> itemRefIdList = new ArrayList<>();
	
	private List<String> sceneRefIdList = new ArrayList<>();
	
	private String time;
	
	public MonsterInvasionRef() {
	}

	public int getLevel() {
		return MGPropertyAccesser.getLevel(getProperty());
	}

	public double getExpMultiple() {
		return MGPropertyAccesser.getExpMultiple(getProperty());
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public List<String> getItemRefIdList() {
		return itemRefIdList;
	}

	public void addItemRefIdList(String itemRefId) {
		this.itemRefIdList.add(itemRefId);
	}

	public List<String> getSceneRefIdList() {
		return sceneRefIdList;
	}

	public void addSceneRefIdList(String sceneRefId) {
		this.sceneRefIdList.add(sceneRefId);
	}
}
