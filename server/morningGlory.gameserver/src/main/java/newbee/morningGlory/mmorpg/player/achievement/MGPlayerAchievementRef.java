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
package newbee.morningGlory.mmorpg.player.achievement;

import java.util.ArrayList;
import java.util.List;

import sophia.game.GameRoot;
import sophia.game.ref.AbstractGameRefObjectBase;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public final class MGPlayerAchievementRef extends AbstractGameRefObjectBase {
	private static final long serialVersionUID = 6494091710894238352L;
	private List<ItemPair> itemPairs = new ArrayList<ItemPair>();

	public MGPlayerAchievementRef() {

	}

	public MGPlayerAchievementRef getNextAchieveRef() {
		String nextAchieve = MGPropertyAccesser.getNextAchieve(getProperty());
		return (MGPlayerAchievementRef) GameRoot.getGameRefObjectManager().getManagedObject(nextAchieve);
	}

	public String getTargetRefId() {
		return MGPropertyAccesser.getTargetID(getProperty());
	}

	public int getTargetNum() {
		return MGPropertyAccesser.getTargetNum(getProperty());
	}
	
	

	public List<ItemPair> getItemPairs() {
		return itemPairs;
	}

	public void setItemPairs(List<ItemPair> itemPairs) {
		this.itemPairs = itemPairs;
	}

}
