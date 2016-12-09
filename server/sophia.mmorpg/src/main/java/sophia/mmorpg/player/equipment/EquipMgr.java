package sophia.mmorpg.player.equipment;

import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemWriter;

public class EquipMgr {
	private Player player;
	
	public EquipMgr(Player player){
		this.player = player;
	}
	public ByteArrayReadWriteBuffer writeEquipListToBufferArray() {
		ByteArrayReadWriteBuffer equipdItemDataBuffer = new ByteArrayReadWriteBuffer();
		PlayerEquipBody equipBody = player.getPlayerEquipBodyConponent().getPlayerBody();
		byte count = 0;
		for (PlayerEquipBodyArea area : equipBody.getBodyAreaList()) {
			Item[] equips = area.getEquipmentArray();
			if (equips.length == 1) {
				if (equips[0] != null)
					count++;
			} else if (equips.length == 2) {
				if (equips[0] != null)
					count++;
				if (equips[1] != null)
					count++;
			}
		}
		equipdItemDataBuffer.writeByte(count);
		for (PlayerEquipBodyArea area : equipBody.getBodyAreaList()) {// 获取玩家装备身体部位
			Item[] equipArray = area.getEquipmentArray();

			byte id = area.getId();
			if (null == equipArray || equipArray.length == 0) {
				continue;// 该部位没有装备
			}

			if (area.isLeftRightBodyArea()) {
				if (area.getEquipment(PlayerEquipBodyArea.Left_Position) != null) {
					Item item = area.getEquipment(PlayerEquipBodyArea.Left_Position);
					write(item, PlayerEquipBodyArea.Left_Position, id, equipdItemDataBuffer);

				}

				if (area.getEquipment(PlayerEquipBodyArea.Right_Position) != null) {
					Item item = area.getEquipment(PlayerEquipBodyArea.Right_Position);
					write(item, PlayerEquipBodyArea.Right_Position, id, equipdItemDataBuffer);

				}
			} else if (area.getEquipment() != null) {
				Item item = area.getEquipment();
				write(item, PlayerEquipBodyArea.Left_Position, id, equipdItemDataBuffer);

			}

		}
		return equipdItemDataBuffer;
	}

	public void write(Item item, byte position, byte id, ByteArrayReadWriteBuffer buffer) {

		buffer.writeString(item.getId());
		buffer.writeByte(id);
		buffer.writeByte(position);
		buffer.writeString(item.getItemRefId());
		ItemWriter.write(player, buffer, item);
		
	}

	public String getName() {
		return "返回武器列表";
	}

	

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

}