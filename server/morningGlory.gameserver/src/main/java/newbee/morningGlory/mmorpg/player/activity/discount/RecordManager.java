package newbee.morningGlory.mmorpg.player.activity.discount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import newbee.morningGlory.code.MGErrorCode;
import newbee.morningGlory.mmorpg.player.store.MGPlayerShopComponent;
import newbee.morningGlory.mmorpg.player.store.event.G2C_Store_BuyItemResp;
import newbee.morningGlory.mmorpg.player.store.event.StoreEventDefines;
import newbee.morningGlory.mmorpg.store.ref.DiscountItemRef;

import org.apache.log4j.Logger;

import sophia.foundation.communication.core.MessageFactory;
import sophia.game.GameRoot;
import sophia.mmorpg.Mail.Mail;
import sophia.mmorpg.Mail.MailMgr;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.player.money.PlayerMoneyComponent;
import sophia.mmorpg.utils.RuntimeResult;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;

public class RecordManager {
	private static Logger logger = Logger.getLogger(RecordManager.class);
	
	private static final short NO_LIMIT_NUM = -1;

	// <discountRefId, number>
	private static Map<String, Short> allLimitItems = new HashMap<String, Short>();

	// <discountRefId, <playerId, alradyBuyNum>>
	private static Map<String, ConcurrentHashMap<String, Short>> personalLimitItems = new HashMap<String, ConcurrentHashMap<String, Short>>();

	private static RecordManager instance = new RecordManager();

	public static RecordManager getInstance() {
		return instance;
	}

	public static synchronized void clearAll() {
		allLimitItems.clear();
		personalLimitItems.clear();
	}

	public static synchronized void initilaizeAllLimitCount(Map<String, DiscountItemRef> itemRefData) {
		clearAll();

		for (Entry<String, DiscountItemRef> entry : itemRefData.entrySet()) {
			String discountRefId = entry.getKey();
			DiscountItemRef discountItemRef = entry.getValue();
			
			short itemLimitNum = discountItemRef.getItemLimitNum();
			
			allLimitItems.put(discountRefId, itemLimitNum);

			if (logger.isInfoEnabled()) {
				logger.info("allLimit initialize discountRefId = " + discountRefId);
			}
		}
	}

	public static synchronized void restore() {

		long saveTime = DiscountPersistenceHelper.getSaveTime();
		long now = System.currentTimeMillis();
		boolean SameDiscountPeriod = DiscountTimeMgr.isSamePeriod(saveTime, now);

		if (saveTime == 0) {
			if (logger.isDebugEnabled()) {
				logger.debug("insert discount data into database!");
			}
			
			Map<String, Short> copyAllLimitItems = getCopyAllLimitItems();
			Map<String, ConcurrentHashMap<String, Short>> personalLimitItems = getPersonalLimitItems();
			DiscountPersistenceHelper.insertDataTo(copyAllLimitItems, personalLimitItems);
		}

		if (SameDiscountPeriod) {
			if (logger.isDebugEnabled()) {
				logger.debug("selectSaveTime and now is same period!");
			}
			
			Map<String, Short> resotreAllLimitData = DiscountPersistenceHelper.resotreAllLimitData();
			setAllLimitItems(resotreAllLimitData);
			
			Map<String, ConcurrentHashMap<String, Short>> restorePersonalLimitData = DiscountPersistenceHelper.restorePersonalLimitData();
			setPersonalLimitItems(restorePersonalLimitData);

			DiscountRefDataManager.restoreRefData(resotreAllLimitData);
		}
		
	}

	public static synchronized Map<String, Short> getCopyAllLimitItems() {
		Map<String, Short> copyAllLimitItems = new HashMap<String, Short>();

		for (Entry<String, Short> entry : allLimitItems.entrySet()) {
			String discountRefId = entry.getKey();
			Short number = entry.getValue();
			copyAllLimitItems.put(discountRefId, number);
		}

		return copyAllLimitItems;
	}

	/**
	 * 个人剩余可买数量
	 * 
	 * @param playerId
	 * @param discountRefId
	 * @return
	 */
	public static synchronized short getPersonalCanBuyCount(String playerId, String discountRefId) {
		ConcurrentHashMap<String, Short> map = personalLimitItems.get(discountRefId);

		short maxPersonalLimitNum = DiscountRefDataManager.getMaxLimitNum(discountRefId, DiscountRefDataManager.Personal_Limit_Type);
		if (maxPersonalLimitNum < 0) {
			return RecordManager.NO_LIMIT_NUM;
		}

		if (map == null) {
			return maxPersonalLimitNum;
		}
		
		Short alreadyBuyCount = map.get(playerId);
		if (alreadyBuyCount == null) {
			return maxPersonalLimitNum;
			
		} else {
			return (short) (maxPersonalLimitNum - alreadyBuyCount);
		}
	}

	public void discountBuyItem(String discountRefId, short count, Player player) {
		Preconditions.checkArgument(player != null);
		short actionEventId = StoreEventDefines.C2G_Store_BuyItemReq;

		RuntimeResult runtimeResult = buyDiscountItem(discountRefId, count, player);

		G2C_Store_BuyItemResp res = MessageFactory.getConcreteMessage(StoreEventDefines.G2C_Store_BuyItemResp);

		if (runtimeResult.isError()) {
			if (runtimeResult.getApplicationCode() == MGErrorCode.CODE_STORE_NUM_PERSON_LIMIT) {
				res.setTemp(MGPlayerShopComponent.NotEnought);
			} else if (runtimeResult.getApplicationCode() == MGErrorCode.CODE_STORE_NUM_PERSON_LIMIT) {
				res.setTemp(MGPlayerShopComponent.NotEnought);
			} else {
				res.setTemp(MGPlayerShopComponent.Fail);
			}

			ResultEvent.sendResult(player.getIdentity(), actionEventId, runtimeResult.getCode());
		} else {
			res.setTemp(MGPlayerShopComponent.Succeed);
		}

		GameRoot.sendMessage(player.getIdentity(), res);

	}

	public RuntimeResult buyDiscountItem(String discountRefId, short count, Player player) {
		RuntimeResult runtimeResult = RuntimeResult.OK();

		if (count <= 0) {
			return RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_STORE_INVALID_PARAM);
		}

		if (!DiscountRefDataManager.isInSalePeriod(discountRefId)) {
			return RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_STORE_OUT_OF_DATE);
		}

		DiscountItemRef discountItemRef = DiscountRefDataManager.getCrtDiscountItemRef(discountRefId);
		if (discountItemRef == null) {
			return RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_STORE_OUT_OF_DATE);
		}

		// 判断金钱是否足够
		runtimeResult = DiscountRefDataManager.ifMoneyEnough(discountItemRef, player, count);
		if (runtimeResult.isError()) {
			return runtimeResult;
		}

		byte retVal = recordCount(player, discountItemRef, count);
		if (retVal == 1) {
			return RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_STORE_NUM_PERSON_LIMIT);
		}

		if (retVal == 2) {
			return RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_STORE_NUM_PERSON_LIMIT);
		}

		// 扣除金钱
		payMoney(discountItemRef, player, count);

		// 获得商品
		sendItem(player, discountItemRef, count);

		return runtimeResult;
	}

	private void payMoney(DiscountItemRef discountItemRef, Player player, int count) {

		PlayerMoneyComponent playerMoneyCompoent = player.getPlayerMoneyComponent();

		int newSaleCurrency = discountItemRef.getNewSaleCurrency();
		int newSalePrice = discountItemRef.getNewSalePrice();

		int needUnbinedGold = 0;
		int needGold = 0;
		int needBinedGold = 0;

		// saleCurrency 1--unbindedGold 2- gold
		if (1 == newSaleCurrency) {
			needUnbinedGold = newSalePrice * count;
		}

		if (2 == newSaleCurrency) {
			needGold = newSalePrice * count;
		}

		playerMoneyCompoent.subBindGold(needUnbinedGold, ItemOptSource.Store);
		playerMoneyCompoent.subGold(needGold, ItemOptSource.Store);
		playerMoneyCompoent.subBindGold(needBinedGold, ItemOptSource.Store);
	}

	private byte recordCount(Player player, DiscountItemRef discountItemRef, short count) {
		byte result = 0;
		String discountRefId = discountItemRef.getId();

		boolean isAllLimitItem = DiscountRefDataManager.isAllLimitItem(discountRefId);
		boolean isPersonalLimitItem = DiscountRefDataManager.isPersonalLimitItem(discountRefId);
		boolean isLimitCountEnough = isLimitCountEnough(discountRefId, count, player.getId());

		if (isAllLimitItem && !isPersonalLimitItem) {
			if (!subAllLimitCount(discountRefId, count)) {
				result = 1;
				return result;
			}
		}

		if (!isAllLimitItem && isPersonalLimitItem) {
			if (!subPersonalLimitCount(discountRefId, count, player.getId())) {

				result = 2;
				return result;
			}
		}

		if (isAllLimitItem && isPersonalLimitItem) {
			if (isLimitCountEnough) {
				synchronized (RecordManager.class) {
					subAllLimitCount(discountRefId, count);
					subPersonalLimitCount(discountRefId, count, player.getId());
				}
			} else {
				if (!isAllLimitCountEnough(discountRefId, count)) {
					result = 1;
					return result;
				} 
				
				if (!isPersonalLimitCountEnough(discountRefId, count, player.getId())) {
					result = 2;
					return result;
				} 
			}
		}

		return result;
	}

	public synchronized boolean subAllLimitCount(String discountRefId, short count) {
		if (!isAllLimitCountEnough(discountRefId, count)) {
			return false;
		}

		short number = getAllLimitRemainCount(discountRefId);
		number = (short) (number - count);
		allLimitItems.put(discountRefId, number);

		return true;
	}

	public synchronized boolean subPersonalLimitCount(String discountRefId, short count, String playerId) {
		if (!isPersonalLimitCountEnough(discountRefId, count, playerId)) {
			return false;
		}

		ConcurrentHashMap<String, Short> map = personalLimitItems.get(discountRefId);
				
		if (map == null) {
			map = new ConcurrentHashMap<>();
			map.put(playerId, count);
			personalLimitItems.put(discountRefId, map);
			
		} else if (map.get(playerId) == null) {
			map.put(playerId, count);
			
		} else {
			short alreadyBuyCount = map.get(playerId);
			map.put(playerId, (short) (alreadyBuyCount + count));
		}

		return true;
	}

	public synchronized boolean isLimitCountEnough(String discountRefId, short count, String playerId) {
		return isPersonalLimitCountEnough(discountRefId, count, playerId) && isAllLimitCountEnough(discountRefId, count);
	}

	public synchronized boolean isPersonalLimitCountEnough(String discountRefId, short count, String playerId) {
		short personalCanBuyCount = getPersonalCanBuyCount(playerId, discountRefId);
		if (personalCanBuyCount < count) {
			return false;
		}

		return true;
	}

	public synchronized boolean isAllLimitCountEnough(String discountRefId, short count) {
		Short number = getAllLimitRemainCount(discountRefId);
		if (number == null) {
			return false;
		}
		
		if (number < count) {
			return false;
		}

		return true;
	}

	public synchronized Short getAllLimitRemainCount(String discountRefId) {
		return allLimitItems.get(discountRefId);
	}

	private void sendItem(Player player, DiscountItemRef discountItemRef, int count) {
		List<ItemPair> itemPairs = new ArrayList<ItemPair>();

		String itemRefId = discountItemRef.getItemRefId();
		byte bindStatus = discountItemRef.getBindStatus();
		ItemPair itemPair = new ItemPair(itemRefId, count, bindStatus);
		itemPairs.add(itemPair);

		if (ItemFacade.isItemBagSlotEnough(player, itemPairs)) {
			ItemFacade.addItem(player, itemPairs, ItemOptSource.Store);
			return;
		}

		String content = new String("由于背包满，打折活动商品改为邮件发送。");
		String json = (new Gson()).toJson(itemPairs);
		MailMgr.sendMailById(player.getId(), content, Mail.gonggao, json, 0, 0, 0);
	}

	public static synchronized Map<String, ConcurrentHashMap<String, Short>> getPersonalLimitItems() {
		return personalLimitItems;
	}

	public static synchronized void setAllLimitItems(Map<String, Short> allLimitItems) {
		RecordManager.allLimitItems = allLimitItems;
	}

	public static synchronized void setPersonalLimitItems(Map<String, ConcurrentHashMap<String, Short>> personalLimitItems) {
		RecordManager.personalLimitItems = personalLimitItems;
	}

}
