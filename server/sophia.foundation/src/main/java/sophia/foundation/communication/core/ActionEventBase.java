/**
 * 
 */
package sophia.foundation.communication.core;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.authentication.Identity;
import sophia.foundation.util.DebugUtil;
import sophia.foundation.util.IoBufferUtil;

public abstract class ActionEventBase {
	private static final Logger logger = Logger.getLogger(ActionEventBase.class);
	
	private static final short DefaultBufferSize = 512;
	
	public static final short zipDataMinLength = 150;
	
	public static final int Immediately_Priority = Integer.MAX_VALUE;
	public static final int Delay_Priority = Integer.MIN_VALUE;
	// public long initTime = System.currentTimeMillis();

	protected short actionEventId;
	protected byte ziped;
	// protected int crcSum;
	
	protected short bufferSize = DefaultBufferSize;

	protected Identity identity;

	public ActionEventBase() {

	}

	public ActionEventBase(final short actionEventId) {
		this.actionEventId = actionEventId;
	}

	protected ActionEventBase(final short actionEventId, Identity identity) {
		this.actionEventId = actionEventId;
		this.identity = identity;
	}

	public final short getActionEventId() {
		return actionEventId;
	}

	public final void setActionEventId(final short actionEventId) {
		this.actionEventId = actionEventId;
	}

	public final Identity getIdentity() {
		return identity;
	}

	public final void setIdentity(Identity identity) {
		this.identity = identity;
	}
	
	public final void setBufferSize(final short bufferSize) {
		this.bufferSize = bufferSize;
	}

	public final void unpackFromBuffer(IoBuffer buffer) {
		if (buffer == null) {
			throw new NullPointerException();
		}

		try {
			unpackHead(buffer);
			unpackBody(buffer);
		} catch (Exception e) {
			logger.error(DebugUtil.printStack(e));
		}

	}

	public final IoBuffer packToBuffer() {
		final IoBuffer ioBuffer = IoBuffer.allocate(4096, false);
		ioBuffer.setAutoExpand(true);

		packHead(ioBuffer);
		if(ziped==1){
			IoBuffer newBuffer = IoBuffer.allocate(2048, false);	
			newBuffer.setAutoExpand(true);	
			
			packBody(newBuffer);
			
			ByteArrayPool bytePool = ByteArrayPool.pool.obtain();
			bytePool.setByteData(newBuffer);
			int len = bytePool.getlength();
			if (logger.isDebugEnabled()) {
				logger.debug(this.actionEventId +" before compress data size= " + len);
			}
			
			if(len > zipDataMinLength){
				setZiped((byte)1);
				byte[] zipData = zipBuffer(bytePool.getData());
				ioBuffer.putShort((short)len);
				ioBuffer.putShort((short)zipData.length);
				ioBuffer.put(zipData);	
				if (logger.isDebugEnabled()) {
					logger.debug(this.actionEventId +" after compress  data size= " + zipData.length);
				}
			}else{
				setZiped((byte)0);
				ioBuffer.put(bytePool.getData());
			}
			
			newBuffer.clear();
			ByteArrayPool.pool.recycle(bytePool);
			
		}else{
			packBody(ioBuffer);
		}

		int position = ioBuffer.position();
		// fix 2
		// ioBuffer.putInt(0, position - 4);
		
		ioBuffer.putShort(0, (short) (position - 2));
		ioBuffer.put(2, ziped);
		ioBuffer.flip();
		
		return ioBuffer;
	}

	public abstract void unpackBody(IoBuffer buffer);

	protected abstract IoBuffer packBody(final IoBuffer buffer);

	private final void packHead(IoBuffer buffer) {
		// fix 2
		// buffer.putInt(0);
		buffer.putShort((short) 0);
		buffer.put((byte)0);
		buffer.putShort(actionEventId);
		
	}

	private final IoBuffer unpackHead(final IoBuffer buffer) {
		// ziped = buffer.get();
		// this.actionEventId = buffer.getShort();
		return buffer;
	}

	public int getPriority() {
		return Immediately_Priority;
	}

	/**
	 * 返回消息名称
	 * 
	 * @return
	 */
	public String getName() {
		return "";
	}

	protected String getString(IoBuffer in) {

		return IoBufferUtil.getString(in);

	}

	protected IoBuffer putString(IoBuffer out, String s) {

		return IoBufferUtil.putString(out, s);
	}

	private static final byte TRUE = 1;
	private static final byte FALSE = 0;

	protected boolean getBoolean(IoBuffer in) {
		return byte2boolean(in.get());
	}

	protected void putBoolean(IoBuffer out, boolean value) {
		out.put(boolean2byte(value));
	}

	private boolean byte2boolean(byte value) {
		if (value == TRUE) {
			return true;
		}
		if (value == FALSE) {
			return false;
		}
		throw new IllegalArgumentException("无效的boolean预定义值:" + value);
	}

	private byte boolean2byte(boolean value) {
		if (value) {
			return TRUE;
		} else {
			return FALSE;
		}
	}

	protected byte[]  zipBuffer(byte[] dataBuffer){
		byte[] zipData = Zip.gZip(dataBuffer);
		return zipData;
	}
	protected IoBuffer unZipBuffer(byte[] zipData){
		byte[] unZipData = Zip.unGZip(zipData);		
		IoBuffer ioBuffer = IoBuffer.wrap(unZipData);	
		return ioBuffer;
	}
	
	public void setZiped(byte ziped){
		this.ziped = ziped;
	}
	public byte getZiped(){
		return this.ziped;
	}

}
