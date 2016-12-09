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
package newbee.morningGlory.mmorpg.sprite.buff;

/**
 * 周期计时类型
 */
public final class MGFightSpriteBuffDurationType {
	/** 从创建开始，绝对的计时*/
	public static final byte Always_Duration_Tick = 1;
	/** 从创建开始，只在线才计时*/
	public static final byte Online_Only_Duration_Tick = 2;
	/** 从创建开始，运行期确定怎么个情况下计时*/
	public static final byte RuntimeSet_Duration_Tick = 3;
	
	private MGFightSpriteBuffDurationType() {
		
	}
}
