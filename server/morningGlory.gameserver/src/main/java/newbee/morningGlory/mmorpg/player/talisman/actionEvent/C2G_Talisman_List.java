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
package newbee.morningGlory.mmorpg.player.talisman.actionEvent;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public final class C2G_Talisman_List extends ActionEventBase {
	
	public C2G_Talisman_List() {
		actionEventId = TalisManEventDefines.C2G_Talisman_List;
	}
	@Override
	protected IoBuffer packBody(IoBuffer paramIoBuffer) {
		return null;
	}
	@Override
	public void unpackBody(IoBuffer paramIoBuffer) {
		
	}
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "求情法宝列表";
	}
}
