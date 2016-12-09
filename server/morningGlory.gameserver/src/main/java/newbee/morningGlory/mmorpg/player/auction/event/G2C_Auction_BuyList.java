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
package newbee.morningGlory.mmorpg.player.auction.event;

import java.util.List;

import newbee.morningGlory.mmorpg.auction.AuctionItem;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemWriter;

public class G2C_Auction_BuyList extends ActionEventBase {
	private List<AuctionItem> auctionItems;
	private int from;
	private int to;
	private int maxCount;
	private int tag;

	private Player player;

	private static final Logger logger = Logger.getLogger(G2C_Auction_BuyList.class);

	public G2C_Auction_BuyList(List<AuctionItem> auctionItems, int from, int to, int maxCount, int tag, Player player) {
		super();
		this.auctionItems = auctionItems;
		this.from = from;
		this.to = to;
		this.maxCount = maxCount;
		this.setTag(tag);
		this.setPlayer(player);
		this.actionEventId = MGAuctionDefines.G2C_Auction_BuyList;
	}

	public G2C_Auction_BuyList() {
		super();
		this.actionEventId = MGAuctionDefines.G2C_Auction_BuyList;
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.putInt(tag);
		buffer.putInt(from);
		buffer.putInt(to);
		buffer.putInt(maxCount);
		buffer.putInt(auctionItems.size());
		for (AuctionItem auctionItem : auctionItems) {
			Item item = auctionItem.getItem();
			String itemId = item.getId();
			String itemRefId = item.getItemRefId();
			int remainTime = auctionItem.getRemainTime();
			int price = auctionItem.getPrice();
			putString(buffer, itemId);
			putString(buffer, itemRefId);
			buffer.putInt(remainTime);
			buffer.putInt(price);

			ItemWriter.write(player, buffer, item);
		}
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {

	}

	public List<AuctionItem> getAuctionItems() {
		return auctionItems;
	}

	public void setAuctionItems(List<AuctionItem> auctionItems) {
		this.auctionItems = auctionItems;
	}

	public int getTo() {
		return to;
	}

	public void setTo(int to) {
		this.to = to;
	}

	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public int getMaxCount() {
		return maxCount;
	}

	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

}
