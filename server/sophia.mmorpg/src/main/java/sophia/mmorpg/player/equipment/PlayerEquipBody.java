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
package sophia.mmorpg.player.equipment;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import sophia.mmorpg.item.Item;

/**
 * 玩家-装备身体
 */
public final class PlayerEquipBody {
	private List<PlayerEquipBodyArea> bodyAreaList;

	public PlayerEquipBody() {
	}

	public final PlayerEquipBodyArea getBodyArea(byte bodyAreaId) {
		for (PlayerEquipBodyArea bodyArea : bodyAreaList) {
			if (bodyArea.getId() == bodyAreaId) {
				return bodyArea;
			}
		}

		return null;
	}
	public final PlayerEquipBodyArea getBodyAreaEquip(String id) {
		for (PlayerEquipBodyArea bodyArea : bodyAreaList) {
			if (bodyArea.isLeftRightBodyArea()) {
				Item equipment = bodyArea.getEquipment(PlayerEquipBodyArea.Left_Position);
				if(equipment !=null && StringUtils.equals(equipment.getId(),id)){
					return bodyArea;
				}else{
					equipment = bodyArea.getEquipment(PlayerEquipBodyArea.Right_Position);
					if(equipment !=null && StringUtils.equals(equipment.getId(),id)){
						return bodyArea;
					}
				}
			}else{
				Item equipment = bodyArea.getEquipment();
				if(equipment !=null && StringUtils.equals(equipment.getId(),id)){
					return bodyArea;
				}
			}
			
		}

		return null;
	}
	public final List<PlayerEquipBodyArea> getBodyAreaList() {
		return bodyAreaList;
	}

	public final void setBodyAreaList(List<PlayerEquipBodyArea> bodyAreaList) {
		this.bodyAreaList = bodyAreaList;
	}
}
