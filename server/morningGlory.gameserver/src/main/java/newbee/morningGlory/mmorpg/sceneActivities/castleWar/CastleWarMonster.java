package newbee.morningGlory.mmorpg.sceneActivities.castleWar;

import newbee.morningGlory.MorningGloryContext;
import newbee.morningGlory.mmorpg.sceneActivities.SceneActivityMgr;
import newbee.morningGlory.mmorpg.sceneActivities.SceneActivityType;
import newbee.morningGlory.mmorpg.union.MGUnionMgr;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.property.PropertyDictionary;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.gameArea.GameArea;
import sophia.mmorpg.monster.Monster;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class CastleWarMonster {

	private static final Logger logger = Logger.getLogger(CastleWarMonster.class);

	public CastleWarMonster() {
	}

	public void createMonster(SceneActivityType type) {
		GameArea gameArea = MMORPGContext.getGameAreaComponent().getGameArea();
		CastleWarMgr castleWarMgr = (CastleWarMgr) SceneActivityMgr.getInstance().getSceneAcitityByType(type);
		String sceneId = castleWarMgr.getCastleWarRef().getCastleWarSceneTransfer().getTargetScene();
		GameScene gameScene = gameArea.getSceneById(sceneId);
		MGUnionMgr unionMgr = MorningGloryContext.getUnionSystemComponent().getUnionMgr();
		String kingCityUnionName = unionMgr.getKingCityUnionName();
		castleWarMgr.setMonsterOwner(kingCityUnionName);
		if (gameScene != null) {
			gameScene.getRefreshMonsterMgrComponent().sceneTimeInRange(kingCityUnionName, 0);
		}
	}

	public void setMonsterUnion(Monster monster) {
		PropertyDictionary pd = monster.getProperty();
		MGUnionMgr unionMgr = MorningGloryContext.getUnionSystemComponent().getUnionMgr();
		String kingCityUnionName = unionMgr.getKingCityUnionName();
		if (StringUtils.isEmpty(kingCityUnionName)) {
			logger.info("Do not has KingCityUnion Now~!");
			return;
		}
		MGPropertyAccesser.setOrPutUnionName(pd, kingCityUnionName);
	}

	public void refreshMonster(Player killer, int killMonsterTime) {
		GameArea gameArea = MMORPGContext.getGameAreaComponent().getGameArea();
		CastleWarMgr castleWarMgr = (CastleWarMgr) SceneActivityMgr.getInstance().getSceneAcitityByType(SceneActivityType.CastleWarMgr);
		String sceneId = castleWarMgr.getCastleWarRef().getCastleWarSceneTransfer().getTargetScene();
		GameScene gameScene = gameArea.getSceneById(sceneId);
		PropertyDictionary pd = killer.getProperty();
		String unionName = MGPropertyAccesser.getUnionName(pd);
		if (StringUtils.isEmpty(unionName)) {
			logger.error("CastleWar's Monster Killer doesn't have the union.");
		} else {
			castleWarMgr.setMonsterOwner(unionName);
			castleWarMgr.resetKingCityUnion(unionName);
		}
		gameScene.getRefreshMonsterMgrComponent().sceneTimeInRange(unionName, killMonsterTime);
		castleWarMgr.broadCastCastleWarMonsterDead(killer);
		castleWarMgr.sendMonsterRefreshMessage();
	}
}
