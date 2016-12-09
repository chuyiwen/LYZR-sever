package sophia.mmorpg.player.mount.mountModule;

import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.gameEvent.ChineseModeQuest_GE;
import sophia.mmorpg.player.mount.Mount;
import sophia.mmorpg.player.mount.MountManager;
import sophia.mmorpg.player.mount.MountRef;
import sophia.mmorpg.player.mount.PlayerMountComponent;
import sophia.mmorpg.player.mount.PlayerMountFacade;
import sophia.mmorpg.player.mount.gameEvent.MGMountLevelUp_GE;
import sophia.mmorpg.player.quest.ref.order.QuestChineseOrderDefines;

import com.google.common.base.Strings;

public class LevelModule extends AbstractModule {

	public static final String ChineseModeQuest_GE_Id = ChineseModeQuest_GE.class.getSimpleName();

	/**
	 * @param owner
	 * 
	 */
	public LevelModule(MountManager owner) {
		super(owner);
	}

	public void rewardExp() {
		Mount crtMount = owner.getCrtMount();
		long exp = crtMount.getExp();
		if (exp < 0) {
			return;
		}
		MountRef crtMountRef = crtMount.getMountRef();
		if (crtMountRef == null) {
			return;
		}
		String nextMountRefId;
		MountRef nextMountRef;
		long crtMaxExp = 0;
		Player player = owner.getPlayer();
		do {
			nextMountRefId = crtMount.getNextRefId();
			if (Strings.isNullOrEmpty(nextMountRefId.trim())) {
				// 没有下一阶
				break;
			}
			nextMountRef = GameRoot.getGameRefObjectManager().getManagedObject(nextMountRefId, MountRef.class);
			if (nextMountRef == null) {
				// 没有下一阶
				break;
			}
			crtMaxExp = crtMount.getCrtMaxExp();
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

			// 去掉上一个坐骑的属性
			player.getPlayerMountComponent().getMountEffectMgr().detachEffect(crtMount);
			if (player.isOnMount()) {
				// 玩家是上马状态
				player.getPlayerMountComponent().getMountEffectMgr().detach(crtMount);
			}

			// 升级，换一个坐骑REF
			crtMount.setMountRef(nextMountRef);

			sendGameEventMessage(owner.getPlayer(), nextMountRef, crtMountRef);

			exp = exp - crtMaxExp;
			// 增加玩家属性，速度 并通知客户端
			player.getPlayerMountComponent().getMountEffectMgr().attachEffect(crtMount);
			if (player.isOnMount()) {
				// 玩家是上马状态
				player.getPlayerMountComponent().getMountEffectMgr().attachSpeedEffect(crtMount);
			}
			PlayerMountFacade.changePropertyAndBroadcast(player);
		} while (true);

		crtMount.setExp(exp);
	}

	public void sendGameEventMessage(Player player, MountRef crtMountRef, MountRef lastMountRef) {
		// 判断是否升阶
		if (crtMountRef == null || lastMountRef == null) {
			return;
		}
		if (crtMountRef.getStageLevel() > lastMountRef.getStageLevel()) {
			MGMountLevelUp_GE MGMountLevelUp_GE = new MGMountLevelUp_GE(crtMountRef.getId());
			GameEvent<MGMountLevelUp_GE> ge = (GameEvent<MGMountLevelUp_GE>) GameEvent.getInstance(PlayerMountComponent.MGMountLevelUp_GE_ID, MGMountLevelUp_GE);
			player.handleGameEvent(ge);

			sendChineseModeGameEventMessage();
		}
	}

	public void sendChineseModeGameEventMessage() {
		Mount crtMount = owner.getCrtMount();
		if (crtMount == null) {
			return;
		}
		ChineseModeQuest_GE chineseModeQuest_GE = new ChineseModeQuest_GE();
		chineseModeQuest_GE.setType(ChineseModeQuest_GE.CourseType);
		chineseModeQuest_GE.setOrderEventId(QuestChineseOrderDefines.MountLevelUp);
		chineseModeQuest_GE.setNumber(crtMount.getMountRef().getStageLevel());
		chineseModeQuest_GE.setCount(crtMount.getMountRef().getStartLevel());
		GameEvent<ChineseModeQuest_GE> event = GameEvent.getInstance(ChineseModeQuest_GE_Id, chineseModeQuest_GE);
		owner.getPlayer().handleGameEvent(event);
	}

}
