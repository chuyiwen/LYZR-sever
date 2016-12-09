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
package newbee.morningGlory.mmorpg.auction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import newbee.morningGlory.mmorpg.auction.AuctionSort.AuctionItemNameSort;
import newbee.morningGlory.mmorpg.auction.AuctionSort.AuctionItemTypeSort;
import newbee.morningGlory.mmorpg.auction.AuctionSort.AuctionLevelSort;
import newbee.morningGlory.mmorpg.auction.AuctionSort.AuctionPriceSort;
import newbee.morningGlory.mmorpg.auction.AuctionSort.AuctionQualitySort;
import newbee.morningGlory.mmorpg.auction.AuctionSort.AuctionRemainTimeSort;
import newbee.morningGlory.mmorpg.auction.persistence.AuctionItemDAO;
import newbee.morningGlory.stat.MGStatFunctions;
import newbee.morningGlory.stat.logs.StatAuction;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.property.PropertyDictionary;
import sophia.foundation.util.DebugUtil;
import sophia.mmorpg.Mail.Mail;
import sophia.mmorpg.Mail.MailMgr;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class AuctionMgr {

	private static Logger logger = Logger.getLogger(AuctionMgr.class);
	private List<AuctionItem> auctionList = new CopyOnWriteArrayList<AuctionItem>();

	private Map<Byte, Comparator<AuctionItem>> sortMap = new HashMap<>(6);

	public AuctionMgr() {
		sortMap.put(AuctionSortType.Level, new AuctionLevelSort());
		sortMap.put(AuctionSortType.Price, new AuctionPriceSort());
		sortMap.put(AuctionSortType.Quality, new AuctionQualitySort());
		sortMap.put(AuctionSortType.RemainTime, new AuctionRemainTimeSort());
		sortMap.put(AuctionSortType.ItemType, new AuctionItemTypeSort());
		sortMap.put(AuctionSortType.ItemRefId, new AuctionItemNameSort());
	}

	public List<AuctionItem> getAuctionList() {
		return auctionList;
	}

	public void setAuctionList(List<AuctionItem> auctionList) {
		this.auctionList = auctionList;
	}

	public int defaultPrice(Player player, String itemId, int count) {
		Item target = ItemFacade.getItemById(player, itemId);
		if (target == null) {
			return AuctionItem.AUCTION_DEFAULT_PRICE;
		}
		double minPrice = Double.MAX_VALUE;
		for (AuctionItem auction : auctionList) {
			Item item = auction.getItem();
			double individualPrice = auction.getPrice() * 1.0 / auction.getItem().getNumber();
			if (item.getItemRefId().equals(target.getItemRefId()) && individualPrice < minPrice) {
				minPrice = individualPrice;
			}
		}

		if (minPrice == Double.MAX_VALUE) {
			minPrice = AuctionItem.AUCTION_DEFAULT_PRICE;
		}

		if (count <= 0 || count > target.getNumber()) {
			return AuctionItem.AUCTION_DEFAULT_PRICE;
		}

		int defaultMoney = (int) Math.max(Math.floor(minPrice * count), 1);
		return defaultMoney;
	}

	/**
	 * 指定排序方式对拍卖物品集合排序
	 * 
	 * @param sortType
	 * @return
	 */
	public List<AuctionItem> getAuctionListBySort(byte sortType) {
		Comparator<AuctionItem> comparator = sortMap.get(sortType);
		Collections.sort(auctionList, comparator);
		return auctionList;
	}

	/**
	 * 指定排序方式排序指定的集合
	 * 
	 * @param auctionItems
	 * @param sortType
	 * @return
	 */
	public void sortAuctionItems(List<AuctionItem> auctionItems, byte sortType) {
		Comparator<AuctionItem> comparator = sortMap.get(sortType);
		Collections.sort(auctionItems, comparator);
	}

	/***
	 * 通过物品名查找
	 * 
	 * @param name
	 * @param auctionItems
	 * @return
	 */
	public List<AuctionItem> findAuctionsByName(String name, List<AuctionItem> auctionItems) {
		List<AuctionItem> list = new ArrayList<AuctionItem>();
		for (AuctionItem auctionItem : auctionItems) {
			if (StringUtils.contains(auctionItem.getName(), name)) {
				list.add(auctionItem);
			}
		}
		return list;
	}

	/**
	 * 通过品质查找
	 * 
	 * @param quality
	 * @param auctionItems
	 * @return
	 */
	public List<AuctionItem> findAuctionsByQuality(byte quality, List<AuctionItem> auctionItems) {
		List<AuctionItem> list = new ArrayList<AuctionItem>();
		for (AuctionItem auctionItem : auctionItems) {
			if (auctionItem.getQuality() == quality) {
				list.add(auctionItem);
			}
		}
		return list;
	}

	public List<AuctionItem> findAuctionsByBodyAreaId(short bodyAreaId, List<AuctionItem> auctionItems) {
		List<AuctionItem> list = new ArrayList<AuctionItem>();
		for (AuctionItem auctionItem : auctionItems) {
			Item item = auctionItem.getItem();
			if (item == null) {
				continue;
			}
			if (item.isEquip() && item.isSameBodyAreaId(bodyAreaId)) {
				list.add(auctionItem);
			}
		}
		return list;
	}

	/**
	 * 通过使用等级查找
	 * 
	 * @param level
	 * @param auctionItems
	 * @return
	 */
	public List<AuctionItem> findAuctionsByLevel(short level, List<AuctionItem> auctionItems) {
		List<AuctionItem> list = new ArrayList<AuctionItem>();
		for (AuctionItem auctionItem : auctionItems) {
			if (auctionItem.getUseLevel() >= level) {
				list.add(auctionItem);
			}
		}
		return list;
	}

	/**
	 * 通过物品类型查找
	 * 
	 * @param itemType
	 * @param auctionItems
	 * @return
	 */
	public List<AuctionItem> findAuctionsByItemType(short itemType, List<AuctionItem> auctionItems) {
		List<AuctionItem> list = new ArrayList<AuctionItem>();
		for (AuctionItem auctionItem : auctionItems) {
			if (auctionItem.getItemType() == itemType) {
				list.add(auctionItem);
			}
		}
		return list;
	}

	/**
	 * 通过物品是否可用查找
	 * 
	 * @param player
	 * @param auctionItems
	 * @return
	 */
	public List<AuctionItem> findAuctionsByCanUse(Player player, List<AuctionItem> auctionItems) {
		List<AuctionItem> list = new ArrayList<AuctionItem>();
		if (player == null) {
			return list;
		}
		
		int playerLevel = player.getExpComponent().getLevel(); // 玩家等级
		byte playerKnight = MGPropertyAccesser.getKnight(player.getProperty()); // 玩家爵位
		byte playerProfessionId = MGPropertyAccesser.getProfessionId(player.getProperty());
		byte playerGender = MGPropertyAccesser.getGender(player.getProperty());

		for (AuctionItem auctionItem : auctionItems) {

			Item useItem = auctionItem.getItem();
			int itemLevel = auctionItem.getUseLevel();
			byte useKnight = MGPropertyAccesser.getUseKnight(useItem.getItemRef().getProperty()); // 物品使用爵位
			if (useItem.isEquip()) {
				PropertyDictionary itemPd = useItem.getItemRef().getProperty();
				byte equipGender = MGPropertyAccesser.getGender(itemPd);
				byte equipProfessionId = MGPropertyAccesser.getProfessionId(itemPd);
				if (equipGender != AuctionItem.AUCTION_NOT_LIMITED && playerGender != equipGender) {
					continue;
				}

				if (equipProfessionId != AuctionItem.AUCTION_NOT_LIMITED && playerProfessionId != equipProfessionId) {
					continue;
				}
			}

			if (playerKnight < useKnight && useKnight != AuctionItem.AUCTION_NOT_LIMITED) {
				continue;
			}

			if (playerLevel < itemLevel) { // 是否达到使用等级
				continue;
			}
			list.add(auctionItem);
		}
		return list;
	}

	/**
	 * 根据条件查找拍卖列表
	 * 
	 * @param player
	 *            进行查找操作的玩家
	 * @param name
	 *            物品的名字筛选。为空字符串时表示不限制名字
	 * @param level
	 *            物品的使用等级筛选。为-1表示不限等级
	 * @param itemType
	 *            物品类型 ，1表示装备，2表示非装备， 字段 为-1表示不限品质
	 * @param bodyAreaId
	 *            装备的穿戴部位。
	 * @param canUseLimit
	 *            1 = 为1表示只筛选可使用的(满足使用等级和职业)；为0表示不限
	 * @return
	 */
	public List<AuctionItem> findAuction(Player player, String name, short level, short itemType, short bodyAreaId, short canUseLimit) {
		List<AuctionItem> findAuctions = getNotExpiredAuctionItem(auctionList);

		if (!StringUtils.isEmpty(name)) {
			findAuctions = findAuctionsByName(name, findAuctions);
		}
		if (itemType != AuctionItem.AUCTION_DEFAULT) {
			findAuctions = findAuctionsByItemType(itemType, findAuctions);
		}
		if (bodyAreaId != AuctionItem.AUCTION_DEFAULT) {
			findAuctions = findAuctionsByBodyAreaId(bodyAreaId, findAuctions);
		}
		if (level != AuctionItem.AUCTION_DEFAULT) {
			findAuctions = findAuctionsByLevel(level, findAuctions);
		}
		if (canUseLimit == AuctionItem.AUCTION_USABLE) {
			findAuctions = findAuctionsByCanUse(player, findAuctions);
		}

		sortAuctionItems(findAuctions, AuctionSortType.RemainTime);
		sortAuctionItems(findAuctions, AuctionSortType.Price);
		sortAuctionItems(findAuctions, AuctionSortType.ItemRefId);
		sortAuctionItems(findAuctions, AuctionSortType.Level);
		sortAuctionItems(findAuctions, AuctionSortType.Quality);
		sortAuctionItems(findAuctions, AuctionSortType.ItemType);

		return findAuctions;

	}

	/**
	 * 获取指定的拍卖物通过Id
	 * 
	 * @param itemId
	 * @return
	 */
	public AuctionItem getAuctionItemByItemId(String itemId) {
		for (AuctionItem auctionItem : auctionList) {
			if (StringUtils.equals(itemId, auctionItem.getItem().getId())) {
				return auctionItem;
			}
		}
		return null;
	}

	/**
	 * 获取我的出售列表
	 * 
	 * @param who
	 * @return
	 */
	public List<AuctionItem> getAuctionItems(Player who) {
		List<AuctionItem> list = new ArrayList<AuctionItem>();
		if (who == null) {
			return list;
		}

		for (AuctionItem auctionItem : auctionList) {
			String id = auctionItem.getPlayerId();
			if (!auctionItem.isExpired() && StringUtils.equals(who.getId(), id)) {
				list.add(auctionItem);
			}
		}

		return list;
	}

	/**
	 * 删除指定的拍卖物通过 itemId
	 * 
	 * @param itemId
	 * @return
	 */
	public synchronized boolean removeAuctionItemByItemId(String itemId) {
		AuctionItem auctionItem = getAuctionItemByItemId(itemId);
		boolean remove = auctionList.remove(auctionItem);
		if (auctionItem != null && remove) {
			MGAuctionItemSaver.getInstance().removeImmediateData(auctionItem.getId());
		}
		return remove;
	}

	/**
	 * 删除指定的拍卖物
	 * 
	 * @param auctionItem
	 * @return
	 */
	public synchronized boolean removeAuctionItem(AuctionItem auctionItem) {
		boolean remove = auctionList.remove(auctionItem);
		if (auctionItem != null && remove) {
			MGAuctionItemSaver.getInstance().removeImmediateData(auctionItem.getId());
		}
		return remove;
	}

	/**
	 * 增加拍卖物
	 * 
	 * @param auctionItem
	 * @return
	 */
	public synchronized boolean addAuctionItem(AuctionItem auctionItem) {
		boolean add = false;
		if (auctionList.contains(auctionItem)) {
			try {
				throw new RuntimeException("已存在此id拍卖物");
			} catch (Exception e) {
				logger.error(DebugUtil.printStack(e));
			}
			return add;
		}
		add = auctionList.add(auctionItem);
		if (add) {
			MGAuctionItemSaver.getInstance().saveImmediateData(auctionItem);
		}
		return add;
	}

	/**
	 * 增加拍卖物(集合初始化)
	 * 
	 * @param auctionItem
	 * @return
	 */
	public synchronized void initAuctionItem(AuctionItem auctionItem) {
		AuctionItem auction = getAuctionItemByItemId(auctionItem.getItem().getId());
		if (auction != null) {
			logger.error("checkItemIsValid initAuctionItem,exsit the same of auctionItem =" + auctionItem);

			if (auction.getStartTime() < auctionItem.getStartTime()) {
				removeAuctionItem(auction);
			} else {
				MGAuctionItemSaver.getInstance().removeImmediateData(auctionItem.getId());
				return;
			}

		}
		auctionList.add(auctionItem);
	}

	/**
	 * 获得过期的拍卖物
	 */
	public List<AuctionItem> getExpiredAuctionItem() {

		return getExpiredAuctionItem(auctionList);
	}

	/**
	 * 获得过期的拍卖物
	 */
	public List<AuctionItem> getExpiredAuctionItem(List<AuctionItem> auctionItems) {
		List<AuctionItem> list = new ArrayList<AuctionItem>();
		for (AuctionItem auctionItem : auctionItems) {
			if (auctionItem.isExpired()) {
				list.add(auctionItem);
			}
		}
		return list;
	}

	/**
	 * 获得未过期的拍卖物
	 */
	public List<AuctionItem> getNotExpiredAuctionItem(List<AuctionItem> auctionItems) {
		List<AuctionItem> list = new ArrayList<AuctionItem>();
		for (AuctionItem auctionItem : auctionItems) {
			if (!auctionItem.isExpired()) {
				list.add(auctionItem);
			}
		}
		return list;
	}

	/**
	 * 创建一个拍卖物
	 * 
	 * @param playerId
	 * @param item
	 * @param price
	 * @param remainTime
	 * @return
	 */
	public synchronized AuctionItem createAuctionItem(String playerId, Item item, int price, long startTime, long endTime) {
		return new AuctionItem(playerId, item, price, startTime, endTime);
	}

	public void checkExpiredAuctionItem() {
		for (AuctionItem auctionItem : auctionList) {
			Item item = auctionItem.getItem();
			if (auctionItem.isExpired()) {
				if (removeAuctionItemByItemId(item.getId())) {
					if (!checkItemIsValid(auctionItem)) {
						logger.error("checkItemIsValid , auctionItem =" + auctionItem);
						continue;
					}
					Player seller = auctionItem.getPlayer();
					MailMgr.sendMailById(seller.getId(), "拍卖行出售物品失败", "您出售的" + item.getName() + "24小时内无人购买，请在附件中收取物品", item, Mail.auctionExpired);

					MGStatFunctions.AuctionStat(seller, StatAuction.Expried, auctionItem.getPrice(), seller.getName(), item);
				}
			}
		}
	}

	public synchronized void checkExpiredAuctionItem(Player player) {
		for (AuctionItem auctionItem : auctionList) {
			Item item = auctionItem.getItem();
			if (StringUtils.equals(player.getId(), auctionItem.getPlayerId()) && auctionItem.isExpired()) {
				if (removeAuctionItemByItemId(item.getId())) {
					if (!checkItemIsValid(auctionItem)) {
						logger.error("checkItemIsValid , auctionItem =" + auctionItem);
						continue;
					}
					Player seller = auctionItem.getPlayer();
					MailMgr.sendMailById(seller.getId(), "拍卖行出售物品失败", "您出售的" + item.getName() + "24小时内无人购买，请在附件中收取物品", item, Mail.auctionExpired);
					MGStatFunctions.AuctionStat(seller, StatAuction.Expried, auctionItem.getPrice(), seller.getName(), item);
					if (!StringUtils.equals(player.getId(), seller.getId())) {
						logger.error("checkExpiredAuctionItem , seller =" + seller + ", player = " + player);
					}
				}
			}
		}
	}

	public int getAuctionItemCount(String playerId) {
		int count = 0;
		for (AuctionItem auctionItem : auctionList) {
			if (auctionItem.getPlayerId().equals(playerId)) {
				++count;
			}
		}
		return count;
	}

	private boolean checkItemIsValid(AuctionItem auctionItem) {

		Item item = auctionItem.getItem();
		Mail mail = MailMgr.getMailByItemId(item.getId());
		if (mail == null) {
			return true;
		}

		if (item.isNonPropertyItem()) {
			return false;
		} else {
			if (auctionItem.getStartTime() < mail.getTime()) {
				return false;
			}
		}

		return true;
	}
}
