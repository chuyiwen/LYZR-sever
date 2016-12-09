package sophia.mmorpg.player.equipment.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemWriter;

public class G2C_Equip_Info extends ActionEventBase {

	private byte bodyId;
	private byte position;
	private Item item;
	private Player player;

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {

		putString(buffer, item.getId()); // item 的唯一标示id
		buffer.put(bodyId);
		buffer.put(position);
		putString(buffer, item.getItemRef().getId());

		ItemWriter.write(player, buffer, item);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {
		// TODO Auto-generated method stub

	}

	public String getName() {
		return "人物装备查看";
	}

	public byte getBodyId() {
		return bodyId;
	}

	public void setBodyId(byte bodyId) {
		this.bodyId = bodyId;
	}

	public byte getPosition() {
		return position;
	}

	public void setPosition(byte posIndex) {
		this.position = posIndex;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

}
