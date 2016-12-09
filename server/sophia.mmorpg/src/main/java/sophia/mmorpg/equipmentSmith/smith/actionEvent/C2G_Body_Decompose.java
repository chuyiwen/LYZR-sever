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
package sophia.mmorpg.equipmentSmith.smith.actionEvent;

import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public final class C2G_Body_Decompose extends ActionEventBase {
	
	private short count;
	
	private List<byte[]> bodys = new ArrayList<byte[]>();
	@Override
	protected IoBuffer packBody(IoBuffer paramIoBuffer) {
		return null;
	}
	@Override
	public void unpackBody(IoBuffer paramIoBuffer) {
		count = paramIoBuffer.getShort();
		for(int i=0;i<count;i++){
			byte[] pos = new byte[2];
			pos[0] = paramIoBuffer.get();
			pos[1] = paramIoBuffer.get();
			bodys.add(pos);
		}
	}
	public short getCount() {
		return count;
	}
	public void setCount(short count) {
		this.count = count;
	}
	public List<byte[]> getBodys() {
		return bodys;
	}
	public void setBodys(List<byte[]> bodys) {
		this.bodys = bodys;
	}
	
	
}
