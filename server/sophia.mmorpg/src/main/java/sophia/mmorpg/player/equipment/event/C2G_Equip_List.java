package sophia.mmorpg.player.equipment.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_Equip_List extends ActionEventBase {

	@Override
	protected IoBuffer packBody(IoBuffer arg0) {
		return null;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {

	}

	public String getName() {
		return "请求装备列表";
	}

}
