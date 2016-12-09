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
package sophia.mmorpg.base.scene.aoi;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import sophia.foundation.util.Pair;

public final class EightDirection {
	/** 右 */
	public static final byte Right_Direction = 0;
	/** 右下 */
	public static final byte RightDown_Direction = 1;
	/** 下 */
	public static final byte Down_Dicretion = 2;
	/** 左下 */
	public static final byte LeftDown_Dicretion = 3;
	/** 左 */
	public static final byte Left_Dicretion = 4;
	/** 左上 */
	public static final byte LeftUp_Dicretion = 5;
	/** 上 */
	public static final byte Up_Dicretion = 6;
	/** 右上 */
	public static final byte RightUp_Dicretion = 7;
	/** 方向总数 **/
	public static final byte nDirection = 8;

	// 八个方向，起始方向是正东，按照逆时针方向旋转，pair的key代表row的增量，value代表column的增量
	private final static Map<Byte, Pair<Integer, Integer>> deltas = new HashMap<>();
	
	private final static Map<Byte, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> angleValue = new HashMap<>();

	static {
		Pair<Integer, Integer> pair1 = new Pair<Integer, Integer>(0, 1);
		Pair<Integer, Integer> pair2 = new Pair<Integer, Integer>(1, 1);
		Pair<Integer, Integer> pair3 = new Pair<Integer, Integer>(1, 0);
		Pair<Integer, Integer> pair4 = new Pair<Integer, Integer>(1, -1);
		Pair<Integer, Integer> pair5 = new Pair<Integer, Integer>(0, -1);
		Pair<Integer, Integer> pair6 = new Pair<Integer, Integer>(-1, -1);
		Pair<Integer, Integer> pair7 = new Pair<Integer, Integer>(-1, 0);
		Pair<Integer, Integer> pair8 = new Pair<Integer, Integer>(-1, 1);
		
		deltas.put(Right_Direction, pair1);
		deltas.put(RightDown_Direction, pair2);
		deltas.put(Down_Dicretion, pair3);
		deltas.put(LeftDown_Dicretion, pair4);
		deltas.put(Left_Dicretion, pair5);
		deltas.put(LeftUp_Dicretion, pair6);
		deltas.put(Up_Dicretion, pair7);
		deltas.put(RightUp_Dicretion, pair8);
		
		angleValue.put(Right_Direction, new Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>(pair1, pair5));
		angleValue.put(Left_Dicretion, new Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>(pair1, pair5));
		angleValue.put(RightDown_Direction, new Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>(pair3, pair1));
		angleValue.put(Down_Dicretion, new Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>(pair3, pair7));
		angleValue.put(Up_Dicretion, new Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>(pair3, pair7));
		angleValue.put(LeftDown_Dicretion, new Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>(pair5, pair7));
		angleValue.put(LeftUp_Dicretion, new Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>(pair5, pair1));
		angleValue.put(RightUp_Dicretion, new Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>(pair1, pair3));
	}

	public static Map<Byte, Pair<Integer, Integer>> getEightangle() {
		return Collections.unmodifiableMap(deltas);
	}

	public static Pair<Integer, Integer> getAngleValue(final byte direction) {
		return deltas.get(direction);
	}
	
	public static Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> getLRAngleValue(final byte direction) {
		return angleValue.get(direction);
	}

}
