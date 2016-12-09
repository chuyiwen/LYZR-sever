package newbee.morningGlory.mmorpg.store.ref;

import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import newbee.morningGlory.mmorpg.player.activity.discount.DiscountPersistenceHelper;
import newbee.morningGlory.mmorpg.player.activity.discount.DiscountRefDataManager;
import newbee.morningGlory.mmorpg.player.activity.discount.RecordManager;
import newbee.morningGlory.mmorpg.player.store.MGPlayerShopComponent;
import newbee.morningGlory.mmorpg.store.DiscountActivityTimer;
import newbee.morningGlory.mmorpg.store.StoreMgr;
import newbee.morningGlory.mmorpg.store.persistence.StoreDao;

import org.apache.log4j.Logger;

import sophia.game.component.AbstractComponent;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.core.timer.SFTimeChimeListener;
import sophia.mmorpg.core.timer.SFTimeUnit;
import sophia.mmorpg.core.timer.SFTimer;
import sophia.mmorpg.core.timer.SFTimerCreater;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.DateTimeUtil;

public final class StoreItemRefMgr extends AbstractComponent {
	public static Logger logger = Logger.getLogger(StoreItemRefMgr.class);
	public static Map<String, MallItemRef> MallItemList = new LinkedHashMap<>();
	public static Map<String, ShopItemRef> ShopItemList = new LinkedHashMap<>();

	public static Map<String, Short> AllLimit = new HashMap<String, Short>();
	public static Map<String, ConcurrentHashMap<String, Short>> personalLimit = new HashMap<String, ConcurrentHashMap<String, Short>>();
	public static List<Integer> refreshTypeList = new ArrayList<>();
	private List<SFTimer> timers = new ArrayList<>();
	private int time = 0;

	@Override
	public void ready() {
		SFTimerCreater timerCreater = MMORPGContext.getTimerCreater();
		SFTimer saveTimer = timerCreater.minuteCalendarChime(new SFTimeChimeListener() {
			@Override
			public void handleTimeChimeCancel() {
			}
			
			@Override
			public void handleTimeChime() {
				time += 1;
				if (time >= 5) {					
					try {
						DiscountPersistenceHelper.updateDataTo();
						updateDataTo();
					} catch (SQLException e) {
						logger.error(e);
					}
					time = 0;
				}
			}
			
			@Override
			public void handleServiceShutdown() {
			}
		});
		timers.add(saveTimer);
		
		
		for (final Integer type : refreshTypeList) {
			if (type == 1) {
				SFTimer timer = timerCreater.calendarChime(new RefStoreTimer(type), SFTimeUnit.HOUR, 0);
				timers.add(timer);
			}
			if (type == 2) {
				SFTimer timer = timerCreater.weekCalendarChime(new RefStoreTimer(type), 1, 0);
				timers.add(timer);
			}
			if (type == 3) {
				SFTimer timer = timerCreater.monthCalendarChime(new RefStoreTimer(type), 1, 0);
				timers.add(timer);
			}
		}
		discountInit();
	}

	public static synchronized void LoadAll() {
		selectStoreLimit(StoreDao.storeLimitData);
		//selectDiscountLimit(StoreDao.discountLimitData);
	}

	private static synchronized void selectStoreLimit(int type) {
		long selectSaveTime = StoreDao.getInstance().selectSaveTime(StoreDao.storeLimitData);
		boolean sameDay = DateTimeUtil.isTheSameDay(selectSaveTime, System.currentTimeMillis());
		if (sameDay) {
			Map<String, Short> selectAllLimitData = StoreDao.getInstance().selectAllLimitData(type);
			if (selectAllLimitData.size() <= 0) {
				logger.info("DataBase Dose Not Have The Store AllLimitData !!!");
			} else {
				AllLimit = selectAllLimitData;
			}

			Map<String, ConcurrentHashMap<String, Short>> selectPersonalLimitData = StoreDao.getInstance().selectPersonalLimitData(type);
			if (selectPersonalLimitData.size() <= 0) {
				logger.info("DataBase Dose Not Have The Store PersonalLimitData !!!");
			} else {
				personalLimit = selectPersonalLimitData;
			}
		}
		
		if (selectSaveTime == 0 && !sameDay) {
			insertDataTo();
		}
	}

	public static synchronized void insertDataTo() {
		StoreDao.getInstance().insertData(AllLimit, personalLimit, StoreDao.storeLimitData);
	}

	public static synchronized void updateDataTo() throws SQLException {
		logger.info("store Data Updata to DataBase.");
		StoreDao.getInstance().updateData(AllLimit, personalLimit, StoreDao.storeLimitData);
	}

	private void discountInit() {
		DiscountRefDataManager.refreshDiscountItems();
		SFTimer timer = MMORPGContext.getTimerCreater().hourCalendarChime(new DiscountActivityTimer());
		timers.add(timer);
		//timers.add(MMORPGContext.getTimerCreater().delayPeriodClaendarChime(new DiscountActivityTimer(), SFTimeUnit.HOUR, 0, 1000 * 3600 * 8L));
		// 购买数量恢复
		RecordManager.restore();
	
	}

	@Override
	public void destroy() {
		for (SFTimer timer : timers) {
			timer.cancel();
			if (logger.isDebugEnabled()) {
				logger.debug(" StoreItemRefMgr was terminated.");
			}
		}
		super.destroy();
	}

	public StoreItemRefMgr() {
	}

	/**
	 * 获取当前商品的总限购数
	 * 
	 * @param itemId
	 * @return
	 */
	public synchronized int getInAllLImitBy(String itemId) {
		return AllLimit.get(itemId);
	}

	/**
	 * 获取个人限购商品当前限购数
	 * 
	 * @param itemId
	 * @param personalId
	 * @return
	 */
	public synchronized int getPersonalNowCount(byte storeType, String itemId, String personalId) {
		int num = 0;
		if (!personalLimit.containsKey(itemId)) {
			personalLimit.put(itemId, new ConcurrentHashMap<String, Short>());
		}
		Map<String, Short> personBuyList = personalLimit.get(itemId);
		if (personBuyList.containsKey(personalId)) {
			num = personBuyList.get(personalId);
		} else {
			if (storeType == StoreMgr.Mall_type) {
				if (MallItemList.containsKey(itemId)) {
					MallItemRef itemRef = MallItemList.get(itemId);
					num = MGPropertyAccesser.getItemLimitNum(itemRef.getProperty());
				}
			}
			if (storeType == StoreMgr.Shop_type) {
				if (ShopItemList.containsKey(itemId)) {
					ShopItemRef itemRef = ShopItemList.get(itemId);
					num = MGPropertyAccesser.getItemLimitNum(itemRef.getProperty());
				}
			}
		}

		return num;
	}

	public synchronized void addPersonalNowCount(byte storeType, String itemId, String personalId, int count) {
		ConcurrentHashMap<String, Short> personBuyList = personalLimit.get(itemId);
		if (personBuyList.containsKey(personalId)) {
			int limitNum = personBuyList.get(personalId);
			personBuyList.put(personalId, (short) (limitNum - count));
			personalLimit.put(itemId, personBuyList);
		} else {
			if (storeType == StoreMgr.Mall_type) {
				if (MallItemList.containsKey(itemId)) {
					MallItemRef itemRef = MallItemList.get(itemId);
					int num = MGPropertyAccesser.getItemLimitNum(itemRef.getProperty());
					personBuyList.put(personalId, (short) (num - count));
					personalLimit.put(itemId, personBuyList);
				}
			}
			if (storeType == StoreMgr.Shop_type) {
				if (ShopItemList.containsKey(itemId)) {
					ShopItemRef itemRef = ShopItemList.get(itemId);
					int num = MGPropertyAccesser.getItemLimitNum(itemRef.getProperty());
					personBuyList.put(personalId, (short) (num - count));
					personalLimit.put(itemId, personBuyList);
				}
			}
		}
	}

	public synchronized void addAllLimitCount(String itemId, int count) {
		int num = AllLimit.get(itemId);
		AllLimit.put(itemId, (short) (num - count));
	}

	/**
	 * 重置指定类型全服限购商品数量
	 */
	public synchronized void resetAllLimitCountByKind(int kind) {
		Set<String> keys = AllLimit.keySet();
		for (String key : keys) {
			String a[] = key.split("_");
			if (a[0].equals("mall")) {
				if (MallItemList.containsKey(key)) {
					MallItemRef mallItem = MallItemList.get(key);
					int refreshDay = MGPropertyAccesser.getNumber(mallItem.getProperty());
					if (refreshDay == kind) {
						resetAllLimitItemCount(key);
					}
				}
			}
			if (a[0].equals("shop")) {
				if (ShopItemList.containsKey(key)) {
					ShopItemRef shopItem = ShopItemList.get(key);
					int refreshDay = MGPropertyAccesser.getNumber(shopItem.getProperty());
					if (refreshDay == kind) {
						resetAllLimitItemCount(key);
					}
				}
			}
		}
	}

	/**
	 * 重置指定全服限购商品数量
	 * 
	 * @param itemId
	 */
	public synchronized void resetAllLimitItemCount(String itemId) {
		if (AllLimit.containsKey(itemId)) {
			String a[] = itemId.split("_");
			if (a[0].equals("mall")) {
				if (MallItemList.containsKey(itemId)) {
					MallItemRef mallItem = MallItemList.get(itemId);
					int limitNum = MGPropertyAccesser.getItemLimitNum(mallItem.getProperty());
					AllLimit.put(itemId, (short) limitNum);
				}
			}
			if (a[0].equals("shop")) {
				if (ShopItemList.containsKey(itemId)) {
					ShopItemRef shopItem = ShopItemList.get(itemId);
					int limitNum = MGPropertyAccesser.getItemLimitNum(shopItem.getProperty());
					AllLimit.put(itemId, (short) limitNum);
				}
			}
		}
	}

	/**
	 * 重置指定类型个人限购商品数量
	 * 
	 * @param personalId
	 */
	public synchronized void resetPersonalLimitCountByKind(int kind) {
		Set<String> keys = personalLimit.keySet();
		for (String key : keys) {
			String a[] = key.split("_");
			if (a[0].equals("mall")) {
				if (MallItemList.containsKey(key)) {
					MallItemRef mallItem = MallItemList.get(key);
					int refreshDay = MGPropertyAccesser.getNumber(mallItem.getProperty());
					if (refreshDay == kind) {
						resetPersonalLimitCount(key);
					}
				}
			}
			if (a[0].equals("shop")) {
				if (ShopItemList.containsKey(key)) {
					ShopItemRef shopItem = ShopItemList.get(key);
					int refreshDay = MGPropertyAccesser.getNumber(shopItem.getProperty());
					if (refreshDay == kind) {
						resetPersonalLimitCount(key);
					}
				}
			}
		}
	}

	/**
	 * 重置指定个人限购商品数量
	 * 
	 * @param itemId
	 * @param personalId
	 */
	public synchronized void resetPersonalLimitCount(String itemId) {
		if (personalLimit.containsKey(itemId)) {
			ConcurrentHashMap<String, Short> limit = personalLimit.get(itemId);
//			Set<String> keys = limit.keySet();
//			for (String key : keys) {
//				String a[] = itemId.split("_");
//				if (a[0].equals("mall")) {
//					if (MallItemList.containsKey(itemId)) {
//						MallItemRef mallItem = MallItemList.get(itemId);
//						int limitNum = MGPropertyAccesser.getItemLimitNum(mallItem.getProperty());
//						limit.put(key, (short) limitNum);
//						personalLimit.put(itemId, limit);
//					}
//				}
//				if (a[0].equals("shop")) {
//					if (ShopItemList.containsKey(itemId)) {
//						ShopItemRef shopItem = ShopItemList.get(itemId);
//						int limitNum = MGPropertyAccesser.getItemLimitNum(shopItem.getProperty());
//						limit.put(key, (short) limitNum);
//						personalLimit.put(itemId, limit);
//					}
//				}
//			}
			for (String playerId : limit.keySet()) {
				Player onlinePlayer = MMORPGContext.getPlayerComponent().getPlayerManager().getOnlinePlayer(playerId);
				if (onlinePlayer != null) {
					MGPlayerShopComponent playeshopComponent = (MGPlayerShopComponent)onlinePlayer.getTagged(MGPlayerShopComponent.Tag);
					playeshopComponent.getStoreMgr().getNewListVersion();
				}
			}
			limit.clear();
			personalLimit.put(itemId, limit);
		}
	}

	public boolean chackIfTimeOut() {
		boolean temp = false;
		long nowSecond = System.currentTimeMillis();
		Date date = new Date(nowSecond);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String nowTime = sdf.format(date).substring(0, 8);
		Set<String> keys = MallItemList.keySet();
		for (String key : keys) {
			MallItemRef mallRef = MallItemList.get(key);
			String limitTime = MGPropertyAccesser.getStoreLimitTime(mallRef.getProperty());
			if (nowTime.equals(limitTime)) {
				MallItemList.remove(key);
			}
		}
		return temp;
	}

}
