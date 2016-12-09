package sophia.mmorpg.auth.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_AuthEvent extends ActionEventBase {

	private String identityId;

	private String identityName;

	private String sign;

	private long timeStamp;

	private String uuid;

	private int qdCode1;

	private int qdCode2;

	public String getIdentityId() {
		return identityId;
	}

	public void setIdentityId(String identityId) {
		this.identityId = identityId;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getIdentityName() {
		return identityName;
	}

	public void setIdentityName(String identityName) {
		this.identityName = identityName;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public int getQdCode1() {
		return qdCode1;
	}

	public void setQdCode1(int qdCode1) {
		this.qdCode1 = qdCode1;
	}

	public int getQdCode2() {
		return qdCode2;
	}

	public void setQdCode2(int qdCode2) {
		this.qdCode2 = qdCode2;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		identityId = getString(buffer);
		identityName = getString(buffer);
		timeStamp = buffer.getLong();
		sign = getString(buffer);
		uuid = getString(buffer);
		qdCode1 = buffer.getInt();
		qdCode2 = buffer.getInt();
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer, identityId);
		putString(buffer, identityName);
		buffer.putLong(timeStamp);
		putString(buffer, sign);
		putString(buffer, uuid);
		buffer.putInt(qdCode1);
		buffer.putInt(qdCode2);
		return buffer;
	}

	@Override
	public String toString() {
		return "C2G_AuthEvent@:identityId:" + identityId + ",identityName:"
				+ identityName + ",sign:" + sign + ",tstamp:" + timeStamp
				+ ",uuid:" + uuid + ",qdCode1:" + qdCode1 + ",qdCode2:"
				+ qdCode2;
	}
	
	
	@Override
	public String getName() {
		return "账号认证";
	}

}
