/**
 * 
 */
package sophia.mmorpg.equipmentSmith.smith.highestEquipment;

import sophia.game.ref.AbstractGameRefObjectBase;

/**
 * @author yinxinglin
 * 
 */
public class HighestEquipmentRef extends AbstractGameRefObjectBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1076581995739134176L;
	private int[] probability;
	private int[] randomCount;
	private int minRate;
	private int maxRate;
	
	public int[] getProbability() {
		return probability;
	}
	public void setProbability(int[] probability) {
		this.probability = probability;
	}
	public int[] getRandomCount() {
		return randomCount;
	}
	public void setRandomCount(int[] randomCount) {
		this.randomCount = randomCount;
	}
	public int getMinRate() {
		return minRate;
	}
	public void setMinRate(int minRate) {
		this.minRate = minRate;
	}
	public int getMaxRate() {
		return maxRate;
	}
	public void setMaxRate(int maxRate) {
		this.maxRate = maxRate;
	}

}
