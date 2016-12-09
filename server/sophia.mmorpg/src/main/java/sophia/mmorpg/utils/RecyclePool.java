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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

public abstract class RecyclePool<T> {
	private final static ArrayList<RecyclePool<?>> _pools = new ArrayList<RecyclePool<?>>();
	private final static Comparator<RecyclePool<?>> _poolsComparator = new Comparator<RecyclePool<?>>() {
		@Override
		public int compare(RecyclePool<?> o1, RecyclePool<?> o2) {
			return o1.getClass().toString().compareTo(o2.getClass().toString());
		}
	};
	private static long _traceTime = System.currentTimeMillis();

	private Queue<T> _list = new LinkedList<T>();

	protected RecyclePool() {
		_pools.add(this);
		Collections.sort(_pools, _poolsComparator);
	}

	protected abstract T instance();

	protected void onObtain(T obj) {
		// to be override
	}

	protected abstract void onRecycle(T obj);

	public static final int Default_Pool_Max_Size = 1000;
	private long _instances = 0;
	private long _obtainNum = 0;
	private long _recycleNum = 0;
	private int _maxSize = Default_Pool_Max_Size;

	public T obtain() {
		synchronized (_list) {
			T e = _list.poll();
			if (e == null) {
				e = instance();
				_instances++;
			}
			onObtain(e);
			_obtainNum++;
			return e;
		}
	}

	public void recycle(T obj) {
		synchronized (_list) {
			if (obj == null)
				return;
			if (_list.size() >= _maxSize)
				return;
			if (_list.contains(obj)) {
				return;
			} else {
				onRecycle(obj);
			}
			_list.add(obj);
			_recycleNum++;
		}
	}

	public int get_maxSize() {
		return _maxSize;
	}

	public void set_maxSize(int _maxSize) {
		this._maxSize = _maxSize;
	}

	public void resetTraceCounter() {
		_traceTime = System.currentTimeMillis();
		synchronized (_list) {
			for (RecyclePool<?> pool : _pools) {
				pool._obtainNum = 0;
				pool._recycleNum = 0;
			}
		}
	}

	public static String tracePools() {
		long t = (System.currentTimeMillis() - _traceTime) / 1000 / 60;
		if (t < 1)
			t = 1;

		String traces = "计数重置时间:" + new Date(_traceTime) + " 约:" + t + " 分钟"
				+ "\r\n";
		traces = traces + "对象池  总数:" + _pools.size() + "\r\n";
		for (RecyclePool<?> pool : _pools) // 平均每分钟获取计数:%-20s
		{
			String item = String.format(
					"池内:%-5s 创建:%-10s 获取:%-15s 回收:%-15s 每分钟:%-10s @%s\r\n",
					pool._list.size(), pool._instances, pool._obtainNum,
					pool._recycleNum, pool._obtainNum / t, pool.getClass()
							.toString());

			traces += item;
		}

		return traces;
	}
}
