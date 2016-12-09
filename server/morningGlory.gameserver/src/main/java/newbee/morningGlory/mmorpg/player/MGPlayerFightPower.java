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
package newbee.morningGlory.mmorpg.player;

import java.util.List;

import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuff;
import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuffComponent;
import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuffMgr;
import sophia.foundation.property.PropertyDictionary;
import sophia.mmorpg.base.sprite.fightProperty.FightEffectProperty;
import sophia.mmorpg.base.sprite.fightProperty.FightPropertyMgr;
import sophia.mmorpg.core.PropertyDictionaryModifyPhase;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.property.PlayerFightPower;

public class MGPlayerFightPower implements PlayerFightPower {
	private Player player;

	@Override
	@SuppressWarnings("unchecked")
	public int getFightPower() {
		PropertyDictionaryModifyPhase snapshot = null;
		try {
			snapshot = player.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotFromPool();
			PropertyDictionary fightPd = snapshot.getPropertyDictionary();
			MGFightSpriteBuffComponent<Player> buffComponent = (MGFightSpriteBuffComponent<Player>) player.getTagged(MGFightSpriteBuffComponent.Tag);
			MGFightSpriteBuffMgr buffMgr = buffComponent.getFightSpriteBuffMgr();
			List<MGFightSpriteBuff> fightSpriteBuffList = buffMgr.getFightSpriteBuffList();
			for (MGFightSpriteBuff buff : fightSpriteBuffList) {
				if (buff.getFightSpriteBuffRef().isChangeFightValueBuff()) {

					PropertyDictionary buffPd = buff.getSpecialProperty();
					for (short symbol : FightEffectProperty.fightEffectSymbols) {
						if (buffPd.contains(symbol)) {
							int value = buffPd.getValue(symbol);
							int fightValue = fightPd.getValue(symbol);
							int newValue = fightValue;
							if (buff.getFightSpriteBuffRef().isPositiveBuff()) {
								newValue = newValue - value;
							} else {
								newValue = newValue + value;
							}
							fightPd.setOrPutValue(symbol, newValue);
						}
					}
				}
			}

			return player.getFightPower(fightPd);
		} finally {
			FightPropertyMgr.recycleSnapshotToPool(snapshot);
		}

	}

	public MGPlayerFightPower(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

}
