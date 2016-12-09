package newbee.morningGlory.mmorpg.player.achievement.medal;

import sophia.foundation.property.PropertyDictionary;
import sophia.game.ref.AbstractGameRefObjectBase;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class MGMedalDataRef extends AbstractGameRefObjectBase {
	private static final long serialVersionUID = 5222736712088674022L;

	public MGMedalDataRef() {

	}
	
	/**
	 * 兑换勋章需要的成就点数
	 */
	public int getNeedAchievement(){
		PropertyDictionary property = getProperty();
		int achievePoint = MGPropertyAccesser.getNeedAchieve(property);
		return achievePoint;
	}
	
	/**
	 * 下个级别勋章的refId
	 */
	public String getNextMedalRefId(){
		return MGPropertyAccesser.getNextMedal(getProperty());
	}
}
