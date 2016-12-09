package newbee.morningGlory.mmorpg.sceneActivities.payonPalace;

import java.util.Map;
import java.util.Map.Entry;

import newbee.morningGlory.code.MGErrorCode;
import newbee.morningGlory.mmorpg.sceneActivities.SceneActivity;
import newbee.morningGlory.mmorpg.sceneActivities.payonPalace.ref.MGPayonPalaceActivityRef;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.gameArea.GameArea;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.utils.RuntimeResult;

public class MGPayonPalaceActivity extends SceneActivity{
	private static Logger logger = Logger.getLogger(MGPayonPalaceActivity.class);

	public MGPayonPalaceActivityRef getMGPayonPalaceActivityRef() {
		return getRef().getComponentRef(MGPayonPalaceActivityRef.class);
	}
	
	@Override
	public boolean checkEnter(Player player) {	
		
		if (getCrtActivityState() != SceneActivity.ACTIVITY_START) {
			return false;
		}
		
		return true;
	}
	
	public RuntimeResult checkEnterScene(Player player,String sceneRefId,String dstSceneRefId){
		
		if (StringUtils.equals(player.getSceneRefId(), sceneRefId)) {
			return RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_INVASIOIN_ALREAD_IN);
		}

		if (!StringUtils.equals(sceneRefId, dstSceneRefId)) {
			return RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_INVASIOIN_WRONG_SCENE);
		}
		
		if (getCrtActivityState() != SceneActivity.ACTIVITY_START) {
			logger.info("付费地宫活动未开启");
			return RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_PAYONPALACE_NOT_START);
		}
		
		//检测是否有物品
		Map<String, Integer> consumptionItems = getMGPayonPalaceActivityRef().getConsumptionItems();	
		for (Entry<String, Integer> consumptionItem : consumptionItems.entrySet()) {
			boolean bEnough = ItemFacade.isEnoughItem(player, consumptionItem.getKey(), consumptionItem.getValue());
			if (!bEnough) {
				return RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_PAYONPALACE_NOT_ENOUGH_ITEM);
			}
		}
		
		return RuntimeResult.OK();
	}
	
	public RuntimeResult consumptionItems(Player player) {
		
		Map<String, Integer> consumptionItems = getMGPayonPalaceActivityRef().getConsumptionItems();	
		for (Entry<String, Integer> consumptionItem : consumptionItems.entrySet()) {
			boolean remove = ItemFacade.removeItem(player, consumptionItem.getKey(), consumptionItem.getValue(), true, ItemOptSource.payOnPalace);
			if (remove) {
				return RuntimeResult.OK();
			}
		}

		return RuntimeResult.ParameterError();
	}

	@Override
	public boolean checkLeave(Player player) {
		return true;
	}

	@Override
	public boolean onPreStart() {

		return true;
	}

	@Override
	public boolean onPreEnd() {
		return true;
	}

	@Override
	public boolean onStart() {

		return true;
	}

	@Override 
	public boolean onCheckEnd() {
		onEnd();
		return true;
	}
	
	@Override
	public boolean onEnd() {

		GameArea gameArea = MMORPGContext.getGameAreaComponent().getGameArea();
		//第一个场景退出
		String firstSceneRefId = getFisrtSceneRefId();	
		GameScene gameFirstScene = gameArea.getSceneById(firstSceneRefId);
		if (null == gameFirstScene) {
			return false;
		}

		if(gameFirstScene.getPlayerMgrComponent().getPlayerMap().isEmpty()){
			return false;
		}
		
		for (Player owner : gameFirstScene.getPlayerMgrComponent().getPlayerMap().values()) {
			MGPayonPalaceActivityComponent payonPalaceComponent = (MGPayonPalaceActivityComponent) owner.getTagged(MGPayonPalaceActivityComponent.Tag);
			payonPalaceComponent.payonPalaceEnd();
		}
		
		//第二个场景退出
		String secSceneRefId =getSecondSceneRefId();
		GameScene gameSecScene = gameArea.getSceneById(secSceneRefId);
		if (null == gameSecScene) {
			return false;
		}
		
		if(gameSecScene.getPlayerMgrComponent().getPlayerMap().isEmpty()){
			return false;
		}

		for (Player player : gameSecScene.getPlayerMgrComponent().getPlayerMap().values()) {
			MGPayonPalaceActivityComponent payonPalaceComponent = (MGPayonPalaceActivityComponent) player.getTagged(MGPayonPalaceActivityComponent.Tag);
			payonPalaceComponent.payonPalaceEnd();
		}
		
		return true;
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {

	}

	@Override
	public void handleActionEvent(ActionEventBase event) {

	}
	
	@Override
	public boolean onEnter(Player player) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onLeave(Player player) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public String getFisrtSceneRefId(){
		String sceneRefId = getRef().getSceneRefId();// 活动场景
		String[] sceneStrArray = sceneRefId.split("\\|");
		return sceneStrArray[0];
	}
	
	public String getSecondSceneRefId(){
		String sceneRefId = getRef().getSceneRefId();// 活动场景
		String[] sceneStrArray = sceneRefId.split("\\|");
		if(sceneStrArray.length<2)
			return null;
		
		return sceneStrArray[1];
	}
}
