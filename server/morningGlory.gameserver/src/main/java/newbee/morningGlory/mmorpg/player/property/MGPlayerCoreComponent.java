package newbee.morningGlory.mmorpg.player.property;

import newbee.morningGlory.mmorpg.player.offLineAI.PlayerAvatar;
import newbee.morningGlory.mmorpg.player.pk.MGPlayerPKComponent;
import newbee.morningGlory.mmorpg.sceneActivities.SceneActivityMgr;
import newbee.morningGlory.mmorpg.sceneActivities.castleWar.CastleWarMgr;
import newbee.morningGlory.mmorpg.union.MGUnionHelper;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.authentication.Identity;
import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.foundation.property.PropertyDictionary;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.base.scene.grid.SceneGrid;
import sophia.mmorpg.base.scene.ref.SceneRef;
import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.base.sprite.SpriteTypeDefine;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.gameArea.GameArea;
import sophia.mmorpg.monster.Monster;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.gameEvent.PlayerDead_GE;
import sophia.mmorpg.player.gameEvent.PlayerRevive_GE;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.property.PlayerReviveType;
import sophia.mmorpg.player.property.event.G2C_Player_KillerInfo;
import sophia.mmorpg.player.property.event.PlayerEventDefines;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.ref.editorgen.MGPropertySymbolDefines;

public class MGPlayerCoreComponent extends ConcreteComponent<Player> {

	private static final Logger logger = Logger.getLogger(MGPlayerCoreComponent.class);

	public static final String Tag = "MGPlayerCoreComponent";

	@Override
	public void ready() {
		addActionEventListener(PlayerEventDefines.C2G_Player_KillerInfo);
		addInterGameEventListener(PlayerRevive_GE.class.getSimpleName());
		addInterGameEventListener(Player.PlayerDead_GE_Id);
		super.ready();
	}

	@Override
	public void suspend() {
		removeActionEventListener(PlayerEventDefines.C2G_Player_KillerInfo);
		removeInterGameEventListener(PlayerRevive_GE.class.getSimpleName());
		removeInterGameEventListener(Player.PlayerDead_GE_Id);
		super.suspend();
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
		short actionEventId = event.getActionEventId();
		Identity identity = event.getIdentity();

		switch (actionEventId) {
		case PlayerEventDefines.C2G_Player_KillerInfo: {
			if (logger.isDebugEnabled()) {
				logger.debug("C2G_Player_KillerInfo");
			}

			Player player = getConcreteParent();
			PropertyDictionary property = player.getProperty();
			String killerCharId = "";
			String killerName = "";
			byte killerType = 0;
			int killerLevel = 0;
			byte killerOccupa = 0;
			int killerFightPower = 0;

			long deadTime = 0;
			if (property.contains(MGPropertySymbolDefines.KillerName_Id)) {
				killerCharId = MGPropertyAccesser.getKillerCharId(property);
				killerName = MGPropertyAccesser.getKillerName(property);
				killerType = MGPropertyAccesser.getKillerType(property);
				deadTime = MGPropertyAccesser.getDeadTime(property);
				killerLevel = MGPropertyAccesser.getKillerLevel(property);
				killerOccupa = MGPropertyAccesser.getKillerOccupa(property);
				killerFightPower = MGPropertyAccesser.getKillerFightPower(property);
			}

			G2C_Player_KillerInfo res = MessageFactory.getConcreteMessage(PlayerEventDefines.G2C_Player_KillerInfo);
			res.setKillerCharId(killerCharId);
			res.setKillerName(killerName);
			res.setKillerType(killerType);
			res.setKillerLevel(killerLevel);
			res.setKillerOccupa(killerOccupa);
			res.setKillerFightPower(killerFightPower);
			res.setDeadTime(deadTime);
			@SuppressWarnings("unchecked")
			MGPlayerPKComponent<Player> playerPKComponent = (MGPlayerPKComponent<Player>) player.getTagged(MGPlayerPKComponent.Tag);
			res.setLootItemRefMap(playerPKComponent.getLootItemRefMap());
			GameRoot.sendMessage(identity, res);
			break;
		}
		}

		super.handleActionEvent(event);
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {
		if (event.isId(PlayerRevive_GE.class.getSimpleName())) {
			Player player = getConcreteParent();
			Identity identity = player.getIdentity();
			PlayerRevive_GE playerRevive_GE = (PlayerRevive_GE) event.getData();

			if (playerRevive_GE.getReviveType() == PlayerReviveType.Revive_GoHome) { // 回城复活
				player.revive();
				if (StringUtils.equals(player.getCrtScene().getRef().getId(), "S012")) {
					if (castleWarRelive(player)) {
						return;
					}
				} else if (StringUtils.equals(player.getCrtScene().getRef().getId(), "S217")) {
					// 跳转到活动副本地图
					SceneRef sceneRef = (SceneRef) GameRoot.getGameRefObjectManager().getManagedObject("S217");
					SceneGrid sceneGrid = sceneRef.getSafeRegions().get(0).getRegion().getRandomUnblockedGrid();
					player.getPlayerSceneComponent().switchTo("S217", sceneGrid.getColumn(), sceneGrid.getRow());
					return;
				} else if (StringUtils.equals(player.getCrtScene().getRef().getId(), "S218") || StringUtils.equals(player.getCrtScene().getRef().getId(), "S219")) {
					// 跳转到活动副本地图
					SceneRef sceneRef = (SceneRef) GameRoot.getGameRefObjectManager().getManagedObject("S218");
					SceneGrid sceneGrid = sceneRef.getSafeRegions().get(0).getRegion().getRandomUnblockedGrid();
					player.getPlayerSceneComponent().switchTo("S218", sceneGrid.getColumn(), sceneGrid.getRow());
					return;
				}
				player.goHome();
			} else if (playerRevive_GE.getReviveType() == PlayerReviveType.Revive_Gold) { // 元宝复活
																							// ->
																							// 金币复活
				// 副本不能原地复活
				if (player.getCrtScene() != null && player.getCrtScene().getRef().getType() == SceneRef.FuBen) {
					ResultEvent.sendResult(identity, PlayerEventDefines.C2G_Player_Revive, MMORPGErrorCode.CODE_PLAYER_REVIVE_INVALID_IN_FUBEN);
					return;
				}

				long deadTime = 0;
				PropertyDictionary property = player.getProperty();
				if (property.contains(MGPropertySymbolDefines.DeadTime_Id)) {
					deadTime = MGPropertyAccesser.getDeadTime(property);
				}

				// 9s限定间隔
				if (System.currentTimeMillis() - deadTime < 9000) {
					ResultEvent.sendResult(identity, PlayerEventDefines.C2G_Player_Revive, MMORPGErrorCode.CODE_PLAYER_REVIVE_INTERVAL);
					return;
				}

				if (player.getPlayerMoneyComponent().subGold(300000, ItemOptSource.Revice)) {
					// if
					// (player.getPlayerMoneyComponent().subUnbindGold(5,ItemOptSource.Revice))
					// {
					player.revive();
				} else {
					ResultEvent.sendResult(identity, PlayerEventDefines.C2G_Player_Revive, MMORPGErrorCode.CODE_PLAYER_REVIVE_UNBINDGOLD_NOT_ENOUGH);
					return;
				}
			} else if (playerRevive_GE.getReviveType() == PlayerReviveType.Revive_Talisman) { // 法宝复活

			}
		} else if (event.isId(Player.PlayerDead_GE_Id)) {
			PlayerDead_GE playerDeadGE = (PlayerDead_GE) event.getData();
			if (playerDeadGE.getPlayer().equals(getConcreteParent())) {
				setKillerInfo(playerDeadGE.getAttacker());
			}
		}
	}

	public boolean castleWarRelive(Player player) {
		boolean ret = false;
		CastleWarMgr castleWarMgr = (CastleWarMgr) SceneActivityMgr.getInstance().getSceneAcitityBySceneRefId("S012");
		byte crtActivityState = castleWarMgr.getCrtActivityState();
		byte preActivityState = castleWarMgr.getPreActivityState();
		if (crtActivityState == 2 || preActivityState == 3) {
			if (MGUnionHelper.isKingCityUnionMember(player)) {
				castleWarMgr.sendLeaveMessage(player);
				castleWarMgr.transferOut(player);
				ret = true;
			} else {
				GameArea gameArea = MMORPGContext.getGameAreaComponent().getGameArea();
				GameRoot.getGameObjectManager().getObjectForId("S002");
				SceneRef sceneRef = (SceneRef) GameRoot.getGameRefObjectManager().getManagedObject("S002");
				SceneGrid sceneGrid = sceneRef.getRandomReviveGrid();
				player.getPlayerSceneComponent().switchTo(gameArea.getSceneById("S002"), sceneGrid.getColumn(), sceneGrid.getRow());
				ret = true;
			}
		}
		return ret;
	}

	private void setKillerInfo(final FightSprite attacker) {
		String killerCharId = "";
		String killerName = "";
		byte killerType = 0;
		int killerLevel = 0;
		byte killerOccupa = 0;
		int killerFightPower = 0;

		if (attacker == null) {

		} else if (attacker instanceof Monster) {
			Monster monster = (Monster) attacker;
			Player owner = null;
			if (!monster.getMonsterRef().isRegularMonster()) {
				owner = (Player) monster.getOwner();
			}

			if (owner != null) {
				killerCharId = owner.getId();
				killerName = owner.getName();
				killerType = SpriteTypeDefine.GameSprite_Player;
				killerLevel = owner.getLevel();
				killerOccupa = owner.getProfession();
				killerFightPower = owner.getFightPower();
			} else {
				killerCharId = monster.getId();
				killerType = SpriteTypeDefine.GameSprite_Monster;
				killerName = monster.getName();
			}
		} else if (attacker instanceof Player) {
			Player player = (Player) attacker;
			killerCharId = player.getId();
			killerName = player.getName();
			killerType = SpriteTypeDefine.GameSprite_Player;
			killerLevel = player.getLevel();
			killerOccupa = player.getProfession();
			killerFightPower = player.getFightPower();
		} else if (attacker instanceof PlayerAvatar) {
			Player owner = ((PlayerAvatar) attacker).getPlayer();
			killerCharId = owner.getId();
			killerName = owner.getName();
			killerType = SpriteTypeDefine.GameSprite_Player;
			killerLevel = owner.getLevel();
			killerOccupa = owner.getProfession();
			killerFightPower = owner.getFightPower();
		}

		Player player = getConcreteParent();
		PropertyDictionary pd = player.getProperty();
		MGPropertyAccesser.setOrPutKillerCharId(pd, killerCharId);
		MGPropertyAccesser.setOrPutKillerName(pd, killerName);
		MGPropertyAccesser.setOrPutKillerType(pd, killerType);
		MGPropertyAccesser.setOrPutDeadTime(pd, System.currentTimeMillis());
		MGPropertyAccesser.setOrPutKillerLevel(pd, killerLevel);
		MGPropertyAccesser.setOrPutKillerOccupa(pd, killerOccupa);
		MGPropertyAccesser.setOrPutKillerFightPower(pd, killerFightPower);
	}
}
