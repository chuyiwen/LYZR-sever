/**
 * 
 */
package sophia.foundation.util;

import org.apache.mina.core.buffer.IoBuffer;


public final class ByteArrayReadWriteBuffer {
	private static final int defaultCapacity = 512;

	private IoBuffer dataBuffer;
	private boolean readOnly=false;

	public ByteArrayReadWriteBuffer() {
		dataBuffer = IoBuffer.allocate(defaultCapacity);
		dataBuffer.setAutoExpand(true);
	}

	public ByteArrayReadWriteBuffer(IoBuffer ioBufer) {
		this.dataBuffer = ioBufer;
		readOnly = ioBufer.isReadOnly();
	}

	public ByteArrayReadWriteBuffer(int capacity) {
		dataBuffer = IoBuffer.allocate(capacity);
		dataBuffer.setAutoExpand(true);
	}

	public ByteArrayReadWriteBuffer(byte[] bytes) {
		dataBuffer = IoBuffer.wrap(bytes);
		readOnly = true;
	}
	
	public int position(){
		return dataBuffer.position();
	}
	
	public void writeInt(int postion,int value){
		dataBuffer.putInt(postion, value);
	}

	public ByteArrayReadWriteBuffer writeByte(byte b) {
		checkReadOnly();
		dataBuffer.put(b);
		return this;
	}

	public ByteArrayReadWriteBuffer writeBytes(byte[] bytes) {
		checkReadOnly();
		dataBuffer.put(bytes);
		return this;
	}

	public ByteArrayReadWriteBuffer writeBoolean(boolean v) {
		checkReadOnly();
		if (v) {
			dataBuffer.put((byte) 1);
		} else {
			dataBuffer.put((byte) 0);
		}
		return this;
	}

	public ByteArrayReadWriteBuffer writeChar(char v) {
		checkReadOnly();
		dataBuffer.putChar(v);
		return this;
	}

	public ByteArrayReadWriteBuffer writeShort(short v) {
		checkReadOnly();
		dataBuffer.putShort(v);
		return this;
	}

	public ByteArrayReadWriteBuffer writeInt(int v) {
		checkReadOnly();
		dataBuffer.putInt(v);
		return this;
	}

	public ByteArrayReadWriteBuffer writeLong(long v) {
		checkReadOnly();
		dataBuffer.putLong(v);
		return this;
	}

	public ByteArrayReadWriteBuffer writeFloat(float v) {
		checkReadOnly();
		dataBuffer.putFloat(v);
		return this;
	}

	public ByteArrayReadWriteBuffer writeDouble(double v) {
		checkReadOnly();
		dataBuffer.putDouble(v);
		return this;
	}

	public ByteArrayReadWriteBuffer writeString(String str) {
		checkReadOnly();
		IoBufferUtil.putString(dataBuffer, str);
		return this;
	}

	public byte readByte() {
		return dataBuffer.get();
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public byte[] readBytes(int size) {
		byte[] bytes = new byte[size];
		dataBuffer.get(bytes);
		return bytes;
	}

	public short readShort() {
		return dataBuffer.getShort();
	}

	public int readUnsignedShort() {
		return dataBuffer.getUnsignedShort();
	}

	public boolean readBoolean() {
		return dataBuffer.get() == 1 ? true : false;
	}

	public int readInt() {
		return dataBuffer.getInt();
	}

	public long readLong() {

		return dataBuffer.getLong();
	}

	public float readFloat() {
		return dataBuffer.getFloat();
	}

	public double readDouble() {
		return dataBuffer.getDouble();
	}

	public char readChar() {
		return dataBuffer.getChar();
	}

	public String readString() {
		
		return IoBufferUtil.getString(dataBuffer);
	}

	public byte[] getData() {
		if( readOnly )
		{
			byte[] bytes = new byte[dataBuffer.limit()];
			dataBuffer.get(bytes);
			return bytes;
		}
		else
		{
			dataBuffer.flip();
			byte[] bytes = new byte[dataBuffer.limit()];
			dataBuffer.get(bytes);
			return bytes;
		}
	}

	public boolean hasRemaining() {
		return dataBuffer.hasRemaining();
	}

	public int remaining() {
		return dataBuffer.remaining();
	}
	
	private void checkReadOnly()
	{
		if( readOnly )
		{
			throw new RuntimeException("当前的ByteArrayReadWriteBuffer被置为只能读取,不能写入!");
		}
	}
}
