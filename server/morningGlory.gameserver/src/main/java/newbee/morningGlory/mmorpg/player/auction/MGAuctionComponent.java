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
package newbee.morningGlory.mmorpg.player.auction;

import java.util.ArrayList;
import java.util.List;

import newbee.morningGlory.MorningGloryContext;
import newbee.morningGlory.mmorpg.auction.AuctionItem;
import newbee.morningGlory.mmorpg.auction.AuctionMgr;
import newbee.morningGlory.mmorpg.auction.AuctionSystemComponent;
import newbee.morningGlory.mmorpg.player.auction.event.C2G_Auction_Buy;
import newbee.morningGlory.mmorpg.player.auction.event.C2G_Auction_BuyList;
import newbee.morningGlory.mmorpg.player.auction.event.C2G_Auction_CancelSell;
import newbee.morningGlory.mmorpg.player.auction.event.C2G_Auction_DefaultPrice;
import newbee.morningGlory.mmorpg.player.auction.event.C2G_Auction_DoSell;
import newbee.morningGlory.mmorpg.player.auction.event.G2C_Auction_Buy;
import newbee.morningGlory.mmorpg.player.auction.event.G2C_Auction_BuyList;
import newbee.morningGlory.mmorpg.player.auction.event.G2C_Auction_CancelSell;
import newbee.morningGlory.mmorpg.player.auction.event.G2C_Auction_DefaultPrice;
import newbee.morningGlory.mmorpg.player.auction.event.G2C_Auction_DoSell;
import newbee.morningGlory.mmorpg.player.auction.event.G2C_Auction_SellList;
import newbee.morningGlory.mmorpg.player.auction.event.MGAuctionDefines;
import newbee.morningGlory.mmorpg.vip.MGPlayerVipComponent;
import newbee.morningGlory.stat.MGStatFunctions;
import newbee.morningGlory.stat.logs.StatAuction;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.GameObjectFactory;
import sophia.mmorpg.Mail.Mail;
import sophia.mmorpg.Mail.MailMgr;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.code.CodeContext;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.code.MMORPGSuccessCode;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;

public class MGAuctionComponent extends ConcreteComponent<Player> {
	private static Logger logger = Logger.getLogger(MGAuctionComponent.class);
	public static final String Tag = "MGAuctionComponent";

	private static final int DEFAULT_AUCTION_PERIOD = 24 * 3600 * 1000;

	// private static final int DEFAULT_AUCTION_PERIOD = 30 * 1000; // for debug

	public MGAuctionComponent() {

	}

	@Override
	public void ready() {
		addActionEventListener(MGAuctionDefines.C2G_Auction_Buy);
		addActionEventListener(MGAuctionDefines.C2G_Auction_BuyList);
		addActionEventListener(MGAuctionDefines.C2G_Auction_CancelSell);
		addActionEventListener(MGAuctionDefines.C2G_Auction_DoSell);
		addActionEventListener(MGAuctionDefines.C2G_Auction_SellList);
		addActionEventListener(MGAuctionDefines.C2G_Auction_DefaultPrice);
	}

	@Override
	public void suspend() {
		removeActionEventListener(MGAuctionDefines.C2G_Auction_Buy);
		removeActionEventListener(MGAuctionDefines.C2G_Auction_BuyList);
		removeActionEventListener(MGAuctionDefines.C2G_Auction_CancelSell);
		removeActionEventListener(MGAuctionDefines.C2G_Auction_DoSell);
		removeActionEventListener(MGAuctionDefines.C2G_Auction_SellList);
		removeActionEventListener(MGAuctionDefines.C2G_Auction_DefaultPrice);
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {

	}

	@Override
	public void handleActionEvent(ActionEventBase event) {

		switch (event.getActionEventId()) {
		case MGAuctionDefines.C2G_Auction_Buy:
			handle_Auction_Buy(event);
			break;
		case MGAuctionDefines.C2G_Auction_BuyList:
			handle_Auction_BuyList(event);
			break;
		case MGAuctionDefines.C2G_Auction_CancelSell:
			handle_Auction_CancelSell(event);
			break;
		case MGAuctionDefines.C2G_Auction_DoSell:
			handle_Auction_DoSell(event);
			break;
		case MGAuctionDefines.C2G_Auction_SellList:
			handle_Auction_SellList(event);
			break;
		case MGAuctionDefines.C2G_Auction_DefaultPrice:
			handle_Auction_DefaultPrice(event);
			break;
		default:
			break;
		}
	}

	private void handle_Auction_DefaultPrice(ActionEventBase event) {
		C2G_Auction_DefaultPrice message = (C2G_Auction_DefaultPrice) event;
		String id = message.getId();
		int count = message.getNumber();

		AuctionSystemComponent auctionSystemComponent = MorningGloryContext.getAuctionSystemComponent();
		AuctionMgr auctionMgr = auctionSystemComponent.getAuctionMgr();
		int defaultPrice = auctionMgr.defaultPrice(getConcreteParent(), id, count);
		if (logger.isDebugEnabled()) {
			logger.debug("handle_Auction_DefaultPrice id " + id + " default price " + defaultPrice);
		}
		AuctionItem auctionItem = auctionMgr.getAuctionItemByItemId(id);
		int number = auctionItem == null ? 1 : auctionItem.getItem().getNumber();
		G2C_Auction_DefaultPrice response = new G2C_Auction_DefaultPrice(id, defaultPrice, number);
		GameRoot.sendMessage(event.getIdentity(), response);

	}

	/**
	 * 我的出售列表
	 * 
	 * @param event
	 */
	private void handle_Auction_SellList(ActionEventBase event) {
		AuctionSystemComponent auctionSystemComponent = MorningGloryContext.getAuctionSystemComponent();
		AuctionMgr auctionMgr = auctionSystemComponent.getAuctionMgr();
		Player player = getConcreteParent();
		List<AuctionItem> myAuctionItems = auctionMgr.getAuctionItems(player);

		if (logger.isDebugEnabled()) {
			logger.debug("handle_Auction_SellList " + myAuctionItems);
		}

		G2C_Auction_SellList res = MessageFactory.getConcreteMessage(MGAuctionDefines.G2C_Auction_SellList);
		res.setAuctionItems(myAuctionItems);
		res.setPlayer(getConcreteParent());
		GameRoot.sendMessage(event.getIdentity(), res);
		auctionMgr.checkExpiredAuctionItem(player);
	}

	/**
	 * 出售物品
	 * 
	 * @param event
	 */
	public void handle_Auction_DoSell(ActionEventBase event) {
		C2G_Auction_DoSell message = (C2G_Auction_DoSell) event;
		int result = doSell(message);
		if (result != MMORPGSuccessCode.CODE_SUCCESS) {
			if (logger.isDebugEnabled()) {
				logger.debug("handle_Auction_DoSell error " + CodeContext.description(result));
			}
			ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), result);
			return;
		}
		G2C_Auction_DoSell res = MessageFactory.getConcreteMessage(MGAuctionDefines.G2C_Auction_DoSell);
		res.setItemId(message.getId());
		GameRoot.sendMessage(event.getIdentity(), res);

	}

	/**
	 * 取消出售
	 * 
	 * @param event
	 */
	private void handle_Auction_CancelSell(ActionEventBase event) {
		C2G_Auction_CancelSell message = (C2G_Auction_CancelSell) event;
		AuctionSystemComponent auctionSystemComponent = MorningGloryContext.getAuctionSystemComponent();
		AuctionMgr auctionMgr = auctionSystemComponent.getAuctionMgr();
		String id = message.getId();
		AuctionItem auctionItem = auctionMgr.getAuctionItemByItemId(id);
		if (auctionItem == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("handle_Auction_CancelSell auction item not exist: " + id);
			}
			ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MMORPGErrorCode.CODE_AUCTION_ITEM_NOT_EXIST);
			return;
		}
		Player player = getConcreteParent();
		Item item = auctionItem.getItem();
		if (!StringUtils.equals(player.getId(), auctionItem.getPlayerId())) {
			ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MMORPGErrorCode.CODE_AUCTION_ITEM_STOLEN);
			logger.error("auctionItem stolen :" + item.getName());
			return;
		}

		boolean cancel = auctionMgr.removeAuctionItemByItemId(id);
		if (cancel) {
			MailMgr.sendMailById(player.getId(), "取消拍卖物品成功", "您已经成功的在拍卖行取消了对" + item.getName() + "的出售，请在附件中收取物品", item, Mail.auctionCancel);

			MGStatFunctions.AuctionStat(player, StatAuction.cancelSell, auctionItem.getPrice(), player.getName(), item);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("handle_Auction_CancelSell id " + id + " player " + player.getName());
		}

		G2C_Auction_CancelSell res = MessageFactory.getConcreteMessage(MGAuctionDefines.G2C_Auction_CancelSell);
		res.setItemId(id);
		GameRoot.sendMessage(event.getIdentity(), res);
	}

	/**
	 * 根据条件获取拍卖物列表
	 * 
	 * @param event
	 */
	private void handle_Auction_BuyList(ActionEventBase event) {
		C2G_Auction_BuyList message = (C2G_Auction_BuyList) event;
		int tag = message.getTag();
		int from = message.getFrom();
		int to = message.getTo();
		short level = message.getLevel();
		short bodyAreaId = message.getBodyAreaId();
		String name = message.getName();
		short canUseLimit = message.getCanUseLimit();
		short itemType = message.getItemType();
		Player player = getConcreteParent();

		if (logger.isDebugEnabled()) {
			logger.debug("handle_Auction_BuyList from " + from + " to " + to + " level " + level + " bodyAreaId " + bodyAreaId + " name \'" + name + "\' canUseLimit "
					+ canUseLimit + " itemType " + itemType);
		}

		if (from > to) {
			ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MMORPGErrorCode.CODE_AUCTION_DATA_ERROR);
			return;
		}

		AuctionSystemComponent auctionSystemComponent = MorningGloryContext.getAuctionSystemComponent();
		AuctionMgr auctionMgr = auctionSystemComponent.getAuctionMgr();
		List<AuctionItem> findAuction = auctionMgr.findAuction(player, name, level, itemType, bodyAreaId, canUseLimit);

		int maxIndex = findAuction.size() - 1;
		List<AuctionItem> auctionItems = new ArrayList<AuctionItem>();

		if (from > maxIndex) {
			G2C_Auction_BuyList res = new G2C_Auction_BuyList(auctionItems, -1, -1, findAuction.size(), tag, player);
			GameRoot.sendMessage(event.getIdentity(), res);
			return;
		} else {
			from = Math.max(0, from);
			to = Math.min(maxIndex, to);

			for (int i = from; i <= to; ++i) {
				auctionItems.add(findAuction.get(i));
			}
			G2C_Auction_BuyList res = new G2C_Auction_BuyList(auctionItems, from, to, findAuction.size(), tag, player);
			GameRoot.sendMessage(event.getIdentity(), res);
			return;

		}

	}

	/**
	 * 购买物品
	 * 
	 * @param event
	 */
	private void handle_Auction_Buy(ActionEventBase event) {
		C2G_Auction_Buy message = (C2G_Auction_Buy) event;
		String id = message.getItemId();
		AuctionSystemComponent auctionSystemComponent = MorningGloryContext.getAuctionSystemComponent();
		AuctionMgr auctionMgr = auctionSystemComponent.getAuctionMgr();
		AuctionItem auctionItem = auctionMgr.getAuctionItemByItemId(id);
		if (auctionItem == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("handle_Auction_Buy auction item not exist: " + id);
			}
			ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MMORPGErrorCode.CODE_AUCTION_ITEM_NOT_EXIST);
			return;
		}
		Player player = getConcreteParent();
		int price = auctionItem.getPrice();
		int haveUnBindedGold = player.getPlayerMoneyComponent().getUnbindGold();
		if (haveUnBindedGold < price) {
			if (logger.isDebugEnabled()) {
				logger.debug("handle_Auction_Buy not enough money: " + id);
			}
			ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MMORPGErrorCode.CODE_AUCTION_NOT_ENOUGH_MONEY);
			return;
		}
		if (auctionMgr.removeAuctionItemByItemId(id)) {
			Item item = auctionItem.getItem();
			Player seller = auctionItem.getPlayer();
			player.getPlayerMoneyComponent().subUnbindGold(price, ItemOptSource.auction);
			MailMgr.sendMailById(seller.getId(), "拍卖行出售物品成功", "您在拍卖行寄卖的" + item.getName() + "已成功出售，出售价格为" + price + "元宝，请在附件处收取元宝。", Mail.auctionDelayed, "", price, 0, 0, "");
			MailMgr.sendMailById(player.getId(), "拍卖行购买物品成功", "您在拍卖行花费" + price + "元宝成功购买了" + item.getName() + "，请在附件处提取物品。", item, Mail.auctionNormal);
			MGStatFunctions.AuctionStat(seller, StatAuction.BeSell, auctionItem.getPrice(), player.getName(), item);
			MGStatFunctions.AuctionStat(player, StatAuction.Buy, auctionItem.getPrice(), seller.getName(), item);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("handle_Auction_Buy buyer " + player.getName() + " seller " + auctionItem.getPlayer().getName() + " price " + price);
		}
		G2C_Auction_Buy res = MessageFactory.getConcreteMessage(MGAuctionDefines.G2C_Auction_Buy);
		res.setItemId(id);
		GameRoot.sendMessage(event.getIdentity(), res);
	}

	private int doSell(C2G_Auction_DoSell message) {
		AuctionSystemComponent auctionSystemComponent = MorningGloryContext.getAuctionSystemComponent();
		AuctionMgr auctionMgr = auctionSystemComponent.getAuctionMgr();

		String itemId = message.getId();
		int number = message.getNumber();
		int price = message.getPrice();
		Player player = getConcreteParent();
		Item item = ItemFacade.getItemById(player, itemId);
		if (item == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("物品不存在");
			}
			return MMORPGErrorCode.CODE_AUCTION_ITEM_NOT_EXIST;
		}
		if (price <= 0 || price > 999999) {
			if (logger.isDebugEnabled()) {
				logger.debug("价格不合理");
			}
			return MMORPGErrorCode.CODE_AUCIION_INVALID_PRICE;
		}
		if (number > item.getNumber() || number <= 0) {
			if (logger.isDebugEnabled()) {
				logger.debug("出售的物品数量比物品本身数量要大");
			}
			return MMORPGErrorCode.CODE_AUCTION_ITEM_NOT_ENOUGH;
		}
		if (item.binded()) {
			if (logger.isDebugEnabled()) {
				logger.debug("物品已绑定");
			}
			return MMORPGErrorCode.CODE_AUCTION_ITEM_BINDED;
		}
		if (auctionMgr.getAuctionItemCount(player.getId()) >= getMaxAuctionCount(player)) {
			if (logger.isDebugEnabled()) {
				logger.debug("超出个数限制");
			}
			return MMORPGErrorCode.CODE_AUCTION_TIMES_LIMITED;
		}

		Item aItem = null;
		if (ItemFacade.removeItemById(player, itemId, number, ItemOptSource.auction)) {
			if (!item.isNonPropertyItem()) { // 锻造过的装备
				aItem = item.getNewItem();
			} else {
				aItem = GameObjectFactory.getItem(item.getItemRefId());
				aItem.setNumber(number);
			}

			long startTime = System.currentTimeMillis();
			long endTime = startTime + DEFAULT_AUCTION_PERIOD;
			AuctionItem auctionItem = auctionMgr.createAuctionItem(player.getId(), aItem, price, startTime, endTime);
			auctionMgr.addAuctionItem(auctionItem);
			MGStatFunctions.AuctionStat(getConcreteParent(), StatAuction.Sell, getConcreteParent().getName(), item, aItem);
			if (logger.isDebugEnabled()) {
				logger.debug("doSell auctionItem " + auctionItem);
			}
			return MMORPGSuccessCode.CODE_SUCCESS;
		}

		return MMORPGErrorCode.CODE_AUCTION_DATA_ERROR;

	}

	private int getMaxAuctionCount(Player player) {
		MGPlayerVipComponent vipComponent = (MGPlayerVipComponent) player.getTagged(MGPlayerVipComponent.Tag);
		int maxAuctionCount = AuctionItem.AUCTION_COUNT_LIMIT + vipComponent.getAddedMaxAuctionCount();
		return maxAuctionCount;
	}

}
