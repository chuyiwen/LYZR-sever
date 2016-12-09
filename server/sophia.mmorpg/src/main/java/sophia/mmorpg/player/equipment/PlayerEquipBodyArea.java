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

import sophia.mmorpg.item.Item;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

/**
 * 玩家-装备身体-部位
 */
public final class PlayerEquipBodyArea {
	/**每类装备对应的部位id*/
	/**武器*/
	public static final byte weaponBodyId = 1;
	/**衣服*/
	public static final byte clothesBodyId = 2;
	/**头盔*/
	public static final byte helmetBodyId = 3;
	/**腰带*/
	public static final byte beltBodyId = 4;
	/**鞋子*/
	public static final byte shoesBodyId = 5;
	/**项链*/
	public static final byte necklaceBodyId = 6;
	/**手镯*/
	public static final byte braceletBodyId = 7;
	/**戒指*/
	public static final byte ringBodyId = 8;
	/**勋章*/
	public static final byte medalBodyId = 9;
	/** 对于分左右的装备部位，取 <b>左部位</b> 的索引位置 */
	public static final byte Left_Position = 0;

	/** 对于分左右的装备部位，取 <b>右部位</b> 的索引位置 */
	public static final byte Right_Position = 1;

	/** 装备部位 Id */
	private byte id;

	/** 装备数组。如果是单个的部位，数组长度=1；如果是分左右的部分，数组长度=2， 0 = left， 1 = right; */
	private Item[] equipmentArray;

	private PlayerEquipBodyArea() {

	}

	public static final PlayerEquipBodyArea singleBodyArea(byte bodyAreaId) {
		PlayerEquipBodyArea bodyArea = new PlayerEquipBodyArea();

		bodyArea.id = bodyAreaId;
		bodyArea.equipmentArray = new Item[1];
		bodyArea.equipmentArray[0] = null;

		return bodyArea;
	}

	public static final PlayerEquipBodyArea leftRightBodyArea(byte bodyAreaId) {
		PlayerEquipBodyArea bodyArea = new PlayerEquipBodyArea();

		bodyArea.id = bodyAreaId;
		bodyArea.equipmentArray = new Item[2];
		bodyArea.equipmentArray[0] = null;
		bodyArea.equipmentArray[1] = null;

		return bodyArea;
	}

	public final byte getId() {
		return id;
	}

	public final boolean equalBodyAreaId(Item equipment) {
		byte areaOfBodyId = MGPropertyAccesser.getAreaOfBody(equipment.getItemRef().getProperty());
		return areaOfBodyId == id;
	}

	public final boolean isLeftRightBodyArea() {
		return equipmentArray.length == 2;
	}

	public final Item setOrResetEquipment(Item equipment) {
		
		if (!equalBodyAreaId(equipment)) {
			throw new RuntimeException(NotEqualBodyAreaId);
		}

		if (isLeftRightBodyArea()) {
			throw new RuntimeException(IsLeftRightBodyArea);
		}

		Item old = this.equipmentArray[0];
		this.equipmentArray[0] = equipment;

		return old;
	}

	public final Item getEquipment() {
		if (isLeftRightBodyArea()) {
			throw new RuntimeException(IsLeftRightBodyArea);
		}

		return this.equipmentArray[0];
	}

	public final void removeEquipment() {
		if (isLeftRightBodyArea()) {
			throw new RuntimeException(IsLeftRightBodyArea);
		}

		this.equipmentArray[0] = null;
	}

	public final Item setOrResetEquipment(Item equipment, byte leftOrRightPositon) {
		if (!equalBodyAreaId(equipment)) {
			throw new RuntimeException(NotEqualBodyAreaId);
		}

		if (!isLeftRightBodyArea()) {
			throw new RuntimeException(IsSingleBodyArea);
		}

		Item old = this.equipmentArray[leftOrRightPositon];
		this.equipmentArray[leftOrRightPositon] = equipment;

		return old;
	}

	public final Item getEquipment(byte leftOrRightPositon) {
		if (!isLeftRightBodyArea()) {
			throw new RuntimeException(IsSingleBodyArea);
		}

		return this.equipmentArray[leftOrRightPositon];
	}

	public final void removeEquipment(byte leftOrRightPositon) {
		if (!isLeftRightBodyArea()) {
			throw new RuntimeException(IsSingleBodyArea);
		}

		this.equipmentArray[leftOrRightPositon] = null;
	}

	public final Item[] getEquipmentArray() {
		return this.equipmentArray;
	}

	private static final String NotEqualBodyAreaId = "装备的装备部位不相等。";

	private static final String IsLeftRightBodyArea = "这是分左右的装备部位。但调用的时候，把它当作单个的装备部位。";

	private static final String IsSingleBodyArea = "这是单个的装备部位。但调用的时候，把它当作分左右的装备部位。";
}
