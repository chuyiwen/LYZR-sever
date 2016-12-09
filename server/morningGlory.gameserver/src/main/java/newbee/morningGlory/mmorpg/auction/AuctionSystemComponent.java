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

import sophia.game.component.AbstractComponent;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.core.timer.SFTimeChimeListener;
import sophia.mmorpg.core.timer.SFTimer;
import sophia.mmorpg.core.timer.SFTimerCreater;

public class AuctionSystemComponent extends AbstractComponent {

	private AuctionMgr auctionMgr = new AuctionMgr();
	private SFTimer timer;
	
	public AuctionSystemComponent() {
		checkAuctionItemPerHour();
	}
	public AuctionMgr getAuctionMgr() {
		return auctionMgr;
	}

	public void setAuctionMgr(AuctionMgr auctionMgr) {
		this.auctionMgr = auctionMgr;
	}

	private void checkAuctionItemPerHour() {
		if (timer != null) {
			return;
		}
		SFTimerCreater timerCreater = MMORPGContext.getTimerCreater();
		timer = timerCreater.halfHourCalendarChime(new SFTimeChimeListener() {

			@Override
			public void handleTimeChimeCancel() {

			}

			@Override
			public void handleTimeChime() {
				auctionMgr.checkExpiredAuctionItem();
			}

			@Override
			public void handleServiceShutdown() {

			}
		});

	}

	
}
