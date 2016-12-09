/**
 * 
 */
package newbee.morningGlory.mmorpg.player.activity.QuickRecharge.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

/**
 * @author Administrator2014-6-19
 *
 */
public class C2G_QuickRecharge_List extends ActionEventBase{
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		
		return null;
	}
	
	@Override
	public void unpackBody(IoBuffer buffer) {
		
	}
	
}
