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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class AuctionItem {

	private String id;
	private String playerId;
	private Item item;
	private int price;
	private long startTime;
	private long endTime;

	public static byte AUCTION_EQUIP = 1;
	public static byte AUCTION_NON_EQUIP = 2;
	public static byte AUCTION_DEFAULT = -1;
	public static byte AUCTION_USABLE = 1;
	public static byte AUCTION_NOT_LIMITED = 0;
	public static byte AUCTION_DEFAULT_PRICE = 1;
	public static byte AUCTION_COUNT_LIMIT = 10;

	public AuctionItem(String playerId, Item item, int price, long startTime, long endTime) {
		setId(UUID.randomUUID().toString());
		this.playerId = playerId;
		this.item = item;
		checkNotNull(item, "item is null");
		this.price = price;
		checkArgument(price >= 0, "price is negative.");
		this.startTime = startTime;
		this.endTime = endTime;
		checkArgument(endTime > startTime, "endTime is before startTime");
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public Player getPlayer() {
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		Player player = playerManager.getPlayer(this.playerId);
		checkNotNull(player, "player manager has not this player!");
		return player;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getRemainTime() {
		long remainTime = this.endTime - System.currentTimeMillis();
		remainTime = remainTime < 0 ? 0 : remainTime;
		int time = (int) (remainTime / 1000);
		return time;
	}

	public boolean isExpired() {
		return getRemainTime() <= 0;
	}

	public byte getItemType() {
		if (item.getItemType() != AUCTION_EQUIP)
			return AUCTION_NON_EQUIP;
		return AUCTION_EQUIP;
	}

	public byte getQuality() {
		return item.getQuality();
	}

	public int getUseLevel() {
		if (item.isEquip()) {
			return MGPropertyAccesser.getEquipLevel(item.getItemRef().getProperty());
		} else {
			return MGPropertyAccesser.getUseLevel(item.getItemRef().getProperty());
		}

	}

	public String getName() {
		return item.getName();
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (endTime ^ (endTime >>> 32));
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((item == null) ? 0 : item.hashCode());
		result = prime * result + ((playerId == null) ? 0 : playerId.hashCode());
		result = prime * result + price;
		result = prime * result + (int) (startTime ^ (startTime >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AuctionItem other = (AuctionItem) obj;
		if (endTime != other.endTime)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (item == null) {
			if (other.item != null)
				return false;
		} else if (!item.equals(other.item))
			return false;
		if (playerId == null) {
			if (other.playerId != null)
				return false;
		} else if (!playerId.equals(other.playerId))
			return false;
		if (price != other.price)
			return false;
		if (startTime != other.startTime)
			return false;
		return true;
	}

	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
		return "AuctionItem [id = " + id + ", playerId=" + playerId + ", item=" + item.getName() + " , itemId=" + item.getId() + ", price=" + price + ", startTime="
				+ sdf.format(new Date(startTime)) + ", endTime=" + sdf.format(new Date(endTime)) + "]";
	}
}
