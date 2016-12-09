package sophia.mmorpg.player.equipment.persistence;

import java.util.List;

import org.apache.log4j.Logger;

import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.mmorpg.GameObjectFactory;
import sophia.mmorpg.equipmentSmith.EqiupmentComponentProvider;
import sophia.mmorpg.equipmentSmith.EquipmentSmithComponent;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.player.equipment.PlayerEquipBody;
import sophia.mmorpg.player.equipment.PlayerEquipBodyArea;
import sophia.mmorpg.player.itemBag.persistence.ItemPersistenceObject;
import sophia.mmorpg.player.persistence.AbstractPersistenceObjectReadWrite;
import sophia.mmorpg.player.persistence.PersistenceObjectReadWrite;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class EquipmentReadWrite extends AbstractPersistenceObjectReadWrite<PlayerEquipBody> implements PersistenceObjectReadWrite<PlayerEquipBody> {

	private static final Logger logger = Logger.getLogger(EquipmentReadWrite.class);
	private PlayerEquipBody playerEquipBody;

	public EquipmentReadWrite(PlayerEquipBody playerEquipBody) {
		this.playerEquipBody = playerEquipBody;
	}

	@Override
	public byte[] toBytes(PlayerEquipBody persistenceObject) {
		return toBytesVer10001(persistenceObject);
	}

	@Override
	public PlayerEquipBody fromBytes(byte[] persistenceBytes) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer(persistenceBytes);
		int version = buffer.readInt();
		if (version == 10000) {
			return fromBytesVer10000(persistenceBytes);
		} else if (version == 10001) {
			return fromBytesVer10001(persistenceBytes);
		}
		return fromBytesVer10001(persistenceBytes);
	}

	@Override
	public String toJsonString(PlayerEquipBody persistenceObject) {
		return null;
	}

	@Override
	public PlayerEquipBody fromJsonString(String persistenceJsonString) {
		return null;
	}

	public byte[] toBytesVer10001(PlayerEquipBody persistenceObject) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();

		buffer.writeInt(Default_Write_Version + 1);
		int count = 0;
		for (PlayerEquipBodyArea bodyArea : persistenceObject.getBodyAreaList()) {
			Item[] equipArray = bodyArea.getEquipmentArray();
			if (equipArray.length == 1) {
				if (equipArray[0] != null)
					count++;
			}
			if (equipArray.length == 2) {
				if (equipArray[0] != null) {
					count++;
				}
				if (equipArray[1] != null) {
					count++;
				}
			}
		}
		buffer.writeInt(count);// 穿戴装备的bodyArea的数量
		for (PlayerEquipBodyArea bodyArea : persistenceObject.getBodyAreaList()) {
			Item[] equipArray = bodyArea.getEquipmentArray();
			if (!bodyArea.isLeftRightBodyArea() && equipArray[0] != null) {
				Item item = equipArray[0];
				byte bodyId = MGPropertyAccesser.getAreaOfBody(item.getItemRef().getProperty());// bodyid==item的AreaOfBody
				buffer.writeByte(bodyId);
				buffer.writeByte(PlayerEquipBodyArea.Left_Position);
				ItemPersistenceObject.toBytes(Default_Write_Version + 1, buffer, item);
			}

			if (bodyArea.isLeftRightBodyArea()) {
				if (equipArray[0] != null) {
					Item item = equipArray[0];
					byte bodyId = MGPropertyAccesser.getAreaOfBody(item.getItemRef().getProperty());// bodyid==item的AreaOfBody
					buffer.writeByte(bodyId);
					buffer.writeByte(PlayerEquipBodyArea.Left_Position);
					ItemPersistenceObject.toBytes(Default_Write_Version + 1, buffer, item);
				}
				if (equipArray[1] != null) {
					Item item = equipArray[1];
					byte bodyId = MGPropertyAccesser.getAreaOfBody(item.getItemRef().getProperty());// bodyid==item的AreaOfBody
					buffer.writeByte(bodyId);
					buffer.writeByte(PlayerEquipBodyArea.Right_Position);
					ItemPersistenceObject.toBytes(Default_Write_Version + 1, buffer, item);
				}
			}

		}

		return buffer.getData();
	}

	private PlayerEquipBody fromBytesVer10001(byte[] persistenceBytes) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer(persistenceBytes);
		buffer.readInt();
		List<PlayerEquipBodyArea> bodyAreaList = playerEquipBody.getBodyAreaList();
		if (logger.isDebugEnabled()) {
			logger.debug("-------------------list size:" + bodyAreaList.size());
		}
		int count = buffer.readInt();
		for (int i = 0; i < count; i++) {

			byte bodyId = buffer.readByte();
			byte position = buffer.readByte();
			Item item = ItemPersistenceObject.fromBytes(Default_Write_Version + 1, buffer);
			for (int j = 0; j < bodyAreaList.size(); j++) {
				PlayerEquipBodyArea bodyArea = bodyAreaList.get(j);// 遍历整个bodyarea的每一个部位，包括没有装备的
				if (bodyArea.getId() == bodyId) {
					if (bodyId == 7 || bodyId == 8) {
						bodyArea.setOrResetEquipment(item, position);
					} else {
						bodyArea.setOrResetEquipment(item);
					}
				}

			}
		}
		playerEquipBody.setBodyAreaList(bodyAreaList);
		return playerEquipBody;
	}

	public byte[] toBytesVer1000(PlayerEquipBody persistenceObject) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();

		buffer.writeInt(Default_Write_Version);
		int count = 0;
		for (PlayerEquipBodyArea bodyArea : persistenceObject.getBodyAreaList()) {
			Item[] equipArray = bodyArea.getEquipmentArray();
			if (equipArray.length == 1) {
				if (equipArray[0] != null)
					count++;
			}
			if (equipArray.length == 2) {
				if (equipArray[0] != null) {
					count++;
				}
				if (equipArray[1] != null) {
					count++;
				}
			}
		}
		buffer.writeInt(count);// 穿戴装备的bodyArea的数量
		for (PlayerEquipBodyArea bodyArea : persistenceObject.getBodyAreaList()) {
			Item[] equipArray = bodyArea.getEquipmentArray();
			if (!bodyArea.isLeftRightBodyArea() && equipArray[0] != null) {
				Item item = equipArray[0];
				toBytes(buffer, item, PlayerEquipBodyArea.Left_Position);
			}

			if (bodyArea.isLeftRightBodyArea()) {
				if (equipArray[0] != null) {
					Item item = equipArray[0];
					toBytes(buffer, item, PlayerEquipBodyArea.Left_Position);
				}
				if (equipArray[1] != null) {
					Item item = equipArray[1];
					toBytes(buffer, item, PlayerEquipBodyArea.Right_Position);
				}
			}

		}

		return buffer.getData();
	}

	private PlayerEquipBody fromBytesVer10000(byte[] persistenceBytes) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer(persistenceBytes);
		buffer.readInt();
		List<PlayerEquipBodyArea> bodyAreaList = playerEquipBody.getBodyAreaList();
		if (logger.isDebugEnabled()) {
			logger.debug("-------------------list size:" + bodyAreaList.size());
		}
		int count = buffer.readInt();
		for (int i = 0; i < count; i++) {
			String id = buffer.readString();
			byte bodyId = buffer.readByte();
			byte position = buffer.readByte();
			String itemRefId = buffer.readString();
			byte bindStatus = buffer.readByte();
			Item item = GameObjectFactory.getItem(itemRefId);
			item.setId(id);
			item.setBindStatus(bindStatus);
			for (int j = 0; j < bodyAreaList.size(); j++) {
				PlayerEquipBodyArea bodyArea = bodyAreaList.get(j);// 遍历整个bodyarea的每一个部位，包括没有装备的
				if (bodyArea.getId() == bodyId) {
					if (bodyId == 7 || bodyId == 8) {
						bodyArea.setOrResetEquipment(item, position);
					} else {
						bodyArea.setOrResetEquipment(item);
					}
				}

			}
			byte isNonPropertyItem = buffer.readByte();
			if (isNonPropertyItem == 1) {

				int itemLength = buffer.readInt();
				byte[] itemProperties = buffer.readBytes(itemLength);
				int washLength = buffer.readInt();
				byte[] washProperties = buffer.readBytes(washLength);

				item.getProperty().loadDictionary(itemProperties);
				EquipmentSmithComponent equipmentSmithComponent = EqiupmentComponentProvider.getEquipmentSmithComponent(item);
				equipmentSmithComponent.getEquipmentSmithMgr().getQiangHuaEquipmentSmith().onPropertyChange();
				equipmentSmithComponent.getEquipmentSmithMgr().getXiLianEquipmentSmith().getPropertyDictionary().loadDictionary(washProperties);
				int attachLenght = buffer.readInt();
				byte[] attachProperties = buffer.readBytes(attachLenght);
				item.getNewAttachPropertyIfNull().loadDictionary(attachProperties);
			}

		}

		playerEquipBody.setBodyAreaList(bodyAreaList);
		return playerEquipBody;
	}

	public ByteArrayReadWriteBuffer toBytes(ByteArrayReadWriteBuffer buffer, Item item, byte position) {

		buffer.writeString(item.getId()); // id
		buffer.writeString(item.getItemRef().getId());
		buffer.writeByte(item.getBindStatus());

		if (!item.isNonPropertyItem()) {
			buffer.writeByte((byte) 1); // 用于标识 isNonPropertyItem

			byte[] itemProperties = item.getProperty().toByteArray();
			byte[] washProperties = EqiupmentComponentProvider.getEquipmentSmithComponent(item).getEquipmentSmithMgr().getXiLianEquipmentSmith().getPropertyDictionary()
					.toByteArray();

			buffer.writeInt(itemProperties.length);
			buffer.writeBytes(itemProperties);
			buffer.writeInt(washProperties.length);
			buffer.writeBytes(washProperties);
			// 放最后
			byte[] attachProperties = item.getNewAttachPropertyIfNull().toByteArray();
			buffer.writeInt(attachProperties.length);
			buffer.writeBytes(attachProperties);

		} else
			buffer.writeByte((byte) 0);

		return buffer;
	}

}
