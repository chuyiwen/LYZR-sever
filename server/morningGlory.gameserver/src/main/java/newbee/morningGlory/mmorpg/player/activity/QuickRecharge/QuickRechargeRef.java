/**
 * 
 */
package newbee.morningGlory.mmorpg.player.activity.QuickRecharge;

import sophia.game.ref.AbstractGameRefObjectBase;

/**
 * @author yinxinglin
 * 
 */
public class QuickRechargeRef extends AbstractGameRefObjectBase {

	private static final long serialVersionUID = -3684918286108468459L;
	private int level;
	private int money;
	private int unbindedGold;
	private int reward;
	private int firstReward;
	private int firstRewardBound;
	private int rewardBound;
	
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public int getUnbindedGold() {
		return unbindedGold;
	}

	public void setUnbindedGold(int unbindedGold) {
		this.unbindedGold = unbindedGold;
	}

	public int getReward() {
		return reward;
	}

	public void setReward(int reward) {
		this.reward = reward;
	}

	public int getFirstReward() {
		return firstReward;
	}

	public void setFirstReward(int firstReward) {
		this.firstReward = firstReward;
	}

	public int getFirstRewardBound() {
		return firstRewardBound;
	}

	public void setFirstRewardBound(int firstRewardBound) {
		this.firstRewardBound = firstRewardBound;
	}

	public int getRewardBound() {
		return rewardBound;
	}

	public void setRewardBound(int rewardBound) {
		this.rewardBound = rewardBound;
	}

	
}
