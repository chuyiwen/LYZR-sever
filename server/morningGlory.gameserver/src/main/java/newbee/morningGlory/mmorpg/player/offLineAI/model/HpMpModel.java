package newbee.morningGlory.mmorpg.player.offLineAI.model;

/**
 *	用来自动回血回蓝时的 model 
 */
public class HpMpModel {

	private int hp;
	private int mp;
	
	private HpMpModel(){}
	
	public static HpMpModel createHpMpModel(int hp, int mp) {
		HpMpModel model = new HpMpModel();
		model.hp = hp;
		model.mp = mp;
		return model;
	}
	public int getHp() {
		return hp;
	}
	public int getMp() {
		return mp;
	}
	
}
