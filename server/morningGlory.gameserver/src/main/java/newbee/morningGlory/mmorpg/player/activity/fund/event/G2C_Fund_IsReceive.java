package newbee.morningGlory.mmorpg.player.activity.fund.event;

import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Fund_IsReceive extends ActionEventBase {

	private int count;
	private Map<Byte, Byte> map;//基金领取状态集合  key:基金类型   value：基金领取状态（1：不可领取   0：可领取）

	@Override
	protected IoBuffer packBody(IoBuffer arg0) {
		arg0.putInt(count);
		if(map==null){
			return arg0;
		}
		if(map.isEmpty()){
			return arg0;
		}
		for(Map.Entry<Byte, Byte> entry : map.entrySet()){
			arg0.put(entry.getKey());//基金类型
			arg0.put(entry.getValue());//基金状态
		}
		return arg0;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {
		// TODO Auto-generated method stub
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Map<Byte, Byte> getMap() {
		return map;
	}

	public void setMap(Map<Byte, Byte> map) {
		this.map = map;
	}

}
