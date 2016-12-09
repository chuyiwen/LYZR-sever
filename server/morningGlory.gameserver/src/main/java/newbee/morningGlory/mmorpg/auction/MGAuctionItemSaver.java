/**
 * 
 */
package newbee.morningGlory.mmorpg.auction;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import newbee.morningGlory.mmorpg.auction.persistence.AuctionItemDAO;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.util.DebugUtil;

import com.google.common.base.Preconditions;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

/**
 * @author Administrator
 * 
 */
public class MGAuctionItemSaver {

	private static Logger logger = Logger.getLogger(MGAuctionItemSaver.class);

	private static MGAuctionItemSaver instance = new MGAuctionItemSaver();
	private static Set<AuctionItem> insertImmediateSetPrimary = Collections.newSetFromMap(new ConcurrentHashMap<AuctionItem, Boolean>());
	private static Set<AuctionItem> insertImmediateSetSecondary = Collections.newSetFromMap(new ConcurrentHashMap<AuctionItem, Boolean>());

	private Set<String> removeImmediateSetPrimary = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
	private Set<String> removeImmediateSetSecondary = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

	private MGAuctionItemSaver() {
	}

	public static MGAuctionItemSaver getInstance() {
		return instance;
	}

	public void save() throws Exception {

		if (insertImmediateSetPrimary.isEmpty()) {
			delete();
			return;
		}
		insertImmediateSetSecondary.addAll(insertImmediateSetPrimary);
		insertImmediateSetPrimary.removeAll(insertImmediateSetSecondary);
		for (AuctionItem auctionItem : insertImmediateSetSecondary) {
			try {
				AuctionItemDAO.getInstance().doSave(auctionItem);
			} catch (MySQLIntegrityConstraintViolationException e) {
				logger.error("save error, auctionItem =" + auctionItem);
				logger.error("save error MySQLIntegrityConstraintViolationException," + DebugUtil.printStack(e));
			} catch (Exception ex) {
				insertImmediateSetPrimary.add(auctionItem);
				logger.error("save error, auctionItem =" + auctionItem);
				logger.error("save error, Data rollBacked!" + DebugUtil.printStack(ex));
			}
		}
		insertImmediateSetSecondary.clear();
		delete();
	}

	public void delete() throws Exception {
		if (removeImmediateSetPrimary.isEmpty()) {
			return;
		}
		removeImmediateSetSecondary.addAll(removeImmediateSetPrimary);
		removeImmediateSetPrimary.removeAll(removeImmediateSetSecondary);
		for (String id : removeImmediateSetSecondary) {
			try {
				AuctionItemDAO.getInstance().doDelete(id);
			} catch (Exception ex) {
				logger.error("delete error, auctionItem id =" + id);
				logger.error("delete error, Data rollBacked!" + DebugUtil.printStack(ex));
			}
		}
		removeImmediateSetSecondary.clear();

	}

	public void shutDownSave() throws Exception {
		save();
		if (!insertImmediateSetPrimary.isEmpty()) {
			throw new RuntimeException("shutDownExcuteSave save AuctionItem Error");
		}

	}

	public void saveImmediateData(AuctionItem auctionItem) {
		boolean result = insertImmediateSetPrimary.add(auctionItem);
		try {
			if (!result)
				throw new RuntimeException("已存在此id拍卖物");
		} catch (Exception e) {
			logger.error(DebugUtil.printStack(e));
		}
	}

	public void removeImmediateData(String id) {
		removeImmediateSetPrimary.add(id);
	}

	public boolean insertAuctionItem(AuctionItem auctionItem) {
		Preconditions.checkArgument(auctionItem != null);
		try {
			AuctionItemDAO.getInstance().doSave(auctionItem);
		} catch (Exception e) {
			logger.error("insertAuctionItem error, auctionItem=" + auctionItem);
			logger.error("insertAuctionItem error, " + DebugUtil.printStack(e));
			return false;
		}

		return true;
	}

	public boolean deleteAuctionItem(String id) {
		Preconditions.checkArgument(id != null);
		try {
			AuctionItemDAO.getInstance().doDelete(id);
		} catch (Exception e) {
			logger.error("deleteAuctionItem error, id=" + id);
			logger.error("deleteAuctionItem error, " + DebugUtil.printStack(e));
			return false;
		}

		return true;
	}

	public AuctionItem getAuctionItem(String id) {
		for (AuctionItem auction : insertImmediateSetPrimary) {
			if (StringUtils.equals(id, auction.getId())) {
				return auction;
			}
		}
		return null;
	}

	public static Set<AuctionItem> getInsertImmediateSetPrimary() {
		return insertImmediateSetPrimary;
	}

	public static Set<AuctionItem> getInsertImmediateSetSecondary() {
		return insertImmediateSetSecondary;
	}

}
