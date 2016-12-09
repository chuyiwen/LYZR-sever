package newbee.morningGlory.mmorpg.player.offLineAI.model;

import java.util.List;

import sophia.mmorpg.player.itemBag.ItemPair;

/**
 *	用于记录玩家离线AI日志的model 
 */
public class AILogModel {

	public static final byte TYPE_1 = 1;//玩家进入场景
	public static final byte TYPE_2 = 2;//玩家死亡
	
	private byte type = 0;//日志类型
	
	
	private String aiGameSceneRefId;//AI场景RefId
	
	
	private String playerId;//杀死该替身的玩家ID
	private String playerName;//杀死该替身的玩家名字
	private List<ItemPair> dorpItems;//死亡掉落的道具
	
	private AILogModel(){}
	
	public static AILogModel createInToGameSceneLogModel(String aiGameSceneRefId) {
		AILogModel model = new AILogModel();
		model.type = TYPE_1;
		model.aiGameSceneRefId = aiGameSceneRefId;
		return model;
	}
	public static AILogModel createDeadLogModel(String playerId,String playerName, List<ItemPair> dorpItems) {
		AILogModel model = new AILogModel();
		model.type = TYPE_2;
		model.playerId = playerId;
		model.playerName = playerName;
		model.dorpItems = dorpItems;
		return model;
	}
	
	
	
	
	public byte getType() {
		return type;
	}

	public String getAiGameSceneRefId() {
		return aiGameSceneRefId;
	}

	public String getPlayerId() {
		return playerId;
	}

	public String getPlayerName() {
		return playerName;
	}

	public List<ItemPair> getDorpItems() {
		return dorpItems;
	}
	
}
