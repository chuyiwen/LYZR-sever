package newbee.morningGlory.mmorpg.player.sortboard;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import newbee.morningGlory.mmorpg.player.sortboard.event.C2G_SortBoard_GetSortBoardData;
import newbee.morningGlory.mmorpg.player.sortboard.event.C2G_SortBoard_GetSortBoardVersion;
import newbee.morningGlory.mmorpg.player.sortboard.event.C2G_SortBoard_GetTopPlayerData;
import newbee.morningGlory.mmorpg.player.sortboard.event.C2G_SortBoard_PFS_GetBoardList;
import newbee.morningGlory.mmorpg.player.sortboard.event.G2C_SortBoard_GetSortBoardData;
import newbee.morningGlory.mmorpg.player.sortboard.event.G2C_SortBoard_GetSortBoardVersion;
import newbee.morningGlory.mmorpg.player.sortboard.event.G2C_SortBoard_GetTopPlayerData;
import newbee.morningGlory.mmorpg.player.sortboard.event.G2C_SortBoard_PFS_GetBoardList;
import newbee.morningGlory.mmorpg.player.sortboard.event.MGSortboardEventDefines;
import newbee.morningGlory.mmorpg.sortboard.SortboardData;
import newbee.morningGlory.mmorpg.sortboard.SortboardHelper;
import newbee.morningGlory.mmorpg.sortboard.SortboardMgr;
import newbee.morningGlory.mmorpg.sortboard.SortboardType;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.authentication.Identity;
import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.chat.sysytem.SystemPromptFacade;
import sophia.mmorpg.player.gameEvent.EnterWorld_SceneReady_GE;

public final class MGSortboardComponent extends ConcreteComponent<Player> {
	public static final Logger logger = Logger.getLogger(MGSortboardComponent.class);

	public static final String Tag = "MGSortboardComponent";

	private String EnterWorld_SceneReady_GE_Id = EnterWorld_SceneReady_GE.class.getSimpleName();

	@Override
	public void ready() {
		addInterGameEventListener(EnterWorld_SceneReady_GE_Id);
		addActionEventListener(MGSortboardEventDefines.C2G_SortBoard_GetSortBoardVersion);
		addActionEventListener(MGSortboardEventDefines.C2G_SortBoard_GetSortBoardData);
		addActionEventListener(MGSortboardEventDefines.C2G_SortBoard_GetTopPlayerData);
		addActionEventListener(MGSortboardEventDefines.C2G_SortBoard_PFS_GetBoardList);
	}

	@Override
	public void suspend() {
		removeInterGameEventListener(EnterWorld_SceneReady_GE_Id);
		removeActionEventListener(MGSortboardEventDefines.C2G_SortBoard_GetSortBoardVersion);
		removeActionEventListener(MGSortboardEventDefines.C2G_SortBoard_GetSortBoardData);
		removeActionEventListener(MGSortboardEventDefines.C2G_SortBoard_GetTopPlayerData);
		removeActionEventListener(MGSortboardEventDefines.C2G_SortBoard_PFS_GetBoardList);
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
		short actionEventId = event.getActionEventId();
		Identity identity = event.getIdentity();
		switch (actionEventId) {
		case MGSortboardEventDefines.C2G_SortBoard_GetSortBoardVersion:
			handle_SortBoard_GetSortBoardVersion((C2G_SortBoard_GetSortBoardVersion) event, actionEventId, identity);
			break;
		case MGSortboardEventDefines.C2G_SortBoard_GetSortBoardData:
			handle_SortBoard_GetSortBoardList((C2G_SortBoard_GetSortBoardData) event, actionEventId, identity);
			break;
		case MGSortboardEventDefines.C2G_SortBoard_GetTopPlayerData:
			handle_SortBoard_GetTopPlayerData((C2G_SortBoard_GetTopPlayerData) event, actionEventId, identity);
			break;
		case MGSortboardEventDefines.C2G_SortBoard_PFS_GetBoardList:
			handle_SortBoard_PFS_GetBoardList((C2G_SortBoard_PFS_GetBoardList) event, actionEventId, identity);
			break;
		}
		super.handleActionEvent(event);
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {
		if (event.isId(EnterWorld_SceneReady_GE_Id)) {
			if (logger.isDebugEnabled()) {
				logger.debug("收到玩家进入世界事件");
			}

			Map<Integer, String> topThreeProfessionPlayer = SortboardMgr.getInstance().getTopThreeProfessionPlayer();
			for (Entry<Integer, String> entry : topThreeProfessionPlayer.entrySet()) {
				if (StringUtils.equals(getConcreteParent().getId(), entry.getValue())) {
					SystemPromptFacade.broadCastBestPlayerLogin(getConcreteParent());
				}
			}
		}
		
		super.handleGameEvent(event);
	}

	private void handle_SortBoard_GetSortBoardVersion(C2G_SortBoard_GetSortBoardVersion event, short actionEventId, Identity identity) {
		int type = event.getSortBoartType();
		SortboardType sortboardType = getSortboardType(type);
		if (sortboardType == null) {
			logger.error("client Send an error sortboardType~!!!!");
			return;
		}
		int version = SortboardMgr.getInstance().getVersionByBoardType(sortboardType);
		if (version == -1) {
			return;
		}
		G2C_SortBoard_GetSortBoardVersion boardVersion = MessageFactory.getConcreteMessage(MGSortboardEventDefines.G2C_SortBoard_GetSortBoardVersion);
		boardVersion.setVersion(version);
		boardVersion.setSortboardType(type);
		GameRoot.sendMessage(identity, boardVersion);
	}

	private void handle_SortBoard_GetSortBoardList(C2G_SortBoard_GetSortBoardData event, short actionEventId, Identity identity) {
		int type = event.getSortBoartType();
		SortboardType sortboardType = getSortboardType(type);
		if (sortboardType == null) {
			logger.error("client Send an error sortboardType~!!!!");
			return;
		}
		SortboardData sortboardData = SortboardMgr.getInstance().getSortboardData(sortboardType);

		int ranking = SortboardHelper.getRanking(sortboardType, getConcreteParent());
		int score = SortboardHelper.getScore(sortboardType, getConcreteParent());

		G2C_SortBoard_GetSortBoardData boardData = MessageFactory.getConcreteMessage(MGSortboardEventDefines.G2C_SortBoard_GetSortBoardData);
		boardData.setSortboardType(type);
		boardData.setSortboardData(sortboardData);
		boardData.setRanking(ranking);
		boardData.setScore(score);
		GameRoot.sendMessage(identity, boardData);
	}

	private void handle_SortBoard_GetTopPlayerData(C2G_SortBoard_GetTopPlayerData event, short actionEventId, Identity identity) {
		List<String> topPlayerData = SortboardHelper.getTopPlayerIdList();
		G2C_SortBoard_GetTopPlayerData topPlayer = MessageFactory.getConcreteMessage(MGSortboardEventDefines.G2C_SortBoard_GetTopPlayerData);
		topPlayer.setTopPlayerData(topPlayerData);
		GameRoot.sendMessage(identity, topPlayer);
	}

	private void handle_SortBoard_PFS_GetBoardList(C2G_SortBoard_PFS_GetBoardList event, short actionEventId, Identity identity) {
		int type = event.getSortBoartType();
		int profession = event.getProfession();
		SortboardType sortboardType = getSortboardType(type);
		if (sortboardType == null) {
			logger.error("client Send an error sortboardType~!!!!");
			return;
		}
		if (sortboardType != SortboardType.PlayerFightPower) {
			return;
		}
		int version = SortboardMgr.getInstance().getVersionByBoardType(sortboardType);
		if (version == -1) {
			return;
		}
		Map<Integer, SortboardData> fightPowerSubBoard = SortboardMgr.getInstance().getFightPowerSubBoard();
		if (!fightPowerSubBoard.containsKey(profession)) {
			return;
		}
		SortboardData sortboardData = fightPowerSubBoard.get(profession);
		G2C_SortBoard_PFS_GetBoardList boardData = MessageFactory.getConcreteMessage(MGSortboardEventDefines.G2C_SortBoard_PFS_GetBoardList);
		boardData.setVersion(version);
		boardData.setProfession((byte) profession);
		boardData.setSortboardType(type);
		boardData.setSortboardData(sortboardData);
		GameRoot.sendMessage(identity, boardData);
	}

	public SortboardType getSortboardType(int type) {
		switch (type) {
		case 0:
			return SortboardType.PlayerFightPower;
		case 1:
			return SortboardType.PlayerLvl;
		case 2:
			return SortboardType.PlayerMoney;
		case 3:
			return SortboardType.PlayerMerit;
		case 4:
			return SortboardType.PlayerWingLvl;
		case 5:
			return SortboardType.MountLvl;
		case 6:
			return SortboardType.TalismanLvl;
		default:
			return null;
		}
	}
}
