package newbee.morningGlory.mmorpg.player.activity.discount;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import newbee.morningGlory.mmorpg.store.persistence.StoreDao;

import org.apache.log4j.Logger;

public class DiscountPersistenceHelper {
	private static Logger logger = Logger.getLogger(DiscountPersistenceHelper.class);
	
	public static void updateDataTo() throws SQLException {
		logger.info("discount Data Updata to DataBase.");
		Map<String, Short> copyAllLimitItems = RecordManager.getCopyAllLimitItems();
		Map<String, ConcurrentHashMap<String, Short>> personalLimitItems = RecordManager.getPersonalLimitItems();
		
		StoreDao.getInstance().updateData(copyAllLimitItems, personalLimitItems, StoreDao.discountLimitData);
	}
	
	public static void insertDataTo(Map<String, Short> copyAllLimitItems, Map<String, ConcurrentHashMap<String, Short>> personalLimitItems) {
		logger.info("discount Data insert to DataBase.");
		
		StoreDao.getInstance().insertData(copyAllLimitItems, personalLimitItems, StoreDao.discountLimitData);
	}
	
	
	public static long getSaveTime() {
		long selectSaveTime = StoreDao.getInstance().selectSaveTime(StoreDao.discountLimitData);
		
		return selectSaveTime;
	}
	
	public static Map<String, Short> resotreAllLimitData() {
		Map<String, Short> discountAllLimitData = StoreDao.getInstance().selectAllLimitData(StoreDao.discountLimitData);

		if (discountAllLimitData.size() <= 0) {
			logger.info("DataBase Dose Not Have The discountAllLimitData !!!");
		} 

		return discountAllLimitData;

	}
	
	public static Map<String, ConcurrentHashMap<String, Short>> restorePersonalLimitData() {
		Map<String, ConcurrentHashMap<String, Short>> discountPersonalLimitData = StoreDao.getInstance().selectPersonalLimitData(StoreDao.discountLimitData);
		
		if (discountPersonalLimitData.size() <= 0) {
			logger.info("DataBase Dose Not Have The discountPersonalLimitData !!!");
		} 
		
		return discountPersonalLimitData;
		
	}
}
