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
package sophia.mmorpg.utils;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Random;

public final class SFRandomUtils {
	private static final Random random = new Random(System.currentTimeMillis());

	private SFRandomUtils() {

	}

	public static final int random10() {
		return random.nextInt(10) + 1;
	}

	public static final int random100() {
		return random.nextInt(100) + 1;
	}

	public static final int random10000() {
		return random.nextInt(10000) + 1;
	}

	public static final int random100w() {
		return random.nextInt(1000000) + 1;
	}

	public static final int random(int range) {
		checkArgument(range > 0, "range must be positive");
		return random.nextInt(range) + 1;
	}

	public static final int random(int lower, int upper) {
		checkArgument(upper >= 0 && lower >= 0 && upper >= lower, "upper must be larger than lower " + upper + " " + lower);
		return lower + random.nextInt(upper - lower + 1);
	}

	/**
	 * 随机指定范围内N个不重复的数 在初始化的无重复待选数组中随机产生一个数放入结果中，
	 * 将待选数组被随机到的数，用待选数组(len-1)下标对应的数替换 然后从len-2里随机产生下一个随机数，如此类推
	 * 
	 * @param max
	 *            指定范围最大值
	 * @param min
	 *            指定范围最小值
	 * @param n
	 *            随机数个数
	 * @return int[] 随机数结果集
	 */
	public static int[] randomArray(int min, int max, int n) {
		int len = max - min + 1;

		if (max < min || n > len) {
			return null;
		}

		// 初始化给定范围的待选数组
		int[] source = new int[len];
		for (int i = min; i < min + len; i++) {
			source[i - min] = i;
		}

		int[] result = new int[n];
		int index = 0;
		for (int i = 0; i < result.length; i++) {
			// 待选数组0到(len-2)随机一个下标
			index = Math.abs(random.nextInt() % len--);
			// 将随机到的数放入结果集
			result[i] = source[index];
			// 将待选数组中被随机到的数，用待选数组(len-1)下标对应的数替换
			source[index] = source[len];
		}

		return result;
	}
}
