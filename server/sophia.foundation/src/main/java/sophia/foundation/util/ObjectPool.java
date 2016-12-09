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
package sophia.foundation.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;

/**
 * 对象池，为了统计对象池，对于需要使用对象池的class, 需要实现一个内嵌的继承对象池的static class
 */
public abstract class ObjectPool<T> {
	
	public static final int DEFAULT_MAX_POOL_SIZE = 1000;
	
	// 内存池列表
	private final LinkedList<T> objList = new LinkedList<>();
	
	// 内存池最大容量
	private int poolMaxSize = DEFAULT_MAX_POOL_SIZE;
	
	public int getPoolMaxSize() {
		return poolMaxSize;
	}

	public void setPoolMaxSize(int poolMaxSize) {
		this.poolMaxSize = poolMaxSize;
	}

	/** 统计计数 */
	// 创建对象个数
	private long instanceCount = 0;
	// 获取对象个数
	private long obtainCount = 0;
	// 回收对象个数
	private long recycleCount = 0;
	
	protected ObjectPool()
	{
		poolList.add(this);
		Collections.sort(poolList, poolComparator);
	}
	
	protected abstract T instance();
	
	public T obtain() {
		synchronized (objList) {
			T obj = objList.pollFirst();
			if (obj == null) {
				obj = instance();
				instanceCount++;
			}
			onObtain(obj);
			obtainCount++;
			return obj;
		}
	}
	
	protected void onObtain(T obj) {
		// to be override
	}

	public void recycle(T obj) {
		synchronized (objList) {
			if (obj == null) {
				return;
			}
			if (objList.size() >= poolMaxSize) {
				return;
			}
			onRecycle(obj);
			objList.addLast(obj);
			recycleCount++;
		}
	}
	
	protected abstract void onRecycle(T obj); 
	
	
	
	////////////////////////////// object pool statistics /////////////////////////////////
	
	
	// object pool list
	private final static ArrayList<ObjectPool<?>> poolList = new ArrayList<ObjectPool<?>>();
	// object pool comparator
	private final static Comparator<ObjectPool<?>> poolComparator = new Comparator<ObjectPool<?>>() {
		@Override
		public int compare(ObjectPool<?> o1, ObjectPool<?> o2) {
			return o1.getClass().toString().compareTo(o2.getClass().toString());
		}
	};

	private static long statisticsTime = System.currentTimeMillis();
	
	public static String trace()
	{
		long t = (System.currentTimeMillis() - statisticsTime) / 1000 / 60 ;
		if( t < 1 ) {
			t = 1;
		}

		String traces = "计数重置时间:" + new Date(statisticsTime) + " 约:" + t + " 分钟" + "\r\n";
		traces = traces + "对象池  总数:" + poolList.size() + "\r\n";
		for( ObjectPool<?> pool : poolList ) //平均每分钟获取计数:%-20s 
		{
			String item = String.format("池内:%-5s 创建:%-10s 获取:%-15s 回收:%-15s 每分钟:%-10s @%s\r\n",
					pool.objList.size(),
					pool.instanceCount, 
					pool.obtainCount,
					pool.recycleCount,
					pool.obtainCount / t, 
					pool.getClass().toString());
			
			traces += item;
		}
		
		return traces;
	}
}
