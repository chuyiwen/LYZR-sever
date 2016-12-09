/**
 *   Copyright 2013-2015 Sophia
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package sophia.mmorpg.player.mount;

import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.foundation.data.PersistenceObject;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.GameObjectFactory;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.base.sprite.fightSkill.gameevent.AfterAttack_GE;
import sophia.mmorpg.base.sprite.state.FSMStateFactory;
import sophia.mmorpg.base.sprite.state.posture.MountedState;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.core.CDMgr;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.chat.sysytem.SystemPromptFacade;
import sophia.mmorpg.player.exp.gameevent.PlayerLevelUp_GE;
import sophia.mmorpg.player.gameEvent.ChineseModeQuest_GE;
import sophia.mmorpg.player.gameEvent.PlayerDead_GE;
import sophia.mmorpg.player.gameEvent.PlayerRevive_GE;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.mount.event.C2G_Mount_Action;
import sophia.mmorpg.player.mount.event.C2G_Mount_Feed;
import sophia.mmorpg.player.mount.event.C2G_Mount_IsOnMount;
import sophia.mmorpg.player.mount.event.C2G_Mount_List;
import sophia.mmorpg.player.mount.event.G2C_Mount_Feed;
import sophia.mmorpg.player.mount.event.G2C_Mount_IsOnMount;
import sophia.mmorpg.player.mount.event.G2C_Mount_List;
import sophia.mmorpg.player.mount.event.MountEventDefines;
import sophia.mmorpg.player.mount.gameEvent.MGMountLevelUp_GE;
import sophia.mmorpg.player.mount.gameEvent.PlayerMountState_GE;
import sophia.mmorpg.player.quest.ref.order.QuestChineseOrderDefines;
import sophia.mmorpg.stat.StatFunctions;
import sophia.mmorpg.utils.RuntimeResult;

import com.google.common.base.Strings;

/**
 * 玩家-坐骑组件
 */
public final class PlayerMountComponent extends ConcreteComponent<Player> {
	private static final Logger logger = Logger.getLogger(PlayerMountComponent.class);
	private MountManager mountManager = new MountManager();
	private PersistenceObject persisteneceObject;
	private MountEffectMgr mountEffectMgr;
	public static final String MGMountLevelUp_GE_ID = MGMountLevelUp_GE.class.getSimpleName();
	public static final String ChineseModeQuest_GE_Id = ChineseModeQuest_GE.class.getSimpleName();
	private static final String PlayerLevelUp_GE_ID = PlayerLevelUp_GE.class.getSimpleName();
	private static final String Mounts = "mount";
	private Player player;
	private int mountSystemOpenLevel = 40;

	// ==================================================================================================================

	private CDMgr reviveCDMgr = new CDMgr(0);

	/** 默认cd:5秒 **/
	private final static String DEFAULT_CDTIME_KEY = "DEFAULT_CDTIME_KEY";

	/** PVPcd:10秒 **/
	private final static String PVP_CDTIME_KEY = "PVP_CDTIME_KEY";

	private ReadWriteLock cdLock = new ReentrantReadWriteLock();

	// ==================================================================================================================
	@Override
	public void ready() {
		player = getConcreteParent();
		addInterGameEventListener(PlayerLevelUp_GE_ID);
		addInterGameEventListener(ChineseModeQuest_GE_Id);
		addInterGameEventListener(PlayerDead_GE.class.getSimpleName());
		addInterGameEventListener(AfterAttack_GE.class.getSimpleName());
		addInterGameEventListener(PlayerRevive_GE.class.getSimpleName());
		addActionEventListener(MountEventDefines.C2G_Mount_IsOnMount);
		addActionEventListener(MountEventDefines.C2G_Mount_List);
		addActionEventListener(MountEventDefines.C2G_Mount_Feed);
		addActionEventListener(MountEventDefines.C2G_Mount_Action);
		super.ready();
	}

	@Override
	public void suspend() {
		removeInterGameEventListener(PlayerLevelUp_GE_ID);
		removeInterGameEventListener(ChineseModeQuest_GE_Id);
		removeInterGameEventListener(PlayerDead_GE.class.getSimpleName());
		removeInterGameEventListener(AfterAttack_GE.class.getSimpleName());
		removeInterGameEventListener(PlayerRevive_GE.class.getSimpleName());
		removeActionEventListener(MountEventDefines.C2G_Mount_IsOnMount);
		removeActionEventListener(MountEventDefines.C2G_Mount_List);
		removeActionEventListener(MountEventDefines.C2G_Mount_Feed);
		removeActionEventListener(MountEventDefines.C2G_Mount_Action);
		super.suspend();
	}

	public PlayerMountComponent() {

	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {
		if (logger.isDebugEnabled()) {
			logger.debug("handleGameEvent");
		}
		if (event.isId(AfterAttack_GE.class.getSimpleName())) {
			FightSprite attacker = ((AfterAttack_GE) event.getData()).getAttacker();
			FightSprite target = ((AfterAttack_GE) event.getData()).getTarget();
			if (this.getMountManager().getCrtMount() == null) {
				return;
			}
			if (player == attacker) {
				if (logger.isDebugEnabled()) {
					logger.debug("玩家主动发起攻击" + ",targetType=" + target.getGameSpriteType());
				}
				if (Player.class.getSimpleName().equals(target.getGameSpriteType())) {
					// a) 与玩家对打/或者造成伤害，攻击下马,进入战斗状态
					this.playerStartPvp();
				} else {
					// b) 打怪，攻击下马(改成客户端请求的方式(this.playerStartDefault();))
				}
			} else if (player == target) {
				// c) 在战斗状态受到伤害，战斗状态持续时间刷新
				if (Player.class.getSimpleName().equals(attacker.getGameSpriteType())) {
					this.playerRefreshPvp();
				}
			}
		} else if (event.isId(PlayerDead_GE.class.getSimpleName())) {
			boolean down = false;
			if (this.player.isDead()) {
				down = this.playerMount_Down();
				if (down) {
					G2C_Mount_IsOnMount response = MessageFactory.getConcreteMessage(MountEventDefines.G2C_Mount_IsOnMount);
					response.setOnMount(player.getFightSpriteStateMgr().isState(FSMStateFactory.getPostureState(MountedState.MountedState_Id)));
					GameRoot.sendMessage(player.getIdentity(), response);
					PlayerMountFacade.changePropertyAndBroadcast(player);
				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug("玩家死亡通知," + this.player.isDead() + ",down=" + down);
			}
		} else if (event.isId(ChineseModeQuest_GE_Id)) {
			ChineseModeQuest_GE chineseModeQuest_GE = (ChineseModeQuest_GE) event.getData();
			if (chineseModeQuest_GE.getType() == ChineseModeQuest_GE.AcceptType && chineseModeQuest_GE.getOrderEventId() == QuestChineseOrderDefines.MountLevelUp) {
				if (mountManager.getLevelModule() == null) {
					if (logger.isDebugEnabled()) {
						logger.debug("玩家当前没有坐骑");
					}
					return;
				}
				mountManager.getLevelModule().sendChineseModeGameEventMessage();
			}
		} else if (event.isId(PlayerLevelUp_GE_ID)) {
			PlayerLevelUp_GE playerLevelUp_GE = (PlayerLevelUp_GE) event.getData();
			int level = playerLevelUp_GE.getCurLevel();

			if (level >= mountSystemOpenLevel && getMountManager().getCrtMount() == null) {
				String mountRefId = "ride_1";
				MountRef mountRef = (MountRef) GameRoot.getGameRefObjectManager().getManagedObject(mountRefId);
				Mount mount = GameObjectFactory.getMount();
				mount.setMountRef(mountRef);
				mount.setExp(0);
				mount.setId(UUID.randomUUID().toString());
				PlayerMountComponent mountComponent = player.getPlayerMountComponent();
				MountEffectMgr mountEffectMgr = new MountEffectMgr(player);
				mountComponent.setMountEffectMgr(mountEffectMgr);
				MountManager mountManager = mountComponent.getMountManager();
				mountManager.setCrtMount(mount);
				mountManager.setOwner(player);
				mountEffectMgr.firstGet(mount);

				MGMountLevelUp_GE MGMountLevelUp_GE = new MGMountLevelUp_GE(mountRefId);
				GameEvent<MGMountLevelUp_GE> ge = (GameEvent<MGMountLevelUp_GE>) GameEvent.getInstance(PlayerMountComponent.MGMountLevelUp_GE_ID, MGMountLevelUp_GE);
				player.handleGameEvent(ge);

				mountManager.getLevelModule().sendChineseModeGameEventMessage();
			}
		}
		super.handleGameEvent(event);
	}

	// ==================================================================================================================
	@Override
	public void handleActionEvent(ActionEventBase event) {
		if (logger.isDebugEnabled()) {
			logger.debug("enter PlayerMountComponent");
		}
		switch (event.getActionEventId()) {
		case MountEventDefines.C2G_Mount_IsOnMount:
			handle_C2G_Mount_IsOnMount((C2G_Mount_IsOnMount) event);
			break;
		case MountEventDefines.C2G_Mount_List:
			handle_C2G_Mount_List((C2G_Mount_List) event);
			break;
		case MountEventDefines.C2G_Mount_Feed:
			handle_C2G_Mount_Feed((C2G_Mount_Feed) event);
			break;
		case MountEventDefines.C2G_Mount_Action:
			handle_C2G_Mount_Action((C2G_Mount_Action) event);
			break;
		default:
			break;
		}
		super.handleActionEvent(event);
	}

	/**
	 * 上马下马动作请求
	 * 
	 * @param actionEvent
	 */
	private void handle_C2G_Mount_Action(C2G_Mount_Action actionEvent) {
		if (logger.isDebugEnabled()) {
			logger.debug("handle_C2G_Mount_Action");
		}
		Mount crtMount = mountManager.getCrtMount();
		if (crtMount == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("crtMount not exit");
			}
			ResultEvent.sendResult(actionEvent.getIdentity(), actionEvent.getActionEventId(), MMORPGErrorCode.CODE_MOUNT_NOT_EXIST);
			return;
		}
		boolean ok = false;
		if (Mount.DOWN == actionEvent.getActionType()) {
			if (logger.isDebugEnabled()) {
				logger.debug("玩家请求下马，参数为 1 ");
			}
			ok = PlayerMountFacade.playerMount_Down(player);
			this.playerStartDefault();
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("玩家请求上马，参数为  0 ");
			}
			int MMORPGErrorCode = checkCDTime();
			if (MMORPGErrorCode != 0) {
				ResultEvent.sendResult(actionEvent.getIdentity(), actionEvent.getActionEventId(), MMORPGErrorCode);
				return;
			}
			ok = PlayerMountFacade.playerMount_Up(player);
			if (ok) {
				sendMountStateGameEvent(player, Mount.UP);
			}
		}
		G2C_Mount_IsOnMount response = MessageFactory.getConcreteMessage(MountEventDefines.G2C_Mount_IsOnMount);
		response.setOnMount(player.getFightSpriteStateMgr().isState(FSMStateFactory.getPostureState(MountedState.MountedState_Id)));
		GameRoot.sendMessage(actionEvent.getIdentity(), response);
		PlayerMountFacade.changePropertyAndBroadcast(player);

		if (logger.isDebugEnabled()) {
			logger.debug("return successed!");
		}
	}

	public static final String MOUNT_EXP_REF_ID = "item_zuoqiExp";

	/**
	 * 坐骑喂养
	 * 
	 * @param actionEvent
	 */
	private void handle_C2G_Mount_Feed(C2G_Mount_Feed actionEvent) {
		if (logger.isDebugEnabled()) {
			logger.debug("handle_C2G_Mount_Feed");
		}
		Mount crtMount = mountManager.getCrtMount();

		if (crtMount == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("crtMount not exit");
			}
			ResultEvent.sendResult(actionEvent.getIdentity(), actionEvent.getActionEventId(), MMORPGErrorCode.CODE_MOUNT_NOT_EXIST);
			return;
		}
		String itemRefId = actionEvent.getItemRefId();
		if (!MOUNT_EXP_REF_ID.equals(itemRefId)) {
			if (logger.isDebugEnabled()) {
				logger.debug("吃错药了,itemRefId wrong!!：" + itemRefId);
			}
			ResultEvent.sendResult(actionEvent.getIdentity(), actionEvent.getActionEventId(), MMORPGErrorCode.CODE_MOUNT_ITEMREFID_WRONG);
			return;
		}
		int num = actionEvent.getNum();
		if (num > 100000 | num <= 0) {
			if (logger.isDebugEnabled()) {
				logger.debug("num wrong!! num:" + num);
			}
			ResultEvent.sendResult(actionEvent.getIdentity(), actionEvent.getActionEventId(), MMORPGErrorCode.CODE_MOUNT_NUM_WRONG);
			return;
		}

		int oldStageLevel = crtMount.getMountRef().getStageLevel();

		RuntimeResult result = player.getItemBagComponent().useItem(itemRefId, num, ItemOptSource.Monut);
		int errorCode = result.getCode();
		if (errorCode != RuntimeResult.OKResult) {
			if (logger.isDebugEnabled()) {
				logger.debug("eat wrong!! errorCode:" + errorCode);
			}
			ResultEvent.sendResult(actionEvent.getIdentity(), actionEvent.getActionEventId(), MMORPGErrorCode.CODE_MOUNT_FEED_WRONG);
			return;
		}
		// 暴击的倍率
		String detail = result.getDetails();
		int baoJiRate = 1;
		if (!Strings.isNullOrEmpty(detail.trim())) {
			try {
				baoJiRate = Integer.valueOf(detail);
			} catch (Exception e) {
				logger.error("", e);
				baoJiRate = 1;
			}
		}
		// 重新获取一次crtMount（有可能升级）
		crtMount = mountManager.getCrtMount();
		// 坐骑日志
		StatFunctions.mountStat(player, crtMount.getCrtRefId(), crtMount.getMountRef().getName(), crtMount.getMountRef().getStartLevel(), crtMount.getExp());
		int stageLevel = crtMount.getMountRef().getStageLevel();
		String mountName = crtMount.getMountRef().getName();
		if (stageLevel > 5 && stageLevel > oldStageLevel) {
			SystemPromptFacade.broadLevelUpMount(player.getName(), getConcreteParent().getId(), stageLevel + "", mountName, Mounts);
		}
		G2C_Mount_Feed response = MessageFactory.getConcreteMessage(MountEventDefines.G2C_Mount_Feed);
		response.setCrtMount(crtMount);
		response.setBaoJi(baoJiRate);
		GameRoot.sendMessage(actionEvent.getIdentity(), response);
		if (logger.isDebugEnabled()) {
			logger.debug("return successed!");
		}
	}

	/**
	 * 查询坐骑状态
	 * 
	 * @param actionEvent
	 */
	private void handle_C2G_Mount_List(C2G_Mount_List actionEvent) {
		if (logger.isDebugEnabled()) {
			logger.debug("handle_C2G_Mount_List");
		}
		Mount crtMount = mountManager.getCrtMount();
		if (crtMount == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("crtMount not exit");
			}
			return;
		}
		G2C_Mount_List response = MessageFactory.getConcreteMessage(MountEventDefines.G2C_Mount_List);
		response.setCrtMount(crtMount);
		response.setOnMount(this.player.getFightSpriteStateMgr().isState(FSMStateFactory.getPostureState(MountedState.MountedState_Id)));
		GameRoot.sendMessage(actionEvent.getIdentity(), response);
		if (logger.isDebugEnabled()) {
			logger.debug("return successed!");
		}
	}

	/**
	 * 查询是否上马中
	 * 
	 * @param actionEvent
	 */
	private void handle_C2G_Mount_IsOnMount(C2G_Mount_IsOnMount actionEvent) {
		if (logger.isDebugEnabled()) {
			logger.debug("handle_C2G_Mount_IsOnMount");
		}
		Mount crtMount = mountManager.getCrtMount();
		if (crtMount == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("crtMount not exit");
			}
			ResultEvent.sendResult(actionEvent.getIdentity(), actionEvent.getActionEventId(), MMORPGErrorCode.CODE_MOUNT_NOT_EXIST);
			return;
		}
		G2C_Mount_IsOnMount response = MessageFactory.getConcreteMessage(MountEventDefines.G2C_Mount_IsOnMount);
		response.setOnMount(player.getFightSpriteStateMgr().isState(FSMStateFactory.getPostureState(MountedState.MountedState_Id)));
		GameRoot.sendMessage(actionEvent.getIdentity(), response);
		if (logger.isDebugEnabled()) {
			logger.debug("return successed!");
		}
	}

	// ==================================================================================================================
	public MountManager getMountManager() {
		return mountManager;
	}

	public void setPersisteneceObject(PersistenceObject persisteneceObject) {
		this.persisteneceObject = persisteneceObject;
	}

	public PersistenceObject getPersisteneceObject() {
		return persisteneceObject;
	}

	public MountEffectMgr getMountEffectMgr() {
		return mountEffectMgr;
	}

	public void setMountEffectMgr(MountEffectMgr mountEffectMgr) {
		this.mountEffectMgr = mountEffectMgr;
	}

	/**
	 * 下马
	 */
	public boolean playerMount_Down() {
		boolean down = PlayerMountFacade.playerMount_Down(this.player);
		if (down) {
			PlayerMountFacade.changePropertyAndBroadcast(this.player);
			sendMountStateGameEvent(player, Mount.DOWN);
		}
		return down;
	}

	// ==================================================================================================================

	/**
	 * PVP状态：开始计时
	 */
	public void playerStartPvp() {
		this.cdLock.writeLock().lock();
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("PVP状态：开始计时");
			}
			this.refreshCDTime(PlayerMountComponent.PVP_CDTIME_KEY);
		} catch (Exception e) {
			logger.error(e);
		} finally {
			this.cdLock.writeLock().unlock();
		}
	}

	/**
	 * PVP状态：持续刷新
	 */
	public void playerRefreshPvp() {
		this.cdLock.writeLock().lock();
		try {
			boolean start = checkCDTimeStart(PlayerMountComponent.PVP_CDTIME_KEY);
			if (start) {
				this.refreshCDTime(PlayerMountComponent.PVP_CDTIME_KEY);
				if (logger.isDebugEnabled()) {
					logger.debug("PVP状态：持续刷新");
				}
				return;
			}
			if (logger.isDebugEnabled()) {
				logger.debug("PVP状态：未开始");
			}
		} catch (Exception e) {
			logger.error(e);
		} finally {
			this.cdLock.writeLock().unlock();
		}
	}

	/**
	 * 默认状态：开始计时(不存在持续刷新的问题) 默认（玩家主动下马，玩家对怪物发动攻击）
	 */
	public void playerStartDefault() {
		this.cdLock.writeLock().lock();
		try {
			boolean start = checkCDTimeStart(PlayerMountComponent.DEFAULT_CDTIME_KEY);
			if (!start) {
				if (logger.isDebugEnabled()) {
					logger.debug("PVE状态：开始计时");
				}
				this.refreshCDTime(PlayerMountComponent.DEFAULT_CDTIME_KEY);
			}
		} catch (Exception e) {
			logger.error(e);
		} finally {
			this.cdLock.writeLock().unlock();
		}
	}

	private int checkCDTime() {
		cdLock.readLock().lock();
		try {
			// 上马检查
			if (!this.checkCDTime(PlayerMountComponent.DEFAULT_CDTIME_KEY)) {
				if (logger.isDebugEnabled()) {
					logger.debug("默认上马CD 未冷却");
				}
				return MMORPGErrorCode.CODE_MOUNT_CD;
			}
			// 战斗状态cd检查
			if (!this.checkCDTime(PlayerMountComponent.PVP_CDTIME_KEY)) {
				if (logger.isDebugEnabled()) {
					logger.debug("战斗状态上马CD 未冷却");
				}
				return MMORPGErrorCode.CODE_MOUNT_PVP_CD;
			}
			return 0;
		} catch (Exception e) {
			logger.error(e);
			return 0;
		} finally {
			cdLock.readLock().unlock();
		}
	}

	// ======================================================================================================
	private void refreshCDTime(String CDType) {
		long cdtime = getCdTime(CDType);
		if (!reviveCDMgr.isCDStarted(CDType)) {
			reviveCDMgr.startCD(CDType, cdtime);
		}
		reviveCDMgr.update(CDType);
	}

	private boolean checkCDTimeStart(String CDType) {
		if (!this.reviveCDMgr.isCDStarted(CDType)) {
			return false;
		}
		if (!this.reviveCDMgr.isOutOfCD(CDType)) {
			return true;
		}
		return false;
	}

	private boolean checkCDTime(String CDType) {
		if (this.reviveCDMgr.isCDStarted(CDType) && !this.reviveCDMgr.isOutOfCD(CDType)) {
			return false;
		}
		return true;

	}

	private long getCdTime(String CDType) {
		switch (CDType) {
		case PlayerMountComponent.DEFAULT_CDTIME_KEY:
			/** 5秒 **/
			return 5 * 1000;
		case PlayerMountComponent.PVP_CDTIME_KEY:
			/** 10秒 **/
			return 10 * 1000;
		default:
			return 0;
		}
	}

	private void sendMountStateGameEvent(Player player, byte mountState) {
		PlayerMountState_GE mountState_GE = new PlayerMountState_GE();
		GameEvent<PlayerMountState_GE> event = GameEvent.getInstance(PlayerMountState_GE.class.getSimpleName(), mountState_GE);
		player.handleGameEvent(event);
		GameEvent.pool(event);
	}
}
