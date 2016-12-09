package newbee.morningGlory.mmorpg.player.offLineAI.persistence;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import newbee.morningGlory.mmorpg.player.offLineAI.PlayerAvatarData;

import org.apache.log4j.Logger;


public class OffLineAIDAOMgr {

	private static final Logger logger = Logger.getLogger(OffLineAIDAOMgr.class);
	
	public static final int DAO_AFTERWRITE_TIME = 10;			//后写入时间 单位:分钟
	public static final int EXIT_CACHE_TIME = 1000 * 60 * 5;	//在离开缓存中可以存在的时间 毫秒
	
	
	/** 所有替身缓存 */
	private static ConcurrentHashMap<String, PlayerAvatarData> allPlayerAvatarData = new ConcurrentHashMap<String, PlayerAvatarData>();
	/** 即将移除缓存的对象 */
	private static ConcurrentHashMap<String, PlayerAvatarData> exitPlayerAvatarData = new ConcurrentHashMap<String, PlayerAvatarData>();
	
	private static ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

	
	/**
	 * 移除一个玩家替身数据对象
	 * @param data
	 */
	public static void removePlayerAvatarData(PlayerAvatarData data) {
		try {
			if (data == null) {
				return;
			}
			PlayerAvatarData oldData = exitPlayerAvatarData.putIfAbsent(data.getPlayerId(), data);
			if (oldData != null) {
				oldData.setExitTime(System.currentTimeMillis());
			} else {
				data.setExitTime(System.currentTimeMillis());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	public static void removePlayerAvatarData(String playerId) {
		removePlayerAvatarData(allPlayerAvatarData.get(playerId));
	}

	/**
	 * 后写入
	 */
	public static void afterWrite() {
		try {
			//从exitPlayerAvatarData找出要剔除缓存的对象
			long now = System.currentTimeMillis();
			ArrayList<PlayerAvatarData> exitList = new ArrayList<PlayerAvatarData>();//可以移除的缓存
			Iterator<PlayerAvatarData> iterator = exitPlayerAvatarData.values().iterator();
			while (iterator.hasNext()) {
				PlayerAvatarData playerAvatarData = iterator.next();
				if (playerAvatarData.isCanExitCache(now)) {
					exitList.add(playerAvatarData);
				}
			}
			//更新所有的缓存记录
			OffLineAIDAO.updatePlayerAvatarData(new ArrayList<PlayerAvatarData>(allPlayerAvatarData.values()));
			
			readWriteLock.writeLock().lock();
			try {
				//移除要剔除的缓存对象
				for (int i = 0; i < exitList.size(); i++) {
					PlayerAvatarData playerAvatarData = exitList.get(i);
					allPlayerAvatarData.remove(playerAvatarData.getPlayerId());
					exitPlayerAvatarData.remove(playerAvatarData.getPlayerId());
				}
			} finally {
				readWriteLock.writeLock().unlock();
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 获取一个玩家的替身数据对象
	 * @param playerId
	 * @return
	 */
	public static PlayerAvatarData getAndLoadPlayerAvatarData(String playerId) {
		readWriteLock.readLock().lock();
		try {
			PlayerAvatarData data = allPlayerAvatarData.get(playerId);
			if (data != null) {
				if(exitPlayerAvatarData.remove(playerId) != null){
					data.setExitTime(0);
				}
				return data;
			}
			data = OffLineAIDAO.selectPlayerAvatarData(playerId);
			if (data == null) {
				data = OffLineAIDAO.instertPlayerAvatarData(playerId);
			}
			PlayerAvatarData oldData = allPlayerAvatarData.putIfAbsent(playerId, data);
			if (oldData != null) {
				return oldData;
			}
			return data;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}finally{
			readWriteLock.readLock().unlock();
		}
	}

}
