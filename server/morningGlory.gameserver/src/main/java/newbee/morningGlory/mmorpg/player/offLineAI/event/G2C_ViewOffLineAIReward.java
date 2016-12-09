package newbee.morningGlory.mmorpg.player.offLineAI.event;

import java.util.List;

import newbee.morningGlory.mmorpg.player.offLineAI.PlayerAvatarData;
import newbee.morningGlory.mmorpg.player.offLineAI.model.AILogModel;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.player.itemBag.ItemPair;

/**
 * 玩家查看离线背包
 */
public class G2C_ViewOffLineAIReward extends ActionEventBase{

	private PlayerAvatarData playerAvatarData;
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		List<ItemPair> itemList = playerAvatarData.getReadItemList();
		short itemListSize = itemList == null ? 0 : (short)itemList.size();
		buffer.putShort(itemListSize);
		for (int i = 0; i < itemListSize; i++) {
			ItemPair itemPair = itemList.get(i);
			putString(buffer, itemPair.getItemRefId());
			buffer.putInt(itemPair.getNumber());
		}
		buffer.putInt(playerAvatarData.getExp());
		buffer.putInt(playerAvatarData.getMoney());
		
		List<AILogModel> aiLogModelList = playerAvatarData.getAiLogModelList();
		short aiLogModelListSize = aiLogModelList == null ? 0 : (short)aiLogModelList.size();
		buffer.putShort(aiLogModelListSize);
		for (int i = 0; i < aiLogModelListSize; i++) {
			AILogModel aiLogModel = aiLogModelList.get(i);
			buffer.put(aiLogModel.getType());
			if(aiLogModel.getType() == AILogModel.TYPE_1){
				putString(buffer,aiLogModel.getAiGameSceneRefId());
			}else if(aiLogModel.getType() == AILogModel.TYPE_2){
				putString(buffer,aiLogModel.getPlayerId());
				putString(buffer,aiLogModel.getPlayerName());
				
				List<ItemPair> dorpItems = aiLogModel.getDorpItems();
				short dorpItemsSize = dorpItems == null ? 0 : (short)dorpItems.size();
				buffer.putShort(dorpItemsSize);
				for (int j = 0; j < dorpItemsSize; j++) {
					ItemPair itemPair = dorpItems.get(j);
					putString(buffer,itemPair.getItemRefId());
					buffer.putInt(itemPair.getNumber());
				}
			}
		}
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
	}

	public void setPlayerAvatarData(PlayerAvatarData playerAvatarData) {
		this.playerAvatarData = playerAvatarData;
	}

	
	
}
