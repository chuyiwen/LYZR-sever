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
package sophia.mmorpg.player.scene.event;

import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_Scene_PickUp extends ActionEventBase {

	private List<String> lootIdList = new ArrayList<>();
	
	@Override
	public void unpackBody(IoBuffer buffer) {
		short size = buffer.getShort();
		while (size-- > 0) {
			lootIdList.add(getString(buffer));
		}
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {		
		buffer.putShort((short)lootIdList.size());
		for(int i=0;i<lootIdList.size();i++){
			putString(buffer, lootIdList.get(i));
		}
		return buffer;
	}

	public List<String> getLootIdList() {
		return lootIdList;
	}

	public void setLootIdList(List<String> lootIdList) {
		this.lootIdList = lootIdList;
	}

}
