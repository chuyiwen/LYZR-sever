package newbee.morningGlory.mmorpg.player.activity.discount;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import newbee.morningGlory.code.MGErrorCode;
import newbee.morningGlory.mmorpg.store.ref.DiscountItemRef;
import newbee.morningGlory.mmorpg.store.ref.DiscountRef;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.game.GameRoot;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.money.PlayerMoneyComponent;
import sophia.mmorpg.utils.RuntimeResult;

import com.google.common.base.Preconditions;

public class DiscountRefDataManager {
	private static Logger logger = Logger.getLogger(DiscountRefDataManager.class);
	
	public static final byte All_Limit_Type = 1;
	
	public static final byte Personal_Limit_Type = 2;
	
	
	private static Map<String, DiscountItemRef> crtDiscountItemRef = new HashMap<String, DiscountItemRef>();
	
	/**
	 * 每次刷新，重新load引用数据和record归零
	 */
	public static synchronized void refreshDiscountItems() {
		Map<String, DiscountItemRef> refreshData = getRefreshData();
		
		initItemRefData(refreshData);

		RecordManager.initilaizeAllLimitCount(refreshData);
	}
	
//	public static void restoreRefData() {
//		logger.debug("reset refData...");
//		
//		Map<String, DiscountItemRef> refreshData = getRefreshData();
//		for (Entry<String, DiscountItemRef> entry : refreshData.entrySet()) {
//			String discountRefId = entry.getKey();
//			
//			DiscountItemRef discountItemRef = entry.getValue();
//			
//			crtDiscountItemRef.put(discountRefId, discountItemRef);
//		}
//
//	}
	
	public static void restoreRefData(Map<String, Short> resotreAllLimitData) {
		logger.debug("restore refData...");
		crtDiscountItemRef.clear();
		Map<Short, Map<String, DiscountItemRef>> discountItemRefMap = getDiscountItemRefMap();
		for (String discountRefId : resotreAllLimitData.keySet()) {
			for (Map<String, DiscountItemRef> refValue : discountItemRefMap.values()) {
				for (String key : refValue.keySet()) {
					if (StringUtils.equals(key, discountRefId)) {
						DiscountItemRef itemRef = refValue.get(key);
						
						crtDiscountItemRef.put(discountRefId, itemRef);
						
					}
				}
			}
			
		}
		
	}
	
	private static void initItemRefData(Map<String, DiscountItemRef> itemRefData) {
		crtDiscountItemRef.clear();
		for (Entry<String, DiscountItemRef> entry : itemRefData.entrySet()) {
			crtDiscountItemRef.put(entry.getKey(), entry.getValue());
		}
	}
	
	private static Map<String, DiscountItemRef> getRefreshData() {
		Map<Short, Map<String, DiscountItemRef>> discountItemRefMap = getDiscountItemRefMap();
		
		short nextBatch = BatchMgr.getNextBatch();
		
		Map<String, DiscountItemRef> itemRefData = discountItemRefMap.get(nextBatch);
		
		Preconditions.checkNotNull(itemRefData, "batch = " + nextBatch + " itemRefData is null");
		
		return itemRefData;
	}
	
	private static Map<Short, Map<String, DiscountItemRef>> getDiscountItemRefMap() {
		DiscountRef discountRef = (DiscountRef) GameRoot.getGameRefObjectManager().getManagedObject(DiscountRef.discount_item);

		if (discountRef == null) {
			throw new RuntimeException("load discount ref data error!");
		}

		Map<Short, Map<String, DiscountItemRef>> discountItemRefMap = discountRef.getDiscountItemRefMap();

		if (discountItemRefMap == null || discountItemRefMap.size() == 0) {
			throw new RuntimeException("load discount ref data error!");
		}
		
		return discountItemRefMap;
	}
	
	
	/**
	 * 
	 * @param discountRefId
	 * @param type
	 * @return
	 */
	public static short getMaxLimitNum(String discountRefId, byte type) {
		short maxNum = -1;
		for (Entry<String, DiscountItemRef> entry : crtDiscountItemRef.entrySet()) {
			if (StringUtils.equals(entry.getKey(), discountRefId)) {

				if (type == All_Limit_Type) {
					maxNum = entry.getValue().getItemLimitNum();
					return maxNum;
				}

				if (type == Personal_Limit_Type) {
					maxNum = entry.getValue().getPersonalLimitNum();
					return maxNum;
				}

			} 
		}
		
		logger.error("dicountRefId = " + discountRefId + " is not exist in crtDiscountItemRef");

		return maxNum;
	}
	
	public static synchronized boolean isInSalePeriod(String discountRefId) {
		return crtDiscountItemRef.containsKey(discountRefId);
	}
	
	
	public static synchronized Map<String, DiscountItemRef> getDiscountItemRef() {
		return crtDiscountItemRef;
	}
	
	public static synchronized DiscountItemRef getCrtDiscountItemRef(String discountRefId) {
		return getDiscountItemRef().get(discountRefId);
	}
	
	public static boolean isAllLimitItem(String discountRefId) {
		DiscountItemRef discountItemRef = getCrtDiscountItemRef(discountRefId);
		if (null == discountItemRef) {
			return false;
		}

		return discountItemRef.getItemLimitNum() >= 0;
	}
	
	public static boolean isPersonalLimitItem(String discountRefId) {
		DiscountItemRef discountItemRef = getCrtDiscountItemRef(discountRefId);
		if (null == discountItemRef) {
			return false;
		}

		return discountItemRef.getPersonalLimitNum() >= 0;
	}
	
	public static RuntimeResult ifMoneyEnough(DiscountItemRef discountItemRef, Player player, int count) {
		Preconditions.checkArgument(count > 0, "payIfMoneyEnough error, count=" + count + ", player=" + player);
		Preconditions.checkNotNull(discountItemRef, "payIfMoneyEnough error, discountRefId=" + discountItemRef.getId() + " can't find");

		PlayerMoneyComponent playerMoneyCompoent = player.getPlayerMoneyComponent();
		int unbindGold = playerMoneyCompoent.getUnbindGold();
		int gold = playerMoneyCompoent.getGold();
		int bindGold = playerMoneyCompoent.getBindGold();

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

		if (needUnbinedGold > 0 && needUnbinedGold > unbindGold) {
			return RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_STORE_NOT_ENOUGHT_UNBINDGOLD);
		}

		if (needGold > 0 && needGold > gold) {
			return RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_STORE_NOT_ENOUGHT_GOLD);
		}

		if (needBinedGold > 0 && needBinedGold > bindGold) {
			return RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_STORE_NOT_ENOUGHT_BINDGOLD);
		}

		return RuntimeResult.OK();
	}
}
