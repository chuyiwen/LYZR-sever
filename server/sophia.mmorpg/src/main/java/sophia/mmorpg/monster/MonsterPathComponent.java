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
package sophia.mmorpg.monster;

import java.util.List;

import org.apache.log4j.Logger;

import sophia.foundation.util.DebugUtil;
import sophia.foundation.util.Position;
import sophia.mmorpg.base.sprite.aoi.SpritePathComponent;
import sophia.mmorpg.base.sprite.state.movement.StopState;

public class MonsterPathComponent extends SpritePathComponent<Monster> {
	
	private static final Logger logger = Logger.getLogger(MonsterPathComponent.class);

	@Override
	protected void stop() {
		if (peaks.size() != 0) {
			getConcreteParent().changeState(StopState.StopState_Id);
		}
	
		super.stop();
	}

	@Override
	public boolean startMove(List<Position> pathPeaks) {		
		if (getConcreteParent().isDead()) {
			if (logger.isDebugEnabled()) {
				logger.debug("startMove error, monster id dead" + DebugUtil.printStack());
			}
			
			return false;
		}
		
		// 由于频繁调用，且这种情况也是可以容忍的，所以改成logger.debug输出
		//Preconditions.checkArgument(!getConcreteParent().isDead(), "startMove error, monster id dead");
		return super.startMove(pathPeaks);
	}

	@Override
	public boolean startMove(Position src, Position dst) {
		if (getConcreteParent().isDead()) {
			if (logger.isDebugEnabled()) {
				logger.debug("startMove error, monster id dead" + DebugUtil.printStack());
			}
			
			return false;
		}
		
		// 由于频繁调用，且这种情况也是可以容忍的，所以改成logger.debug输出
		//Preconditions.checkArgument(!getConcreteParent().isDead(), "startMove error, monster id dead");
		return super.startMove(src, dst);
	}

	@Override
	public void stopMove(Position pos) {
		if (getConcreteParent().isDead()) {
			if (logger.isDebugEnabled()) {
				logger.debug("stopMove error, monster id dead" + DebugUtil.printStack());
			}
			
			return;
		}
		
		// 由于频繁调用，且这种情况也是可以容忍的，所以改成logger.debug输出
		//Preconditions.checkArgument(!getConcreteParent().isDead(), "stopMove error, monster id dead");
		super.stopMove(pos);
	}

	@Override
	public boolean jumpTo(int x, int y) {
		if (getConcreteParent().isDead()) {
			if (logger.isDebugEnabled()) {
				logger.debug("jumpTo error, monster id dead" + DebugUtil.printStack());
			}
			
			return false;
		}
		
		// 由于频繁调用，且这种情况也是可以容忍的，所以改成logger.debug输出
		//Preconditions.checkArgument(!getConcreteParent().isDead(), "jumpTo error, monster id dead");
		return super.jumpTo(x, y);
	}

	@Override
	public void silentMoveTo(int x, int y) {
		if (getConcreteParent().isDead()) {
			if (logger.isDebugEnabled()) {
				logger.debug("silentMoveTo error, monster id dead" + DebugUtil.printStack());
			}
			
			return;
		}
		
		// 由于频繁调用，且这种情况也是可以容忍的，所以改成logger.debug输出
		//Preconditions.checkArgument(!getConcreteParent().isDead(), "silentMoveTo error, monster id dead");
		super.silentMoveTo(x, y);
	}
	
	
}
