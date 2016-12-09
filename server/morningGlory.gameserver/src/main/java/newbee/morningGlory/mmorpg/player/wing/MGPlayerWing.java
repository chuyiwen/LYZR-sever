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
package newbee.morningGlory.mmorpg.player.wing;

import org.apache.log4j.Logger;

import sophia.foundation.property.PropertyDictionary;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public final class MGPlayerWing {
	private static Logger logger = Logger.getLogger(MGPlayerWing.class.getName());

	private MGPlayerWingRef playerWingRef;
	
	private long exp;

	public MGPlayerWing() {
	}

	public MGPlayerWingRef getPlayerWingRef() {
		return playerWingRef;
	}

	/**
	 * 设置翅膀的Ref，获得新的翅膀的时候要set一次
	 * 
	 * @param playerWingRef
	 */
	public void setPlayerWingRef(MGPlayerWingRef playerWingRef) {
		this.playerWingRef = playerWingRef;
	}

	@Override
	public String toString() {
		return "MGPlayerWing [playerWingRef=" + playerWingRef + "]";
	}

	public int getWingModleId() {
		MGPlayerWingRef playerWingRef = getPlayerWingRef();
		int modleId = 0;
		if (playerWingRef != null) {
			modleId = MGPropertyAccesser.getModelId(playerWingRef.getProperty());
		}
		return modleId;
	}

	public void broadcastWingModelProperty(Player player) {
		PropertyDictionary pd = new PropertyDictionary();
		MGPropertyAccesser.setOrPutWingModleId(pd, getWingModleId());
		MGPropertyAccesser.setOrPutWingModleId(player.getProperty(), getWingModleId());
		player.getAoiComponent().broadcastProperty(pd);
		player.notifyPorperty(pd);
	}

	public void setExp(long totalExp) {
		exp = totalExp;
	}

	public long getExp() {
		return exp;
	}

	public long getCrtMaxExp() {
		if (isCrtPlayerWingRefNull()) {
			return 0;
		}
		
		return MGPropertyAccesser.getMaxExp(playerWingRef.getProperty());
	}

	public String getNextRefId() {
		if (isCrtPlayerWingRefNull()) {
			return null;
		}
		
		return MGPropertyAccesser.getWingNextRefId(playerWingRef.getProperty());
	}

	public byte getCrtStageLevel() {
		if (isCrtPlayerWingRefNull()) {
			return 0;
		}
		
		return MGPropertyAccesser.getStageLevel(playerWingRef.getProperty());
	}
	
	public byte getCrtStarLevel() {
		if (isCrtPlayerWingRefNull()) {
			return 0;
		}
		
		return MGPropertyAccesser.getStartLevel(playerWingRef.getProperty());
	}

	public boolean isCrtPlayerWingRefNull() {
		if (playerWingRef == null) {
			logger.error("crtPlayerWingRef is null");
			return true;
		}

		return false;
	}
}
