package newbee.morningGlory.mmorpg.player.offLineAI.ref;

import sophia.game.ref.AbstractGameRefObjectBase;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class HandUpSkillRef extends AbstractGameRefObjectBase {

	private static final long serialVersionUID = -2142815706248206903L;
	
	
	private String skillRefId;

	public void setSkillRefId(String skillRefId) {
		this.skillRefId = skillRefId;
	}
	
	/** 技能 */
	public String getSkillRefId(){
		return this.skillRefId;
	}
	/** 职业 */
	public byte getProfessionId(){
		return MGPropertyAccesser.getProfessionId(this.getProperty());
	}
	/** 目标个数 */
	public int getTargetNum(){
		return MGPropertyAccesser.getTargetNum(this.getProperty());
	}
	
	
	public int getPriority(){
		return MGPropertyAccesser.getPriority(this.getProperty());
	}
	public String getBuffRefId(){
		return MGPropertyAccesser.getBuffRefId(this.getProperty());
	}
}
