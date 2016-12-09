package newbee.morningGlory.mmorpg.player.wing.wingModule;

import newbee.morningGlory.mmorpg.player.wing.MGPlayerWing;
import newbee.morningGlory.mmorpg.player.wing.MGPlayerWingComponent;
import newbee.morningGlory.mmorpg.player.wing.MGPlayerWingRef;
import newbee.morningGlory.mmorpg.player.wing.actionEvent.MGWingLevelUp_GE;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.player.Player;

import com.google.common.base.Strings;


public class LevelModule extends AbstractModule{

	public LevelModule(WingManager wingManager) {
		super(wingManager);
	}

	public void rewardExp() {
		MGPlayerWing playerWing = wingManager.getPlayerWing();
		long exp = playerWing.getExp();
		if (exp < 0) {
			return;
		}
		MGPlayerWingRef playerWingRef = playerWing.getPlayerWingRef();
		if (playerWingRef == null) {
			return;
		}
		String nextWingRefId;
		MGPlayerWingRef nextWingRef;
		long crtMaxExp = 0;
		Player player = wingManager.getPlayer();
		do {
			nextWingRefId = playerWing.getNextRefId();
			if (Strings.isNullOrEmpty(nextWingRefId)) {
				// 没有下一阶
				break;
			}
			nextWingRef = (MGPlayerWingRef)GameRoot.getGameRefObjectManager().getManagedObject(nextWingRefId, MGPlayerWingRef.class);
			if (nextWingRef == null) {
				// 没有下一阶
				break;
			}
			crtMaxExp = playerWing.getCrtMaxExp();
			// 已到达最大等级
			if (Integer.MAX_VALUE == crtMaxExp) {
				if (exp > crtMaxExp) {
					exp = crtMaxExp;
				}
				break;
			}
			if (crtMaxExp <= 0) {
				break;
			}

			// 不够升级
			if (exp < crtMaxExp) {
				break;
			}

			// 去掉上一个翅膀的属性
			MGPlayerWingComponent playerWingComponent = (MGPlayerWingComponent)player.getTagged(MGPlayerWingComponent.Tag);
			playerWingComponent.getWingEffectMgr().detachAndSnapshot(playerWing);
			
			// 升级，换一个翅膀REF
			playerWing.setPlayerWingRef(nextWingRef);
			
			sendGameEventMessage(wingManager.getPlayer(), nextWingRef, playerWingRef);

			exp = exp - crtMaxExp;
			// 增加玩家属性，速度 并通知客户端
			playerWingComponent.getWingEffectMgr().attach(playerWing);
			
			playerWing.broadcastWingModelProperty(player);
		} while (true);

		playerWing.setExp(exp);
	}
	
	public void sendGameEventMessage(Player player, MGPlayerWingRef crtWingRef, MGPlayerWingRef lastWingRef) {
		// 判断是否升阶
		if (crtWingRef == null || lastWingRef == null) {
			return;
		}
		if (crtWingRef.getCrtWingStageLevel() > lastWingRef.getCrtWingStageLevel()) {
			MGWingLevelUp_GE MGWingLevelUp_GE = new MGWingLevelUp_GE(crtWingRef.getId());
			
			GameEvent<MGWingLevelUp_GE> ge = (GameEvent<MGWingLevelUp_GE>) GameEvent.getInstance(MGPlayerWingComponent.MGWingLevelUp_GE_ID, MGWingLevelUp_GE);
			player.handleGameEvent(ge);

			MGPlayerWingComponent playerWingComponent = (MGPlayerWingComponent) player.getTagged(MGPlayerWingComponent.Tag);
			playerWingComponent.sendChineseModeGameEventMessage();
		}
	}
}
