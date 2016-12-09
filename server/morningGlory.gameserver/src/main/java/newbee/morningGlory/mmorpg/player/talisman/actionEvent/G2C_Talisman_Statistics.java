/**
 * 
 */
package newbee.morningGlory.mmorpg.player.talisman.actionEvent;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;


/**
 * @author yinxinglin
 *
 */
public class G2C_Talisman_Statistics extends ActionEventBase {
	
	private Map<Byte,Long> map ;
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		if(map != null){
			buffer.putShort((short)map.size());
			for(Entry<Byte,Long> entry : map.entrySet()){
				buffer.put(entry.getKey());
				buffer.putLong(entry.getValue());
			}
		}
		return buffer;
	}
	@Override
	public void unpackBody(IoBuffer buffer) {
		
		
	}
	public Map<Byte,Long> getMap() {
		return map;
	}
	public void setMap(Map<Byte,Long> map) {
		this.map = map;
	}
	
}
