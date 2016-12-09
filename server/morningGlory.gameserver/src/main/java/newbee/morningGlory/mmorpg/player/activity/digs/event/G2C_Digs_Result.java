/**
 *   Copyright 2013-2015 Sophia
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
*/
package newbee.morningGlory.mmorpg.player.activity.digs.event;

import java.util.Collection;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.player.itemBag.ItemPair;

public class G2C_Digs_Result extends ActionEventBase {
	
	private Collection<ItemPair> digs;
	
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.putShort((short)digs.size());
		for(ItemPair item : digs){
			putString(buffer, item.getItemRefId());
			buffer.putInt(item.getNumber());
		}
		return buffer;
	}
	@Override
	public void unpackBody(IoBuffer buffer) {
		// TODO Auto-generated method stub
		
	}
	public Collection<ItemPair> getDigs() {
		return digs;
	}
	public void setDigs(Collection<ItemPair> digs) {
		this.digs = digs;
	}
	
	
}
