package newbee.morningGlory.mmorpg.player.union.actionEvent;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_Union_Chat extends ActionEventBase {
	private String unionName;
	private String msg;

	@Override
	public void unpackBody(IoBuffer buffer) {
		unionName = getString(buffer);
		this.msg = getString(buffer);
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer, msg);
		return buffer;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getName() {
		return "公会";
	}

	public String getUnionName() {
		return unionName;
	}

	public void setUnionName(String unionName) {
		this.unionName = unionName;
	}

}
