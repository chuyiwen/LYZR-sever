/**
 * 
 */
package newbee.morningGlory.mmorpg.player.activity.QuickRecharge.event;

import java.util.Set;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

/**
 * @author Administrator2014-6-19
 *
 */
public class G2C_QuickRecharge_List extends ActionEventBase{
	
	private Set<String> quickRefSet;
	
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		int size = quickRefSet.size();
		buffer.put((byte)size);
		for(String quickRefId : quickRefSet){
			putString(buffer, quickRefId);
		}
		return buffer;
	}
	
	@Override
	public void unpackBody(IoBuffer buffer) {
		
	}

	public Set<String> getQuickRefSet() {
		return quickRefSet;
	}

	public void setQuickRefSet(Set<String> quickRefSet) {
		this.quickRefSet = quickRefSet;
	}
	
	
}
