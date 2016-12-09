/**
 * 
 */
package sophia.mmorpg.Mail.persistence;

import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.player.itemBag.persistence.ItemPersistenceObject;

/**
 * @author Administrator
 *
 */
public class MailPersistenceObject {

	private static MailPersistenceObject instance = new MailPersistenceObject();
	private static int Default_Write_Version = 10000;

	private MailPersistenceObject() {

	}

	public static MailPersistenceObject getInstance() {
		return instance;
	}

	public byte[] toBytes(Item item) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();
		buffer.writeInt(Default_Write_Version + 1);
		try {
			ItemPersistenceObject.toBytes10001(buffer, item);
		} catch (Exception e) {
			throw e;
		}

		return buffer.getData();
	}

	public Item fromBytes(byte[] persistence) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer(persistence);
		int version = buffer.readInt();
		if (version == Default_Write_Version + 1) {
			Item item = null;
			try {
				item = ItemPersistenceObject.fromBytes(Default_Write_Version + 1, buffer);
			} catch (Exception e) {
				item = ItemPersistenceObject.fromBytes(Default_Write_Version, buffer);
			}
			return item;
		}
		buffer = new ByteArrayReadWriteBuffer(persistence);
		return ItemPersistenceObject.fromBytes(Default_Write_Version, buffer);

	}
}
