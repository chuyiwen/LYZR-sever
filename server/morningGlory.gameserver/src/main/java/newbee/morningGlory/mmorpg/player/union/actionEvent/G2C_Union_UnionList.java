package newbee.morningGlory.mmorpg.player.union.actionEvent;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.util.ByteArrayReadWriteBuffer;

public class G2C_Union_UnionList extends ActionEventBase {
	public static final byte ThreeDaysAgo = 1;
	public static final byte Yesterday = 2;
	public static final byte Today = 3;
	public static final byte Online = 4;
	
	private byte unionListType;
	private ByteArrayReadWriteBuffer byteArrayReadWriteBuffer;

	public G2C_Union_UnionList(){
		ziped =(byte)1;
	}
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.put(unionListType);
		buffer.put(byteArrayReadWriteBuffer.getData());
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {

	}

	public void setUnionListType(byte unionListType) {
		this.unionListType = unionListType;
	}

	public void setByteArrayReadWriteBuffer(ByteArrayReadWriteBuffer byteArrayReadWriteBuffer) {
		this.byteArrayReadWriteBuffer = byteArrayReadWriteBuffer;
	}

}
