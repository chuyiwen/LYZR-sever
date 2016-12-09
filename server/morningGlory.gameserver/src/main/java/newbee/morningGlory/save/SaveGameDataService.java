package newbee.morningGlory.save;

import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import newbee.morningGlory.GameApp;
import newbee.morningGlory.MorningGloryContext;
import newbee.morningGlory.mmorpg.auction.MGAuctionItemSaver;
import newbee.morningGlory.mmorpg.ladder.MGLadderMemberSaver;
import newbee.morningGlory.mmorpg.player.gameInstance.GameInstanceMgr;
import newbee.morningGlory.mmorpg.union.MGUnionSaver;

import org.apache.log4j.Logger;

import sophia.foundation.util.DebugUtil;
import sophia.mmorpg.friend.ChatFriendSaver;

import com.google.common.util.concurrent.AbstractIdleService;
import com.google.common.util.concurrent.Service;

public class SaveGameDataService extends AbstractIdleService implements Service {

	private static final Logger logger = Logger.getLogger(SaveGameDataService.class.getName());

	private static final long Save_Interval_Time = 5 * 60 * 1000;

	private static SaveGameDataService saveGameDataService = new SaveGameDataService();

	private ScheduledFuture<?> scheduledFuture;

	public static SaveGameDataService getInstance() {
		return saveGameDataService;
	}

	private SaveGameDataService() {

	}

	@Override
	public void startUp() throws Exception {
		scheduledFuture = Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new Runnable() {
			public void run() {
				try {
					saveGameData();
				} catch (Throwable e) {
					logger.error(DebugUtil.printStack(e));
				}
			}
		}, Save_Interval_Time, Save_Interval_Time, TimeUnit.MILLISECONDS);
	}

	@Override
	protected void shutDown() {
		scheduledFuture.cancel(false);

		try {
			if (logger.isInfoEnabled()) {
				logger.info("waiting for save game data future stop");
			}

			scheduledFuture.get();
		} catch (Exception e) {
		}

		if (logger.isInfoEnabled()) {
			logger.info("save game data future was terminated");
		}

		shutDownSaveGameData();

	}

	/**
	 * 保存游戏全局数据到DB
	 */
	public void saveGameData() throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info("save Game data to DB...");
		}

		GameInstanceMgr gameInstanceMgr = MorningGloryContext.getGameInstanceSystemComponent().getGameInstanceMgr();
		if (gameInstanceMgr != null) {
			gameInstanceMgr.saveGameInstanceData();
		}

		MGLadderMemberSaver.getInstance().save();
		MGUnionSaver.getInstance().save();
		ChatFriendSaver.getInstance().save();
		MGAuctionItemSaver.getInstance().save();
	}

	public void shutDownSaveGameData() {

		try {
			GameInstanceMgr gameInstanceMgr = MorningGloryContext.getGameInstanceSystemComponent().getGameInstanceMgr();
			if (gameInstanceMgr != null) {
				gameInstanceMgr.saveGameInstanceData();
			}
		} catch (Exception e) {
			GameApp.getShutDownFailedSet().add(e);
		}

		try {
			MGLadderMemberSaver.getInstance().shutDownSave();
		} catch (Exception e) {
			GameApp.getShutDownFailedSet().add(e);
		}

		try {
			MGUnionSaver.getInstance().shutDownSave();
		} catch (Exception e) {
			GameApp.getShutDownFailedSet().add(e);
		}

		try {
			ChatFriendSaver.getInstance().shutDownSave();
		} catch (SQLException e) {
			GameApp.getShutDownFailedSet().add(e);
		}

		try {
			MGAuctionItemSaver.getInstance().shutDownSave();
		} catch (Exception e) {
			GameApp.getShutDownFailedSet().add(e);
		}

	}

}
