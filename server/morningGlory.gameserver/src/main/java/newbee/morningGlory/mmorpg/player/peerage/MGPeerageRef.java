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

import java.util.List;

import com.google.common.base.Strings;

import sophia.game.GameRoot;
import sophia.game.ref.AbstractGameRefObjectBase;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

/**
 * 爵位
 */
public final class MGPeerageRef extends AbstractGameRefObjectBase {
	private static final long serialVersionUID = 1740083297627269691L;

	private int levelCondition;// 所需等级

	private int meritCondition;// 所需功勋

	List<ItemPair> itemPairs;

	public MGPeerageRef() {
	}

	public byte getCrtKnightLevel() {
		return MGPropertyAccesser.getKnight(getProperty());
	}

	public MGPeerageRef getNextPeerageRef() {
		String nextPeerageRefId = MGPropertyAccesser
				.getKnightNextRefId(getProperty());
		MGPeerageRef nextPeerageRef = null;
		if (!Strings.isNullOrEmpty(nextPeerageRefId)) {
			nextPeerageRef = (MGPeerageRef) GameRoot.getGameRefObjectManager()
					.getManagedObject(nextPeerageRefId);
		}
		return nextPeerageRef;
	}

	public int getLevelCondition() {
		return levelCondition;
	}

	public void setLevelCondition(int levelCondition) {
		this.levelCondition = levelCondition;
	}

	public int getMeritCondition() {
		return meritCondition;
	}

	public void setMeritCondition(int meritCondition) {
		this.meritCondition = meritCondition;
	}

	public List<ItemPair> getItemPairs() {
		return itemPairs;
	}

	public void setItemPairs(List<ItemPair> itemPairs) {
		this.itemPairs = itemPairs;
	}
	
	public int getRoleGrade(){
		return MGPropertyAccesser.getRoleGrade(getProperty());
	}

}
