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

import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.util.ObjectPool;
import sophia.foundation.util.Position;

public class C2G_Scene_Start_Move extends ActionEventBase {
	
	public static final byte PeaksMinimal = 2;
	public static final byte PeaksMaximal = 10;
	
	private List<Position> pathPeaks;

	@Override
	public void unpackBody(IoBuffer buffer) {
		pathPeaks = pool.obtain();
		byte count = buffer.get();
		for (byte i = 0; i < count; i ++) {
			pathPeaks.add(new Position(buffer.getInt(), buffer.getInt()));
		}
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		byte size = (byte) pathPeaks.size();
		buffer.put(size);
		for (int i = 0; i < size; i++) {
			buffer.putInt(pathPeaks.get(i).getX());
			buffer.putInt(pathPeaks.get(i).getY());
		}
		return buffer;
	}

	@Override
	public String getName() {
		return "开始移动";
	}

	public List<Position> getPathPeaks() {
		return pathPeaks;
	}

	public void setPathPeaks(List<Position> pathPeaks) {
		this.pathPeaks = pathPeaks;
	}
	
	private static final ObjectPool<List<Position>> pool = new ObjectPool<List<Position>>() {
		@Override
		protected List<Position> instance() {
			return new ArrayList<Position>(PeaksMaximal);
		}

		@Override
		protected void onRecycle(List<Position> obj) {
			obj.clear();
		}

	};
	
	public void recycle() {
		if (pathPeaks != null) {
			pool.recycle(pathPeaks);
		}
	}
}
