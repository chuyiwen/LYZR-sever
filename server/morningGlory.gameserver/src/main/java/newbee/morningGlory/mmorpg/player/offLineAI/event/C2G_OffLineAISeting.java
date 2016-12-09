package newbee.morningGlory.mmorpg.player.offLineAI.event;

import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

/**
 * 玩家离线挂机的AI设置
 */
public class C2G_OffLineAISeting extends ActionEventBase{
	private int hp;//hp下限设置
	private int mp;//mp下限设置
	private int equipLv;//装备等级    -1为什么都没选
	private List<Byte> qualityList = new ArrayList<Byte>();//装备品质
	private List<Byte> professionIdList = new ArrayList<Byte>();//装备职业
	
	
	@Override
	public void unpackBody(IoBuffer buffer) {
		this.hp = buffer.getInt();
		this.mp = buffer.getInt();
		this.equipLv = buffer.getInt();
		
		byte qualitySize = buffer.get();
		for (int i = 0; i < qualitySize; i++) {
			int quality = buffer.getInt();
			this.qualityList.add((byte)quality);
		}
		byte professionIdSize = buffer.get();
		for (int i = 0; i < professionIdSize; i++) {
			int professionId = buffer.getInt();
			this.professionIdList.add((byte)professionId);
		}
	}
	
	@Override
	protected IoBuffer packBody(IoBuffer arg0) {
		return null;
	}

	public int getHp() {
		return hp;
	}

	public int getMp() {
		return mp;
	}

	public int getEquipLv() {
		return equipLv;
	}

	public List<Byte> getQualityList() {
		return qualityList;
	}

	public List<Byte> getProfessionIdList() {
		return professionIdList;
	}


}
