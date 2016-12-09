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

public class C2G_Auction_DefaultPrice extends ActionEventBase {
	private String id;
	private int number;

	public C2G_Auction_DefaultPrice() {
		super();
		this.actionEventId = MGAuctionDefines.C2G_Auction_DefaultPrice;
	}

	public C2G_Auction_DefaultPrice(String id, int number) {
		super();
		this.id = id;
		this.setNumber(number);
		this.actionEventId = MGAuctionDefines.C2G_Auction_DefaultPrice;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		this.setId(getString(buffer));
		this.setNumber(buffer.getInt());
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer, id);
		buffer.putInt(number);
		return buffer;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

}
