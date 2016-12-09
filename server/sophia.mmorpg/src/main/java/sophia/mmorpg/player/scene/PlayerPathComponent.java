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
package sophia.mmorpg.player.scene;

import java.util.List;

import org.apache.log4j.Logger;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.foundation.util.DebugUtil;
import sophia.foundation.util.Position;
import sophia.game.GameRoot;
import sophia.mmorpg.base.sprite.aoi.SpritePathComponent;
import sophia.mmorpg.base.sprite.state.movement.MoveState;
import sophia.mmorpg.base.sprite.state.movement.StopState;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.scene.event.C2G_Scene_Move;
import sophia.mmorpg.player.scene.event.C2G_Scene_Start_Move;
import sophia.mmorpg.player.scene.event.C2G_Scene_Stop_Move;
import sophia.mmorpg.player.scene.event.C2G_Scene_Sync_Time;
import sophia.mmorpg.player.scene.event.G2C_Scene_Sync_Time;
import sophia.mmorpg.player.scene.event.SceneEventDefines;

public class PlayerPathComponent extends SpritePathComponent<Player> {
	private static final Logger logger = Logger.getLogger(PlayerPathComponent.class);

	// 同步间隔10000ms = 10s
	private static final long SyncInterval = 10000;

	// 上一次收到client发送来的同步包时间
	private long lastSyncTime = 0;
	// 延迟时间
	private long delayTime = 0;

	@Override
	public void ready() {
		addActionEventListener(SceneEventDefines.C2G_Scene_Start_Move);
		addActionEventListener(SceneEventDefines.C2G_Scene_Move);
		addActionEventListener(SceneEventDefines.C2G_Scene_Stop_Move);
		addActionEventListener(SceneEventDefines.C2G_Scene_Sync_Time);
		super.ready();
	}

	@Override
	public void suspend() {
		removeActionEventListener(SceneEventDefines.C2G_Scene_Start_Move);
		removeActionEventListener(SceneEventDefines.C2G_Scene_Stop_Move);
		removeActionEventListener(SceneEventDefines.C2G_Scene_Sync_Time);
		removeActionEventListener(SceneEventDefines.C2G_Scene_Move);
		super.suspend();
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
		switch (event.getActionEventId()) {
		case SceneEventDefines.C2G_Scene_Start_Move: {
			Player player = getConcreteParent();
			if (!player.isSceneReady()) {
				break;
			}

			C2G_Scene_Start_Move req = (C2G_Scene_Start_Move) event;
			try {
				if (req.getPathPeaks().size() < C2G_Scene_Start_Move.PeaksMinimal || req.getPathPeaks().size() > C2G_Scene_Start_Move.PeaksMaximal) {
					break;
				}

				boolean started = startMove(req.getPathPeaks());
				if (started) {
					player.changeState(MoveState.MoveState_Id);
				}
				
				if (logger.isDebugEnabled()) {
					logger.debug("Start Move " + req.getPathPeaks() + ", playerName=" + player.getName());
				}
			} finally {
				req.recycle();
			}
			
			break;
		}
		case SceneEventDefines.C2G_Scene_Move:{
			Position srcPosition = new Position();
			Position dstPosition = new Position();
			C2G_Scene_Move req = (C2G_Scene_Move) event;
			srcPosition.setX(req.getSrcX());
			srcPosition.setY(req.getSrcY());
			dstPosition.setX(req.getDstX());
			dstPosition.setY(req.getDstY());
			startMove(srcPosition, dstPosition);
			break;
		}
		case SceneEventDefines.C2G_Scene_Stop_Move: {
			Player player = getConcreteParent();
			if (logger.isDebugEnabled()) {
				logger.debug("C2G_Scene_Stop_Move, player=" + player);
			}
			
			if (!player.isSceneReady()) {
				break;
			}
			

			C2G_Scene_Stop_Move req = (C2G_Scene_Stop_Move) event;
			stopMove(req.getPosition());
			
			if (logger.isDebugEnabled()) {
				logger.debug("C2G_Scene_Stop_Move, stopMoveTo=" + req.getPosition() + ", player=" + player);
			}
			
			break;
		}
		case SceneEventDefines.C2G_Scene_Sync_Time: {
			long now = System.currentTimeMillis();
			if (lastSyncTime == 0) {
				lastSyncTime = now;
				break;
			}
			
			C2G_Scene_Sync_Time req = (C2G_Scene_Sync_Time) event;
			G2C_Scene_Sync_Time res = MessageFactory.getConcreteMessage(SceneEventDefines.G2C_Scene_Sync_Time);
			res.setClientStamp(req.getClientStamp());
			res.setServerStamp(now);
			GameRoot.sendMessage(event.getIdentity(), res);

			// SyncInterval间隔以上的同步是正确的
			long diff = now - lastSyncTime - SyncInterval;
			// 同步包的延迟不能大过5s
			if (diff >= 0 && diff < 5000) {
				delayTime = diff;
			}
			
			lastSyncTime = now;
			if (logger.isDebugEnabled()) {
				logger.debug("delayTime " + delayTime + " now:" + now + " lastSyncTime:" + lastSyncTime);
			}
			
			break;
		}
		}
		super.handleActionEvent(event);
	}

	@Override
	protected void moveTo() {
		if (delayTime > 0) {
			int moveGrids = calcMoveGrids(delayTime);
			if (moveGrids > 0) {
				if (logger.isDebugEnabled()) {
					logger.debug("delay move " + delayTime);
				}
				
				move(moveGrids);
				if (isEnding()) {
					Position crtPosition = getConcreteParent().getCrtPosition();
					stopToPosition(crtPosition.getX(), crtPosition.getY());
				}
			}
		}

		super.moveTo();
	}

	@Override
	protected void stop() {
		if (peaks.size() != 0) {
			getConcreteParent().changeState(StopState.StopState_Id);
		}
		
		super.stop();
	}

	@Override
	public boolean startMove(List<Position> pathPeaks) {
		if (!getConcreteParent().isSceneReady()) {
			if (logger.isDebugEnabled()) {
				logger.debug("startMove error, sceneReady is false" + DebugUtil.printStack());
			}
			
			return false;
		}
		
		if (getConcreteParent().isDead()) {
			if (logger.isDebugEnabled()) {
				logger.debug("startMove error, player id dead" + DebugUtil.printStack());
			}
			
			return false;
		}
		
		Player player = getConcreteParent();
		player.getPlayerSceneComponent().interruptPluck();
		// 由于频繁调用，且这种情况也是可以容忍的，所以改成logger.debug输出
//		Preconditions.checkArgument(getConcreteParent().isSceneReady(), "sceneReady is false");
//		Preconditions.checkArgument(!getConcreteParent().isDead(), "startMove error, player id dead");
		return super.startMove(pathPeaks);
	}

	@Override
	public boolean startMove(Position src, Position dst) {
		if (!getConcreteParent().isSceneReady()) {
			if (logger.isDebugEnabled()) {
				logger.debug("startMove error, sceneReady is false" + DebugUtil.printStack());
			}
			
			return false;
		}
		
		if (getConcreteParent().isDead()) {
			if (logger.isDebugEnabled()) {
				logger.debug("startMove error, player id dead" + DebugUtil.printStack());
			}
			
			return false;
		}
		
		Player player = getConcreteParent();
		player.getPlayerSceneComponent().interruptPluck();
		// 由于频繁调用，且这种情况也是可以容忍的，所以改成logger.debug输出
//		Preconditions.checkArgument(getConcreteParent().isSceneReady(), "sceneReady is false");
//		Preconditions.checkArgument(!getConcreteParent().isDead(), "startMove error, player id dead");
		return super.startMove(src, dst);
	}

	@Override
	public boolean jumpTo(int x, int y) {
		if (!getConcreteParent().isSceneReady()) {
			if (logger.isDebugEnabled()) {
				logger.debug("jumpTo error, sceneReady is false" + DebugUtil.printStack());
			}
			
			return false;
		}
		
		if (getConcreteParent().isDead()) {
			if (logger.isDebugEnabled()) {
				logger.debug("jumpTo error, player id dead" + DebugUtil.printStack());
			}
			
			return false;
		}
		
		// 由于频繁调用，且这种情况也是可以容忍的，所以改成logger.debug输出
//		Preconditions.checkArgument(getConcreteParent().isSceneReady(), "sceneReady is false");
//		Preconditions.checkArgument(!getConcreteParent().isDead(), "jumpTo error, player id dead");
		if (logger.isDebugEnabled()) {
			logger.debug("jumpTo x=" + x + ", y=" + y + ", player=" + getConcreteParent());
		}
		Player player = getConcreteParent();
		player.getPlayerSceneComponent().interruptPluck();
		return super.jumpTo(x, y);
	}

	@Override
	public void silentMoveTo(int x, int y) {
		if (!getConcreteParent().isSceneReady()) {
			if (logger.isDebugEnabled()) {
				logger.debug("silentMoveTo error, sceneReady is false" + DebugUtil.printStack());
			}
			
			return;
		}
		
		if (getConcreteParent().isDead()) {
			if (logger.isDebugEnabled()) {
				logger.debug("silentMoveTo error, player id dead" + DebugUtil.printStack());
			}
			
			return;
		}
		
		// 由于频繁调用，且这种情况也是可以容忍的，所以改成logger.debug输出
//		Preconditions.checkArgument(getConcreteParent().isSceneReady(), "sceneReady is false");
//		Preconditions.checkArgument(!getConcreteParent().isDead(), "silentMoveTo error, player id dead");
		if (logger.isDebugEnabled()) {
			logger.debug("silentMoveTo x=" + x + ", y=" + y + ", player=" + getConcreteParent());
		}
		super.silentMoveTo(x, y);
		Player player = getConcreteParent();
		player.getPlayerSceneComponent().interruptPluck();
		player.changeState(MoveState.MoveState_Id);
	}

	@Override
	public void stopMove(Position pos) {
		if (!getConcreteParent().isSceneReady()) {
			if (logger.isDebugEnabled()) {
				logger.debug("stopMove error, sceneReady is false" + DebugUtil.printStack());
			}
			
			return;
		}
		
		if (getConcreteParent().isDead()) {
			if (logger.isDebugEnabled()) {
				logger.debug("stopMove error, player id dead" + DebugUtil.printStack());
			}
			
			return;
		}
		
		// 由于频繁调用，且这种情况也是可以容忍的，所以改成logger.debug输出
//		Preconditions.checkArgument(getConcreteParent().isSceneReady(), "sceneReady is false");
//		Preconditions.checkArgument(!getConcreteParent().isDead(), "stopMove error, player id dead");
		if (logger.isDebugEnabled()) {
			logger.debug("stopMove " + pos + ", player=" + getConcreteParent());
		}
		super.stopMove(pos);
		Player player = getConcreteParent();
		player.getPlayerSceneComponent().interruptPluck();
	}

	@Override
	public void setMoveSpeed(int moveSpeed) {
		if (logger.isDebugEnabled()) {
			logger.debug("setMoveSpeed " + moveSpeed + ", player=" + getConcreteParent());
		}
		super.setMoveSpeed(moveSpeed);
	}
}
