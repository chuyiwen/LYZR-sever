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

import newbee.morningGlory.event.MGEventDefines;
import sophia.foundation.communication.core.MessageFactory;

public class MGAuctionDefines {

	public static final short C2G_Auction_BuyList = MGEventDefines.Auction_Message_Begin + 1;
	public static final short G2C_Auction_BuyList = MGEventDefines.Auction_Message_Begin + 2;
	public static final short C2G_Auction_Buy = MGEventDefines.Auction_Message_Begin + 3;
	public static final short G2C_Auction_Buy = MGEventDefines.Auction_Message_Begin + 4;
	public static final short C2G_Auction_SellList = MGEventDefines.Auction_Message_Begin + 5;
	public static final short G2C_Auction_SellList = MGEventDefines.Auction_Message_Begin + 6;
	public static final short C2G_Auction_DoSell = MGEventDefines.Auction_Message_Begin + 7;
	public static final short G2C_Auction_DoSell = MGEventDefines.Auction_Message_Begin + 8;
	public static final short C2G_Auction_CancelSell = MGEventDefines.Auction_Message_Begin + 9;
	public static final short G2C_Auction_CancelSell = MGEventDefines.Auction_Message_Begin + 10;
	public static final short C2G_Auction_DefaultPrice = MGEventDefines.Auction_Message_Begin + 11;
	public static final short G2C_Auction_DefaultPrice = MGEventDefines.Auction_Message_Begin + 12;

	public static void registerActionEvents() {

		MessageFactory.addMessage(C2G_Auction_BuyList, C2G_Auction_BuyList.class);
		MessageFactory.addMessage(G2C_Auction_BuyList, G2C_Auction_BuyList.class);
		MessageFactory.addMessage(C2G_Auction_Buy, C2G_Auction_Buy.class);
		MessageFactory.addMessage(G2C_Auction_Buy, G2C_Auction_Buy.class);
		MessageFactory.addMessage(C2G_Auction_SellList, C2G_Auction_SellList.class);
		MessageFactory.addMessage(G2C_Auction_SellList, G2C_Auction_SellList.class);
		MessageFactory.addMessage(C2G_Auction_DoSell, C2G_Auction_DoSell.class);
		MessageFactory.addMessage(G2C_Auction_DoSell, G2C_Auction_DoSell.class);
		MessageFactory.addMessage(C2G_Auction_CancelSell, C2G_Auction_CancelSell.class);
		MessageFactory.addMessage(G2C_Auction_CancelSell, G2C_Auction_CancelSell.class);
		MessageFactory.addMessage(C2G_Auction_DefaultPrice, C2G_Auction_DefaultPrice.class);
		MessageFactory.addMessage(G2C_Auction_DefaultPrice, G2C_Auction_DefaultPrice.class);

	}
}
