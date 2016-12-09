package sophia.mmorpg.Mail.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_GM_Mail_Send extends ActionEventBase {
	private byte type;
	private String content;
	private String url;
	@Override
	public void unpackBody(IoBuffer buffer) {
		type = buffer.get();
		content=getString(buffer);
		url=getString(buffer);
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		return null;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
