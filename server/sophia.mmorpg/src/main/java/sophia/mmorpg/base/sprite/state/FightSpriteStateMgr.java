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
package sophia.mmorpg.base.sprite.state;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;

import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.base.sprite.state.action.PluckingState;
import sophia.mmorpg.core.state.FSMStateBase;

public final class FightSpriteStateMgr {
	private static final Logger logger = Logger.getLogger(FightSpriteStateMgr.class);
	
	private FightSprite owner;
	
	private MovementState crtMovementState;
	private MovementState defaultMovementState;

	private PostureState crtPostureState;
	private PostureState defaultPostureState;

	private ActionState crtActionState;
	private ActionState defaultActionState;
	
	private Set<GlobalState> globalStates = new HashSet<>();
	
	private Set<AdjunctionState> adjunctionStates = new HashSet<>();

	private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

	public FightSpriteStateMgr(FightSprite fightSprite) {
		this.owner = fightSprite;
	}

	public MovementState getCrtMovementState() {
		readWriteLock.readLock().lock();
		try {
			return crtMovementState;
		} finally {
			readWriteLock.readLock().unlock();
		}
	}

	public void setCrtMovementState(MovementState crtMovementState) {
		readWriteLock.writeLock().lock(); 
		try {
			this.crtMovementState = crtMovementState;
		} finally {
			readWriteLock.writeLock().unlock();
		}
	}
	
	public void setCrtMovementState(final short id) {
		MovementState movementState = FSMStateFactory.getMovementState(id);
		if (movementState == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("setCrtMovementState invalid state id " + id);
			}
			
			return;
		}
		
		readWriteLock.writeLock().lock();
		try {
			this.crtMovementState = movementState;
		} finally {
			readWriteLock.writeLock().unlock();
		}
	}

	public PostureState getCrtPostureState() {
		readWriteLock.readLock().lock();
		try {
			return crtPostureState;
		} finally {
			readWriteLock.readLock().unlock();
		}
	}

	public void setCrtPostureState(PostureState crtPostureState) {
		readWriteLock.writeLock().lock();
		try {
			this.crtPostureState = crtPostureState;
		} finally {
			readWriteLock.writeLock().unlock();
		}
	}
	
	public void setCrtPostureState(final short id) {
		PostureState postureState = FSMStateFactory.getPostureState(id);
		if (postureState == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("setCrtPostureState invalid state id " + id);
			}
			
			return;
		}
		
		readWriteLock.writeLock().lock();
		try {
			this.crtPostureState = postureState;
		} finally {
			readWriteLock.writeLock().unlock();
		}
	}

	public ActionState getCrtActionState() {
		readWriteLock.readLock().lock();
		try {
			return crtActionState;
		} finally {
			readWriteLock.readLock().unlock();
		}
	}

	public void setCrtActionState(ActionState crtActionState) {
		readWriteLock.writeLock().lock();
		try {
			this.crtActionState = crtActionState;
		} finally {
			readWriteLock.writeLock().unlock();
		}
	}
	
	public void setCrtActionState(final short id) {
		ActionState actionState = FSMStateFactory.getActionState(id);
		if (actionState == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("setCrtActionState invalid state id " + id);
			}
			
			return;
		}
		
		readWriteLock.writeLock().lock();
		try {
			this.crtActionState = actionState;
		} finally {
			readWriteLock.writeLock().unlock();
		}
	}

	public MovementState getDefaultMovementState() {
		readWriteLock.readLock().lock();
		try {
			return defaultMovementState;
		} finally {
			readWriteLock.readLock().unlock();
		}
	}

	public void setDefaultMovementState(MovementState defaultMovementState) {
		readWriteLock.writeLock().lock();
		try {
			this.defaultMovementState = defaultMovementState;
		} finally {
			readWriteLock.writeLock().unlock();
		}
	}
	
	public void setDefaultMovementState(final short id) {
		MovementState movementState = FSMStateFactory.getMovementState(id);
		if (movementState == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("setDefaultMovementState invalid state id " + id);
			}
			
			return;
		}
		
		readWriteLock.writeLock().lock();
		try {
			this.defaultMovementState = movementState;
		} finally {
			readWriteLock.writeLock().unlock();
		}
	}

	public PostureState getDefaultPostureState() {
		readWriteLock.readLock().lock();
		try {
			return defaultPostureState;
		} finally {
			readWriteLock.readLock().unlock();
		}
	}

	public void setDefaultPostureState(PostureState defaultPostureState) {
		readWriteLock.writeLock().lock();
		try {
			this.defaultPostureState = defaultPostureState;
		} finally {
			readWriteLock.writeLock().unlock();
		}
	}
	
	public void setDefaultPostureState(final short id) {
		PostureState postureState = FSMStateFactory.getPostureState(id);
		if (postureState == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("setDefaultPostureState invalid state id " + id);
			}
			return;
		}
		
		readWriteLock.writeLock().lock();
		try {
			this.defaultPostureState = postureState;
		} finally {
			readWriteLock.writeLock().unlock();
		}
	}

	public ActionState getDefaultActionState() {
		readWriteLock.readLock().lock();
		try {
			return defaultActionState;
		} finally {
			readWriteLock.readLock().unlock();
		}
	}

	public void setDefaultActionState(ActionState defaultActionState) {
		readWriteLock.writeLock().lock();
		try {
			this.defaultActionState = defaultActionState;
		} finally {
			readWriteLock.writeLock().unlock();
		}
	}
	
	public void setDefaultActionState(final short id) {
		ActionState actionState = FSMStateFactory.getActionState(id);
		if (actionState == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("setDefaultActionState invalid state id " + id);
			}
			return;
		}
		
		readWriteLock.writeLock().lock();
		try {
			this.defaultActionState = actionState;
		} finally {
			readWriteLock.writeLock().unlock();
		}
	}

	public void reset() {
		readWriteLock.writeLock().lock();
		try {
			this.crtMovementState = defaultMovementState;
			this.crtPostureState = defaultPostureState;
			this.crtActionState = defaultActionState;
			this.globalStates.clear();
			this.adjunctionStates.clear();
		} finally {
			readWriteLock.writeLock().unlock();
		}
	}
	
	public boolean cancelState(final short id) {
		FSMStateBase<FightSprite> state = getState(id);
		if (state != null) {
			return cancelState(state);
		}
		
		return false;
	}
	
	public boolean cancelState(FSMStateBase<FightSprite> state) {
		if (!isState(state)) {
			return false;
		}
		
		readWriteLock.writeLock().lock();
		try {
			return _cancelState(state);
		} finally {
			readWriteLock.writeLock().unlock();
		}
	}
	
	public boolean _cancelState(FSMStateBase<FightSprite> state) {
		if (state != null) {
			if (state instanceof AdjunctionState) {
				if (getAdjunctionStates().remove(state)) {
					state.exit(owner);
					return true;
				}
			} else if (state instanceof GlobalState) {
				if (getGlobalStates().remove(state)) {
					state.exit(owner);
					return true;
				}
			} else if (state instanceof MovementState) {
				_switchState(getDefaultMovementState());
				return true;
			} else if (state instanceof ActionState) {
				_switchState(getDefaultActionState());
				return true;
			} else if (state instanceof PostureState) {
				_switchState(getDefaultPostureState());
				return true;
			}
		}
		
		return false;
	}
	
	public boolean switchState(final short id) {
		FSMStateBase<FightSprite> state = getState(id);
		if (state != null) {
			return switchState(state);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("switchState invalid state id " + id);
		}
		return false;
	}

	public boolean switchState(FSMStateBase<FightSprite> state) {
		readWriteLock.writeLock().lock();
		try {
			if (_switchState(state)) {
				replaceState(state);
				return true;
			}
			
			return false;
		} finally {
			readWriteLock.writeLock().unlock();
		}
	}

	private boolean _switchState(FSMStateBase<FightSprite> state) {
		if (state == null || !checkState(state)) {
			return false;
		}
		if (state instanceof MovementState) {
			MovementState movementState = (MovementState) state;
			if (crtMovementState == movementState || !crtMovementState.canTransition(movementState.getId()))
				return false;

			if (logger.isDebugEnabled()) {
				logger.debug(owner + " switchState " + crtMovementState + "-->" + state);
			}

			crtMovementState.exit(owner);
			movementState.enter(owner);
			crtMovementState = movementState;
			return true;
		}
		else if (state instanceof ActionState) {
			ActionState actionState = (ActionState) state;
			if (crtActionState == actionState || !crtActionState.canTransition(actionState.getId()))
				return false;

			if (logger.isDebugEnabled()) {
				logger.debug(owner + " switchState " + crtActionState + "-->" + state);
			}

			crtActionState.exit(owner);
			actionState.enter(owner);
			crtActionState = actionState;
			return true;
		} 
		else if (state instanceof PostureState) {
			PostureState postureState = (PostureState) state;
			if (crtPostureState == postureState || !crtPostureState.canTransition(postureState.getId()))
				return false;

			if (logger.isDebugEnabled()) {
				logger.debug(owner + " switchState " + crtPostureState + "-->" + state);
			}
			
			crtPostureState.exit(owner);
			postureState.enter(owner);
			crtPostureState = postureState;
			return true;
		}
		else if (state instanceof GlobalState) {
			GlobalState globalState = (GlobalState) state;
			if (!getGlobalStates().contains(globalState)) {
				globalState.enter(owner);
				getGlobalStates().add(globalState);
				if (logger.isDebugEnabled()) {
					logger.debug(owner + " switchState GlobalState -->" + state);
				}
			}
			return true;
		}
		else if (state instanceof AdjunctionState) {
			AdjunctionState adjunctionState = (AdjunctionState) state;
			if (!getAdjunctionStates().contains(adjunctionState)) {
				adjunctionState.enter(owner);
				getAdjunctionStates().add(adjunctionState);
				if (logger.isDebugEnabled()) {
					logger.debug(owner + " switchState AdjunctionState -->" + state);
				}
			} 
			return true;
		}
		
		return false;
	}

	private boolean checkState(FSMStateBase<FightSprite> state) {
		short id = state.getId();
		if (crtMovementState.isBlockedBy(id)) {
			return false;
		} else if (crtActionState.isBlockedBy(id)) {
			return false;
		} else if (crtPostureState.isBlockedBy(id)) {
			return false;
		} else if (state instanceof GlobalState) {
			for (GlobalState gs : getGlobalStates()) {
				if (gs.isBlockedBy(id)) {
					return false;
				}
			}
		} else if (state instanceof AdjunctionState) {
			for (AdjunctionState as : getAdjunctionStates()) {
				if (as.isBlockedBy(id)) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	private void replaceState(FSMStateBase<FightSprite> state) {
		short id = state.getId();
		if (crtMovementState.isReplacedBy(id)) {
			_switchState(getDefaultMovementState());
		} 
		
		if (crtActionState.isReplacedBy(id)) {
			_switchState(getDefaultActionState());
		} 
		
		if (crtPostureState.isReplacedBy(id)) {
			_switchState(getDefaultPostureState());
		} 
		
		
		Set<GlobalState> globalStatesSet = getGlobalStates();
		Iterator<GlobalState> iteratorGlobalState = globalStatesSet.iterator();
		while (iteratorGlobalState.hasNext()) {
			GlobalState gs = iteratorGlobalState.next();
			if (gs.isReplacedBy(id)) {
				gs.exit(owner);
				iteratorGlobalState.remove();
			}
		}
		
		Set<AdjunctionState> adjunctionStatesSet = getAdjunctionStates();
		Iterator<AdjunctionState> iteratorAdjunctionState = adjunctionStatesSet.iterator();
		while (iteratorAdjunctionState.hasNext()) {
			AdjunctionState as = iteratorAdjunctionState.next();
			if (as.isReplacedBy(id)) {
				as.exit(owner);
				iteratorAdjunctionState.remove();
			}
		}
	}
	
	public FSMStateBase<FightSprite> getState(final short id) {
		readWriteLock.readLock().lock();
		try {
			MovementState movementState = FSMStateFactory.getMovementState(id);
			if (movementState != null) {
				return movementState;
			}

			ActionState actionState = FSMStateFactory.getActionState(id);
			if (actionState != null) {
				return actionState;
			}

			PostureState postureState = FSMStateFactory.getPostureState(id);
			if (postureState != null) {
				return postureState;
			}

			GlobalState globalState = FSMStateFactory.getGlobalState(id);
			if (globalState != null) {
				return globalState;
			}

			AdjunctionState adjunctionState = FSMStateFactory.getAdjunctionState(id);
			if (adjunctionState != null) {
				return adjunctionState;
			}
		} finally {
			readWriteLock.readLock().unlock();
		}
		
		return null;
	}
	
	public boolean isState(final short id) {
		FSMStateBase<FightSprite> state = getState(id);
		if (state != null) {
			return isState(state);
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("isState invalid state id " + id);
		}
		return false;
	}

	public boolean isState(FSMStateBase<FightSprite> state) {
		if (state == null) {
			return false;
		}
		readWriteLock.readLock().lock();
		try {
			if (state == crtMovementState) {
				return true;
			}
			else if (state == crtActionState) {
				return true;
			} 
			else if (state == crtPostureState) {
				return true;
			} 
			else if (getGlobalStates().contains(state)) {
				return true;
			} 
			else if (getAdjunctionStates().contains(state)) {
				return true;
			}

			return false;
		} finally {
			readWriteLock.readLock().unlock();
		}
	}

	public Set<AdjunctionState> getAdjunctionStates() {
		return adjunctionStates;
	}

	public void setAdjunctionStates(Set<AdjunctionState> adjunctionStates) {
		this.adjunctionStates = adjunctionStates;
	}

	public Set<GlobalState> getGlobalStates() {
		return globalStates;
	}

	public void setGlobalStates(Set<GlobalState> globalStates) {
		this.globalStates = globalStates;
	}
	
	public List<FSMStateBase<FightSprite>> getStateList() {
		List<FSMStateBase<FightSprite>> stateList = new ArrayList<>();

		readWriteLock.readLock().lock();
		try {
			stateList.addAll(getAdjunctionStates());
			stateList.addAll(getGlobalStates());
			
			// 采集
			if (getCrtActionState().getId() == PluckingState.PluckingState_Id) {
				stateList.add(FSMStateFactory.getActionState(PluckingState.PluckingState_Id));
			}
			
		} finally {
			readWriteLock.readLock().unlock();
		}

		return stateList;
	}
}
