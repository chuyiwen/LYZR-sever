package sophia.mmorpg.player.equipment.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemWriter;

public class G2C_Equip_Update extends ActionEventBase {
	private byte eventType;
	private short count;
	private byte bodyId;
	private byte position;
	private Item item;
	private Player player;

	public G2C_Equip_Update(){
		ziped =(byte)1;
	}
	
	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		ByteArrayReadWriteBuffer dataBuffer = new ByteArrayReadWriteBuffer();
		dataBuffer.writeByte(eventType);
		dataBuffer.writeByte((byte)count);
		dataBuffer.writeString(item.getId()); // item 的唯一标示id
		dataBuffer.writeByte(bodyId);
		dataBuffer.writeByte(position);
		String itemRefId = item == null ? "" : item.getItemRef().getId();
		dataBuffer.writeString(itemRefId);
		ItemWriter.write(player, dataBuffer, item);	
		return buffer.put(dataBuffer.getData());
	}

	

	@Override
	public void unpackBody(IoBuffer arg0) {
		// TODO Auto-generated method stub

	}

	public String getName() {
		return "人物装备镶嵌返回";
	}

	public byte getEventType() {
		return eventType;
	}

	public void setEventType(byte eventType) {
		this.eventType = eventType;
	}

	public short getCount() {
		return count;
	}

	public void setCount(short count) {
		this.count = count;
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
