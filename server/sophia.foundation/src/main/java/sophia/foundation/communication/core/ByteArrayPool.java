package sophia.foundation.communication.core;

import java.util.Arrays;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.util.ObjectPool;

public class ByteArrayPool {
	private byte[] bytes = new byte[2048];
	
	public byte[]  getData(){
		return bytes;
	}
	
	public int getlength(){
		if(bytes!=null)
			return bytes.length;
		
		return 0;
	}

	public static final ObjectPool<ByteArrayPool> pool = new ObjectPool<ByteArrayPool>() {

		@Override
		protected ByteArrayPool instance() {
			return new ByteArrayPool();
		}

		@Override
		protected void onRecycle(ByteArrayPool obj) {
			obj.clear();
		}
	};

	public void clear() {
		Arrays.fill(bytes,(byte)0);
	}
	
	public void setByteData(IoBuffer dataBuffer) {
		
		dataBuffer.flip();
		if(dataBuffer.limit()>2048){
			bytes = null;
			bytes = new byte[dataBuffer.limit()];
		}			
		dataBuffer.get(bytes, 0, dataBuffer.limit());

	}
	
	public void setByteData(IoBuffer dataBuffer,int len) {
		
		if(dataBuffer.limit()>2048){
			bytes = null;
			bytes = new byte[len];
		}			
		dataBuffer.get(bytes, 0, len);

	}
}
