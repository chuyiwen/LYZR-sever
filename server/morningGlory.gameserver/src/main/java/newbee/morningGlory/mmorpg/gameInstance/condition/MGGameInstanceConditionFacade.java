package newbee.morningGlory.mmorpg.gameInstance.condition;

import java.util.Map;

import newbee.morningGlory.MorningGloryContext;
import newbee.morningGlory.code.MGErrorCode;
import newbee.morningGlory.mmorpg.gameInstance.MGGameInstanceSystemComponent;
import newbee.morningGlory.mmorpg.player.gameInstance.GameInstanceMgr;
import newbee.morningGlory.mmorpg.player.gameInstance.PlayerGameInstanceComponent;
import newbee.morningGlory.mmorpg.vip.MGPlayerVipComponent;

import org.apache.log4j.Logger;

import sophia.foundation.property.PropertyDictionary;
import sophia.mmorpg.gameInstance.CompleteCondition;
import sophia.mmorpg.gameInstance.GameInstance;
import sophia.mmorpg.gameInstance.GameInstanceRef;
import sophia.mmorpg.gameInstance.GameInstanceSceneRef;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.quest.ref.QuestRef;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.RuntimeResult;

import com.google.common.base.Strings;

public class MGGameInstanceConditionFacade {
	public static final Logger logger = Logger.getLogger(MGGameInstanceConditionFacade.class.getName());

	/**
	 * 进入副本次数检查
	 * 
	 * @param player
	 * @param gameInstanceRef
	 * @return
	 */
	public static RuntimeResult checkCount(Player player, GameInstanceRef gameInstanceRef) {
		MGGameInstanceSystemComponent gameInstanceSystemComponent = MorningGloryContext.getGameInstanceSystemComponent();
		GameInstanceMgr gameInstanceMgr = gameInstanceSystemComponent.getGameInstanceMgr();

		int countsInDay = gameInstanceMgr.getScheduleManager().getInstanceRecordInDay(player, gameInstanceRef.getId());// 返回当天完成副本次数
		int countsInWeek = gameInstanceMgr.getScheduleManager().getInstanceRecordInWeek(player, gameInstanceRef.getId());// 返回当周完成副本次数
		// 1.进入副本次数(天，周)，检查
		MGPlayerVipComponent playerVipComponent = (MGPlayerVipComponent) player.getTagged(MGPlayerVipComponent.Tag);
		int enterGameInstanceCount = playerVipComponent.getEnterGameInstanceCount();
		int realCountSInDay = gameInstanceRef.getCountsADay(player, enterGameInstanceCount);
		int refCountsInDay = gameInstanceRef.getRefCountsADay(player);
		int refCountsInWeek = gameInstanceRef.getCountsAWeek(player);
		// refCountsInDay等于0 没有次数限制
		if (refCountsInDay != 0 && countsInDay >= realCountSInDay) {
			if (logger.isDebugEnabled()) {
				logger.debug("进入副本的次数（天）已经超出限制,当前次数:" + countsInDay);
			}
			// return RuntimeResult.RuntimeError("进入副本的次数（天）已经超出限制");
			return RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_GAME_INSTANCE_ENTER_DAY_LIMIT);
		}
		// refCountsInWeek等于0 没有次数限制
		if (refCountsInWeek != 0 && countsInWeek >= refCountsInWeek) {
			if (logger.isDebugEnabled()) {
				logger.debug("进入副本的次数（周）已经超出限制,当前次数:" + countsInDay);
			}
			// return RuntimeResult.RuntimeError("进入副本的次数（周）已经超出限制");
			return RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_GAME_INSTANCE_ENTER_WEEK_LIMIT);
		}
		return RuntimeResult.OK();
	}

	/**
	 * 副本开放时间检查
	 * 
	 * @param player
	 * @param gameInstanceRef
	 * @return
	 */
	public static RuntimeResult checkOpenTime(Player player, GameInstanceRef gameInstanceRef) {
		long now = System.currentTimeMillis();
		if (gameInstanceRef.getOpen().getOpenTime(now) == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("副本未开放");
			}
			// return RuntimeResult.RuntimeError("副本未开放");
			return RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_GAME_INSTANCE_NOT_OPEN);
		}
		return RuntimeResult.OK();
	}

	/**
	 * 玩家等级检查
	 * 
	 * @param player
	 * @param gameInstanceRef
	 * @return
	 */
	public static RuntimeResult checkPlayerLevel(Player player, GameInstanceRef gameInstanceRef) {
		PropertyDictionary playerPd = player.getProperty();
		int playerLevel = MGPropertyAccesser.getLevel(playerPd);
		// 3.是否达到等级
		if (playerLevel < gameInstanceRef.getLevel()) {
			if (logger.isDebugEnabled()) {
				logger.debug("等级未达到，要求等级：" + gameInstanceRef.getLevel() + "玩家等级:" + playerLevel);
			}
			// return RuntimeResult.RuntimeError("等级未达到");
			return RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_GAME_INSTANCE_NOT_ENOUGH_LEVEL);
		}
		return RuntimeResult.OK();
	}

	/**
	 * 检查玩家是否接受主线任务
	 * 
	 * @param player
	 * @param gameInstanceRef
	 * @return
	 */
	public static RuntimeResult checkQuest(Player player, GameInstanceRef gameInstanceRef) {
		QuestRef crtQuestRef = player.getPlayerQuestComponent().getQuestManager().getCrtQuest().getQuestRef();

		// gameInstanceRef.getQuestRefId()为空 表明不需要接受某个任务才能进此副本
		if (!Strings.isNullOrEmpty(gameInstanceRef.getQuestRefId().trim()) && !gameInstanceRef.getQuestRefId().trim().equals(crtQuestRef.getId())) {
			if (logger.isDebugEnabled()) {
				logger.debug("需要接受主线任务才能进此副本, 任务RefId：" + gameInstanceRef.getQuestRefId());
			}
			// return RuntimeResult.RuntimeError("需要接受主线任务才能进此副本");
			return RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_GAME_INSTANCE_NEED_ACCEPT_MAINQUEST);
		}
		return RuntimeResult.OK();
	}

	/**
	 * 检查玩家杀死指定怪物对象集合
	 * 
	 * @param player
	 * @param gameInstanceSceneRef
	 * @return
	 */
	public static RuntimeResult checkKillMonster(Player player, GameInstanceSceneRef gameInstanceSceneRef) {
		MGGameInstanceSystemComponent gameInstanceSystemComponent = MorningGloryContext.getGameInstanceSystemComponent();
		GameInstanceMgr gameInstanceMgr = gameInstanceSystemComponent.getGameInstanceMgr();
		CompleteCondition completeCondition = gameInstanceSceneRef.getCompleteCondition();

		PlayerGameInstanceComponent playerGameInstanceComponent = (PlayerGameInstanceComponent) player.getTagged(PlayerGameInstanceComponent.Tag);
		String crtGameInstanceId = playerGameInstanceComponent.getCrtGameInstanceId();
		if (Strings.isNullOrEmpty(crtGameInstanceId)) {
			if (logger.isDebugEnabled()) {
				logger.debug("玩家当前没有在副本里面");
			}
			// return RuntimeResult.RuntimeError("未满足通关条件");
			return RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_GAME_INSTANCE_NOT_CURRENT_LAYER);
		}
		GameInstance gameInstace = gameInstanceMgr.getGameInstace(crtGameInstanceId);
		Map<String, Short> monsterKills = gameInstace.getKillRecord(player.getId());

		if (MGGameInstanceSceneFinishCondtionType.Kill_Monster_Type == gameInstanceSceneRef.getSucceedType()) {
			if (monsterKills == null || CompleteCondition.TRUE != completeCondition.checkCompleteCondition(monsterKills)) {
				if (logger.isDebugEnabled()) {
					logger.debug("未满足通关条件");
				}
				// return RuntimeResult.RuntimeError("未满足通关条件");
				return RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_GAME_INSTANCE_EXIST_MONSTER_CURRENT_LAYER);
			}
		}
		return RuntimeResult.OK();
	}
}
