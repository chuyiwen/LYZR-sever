package newbee.morningGlory.mmorpg.player.activity;

import sophia.foundation.util.ByteArrayReadWriteBuffer;

public interface ActivityReadWrite {
	public static final int Default_Write_Version = 10000;
	
	public byte[] toBytes();
	
	public void fromBytes(ByteArrayReadWriteBuffer buffer);
}
