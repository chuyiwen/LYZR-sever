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
package newbee.morningGlory.mmorpg.player.peerage;

import sophia.game.GameRoot;
import sophia.mmorpg.player.Player;

/**
 * 爵位管理
 */
public final class MGPeerageRefMgr {
	private MGPeerageRef crtPeerageRef;
	private String dateTime;
	private Player player;

	public MGPeerageRefMgr() {
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public MGPeerageRef getCrtPeerageRef() {
		return crtPeerageRef;
	}

	public void setCrtPeerageRef(MGPeerageRef crtPeerageRef) {
		this.crtPeerageRef = crtPeerageRef;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public MGPeerageRef getPeerageRef(String peerageRefId){
		return (MGPeerageRef) GameRoot.getGameRefObjectManager().getManagedObject(peerageRefId);
	}
}
