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
package sophia.mmorpg.player.mount;

import sophia.game.component.GameObject;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

/**
 * 玩家-坐骑
 */
public class Mount extends GameObject {

	public static final byte MOUNT_STATE_UP = 1;// 上马状态
	public static final byte MOUNT_STATE_DOWN = -1;// 上马状态

	public static final byte UP = 0; // 请求上马
	public static final byte DOWN = 1; // 请求下马
	
	private MountRef mountRef;
	private long exp;
	private byte mountState;
	
	public Mount() {
		setProperty(null);
	}

	public MountRef getMountRef() {
		return mountRef;
	}

	public void setMountRef(MountRef mountRef) {
		this.mountRef = mountRef;
	}

	public void setExp(long totalExp) {
		exp = totalExp;
	}

	public long getExp() {
		return exp;
	}

	public String getNextRefId() {
		return MGPropertyAccesser.getRideNextRefId(mountRef.getProperty());
	}

	public String getCrtRefId() {
		return mountRef.getId();
	}

	public long getCrtMaxExp() {
		return MGPropertyAccesser.getMaxExp(mountRef.getProperty());
	}

	public byte getMountState() {
		return mountState;
	}

	/**
	 * 上马
	 */
	public void up() {
		setMountState(MOUNT_STATE_UP);
	}

	/**
	 * 下马
	 */
	public void down() {
		setMountState(MOUNT_STATE_DOWN);
	}

	public void setMountState(byte mountState) {
		this.mountState = mountState;
	}

}
