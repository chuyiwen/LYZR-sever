package newbee.morningGlory.mmorpg.store;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import newbee.morningGlory.MorningGloryContext;
import newbee.morningGlory.code.MGErrorCode;
import newbee.morningGlory.mmorpg.store.ref.MallItemRef;
import newbee.morningGlory.mmorpg.store.ref.ShopItemRef;
import newbee.morningGlory.mmorpg.store.ref.StoreItemRefMgr;
import newbee.morningGlory.stat.MGStatFunctions;

import org.apache.commons.lang3.StringUtils;

import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.gameEvent.ChineseModeQuest_GE;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.player.money.PlayerMoneyComponent;
import sophia.mmorpg.player.quest.ref.order.QuestChineseOrderDefines;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.RuntimeResult;

public final class StoreMgr {

	private int listVersion = 1000;

	private StoreItemRefMgr storeItemRefMgr = MorningGloryContext.getStoreItemRefMgr();

	private Map<String, MallItemRef> mallItemRefList = new HashMap<>();

	private Map<String, ShopItemRef> shopItemRefList = new HashMap<>();

	public static final String DISCOUNT_STR = "discount";
	public static final String MALL_STR = "mall";
	public static final String EQUIP_STR = "shop_equip";// 神兵兑换商店字段

	public static final byte Mall_type = 1;
	public static final byte Shop_type = 2;
	public static final byte Discount_type = 3;

	public static final byte UnLimitItem = 0;
	public static final byte PersonalLimitItem = 1;
	public static final byte ServerLimitItem = 2;

	public static final String ChineseModeQuest_GE_Id = ChineseModeQuest_GE.class.getSimpleName();

	public StoreMgr() {
	}

	public static byte getStoreType(String type) {
		if (StringUtils.equals(type, MALL_STR)) {
			return Mall_type;
		} else if (StringUtils.equals(type, DISCOUNT_STR)) {
			return Discount_type;
		} else {
			return Shop_type;
		}
	}

	/**
	 * 获取商城Ref列表
	 * 
	 * @return
	 */
	public Map<String, MallItemRef> getMallItemList() {
		return mallItemRefList;
	}

	public void initMallItemList() {
		mallItemRefList = new LinkedHashMap<String, MallItemRef>(StoreItemRefMgr.MallItemList);
	}

	/**
	 * 获取商店Ref列表
	 * 
	 * @return
	 */
	public Map<String, ShopItemRef> getShopItemList() {
		return shopItemRefList;
	}

	public void initShopItemList() {
		shopItemRefList = new HashMap<String, ShopItemRef>(StoreItemRefMgr.ShopItemList);
	}

	/**
	 * 获取全服限购物品
	 * 
	 * @return
	 */
	public Map<String, Short> getAllLimitList() {
		return StoreItemRefMgr.AllLimit;
	}

	/**
	 * 获取个人限购物品列表
	 * 
	 * @param player
	 * @return
	 */
	public Map<String, Short> getPersonalLimitList(Player player) {
		Map<String, ConcurrentHashMap<String, Short>> origenLimit = new HashMap<String, ConcurrentHashMap<String, Short>>(StoreItemRefMgr.personalLimit);
		Map<String, Short> playerLimit = new HashMap<>();
		Set<String> keys = origenLimit.keySet();
		for (String key : keys) {
			Map<String, Short> temp = origenLimit.get(key);
			Set<String> playerIds = temp.keySet();
			for (String playerId : playerIds) {
				if (playerId.equals(player.getId())) {
					playerLimit.put(key, temp.get(playerId));
				}
			}
		}
		return playerLimit;
	}

	/**
	 * 对比当前版本与后台版本是否一致, true 一致， false 不一致
	 * 
	 * @return
	 */
	public boolean compareMallItemRef() {
		Map<String, MallItemRef> temp = StoreItemRefMgr.MallItemList;
		Set<String> keys = temp.keySet();
		for (String key : keys) {
			MallItemRef mall1 = temp.get(key);
			MallItemRef mall2 = mallItemRefList.get(key);
			if (!mall1.equals(mall2)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 购买全服限购物品
	 * 
	 * @param storeItemId
	 * @param count
	 * @param player
	 * @return
	 */
	public RuntimeResult buyItemInAllLimit(String storeItemId, int count, Player player) {
		RuntimeResult runtimeResult = null;
		int num = storeItemRefMgr.getInAllLImitBy(storeItemId);
		if (num > 0) {
			List<ItemPair> rewardItemList = new ArrayList<>();
			ItemPair item = new ItemPair(storeItemId, count, false);
			rewardItemList.add(item);
			runtimeResult = ItemFacade.addItem(player, rewardItemList, ItemOptSource.Store);
		}
		return runtimeResult;
	}

	/**
	 * 检测某一商品是否过期
	 * 
	 * @param storeType
	 * @param player
	 * @param storeItemId
	 * @param actionEventId
	 * @return
	 */
	public boolean checkLimitTime(byte storeType, Player player, String storeItemId, short actionEventId) {
		if (StringUtils.isEmpty(isTimeLimitItem(storeType, storeItemId))) {
			return true;
		}
		Calendar calendar = Calendar.getInstance();
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
		String nowTime = String.format("%1$d:%2$d:%3$d", hour, minute, second);
		if (mallItemRefList.containsKey(storeItemId)) {
			MallItemRef mallRef = mallItemRefList.get(storeItemId);
			String limitTime1 = MGPropertyAccesser.getStoreLimitTime(mallRef.getProperty());
			if (nowTime.equals(limitTime1)) {
				ResultEvent.sendResult(player.getIdentity(), actionEventId, MGErrorCode.CODE_STORE_OUT_OF_DATE);
				return false;
			}
		} else if (shopItemRefList.containsKey(storeItemId)) {
			ShopItemRef mallRef = shopItemRefList.get(storeItemId);
			String limitTime2 = MGPropertyAccesser.getStoreLimitTime(mallRef.getProperty());
			if (nowTime.equals(limitTime2)) {
				ResultEvent.sendResult(player.getIdentity(), actionEventId, MGErrorCode.CODE_STORE_OUT_OF_DATE);
				return false;
			}
		} else {
			ResultEvent.sendResult(player.getIdentity(), actionEventId, MGErrorCode.CODE_STORE_WRONT_ITEM_ID);
			return false;
		}
		return true;
	}

	/**
	 * 购买物品
	 * 
	 * @param storeItemId
	 * @param count
	 * @param player
	 * @return
	 */
	public RuntimeResult buyItemAndPayMoney(byte storeType, String storeItemId, int count, Player player, short actionEventId) {
		String itemId = getItemIdFromStoreRefId(storeType, storeItemId);
		if (StringUtils.isEmpty(itemId)) {
			return RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_STORE_WRONT_ITEM_ID);
		}
		RuntimeResult runtimeResult = RuntimeResult.ParameterError();
		byte limitType = getItemLimitType(storeType, storeItemId);
		if (limitType == UnLimitItem) {
			runtimeResult = payMoneyAction(itemId, storeType, storeItemId, count, player);
			if (runtimeResult.isError()) {
				int code = runtimeResult.getApplicationCode();
				ResultEvent.sendResult(player.getIdentity(), actionEventId, code);
			}
		} else if (limitType == PersonalLimitItem) {
			int num = storeItemRefMgr.getPersonalNowCount(storeType, storeItemId, player.getId());
			if (num >= count) {
				runtimeResult = payMoneyAction(itemId, storeType, storeItemId, count, player);
				if (runtimeResult.isOK()) {
					storeItemRefMgr.addPersonalNowCount(storeType, storeItemId, player.getId(), count);
				} else {
					int code = runtimeResult.getApplicationCode();
					ResultEvent.sendResult(player.getIdentity(), actionEventId, code);
				}
			} else {
				ResultEvent.sendResult(player.getIdentity(), actionEventId, MGErrorCode.CODE_STORE_NUM_PERSON_LIMIT);
			}
		} else if (limitType == ServerLimitItem) {
			int num = storeItemRefMgr.getInAllLImitBy(storeItemId);
			if (num >= count) {
				runtimeResult = payMoneyAction(itemId, storeType, storeItemId, count, player);
				if (runtimeResult.isOK()) {
					storeItemRefMgr.addAllLimitCount(storeItemId, count);
				} else {
					int code = runtimeResult.getApplicationCode();
					ResultEvent.sendResult(player.getIdentity(), actionEventId, code);
				}
			} else {
				ResultEvent.sendResult(player.getIdentity(), actionEventId, MGErrorCode.CODE_STORE_NUM_SERVER_LIMIT);
			}
		}
		return runtimeResult;
	}

	private byte getItemLimitType(byte storeType, String storeItemId) {
		int limitType = 0;
		if (storeType == StoreMgr.Mall_type) {
			if (StoreItemRefMgr.MallItemList.containsKey(storeItemId)) {
				MallItemRef mallItem = StoreItemRefMgr.MallItemList.get(storeItemId);
				limitType = MGPropertyAccesser.getItemLimitType(mallItem.getProperty());
			}
		} else if (storeType == StoreMgr.Shop_type) {
			if (StoreItemRefMgr.ShopItemList.containsKey(storeItemId)) {
				ShopItemRef shopItem = StoreItemRefMgr.ShopItemList.get(storeItemId);
				limitType = MGPropertyAccesser.getItemLimitType(shopItem.getProperty());
			}
		}

		return (byte) limitType;
	}

	/**
	 * 是否商品有时间限制
	 * 
	 * @param storeItemId
	 * @return
	 */
	private String isTimeLimitItem(byte storeType, String storeItemId) {
		String limitTime = null;
		if (storeType == Mall_type) {
			if (StoreItemRefMgr.MallItemList.containsKey(storeItemId)) {
				MallItemRef mallItem = StoreItemRefMgr.MallItemList.get(storeItemId);
				limitTime = MGPropertyAccesser.getStoreLimitTime(mallItem.getProperty());
			}
		} else if (storeType == Shop_type) {
			if (StoreItemRefMgr.ShopItemList.containsKey(storeItemId)) {
				ShopItemRef shopItem = StoreItemRefMgr.ShopItemList.get(storeItemId);
				limitTime = MGPropertyAccesser.getStoreLimitTime(shopItem.getProperty());
			}
		}
		return limitTime;
	}

	public RuntimeResult payMoneyAction(String itemId, byte storeType, String storeItemId, int count, Player player) {
		boolean isBind = false;
		RuntimeResult absent = isEnoughtMoney(storeType, storeItemId, count, player);
		if (!absent.isOK()) {
			return absent;
		}
		List<ItemPair> rewardItemList = new ArrayList<>();
		if (storeType == Mall_type) {
			MallItemRef mallItemRef = StoreItemRefMgr.MallItemList.get(storeItemId);
			byte bindStatus = MGPropertyAccesser.getBindStatus(mallItemRef.getProperty());
			if (bindStatus != 0) {
				isBind = true;
			}
		} else if (storeType == Shop_type){
			ShopItemRef shopItemRef = StoreItemRefMgr.ShopItemList.get(storeItemId);
			byte bindStatus = MGPropertyAccesser.getBindStatus(shopItemRef.getProperty());
			if (bindStatus != 0) {
				isBind = true;
			}
		}
		ItemPair item = new ItemPair(itemId, count, isBind);
		rewardItemList.add(item);
		RuntimeResult runtimeResult = ItemFacade.addItem(player, rewardItemList, ItemOptSource.Store);

		if (runtimeResult.isOK()) {
			sendChineseModeGameEventMessage(itemId, count, player);
			runtimeResult = payMoney(storeType, storeItemId, count, player);
		} else if (!runtimeResult.isOK()) {
			return runtimeResult;
		}
		return runtimeResult;
	}

	public void sendChineseModeGameEventMessage(String itemRefid, int count, Player player) {
		ChineseModeQuest_GE chineseModeQuest_GE = new ChineseModeQuest_GE();
		chineseModeQuest_GE.setType(ChineseModeQuest_GE.CourseType);
		chineseModeQuest_GE.setOrderEventId(QuestChineseOrderDefines.BuyStoreItem);
		chineseModeQuest_GE.setChineseModeTarget(itemRefid);
		chineseModeQuest_GE.setNumber(count);
		GameEvent<ChineseModeQuest_GE> event = GameEvent.getInstance(ChineseModeQuest_GE_Id, chineseModeQuest_GE);
		player.handleGameEvent(event);
		GameEvent.pool(event);
	}

	/**
	 * 通过商品id获取道具id
	 * 
	 * @param storeItemId
	 * @return
	 */
	private String getItemIdFromStoreRefId(byte storeType, String storeItemId) {
		String ItemId = null;
		if (storeType == Mall_type) {
			if (StoreItemRefMgr.MallItemList.containsKey(storeItemId)) {
				MallItemRef mallItem = StoreItemRefMgr.MallItemList.get(storeItemId);
				ItemId = MGPropertyAccesser.getItemId(mallItem.getProperty());
			}
		}
		if (storeType == Shop_type) {
			if (StoreItemRefMgr.ShopItemList.containsKey(storeItemId)) {
				ShopItemRef shopItem = StoreItemRefMgr.ShopItemList.get(storeItemId);
				ItemId = MGPropertyAccesser.getItemId(shopItem.getProperty());
			}
		}
//		if (storeType == Discount_type) {
//			if (discountMgr.isCurrentBatchContain(storeItemId)) {
//				return discountMgr.getDiscountItemRef(storeItemId).getItemRefId();
//			}
//		}

		return ItemId;
	}

	private RuntimeResult isEnoughtMoney(byte storeType, String storeItemId, int count, Player player) {
		RuntimeResult absent = RuntimeResult.OK();
		if (storeType == Mall_type) {
			if (StoreItemRefMgr.MallItemList.containsKey(storeItemId)) {
				ItemPrice nowItemPrice = StoreItemRefMgr.MallItemList.get(storeItemId).getNowItemPrice();
				absent = moneyCheck(player, nowItemPrice, count);
			}
		}
		if (storeType == Shop_type) {
			if (StoreItemRefMgr.ShopItemList.containsKey(storeItemId)) {
				ItemPrice shopPrice = StoreItemRefMgr.ShopItemList.get(storeItemId).getShopPrice();
				absent = moneyCheck(player, shopPrice, count);
			}
		}
		return absent;
	}

	private RuntimeResult subMoneyAndItem(Player player, ItemPrice itemPrice, int count) {
		RuntimeResult result = RuntimeResult.OK();
		List<Integer> origenPrice = itemPrice.getOrigenPrice();
		Map<String, Integer> experPrice = itemPrice.getExperPrice();
		// 商品支付
		PlayerMoneyComponent playerMoneyCompoent = player.getPlayerMoneyComponent();

		int needGold = origenPrice.get(0) * count;
		int needUnbinedGold = origenPrice.get(1) * count;
		int needBinedGold = origenPrice.get(2) * count;

		if (needGold > 0 && !playerMoneyCompoent.subGold(needGold, ItemOptSource.Store)) {
			return RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_STORE_NOT_ENOUGHT_GOLD);
		}

		if (needUnbinedGold > 0 && !playerMoneyCompoent.subUnbindGold(needUnbinedGold, ItemOptSource.Store)) {
			return RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_STORE_NOT_ENOUGHT_UNBINDGOLD);
		}

		if (needBinedGold > 0 && !playerMoneyCompoent.subBindGold(needBinedGold, ItemOptSource.Store)) {
			return RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_STORE_NOT_ENOUGHT_BINDGOLD);
		}

		for (Entry<String, Integer> entry : experPrice.entrySet()) {
			boolean ret = ItemFacade.removeItem(player, entry.getKey(), entry.getValue() * count, true, ItemOptSource.Store);
			if (!ret) {
				return RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_STORE_NOT_ENOUGHT_ITEM);
			}
		}
		return result;
	}

	private RuntimeResult moneyCheck(Player player, ItemPrice itemPrice, int count) {
		RuntimeResult absent = RuntimeResult.OK();
		List<Integer> origenPrice = itemPrice.getOrigenPrice();
		Map<String, Integer> experPrice = itemPrice.getExperPrice();

		PlayerMoneyComponent playMoneyCompoent = player.getPlayerMoneyComponent();
		int goldMoney = playMoneyCompoent.getGold();
		int unBindMoney = playMoneyCompoent.getUnbindGold();
		int bindedMoney = playMoneyCompoent.getBindGold();

		if (goldMoney < (origenPrice.get(0) * count)) {
			absent = RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_STORE_NOT_ENOUGHT_GOLD);
		} else if (unBindMoney < (origenPrice.get(1) * count)) {
			absent = RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_STORE_NOT_ENOUGHT_UNBINDGOLD);
		} else if (bindedMoney < (origenPrice.get(2) * count)) {
			absent = RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_STORE_NOT_ENOUGHT_BINDGOLD);
		}

		for (Entry<String, Integer> entry : experPrice.entrySet()) {
			int number = ItemFacade.getNumber(player, entry.getKey());
			if (number < entry.getValue() * count) {
				absent = RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_STORE_NOT_ENOUGHT_ITEM);
			}
		}
		return absent;
	}

	private RuntimeResult payMoney(byte storeType, String storeItemId, int count, Player player) {
		RuntimeResult absent = RuntimeResult.OK();

		if (storeType == Mall_type) {
			if (StoreItemRefMgr.MallItemList.containsKey(storeItemId)) {
				MallItemRef mallItemRef = StoreItemRefMgr.MallItemList.get(storeItemId);
				ItemPrice nowItemPrice = mallItemRef.getNowItemPrice();
				absent = subMoneyAndItem(player, nowItemPrice, count);
				int costUnBindGold = nowItemPrice.getOrigenPrice().get(1) * count;
				int costBindGold = nowItemPrice.getOrigenPrice().get(2) * count;
				if (costUnBindGold > 0) {
					MGStatFunctions.storeStat(player, MGPropertyAccesser.getItemId(mallItemRef.getProperty()), count, getItemLimitType(storeType, storeItemId), storeItemId,
							costUnBindGold, 1);
				}
				if (costBindGold > 0) {
					MGStatFunctions.storeStat(player, MGPropertyAccesser.getItemId(mallItemRef.getProperty()), count, getItemLimitType(storeType, storeItemId), storeItemId,
							costBindGold, 2);
				}
			}
		} else if (storeType == Shop_type) {
			if (StoreItemRefMgr.ShopItemList.containsKey(storeItemId)) {
				ShopItemRef shopItemRef = StoreItemRefMgr.ShopItemList.get(storeItemId);
				ItemPrice shopPrice = shopItemRef.getShopPrice();
				absent = subMoneyAndItem(player, shopPrice, count);
			}
		} /*else if (storeItemId.startsWith(DISCOUNT_STR)) {
			return payIfMoneyEnough(storeItemId, player, count);
		}*/
		return absent;
	}


	public int getListVersion() {
		return listVersion;
	}

	public int getNewListVersion() {
		listVersion = listVersion + 1;
		return listVersion;
	}

	public void resetPersonalLimitCountByKind(int kind) {
		storeItemRefMgr.resetPersonalLimitCountByKind(kind);
	}

	public void resetAllLimitCountByKind(int kind) {
		storeItemRefMgr.resetAllLimitCountByKind(kind);
	}

}
