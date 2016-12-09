package sophia.mmorpg.pluck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.base.scene.mgr.LootMgrComponent;
import sophia.mmorpg.base.scene.mgr.PluckMgrComponent;
import sophia.mmorpg.base.sprite.NonFightSprite;
import sophia.mmorpg.base.sprite.SpriteTypeDefine;
import sophia.mmorpg.base.sprite.aoi.SpriteAOIComponent;
import sophia.mmorpg.base.sprite.state.action.PluckingState;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.code.MMORPGSuccessCode;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.player.scene.event.SceneEventDefines;
import sophia.mmorpg.pluck.gameEvent.PluckSuccess_GE;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class Pluck extends NonFightSprite implements Comparable<Pluck> {
	private static Logger logger = Logger.getLogger(Pluck.class);
	public static final String Pluck_GameSPrite_Type = Pluck.class.getSimpleName();
	
	public static final String PluckSuccess_GE_ID = PluckSuccess_GE.class.getSimpleName();
	
	private PluckRef pluckRef;
	// playerIds
	private List<String> ownerList = new ArrayList<>();
	// playerId, 
	private Map<String, Long> playerPluckBeginTime = new HashMap<>();
	
	private long pluckRefreshBeginTime;

	public Pluck() {
		setId(UUID.randomUUID().toString());
		registComponents();
	}

	public Pluck(PluckRef pluckRef) {
		this.pluckRef = pluckRef;
		setId(UUID.randomUUID().toString());
		registComponents();
	}

	public synchronized void clear() {
		pluckRefreshBeginTime = 0L;
		playerPluckBeginTime.clear();
		this.ownerList.clear();
	}

	@SuppressWarnings("unchecked")
	public void registComponents() {
		setAoiComponent((SpriteAOIComponent<Pluck>) createComponent(SpriteAOIComponent.class));
	}

	@Override
	public String getGameSpriteType() {
		return Pluck_GameSPrite_Type;
	}

	@Override
	public byte getSpriteType() {
		return SpriteTypeDefine.GameSprite_Pluck;
	}

	@Override
	public String toString() {
		return "Pluck [refId=" + pluckRef.getId() + " name=" + name + ", crtScene=" + crtScene + ", crtPosition=" + crtPosition + ", getId()=" + getId() + "]";
	}

	public PluckRef getPluckRef() {
		return pluckRef;
	}

	public void setPluckRef(PluckRef pluckRef) {
		this.pluckRef = pluckRef;
	}

	public synchronized boolean isOwnerListEmpty() {
		return this.ownerList.isEmpty();
	}
	
	public synchronized boolean isOwner(Player player) {
		return this.ownerList.contains(player.getId());
	}
	
	private synchronized boolean addOwner(String playerId) {
		if (logger.isDebugEnabled()) { 
			logger.debug("add owner: ownerId = " + playerId);
		}
		
		boolean ret = this.ownerList.add(playerId);
		if (ownerList.size() > 100 && logger.isInfoEnabled()) {
			logger.info("addOwner player count more than 100");
		}
		
		return ret;
	}
	
	private synchronized boolean removeOwner(String playerId) {
		if (logger.isDebugEnabled()) {
			logger.debug("remove owner: ownerId = " + playerId);
		}
		
		return this.ownerList.remove(playerId);
	}
	
	private synchronized void addPlayerPluckBeginTime(String playerId, long millis) {
		this.playerPluckBeginTime.put(playerId, millis);
	}
	
	private synchronized Long getPlayerPluckBeginTime(String playerId) {
		return this.playerPluckBeginTime.get(playerId);
	}
	
	private synchronized void removePlayerPluckBeginTime(String playerId) {
		this.playerPluckBeginTime.remove(playerId);
	}

	public synchronized long getPluckRefreshBeginTime() {
		return pluckRefreshBeginTime;
	}

	public synchronized void setPluckRefreshBeginTime(long pluckRefreshBeginTime) {
		this.pluckRefreshBeginTime = pluckRefreshBeginTime;
	}
	
	public synchronized int beginPluck(Player player) {
		if (getPluckNeedLevel() > player.getExpComponent().getLevel()) {
			return MMORPGErrorCode.CODE_PLUCK_NOT_ENOUGHT_LEVEL;
		}
		
		if (!isSharePluck() && !isOwnerListEmpty()) {
			return MMORPGErrorCode.CODE_PLUCK_BEING_PLUCKED;
		}
		
		if (!isOwner(player)) {
			String playerId = player.getId();
			long now = System.currentTimeMillis();
			addOwner(playerId);
			addPlayerPluckBeginTime(playerId, now);
			player.changeState(PluckingState.PluckingState_Id);
		}
		
		return MMORPGSuccessCode.CODE_SUCCESS;
	}
	
	public synchronized void interruptPluck(Player player) {
		removePlayerPluckBeginTime(player.getId());
		removeOwner(player.getId());
		player.cancelState(PluckingState.PluckingState_Id);
	}
	
	public synchronized void removeSuccessPluckPlayer() {
		if (isOwnerListEmpty()) {
			return;
		}
		
		Iterator<String> iterator = this.ownerList.iterator();
		while(iterator.hasNext()) {
			String playerId = iterator.next();
			Long beginPluckTime = getPlayerPluckBeginTime(playerId);
			Player player = getCrtScene().getPlayerMgrComponent().getPlayer(playerId);
			if (beginPluckTime == null || player == null) {
				removePlayerPluckBeginTime(playerId);
				iterator.remove();
				continue;
			}
			
			long now = System.currentTimeMillis();
			if (now - beginPluckTime > this.getPluckTime() * 1000) {
				removePlayerPluckBeginTime(playerId);
				iterator.remove();
				PluckMgrComponent pluckMgrComponent = getCrtScene().getPluckMgrComponent();
				pluckMgrComponent.leaveWorld(this);
				successPluck(player);
			}
		}
	}
	
	public void successPluck(Player player) {
		if (logger.isDebugEnabled()) {
			logger.debug("采集成功 , pluck=" + this + ", player=" + player);
		}
		player.cancelState(PluckingState.PluckingState_Id);
		if (getOutputType() == PluckConst.NormalOutput) {
			normalOutput(player);
			return;
		} 
		
		outputByLoot(player);
	}
	
	private void outputByLoot(Player player) {
		LootMgrComponent lootMgrComponent = getCrtScene().getLootMgrComponent();
		List<ItemPair> itemPairs = pluckRef.getItemPairs();
		lootMgrComponent.dropItemPair(getCrtPosition(), itemPairs, player);
		List<ItemPair> propertyItemPairs = pluckRef.getPropertyItemPairs();
		if (propertyItemPairs == null) {
			return;
		}

		sendItemPair(player, propertyItemPairs);
	}

	private void normalOutput(Player player) {
		List<ItemPair> allItemPais = pluckRef.getAllItemPais();
		if (allItemPais.isEmpty()) {
			return;
		}

		sendItemPair(player, allItemPais);
	}
	
	private void sendItemPair(Player player, List<ItemPair> itemPairs) {
		if (ItemFacade.addItem(player, itemPairs, ItemOptSource.Pluck).isOK()) {
			sendGameEventMessage(player, itemPairs);
			return;
		} 
		
		ResultEvent.sendResult(player.getIdentity(), SceneEventDefines.C2G_Scene_StartoPluck, MMORPGErrorCode.CODE_ITEM_FULL);
	}
	
	private void sendGameEventMessage(Player player, List<ItemPair> itemPairs) {
		logger.debug("采集物品入包通知");
		PluckSuccess_GE pluckSuccess_GE = new PluckSuccess_GE();
		pluckSuccess_GE.setPluckRefId(getPluckRef().getId());
		pluckSuccess_GE.setItemPairs(itemPairs);
		pluckSuccess_GE.setPluckType(getType());
		GameEvent<?> ge = GameEvent.getInstance(PluckSuccess_GE_ID, pluckSuccess_GE);
		player.handleGameEvent(ge);
	}
	
	/**
	 * 玩家被攻击的时候采集是否可以被打断 true 被攻击时被打断 false 被攻击时不被打断
	 * 
	 * @param pluck
	 * @return
	 */
	public boolean canInterrupteed() {
		return getPluckBehaviour() == PluckConst.Pluck_Behavior_Attacked_Interrupt;
	}

	/**
	 * 采集物的类型
	 * 
	 * @return
	 */
	public byte getPluckType() {
		return MGPropertyAccesser.getIsPluckNpc(this.getPluckRef().getProperty());
	}

	/**
	 * 采集需要时间
	 * @return
	 */
	public int getPluckTime() {
		return MGPropertyAccesser.getPluckTime(this.getPluckRef().getProperty());
	}

	/**
	 * 采集物刷新需要时间
	 * 
	 * @return
	 */
	public int getPluckRefreshTime() {
		return MGPropertyAccesser.getPluckRefreshTime(this.getPluckRef().getProperty());
	}

	/**
	 * 采集所需玩家等级
	 * 
	 * @return
	 */
	public int getPluckNeedLevel() {
		return MGPropertyAccesser.getPluckLevel(this.getPluckRef().getProperty());
	}

	/**
	 * 采集物采集模式
	 * 
	 * @return
	 */
	public int getPluckMode() {
		return MGPropertyAccesser.getPluckMode(this.getPluckRef().getProperty());
	}

	/**
	 * 玩家被攻击时，能否被打断
	 * 
	 * @return
	 */
	public byte getPluckBehaviour() {
		return MGPropertyAccesser.getPluckBehavior(this.getPluckRef().getProperty());
	}

	/**
	 * 采集物级别类型:低级、中级、高级
	 * @return
	 */
	public byte getType() {
		return MGPropertyAccesser.getPluckType(this.pluckRef.getProperty());
	}
	
	/**
	 * 采集物共享类型
	 * @return
	 */
	public byte getShareType() {
		return MGPropertyAccesser.getShareType(this.pluckRef.getProperty());
	}
	
	public boolean isSharePluck() {
		return this.getShareType() == PluckConst.MultiShareType;
	}

	/**
	 * 产出方式
	 * @return
	 */
	public byte getOutputType() {
		return MGPropertyAccesser.getOutputType(this.pluckRef.getProperty());
	}
	
	@Override
	public int compareTo(Pluck o) {
		return (int) (this.pluckRefreshBeginTime - o.pluckRefreshBeginTime) / 1000;
	}

}
