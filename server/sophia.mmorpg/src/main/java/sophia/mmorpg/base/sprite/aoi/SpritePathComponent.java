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
package sophia.mmorpg.base.sprite.aoi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import sophia.foundation.util.Pair;
import sophia.foundation.util.Position;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.base.scene.aoi.gameEvent.AOIJumpTo_GE;
import sophia.mmorpg.base.scene.aoi.gameEvent.AOIMoveTo_GE;
import sophia.mmorpg.base.scene.aoi.gameEvent.AOIMove_GE;
import sophia.mmorpg.base.scene.aoi.gameEvent.AOISilentMove_GE;
import sophia.mmorpg.base.scene.aoi.gameEvent.AOIStopMove_GE;
import sophia.mmorpg.base.scene.grid.SceneGrid;
import sophia.mmorpg.base.scene.grid.SceneTerrainLayer;
import sophia.mmorpg.base.scene.tick.SceneTick_GE;
import sophia.mmorpg.base.sprite.Sprite;

import com.google.common.base.Preconditions;

public class SpritePathComponent<T extends Sprite> extends ConcreteComponent<T> {

	private static final Logger logger = Logger.getLogger(SpritePathComponent.class);

	private static final String SceneTick_GE_Id = SceneTick_GE.class.getSimpleName();
	private static final String AOIMove_GE_Id = AOIMove_GE.class.getSimpleName();
	private static final String AOIMoveTo_GE_Id = AOIMoveTo_GE.class.getSimpleName();
	private static final String AOIStopMove_GE_Id = AOIStopMove_GE.class.getSimpleName();
	private static final String AOIJumpTo_GE_Id = AOIJumpTo_GE.class.getSimpleName();
	private static final String AOISilentMove_GE_Id = AOISilentMove_GE.class.getSimpleName();

	protected static final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);

	protected List<PeakInfo> peaks = new ArrayList<PeakInfo>(10);
	
	private Object mutex = this;

	// 当前转折点
	private int crtPeakIdx;

	// 记录新旧位置
	private Position oldPosition = new Position();
	// 行走结束位置
	private Position endPosition = new Position();

	// 移动速度
	private volatile int moveSpeed;
	// 开始移动时间
	private long startMoveTime;
	// 当前已移动格子数
	private int curMoveGrids;

	public SpritePathComponent() {
	}

	@Override
	public void ready() {
		addInterGameEventListener(SceneTick_GE_Id);
		super.ready();
	}

	@Override
	public void suspend() {
		removeInterGameEventListener(SceneTick_GE_Id);
		super.suspend();
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {
		if (event.isId(SceneTick_GE_Id)) {
			SceneTick_GE ge = (SceneTick_GE) event.getData();
			tick(ge.getTimestamp());
		}

		super.handleGameEvent(event);
	}
	
	public boolean isRunning() {
		synchronized (mutex) {
			return !isEnding();
		}
	}
	
	public Position getEndPosition() {
		PeakInfo peakInfo = null;
		synchronized (mutex) {
			int size = peaks.size();
			if (size != 0) {
				peakInfo = peaks.get(size - 1);
			}
		}
		
		if (peakInfo == null) {
			Sprite sprite = getConcreteParent();
			Position crtPosition = sprite.getCrtPosition();
			endPosition.setPosition(crtPosition.getX(), crtPosition.getY());
			return endPosition;
		}
		
		endPosition.setPosition(peakInfo.getColumn(), peakInfo.getRow());
		return endPosition;
	}

	public PathInfo getPathInfo() {
		PeakInfo lastPeakInfo = null;
		synchronized (mutex) {
			int index = peaks.size() - 1;
			if (index < 0) {
				return null;
			}
			
			lastPeakInfo = peaks.get(index);
		}

		PathInfo pathInfo = new PathInfo();
		Sprite sprite = getConcreteParent();
		SpriteInfo spriteInfo = new SpriteInfo(sprite);
		pathInfo.setSpriteInfo(spriteInfo);
		Position crtPosition = sprite.getCrtPosition();
		pathInfo.setStartPosition(crtPosition.getX(), crtPosition.getY());
		pathInfo.setEndPosition(lastPeakInfo.getColumn(), lastPeakInfo.getRow());
		pathInfo.setServerStamp(System.currentTimeMillis());
		return pathInfo;
	}

	public boolean startMove(final List<Position> pathPeaks) {
		if (logger.isDebugEnabled()) {
			logger.debug("Start Move " + pathPeaks);
		}
		
		int size = pathPeaks.size();
		// 行走路径转折点数范围 [2, 10]
		Preconditions.checkArgument(size >= 2);
		Preconditions.checkArgument(size <= 10);

		Position start = pathPeaks.get(0);
		Position crtPosition = getConcreteParent().getCrtPosition();
		boolean diffStartPos = false;
		// 起始坐标不同，有误差，判定是否在容忍范围内
		if (crtPosition.getX() != start.getX() || crtPosition.getY() != start.getY()) {
			diffStartPos = true;
			if (!checkValidDistance(start)) {
				logger.error("Start Move Invalid Distance crtPosition " + getConcreteParent().getCrtPosition() + " startPosition " + start);
				return false;
			}
		}

		SceneTerrainLayer terrainLayer = getConcreteParent().getCrtScene().getRef().getTerrainLayer();
		synchronized (mutex) {
			stop();

			for (int i = 1; i < size; i++) {
				Position src = pathPeaks.get(i - 1);
				// 非法的坐标
				if (!terrainLayer.isInMatrixRange(src.getY(), src.getX())) {
					stop();
					logger.error("Start Move Invalid Position " + src);
					return false;
				}
				
				Position dst = pathPeaks.get(i);
				// 非法的坐标
				if (!terrainLayer.isInMatrixRange(dst.getY(), dst.getX())) {
					stop();
					logger.error("Start Move Invalid Position " + dst);
					return false;
				}
				
				// 校验两点连线是为8个方向的角度上
				int dx = dst.getX() - src.getX();
				int dy = dst.getY() - src.getY();
				int gridCount = 0;
				if (dy != 0) {
					if (dx == 0 || Math.abs(dx) == Math.abs(dy)) {
						gridCount = dy;
					}
				} else if (dx != 0) {
					if (dy == 0 || Math.abs(dx) == Math.abs(dy)) {
						gridCount = dx;
					}
				}
				
				// 非法的路径
				if (gridCount == 0) {
					stop();
					logger.error("Start Move Invalid Path " + src + " " + dst);
					return false;
				}

				PeakInfo peakInfo = new PeakInfo(src.getY(), src.getX(), dx, dy);
				peakInfo.setGridCount(Math.abs(gridCount));
				peaks.add(peakInfo);
			}

			// 落单的最后转折点
			Position last = pathPeaks.get(size - 1);
			peaks.add(new PeakInfo(last.getY(), last.getX()));

			if (logger.isDebugEnabled()) {
				for (PeakInfo peakInfo : peaks) {
					logger.debug(peakInfo);
				}
			}

			// 设置新的位置
			if (diffStartPos) {
				logger.debug("Start Move Correct Position " + start);
				moveToPosition(start.getX(), start.getY());
			}
			
			setPosition(start.getX(), start.getY());

			moveTo();
		}

		return true;
	}

	public boolean startMove(final Position src, final Position dst) {
		if(src == null || dst == null) {
			return false;
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("Start Move " + src + " " + dst);
		}

		Sprite sprite = getConcreteParent();
		SceneTerrainLayer layer = sprite.getCrtScene().getTerrainLayer();
		if (!layer.isInMatrixRange(src.getY(), src.getX())) {
//			logger.error("Start Move Invalid Position " + src);
			return false;
		}
		final SceneGrid srcGrid = layer.getSceneGrid(src.getY(), src.getX());
		if (srcGrid.isBlocked()) {
//			logger.error("Start Move SrcGrid blocked " + srcGrid);
			return false;
		}

		if (!layer.isInMatrixRange(dst.getY(), dst.getX())) {
//			logger.error("Start Move Invalid Position " + dst);
			return false;
		}
		final SceneGrid dstGrid = layer.getSceneGrid(dst.getY(), dst.getX());
		if (dstGrid.isBlocked()) {
//			logger.error("Start Move DstGrid blocked " + dstGrid);
			return false;
		}

		Position crtPosition = sprite.getCrtPosition();
		// 起始坐标不同，有误差，判定是否在容忍范围内
		if (crtPosition.getX() != src.getX() || crtPosition.getY() != src.getY()) {
			if (!checkValidDistance(src)) {
//				logger.error("Start Move crtPosition " + sprite.getCrtPosition() + " startPosition " + src);
				return false;
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Start Move Success");
		}

		executorService.submit(new Runnable() {
			@Override
			public void run() {
				synchronized (mutex) {
					stop();

					findPath(srcGrid, dstGrid);

					// 设置新的位置
					Position crtPosition = getConcreteParent().getCrtPosition();
					if (crtPosition.getX() != src.getX() || crtPosition.getY() != src.getY()) {
						moveToPosition(src.getX(), src.getY());
					}

					setPosition(src.getX(), src.getY());

					moveTo();
				}
			}
		});

		return true;
	}

	public void stopMove(Position pos) {
		if (logger.isDebugEnabled()) {
			logger.debug("Stop Move " + pos);
		}

		Sprite sprite = getConcreteParent();
		SceneTerrainLayer terrainLayer = sprite.getCrtScene().getRef().getTerrainLayer();
		if (!terrainLayer.isInMatrixRange(pos.getY(), pos.getX())) {
//			logger.error("Stop Move Invalid Position " + pos);
			return;
		}
		
		SceneGrid dstGrid = terrainLayer.getSceneGrid(pos.getY(), pos.getX());
		if (dstGrid.isBlocked()) {
//			logger.error("Stop Move Position blocked " + pos);
			return;
		}
		
		if (!checkValidDistance(pos)) {
//			logger.error("Stop Move crtPosition " + sprite.getCrtPosition() + " stopPosition " + pos);
			return;
		}
		
		synchronized (mutex) {
			stop();
			stopToPosition(pos.getX(), pos.getY());
		}
	}

	public boolean jumpTo(Position pos) {
		return jumpTo(pos.getX(), pos.getY());
	}
	
	public boolean jumpTo(int x, int y) {
		if (logger.isDebugEnabled()) {
			logger.debug("JumpTo " + x + " " + y);
		}

		Sprite sprite = getConcreteParent();
		SceneTerrainLayer terrainLayer = sprite.getCrtScene().getRef().getTerrainLayer();
		if (!terrainLayer.isInMatrixRange(y, x)) {
//			logger.error("JumpTo Invalid Position");
			return false;
		}
		
		synchronized (mutex) {
			stop();
			
			sprite.getCrtPosition().setPosition(x, y);
			AOIJumpTo_GE jumpToGE = new AOIJumpTo_GE();
			jumpToGE.setSprite(sprite);
			jumpToGE.setSrcPosition(oldPosition);
			PositionInfo posInfo = new PositionInfo();
			SpriteInfo spriteInfo = new SpriteInfo(sprite);
			posInfo.setSpriteInfo(spriteInfo);
			posInfo.setPosition(x, y);
			jumpToGE.setPositionInfo(posInfo);
			sendGameEvent(AOIJumpTo_GE_Id, jumpToGE, sprite.getCrtScene().getId());
			oldPosition.setPosition(x, y);
		}
		
		return true;
	}
	
	public void silentMoveTo(Position pos) {
		silentMoveTo(pos.getX(), pos.getY());
	}
	
	public void silentMoveTo(int x, int y) {
		if (logger.isDebugEnabled()) {
			logger.debug("silentMoveTo " + x + " " + y);
		}

		Sprite sprite = getConcreteParent();
		SceneTerrainLayer terrainLayer = sprite.getCrtScene().getRef().getTerrainLayer();
		if (!terrainLayer.isInMatrixRange(y, x)) {
//			logger.error("silentMoveTo Invalid Position");
			return;
		}
		
		synchronized (mutex) {
			stop();
			
			sprite.getCrtPosition().setPosition(x, y);
			AOISilentMove_GE silentMoveGE = new AOISilentMove_GE();
			silentMoveGE.setSprite(sprite);
			silentMoveGE.setSrcPosition(oldPosition);
			PositionInfo posInfo = new PositionInfo();
			SpriteInfo spriteInfo = new SpriteInfo(sprite);
			posInfo.setSpriteInfo(spriteInfo);
			posInfo.setPosition(x, y);
			silentMoveGE.setPositionInfo(posInfo);
			sendGameEvent(AOISilentMove_GE_Id, silentMoveGE, sprite.getCrtScene().getId());
			oldPosition.setPosition(x, y);
		}
	}

	public void silentStop() {
		synchronized (mutex) {
			stop();
		}
	}

	public int getMoveSpeed() {
		return moveSpeed;
	}

	public void setMoveSpeed(int moveSpeed) {
		Preconditions.checkArgument(moveSpeed >= 0, "moveSpeed不应该小于0");
		this.moveSpeed = moveSpeed;
	}

	protected void tick(final long timestamp) {
		synchronized (mutex) {
			if (isEnding()) {
				return;
			}
			
			int moveGrids = calcMoveGrids(timestamp - startMoveTime);
			
			// 没有移动
			moveGrids -= curMoveGrids;
			if (moveGrids <= 0) {
				return;
			}
			
			curMoveGrids += moveGrids;
			move(moveGrids);
		}
		
//		executorService.execute(new Runnable() {
//			@Override
//			public void run() {
//				synchronized (mutex) {
//					if (isEnding()) {
//						return;
//					}
//
//					int moveGrids = calcMoveGrids(timestamp - lastMoveTime);
//					// 没有移动
//					if (moveGrids <= 0) {
//						return;
//					}
//
//					lastMoveTime = timestamp;
//					move(moveGrids);
//				}
//			}
//		});
	}
	
	protected void setPosition(int x, int y) {
		getConcreteParent().getCrtPosition().setPosition(x, y);
		oldPosition.setPosition(x, y);
	}

	protected void stop() {
		peaks.clear();
		crtPeakIdx = 0;
	}
	
	protected boolean isEnding() {
		return peaks.size() == 0;
	}
	
	protected boolean checkValidDistance(Position pos) {
		// 误差最大一个速度，也就是一秒移动的格子数
//		Position crtPosition = getConcreteParent().getCrtPosition();
//		if (2 * moveSpeed < TerrainGridGeom.getDistance(crtPosition.getX(), crtPosition.getY(), pos.getX(), pos.getY())) {
//			return false;
//		}

		return true;
	}

	protected int calcMoveGrids(long millisecond) {
		return (int) (moveSpeed * millisecond / 1000);
	}

	protected void moveTo() {
		if (isEnding()) {
			return;
		}

		startMoveTime = System.currentTimeMillis();
		curMoveGrids = 0;

		Sprite sprite = getConcreteParent();
		Position oldPos = sprite.getCrtPosition();
		oldPosition.setPosition(oldPos.getX(), oldPos.getY());

		AOIMoveTo_GE moveToGE = new AOIMoveTo_GE();
		PathInfo pathInfo = getPathInfo();
		moveToGE.setSprite(sprite);
		moveToGE.setPathInfo(pathInfo);
		sendGameEvent(AOIMoveTo_GE_Id, moveToGE, sprite.getCrtScene().getId());

		if (logger.isDebugEnabled()) {
			logger.debug("moveTo " + pathInfo.getStartPosition() + " " + pathInfo.getEndPosition());
		}
	}
	
	protected void stopToPosition(final int x, final int y) {
		Sprite sprite = getConcreteParent();
		AOIStopMove_GE stopMoveGE = AOIStopMove_GE.pool.obtain();
		stopMoveGE.setSprite(sprite);
		stopMoveGE.setSrcPosition(sprite.getCrtPosition());
		PositionInfo posInfo = new PositionInfo();
		SpriteInfo spriteInfo = new SpriteInfo(sprite);
		posInfo.setSpriteInfo(spriteInfo);
		posInfo.setPosition(x, y);
		stopMoveGE.setPositionInfo(posInfo);
		sendGameEvent(AOIStopMove_GE_Id, stopMoveGE, sprite.getCrtScene().getId());
		AOIStopMove_GE.pool.recycle(stopMoveGE);
		setPosition(x, y);
	}

	protected void moveToPosition(final int x, final int y) {
		Sprite sprite = getConcreteParent();
		sprite.getCrtPosition().setPosition(x, y);
		AOIMove_GE moveGE = AOIMove_GE.pool.obtain();
		moveGE.setSprite(sprite);
		moveGE.setSrcPosition(oldPosition);
		sendGameEvent(AOIMove_GE_Id, moveGE, sprite.getCrtScene().getId());
		AOIMove_GE.pool.recycle(moveGE);
		oldPosition.setPosition(x, y);
	}

	protected void move(final int grids) {
		int leftMoveGrids = grids;
		Sprite sprite = getConcreteParent();
		Position crtPosition = sprite.getCrtPosition();
		int size = peaks.size();
		for (int i = crtPeakIdx; i < size; i++) {
			PeakInfo peakInfo = peaks.get(i);
			int oldGridCount = peakInfo.getCrtGridCount();
			int moveGrids = peakInfo.move(leftMoveGrids);
			int crtGridCount = peakInfo.getCrtGridCount();
			int totalCount = peakInfo.getTotalCount();
			if (crtGridCount < totalCount) {
				Pair<Integer, Integer> angle = peakInfo.getAngle();
				int row = peakInfo.getRow() + moveGrids * angle.getKey();
				int column = peakInfo.getColumn() + moveGrids * angle.getValue();
				crtPosition.setPosition(column, row);
				break;
			}

			// 到达目标，最后一个转折点
			if (++crtPeakIdx == size) {
				if (logger.isDebugEnabled()) {
					logger.debug("endPosition " + crtPosition);
				}
				stop();
				break;
			}
			
			peakInfo = peaks.get(crtPeakIdx);
			crtPosition.setPosition(peakInfo.getColumn(), peakInfo.getRow());
			leftMoveGrids += oldGridCount - totalCount;
			if (leftMoveGrids <= 0) {
				break;
			}
		}

		if (oldPosition.getX() != crtPosition.getX() || oldPosition.getY() != crtPosition.getY()) {
			moveToPosition(crtPosition.getX(), crtPosition.getY());
			if (logger.isDebugEnabled()) {
				logger.debug("move success " + crtPosition);
			}
		}
	}

	protected boolean findPath(SceneGrid start, SceneGrid end) {
		SceneTerrainLayer layer = getConcreteParent().getCrtScene().getTerrainLayer();
		List<SceneGrid> pathGrids = layer.getPath(start, end);
		if (pathGrids == null) {
			return false;
		}
		int gridSize = pathGrids.size();
		if (gridSize <= 1) {
			return false;
		}
		int lastDx = 0;
		int lastDy = 0;
		for (int i = 1; i < gridSize; i++) {
			SceneGrid src = pathGrids.get(i - 1);
			SceneGrid dst = pathGrids.get(i);
			int dy = dst.getRow() - src.getRow();
			int dx = dst.getColumn() - src.getColumn();
			if (lastDy != dy || lastDx != dx) {
				peaks.add(new PeakInfo(src.getRow(), src.getColumn(), dx, dy));
			}
			lastDx = dx;
			lastDy = dy;
		}

		// 落单的最后转折点
		SceneGrid last = pathGrids.get(gridSize - 1);
		peaks.add(new PeakInfo(last.getRow(), last.getColumn()));

		// 设置转折点的gridCount
		for (int i = 1; i < peaks.size(); i++) {
			PeakInfo peakInfo = peaks.get(i - 1);
			PeakInfo nextPeakInfo = peaks.get(i);

			int gridCount = Math.abs(nextPeakInfo.getColumn() - peakInfo.getColumn());
			if (gridCount == 0) {
				gridCount = Math.abs(nextPeakInfo.getRow() - peakInfo.getRow());
			}

			peakInfo.setGridCount(gridCount);
		}

		if (logger.isDebugEnabled()) {
			for (PeakInfo peakInfo : peaks) {
				logger.debug(peakInfo);
			}
		}

		return true;
	}
}
