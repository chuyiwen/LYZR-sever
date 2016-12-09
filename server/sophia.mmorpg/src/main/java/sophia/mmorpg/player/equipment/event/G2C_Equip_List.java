package sophia.mmorpg.player.equipment.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.mmorpg.player.equipment.EquipMgr;

public class G2C_Equip_List extends ActionEventBase {
	private EquipMgr equipMgr;

	public G2C_Equip_List(){
		ziped =(byte)1;
	}
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		ByteArrayReadWriteBuffer byteArrayReadWriteBuffer = equipMgr.writeEquipListToBufferArray();
		buffer.put(byteArrayReadWriteBuffer.getData());
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {
		// TODO Auto-generated method stub

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

}
