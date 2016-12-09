package sophia.mmorpg.player.property.event;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.mmorpg.player.equipment.EquipMgr;

public class G2C_OtherPlayer_EquipList extends ActionEventBase {
	private static Logger logger = Logger.getLogger(G2C_OtherPlayer_EquipList.class);
	private EquipMgr equipMgr;
	private String charId;

	public G2C_OtherPlayer_EquipList(){
		ziped =(byte)1;
	}
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		logger.debug("装备列表返回");
		ByteArrayReadWriteBuffer byteArrayReadWriteBuffer = equipMgr.writeEquipListToBufferArray();
		putString(buffer, charId);
		buffer.put(byteArrayReadWriteBuffer.getData());
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
	}

	public String getName() {
		return "返回装备列表";
	}

	public EquipMgr getEquipMgr() {
		return equipMgr;
	}

	public void setEquipMgr(EquipMgr equipMgr) {
		this.equipMgr = equipMgr;
	}

	public String getCharId() {
		return charId;
	}

	public void setCharId(String charId) {
		this.charId = charId;
	}

}
