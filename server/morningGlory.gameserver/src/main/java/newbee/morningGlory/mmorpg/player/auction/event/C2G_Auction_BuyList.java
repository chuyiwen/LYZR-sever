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

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_Auction_BuyList extends ActionEventBase {

	private int tag;
	private int from;
	private int to;
	private String name;
	private short bodyAreaId;
	private short level;
	private short canUseLimit;
	private short itemType;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		tag = buffer.getInt();
		from = buffer.getInt();
		to = buffer.getInt();
		name = getString(buffer);
		bodyAreaId = buffer.getShort();
		level = buffer.getShort();
		canUseLimit = buffer.getShort();
		itemType = buffer.getShort();
	}

	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public int getTo() {
		return to;
	}

	public void setTo(int to) {
		this.to = to;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public short getBodyAreaId() {
		return bodyAreaId;
	}

	public void setBodyAreaId(short id) {
		this.bodyAreaId = id;
	}

	public short getLevel() {
		return level;
	}

	public void setLevel(short level) {
		this.level = level;
	}

	public short getCanUseLimit() {
		return canUseLimit;
	}

	public void setCanUseLimit(short canUseLimit) {
		this.canUseLimit = canUseLimit;
	}

	public short getItemType() {
		return itemType;
	}

	public void setItemType(short itemType) {
		this.itemType = itemType;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

}
