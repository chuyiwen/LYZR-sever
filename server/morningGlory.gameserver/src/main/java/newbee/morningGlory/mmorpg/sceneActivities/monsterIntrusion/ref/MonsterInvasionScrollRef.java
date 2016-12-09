package newbee.morningGlory.mmorpg.sceneActivities.monsterIntrusion.ref;

import sophia.game.ref.AbstractGameRefObjectBase;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class MonsterInvasionScrollRef extends AbstractGameRefObjectBase{


	private static final long serialVersionUID = 4314276675804048629L;

	private int stage;
	private String range;
	
	
	public MonsterInvasionScrollRef() {
		
	}

	public String getTips(){
		return MGPropertyAccesser.getTips(getProperty());
	}
	

	public int getStage() {
		return stage;
	}


	public void setStage(int stage) {
		this.stage = stage;
	}


	public String getRange() {
		return range;
	}


	public void setRange(String range) {
		this.range = range;
	}

	
	
}
