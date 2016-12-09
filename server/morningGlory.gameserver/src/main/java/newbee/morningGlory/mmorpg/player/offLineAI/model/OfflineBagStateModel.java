package newbee.morningGlory.mmorpg.player.offLineAI.model;

/**
 * 离线背包状态model
 */
public class OfflineBagStateModel {

	private int minNum;
	private int maxNum;
	private int state;
	
	private OfflineBagStateModel(){}
	
	public static OfflineBagStateModel createOfflineBagStateModel(int minNum, int maxNum, int state) {
		OfflineBagStateModel model = new OfflineBagStateModel();
		model.minNum = minNum;
		model.maxNum = maxNum;
		model.state = state;
		return model;
	}
	public boolean isOK(int num){
		return num >= this.minNum && num <= this.maxNum;
	}
	
	public int getState() {
		return state;
	}
	
	
}
