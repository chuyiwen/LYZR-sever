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

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
/**
 * 
 * 
 * 本协议仅供机器人测试
 */
public class C2G_Scene_Move extends ActionEventBase {
	private int srcX; // 开始x
	private int srcY; // 开始y
	private int dstX; // 结束x
	private int dstY; // 结束y

	@Override
	public void unpackBody(IoBuffer buffer) {
		srcX = buffer.getInt();
		srcY = buffer.getInt();
		dstX = buffer.getInt();
		dstY = buffer.getInt();
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.putInt(srcX);
		buffer.putInt(srcY);
		buffer.putInt(dstX);
		buffer.putInt(dstY);
		return buffer;
	}

	@Override
	public String getName() {
		return "开始移动";
	}

	public int getSrcX() {
		return srcX;
	}

	public void setSrcX(int srcX) {
		this.srcX = srcX;
	}

	public int getSrcY() {
		return srcY;
	}

	public void setSrcY(int srcY) {
		this.srcY = srcY;
	}

	public int getDstX() {
		return dstX;
	}

	public void setDstX(int dstX) {
		this.dstX = dstX;
	}

	public int getDstY() {
		return dstY;
	}

	public void setDstY(int dstY) {
		this.dstY = dstY;
	}
}
