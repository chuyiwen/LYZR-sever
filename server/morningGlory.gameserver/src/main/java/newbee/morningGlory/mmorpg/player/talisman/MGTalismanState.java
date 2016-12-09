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
package newbee.morningGlory.mmorpg.player.talisman;

/**
 * 法宝-状态
 */
public final class MGTalismanState {
	public static final byte Active_State = 2;
	
	public static final byte Inactive_State = 1;
	
	public static final byte InAcquire_State = 0;
	
	public static final byte WingQuest_Complete = 1;
	
	public static final byte MountQuest_Complete = 1;
		
	private MGTalismanState() {
		
	}
}
