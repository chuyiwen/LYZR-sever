package sophia.mmorpg.player.mount.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_Mount_Feed extends ActionEventBase {
	// 数量
	private int num;
	// 物品ID
	private String itemRefId;

	@Override
	public void unpackBody(IoBuffer buffer) {
		num = buffer.getInt();
		itemRefId = getString(buffer);
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		return buffer;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getItemRefId() {
		return itemRefId;
	}

	public void setItemRefId(String itemRefId) {
		this.itemRefId = itemRefId;
	}

	public String getName() {
		return "喂养";
	}
}
