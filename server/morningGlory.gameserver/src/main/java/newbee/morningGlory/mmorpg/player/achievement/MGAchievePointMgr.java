package newbee.morningGlory.mmorpg.player.achievement;

import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;

import sophia.foundation.property.PropertyDictionary;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class MGAchievePointMgr {
	private static Logger logger = Logger.getLogger(MGAchievePointMgr.class);

	private int achievePoint;
	private Player player;

	public MGAchievePointMgr() {

	}

	public void notifyProperty() {
		if(logger.isDebugEnabled()){
			logger.debug("玩家当前成就点数:"+getAchievePoint());
		}
		PropertyDictionary property = new PropertyDictionary();
		MGPropertyAccesser.setOrPutAchievement(property, getAchievePoint());
		MGPropertyAccesser.setOrPutAchievement(player.getProperty(), getAchievePoint());
		player.notifyPorperty(property);
	}

	public int getAchievePoint() {
		return achievePoint;
	}

	public void setAchievePoint(int achievePoint) {
		if (achievePoint < 0)
			achievePoint = 0;
		this.achievePoint = achievePoint;
	}

	private void setAchievePointImpl(int achievePoint) {
		Preconditions.checkArgument(achievePoint>=0);
		
		this.achievePoint = achievePoint;
		
		notifyProperty();
	}

	public void addAchievePoint(int value) {
		if (value < 0)
			return;
		setAchievePointImpl(this.achievePoint + value);
	}

	public boolean subAchievePoint(int value) {
		Preconditions.checkArgument(value>=0);

		if (value > getAchievePoint()) {
			logger.error("value = " + value + ", has achievePoint " + achievePoint);
			return false;
		}
		
		setAchievePointImpl(this.achievePoint - value);
		return true;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

}
