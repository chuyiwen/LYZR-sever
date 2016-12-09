package newbee.morningGlory.mmorpg.player.offLineAI;

import org.apache.log4j.Logger;

import newbee.morningGlory.mmorpg.player.offLineAI.persistence.OffLineAIDAOMgr;

public class OffLineAIFacade {

	private static final Logger logger = Logger.getLogger(OffLineAIFacade.class);
	
	/**
	 * 放入指定的道具进入离线背包
	 * @param playerId	玩家ID
	 * @param itemRefId	道具ref
	 * @param num		道具数量
	 * @return false:放入失败  true:放入成功
	 */
	public static boolean putItemToOffLineBag(String playerId,String itemRefId, int num) {
		try {
			PlayerAvatarData andLoadPlayerAvatarData = OffLineAIDAOMgr.getAndLoadPlayerAvatarData(playerId);
			if (andLoadPlayerAvatarData == null) {
				return false;
			}
			return andLoadPlayerAvatarData.addItem(itemRefId, num);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}
	}
	
	/**
	 * 是否可以放入指定数量的道具进入离线背包
	 * @param playerId	玩家ID
	 * @param itemRefId	道具ref
	 * @param num		道具数量
	 * @return
	 */
	public static boolean isCanPutItemToOffLineBag(String playerId,String itemRefId,int num){
		try {
			PlayerAvatarData andLoadPlayerAvatarData = OffLineAIDAOMgr.getAndLoadPlayerAvatarData(playerId);
			if (andLoadPlayerAvatarData == null) {
				return false;
			}
			int canPutItemNum = andLoadPlayerAvatarData.getCanPutItemNum_lock(itemRefId);
			if (canPutItemNum < num) {
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}
	}
	
}
