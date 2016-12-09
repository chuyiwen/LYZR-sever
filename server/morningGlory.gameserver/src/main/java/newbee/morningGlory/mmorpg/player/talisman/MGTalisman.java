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
package newbee.morningGlory.mmorpg.player.talisman;

import groovy.lang.Closure;

import java.util.ArrayList;
import java.util.List;

import newbee.morningGlory.mmorpg.player.talisman.level.MGTalismanDataConfig;
import newbee.morningGlory.mmorpg.player.talisman.level.MGTalismanLevelSmith;
import sophia.game.GameRoot;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.RuntimeResult;

/**
 * 法宝
 */
public final class MGTalisman {
	
	private MGTalismanRef talismanRef;
	
	public static final int DEFAULT_HANDLE_TIME = 60 * 60 * 1000;
	
	private byte state = MGTalismanState.InAcquire_State;
	
	private int handleFirstTime = DEFAULT_HANDLE_TIME;
	
	private long lastHandleTime = 0;
	
	private boolean IsNotify = true;
	private final MGTalismanLevelSmith levelSmith = new MGTalismanLevelSmith(this);

	private List<String> gameEventIdSet = new ArrayList<>();

	public MGTalisman() {

	}

	public MGTalisman(MGTalismanRef talismanRef) {
		this.talismanRef = talismanRef;
	}

	public void levelUp(Player player,int level) {
		MGTalismanDataConfig talismanDataConfig = (MGTalismanDataConfig) GameRoot.getGameRefObjectManager().getManagedObject(MGTalismanDataConfig.MGTalisman_Id);		
		int nextLevel = level;	
		String key = getTalismanRef().getId()+"_"+nextLevel;
		MGTalismanRef talismanRef =  talismanDataConfig.getTalismanLevelDataMap().get(key);	
		setTalismanRef(talismanRef);
	}

	public byte getLevel() {
		return MGPropertyAccesser.getTalisManLevel(this.getTalismanRef().getProperty());
	}

	public RuntimeResult active(Player player) {
		RuntimeResult ret = RuntimeResult.OK();		
		
		state = MGTalismanState.Active_State;
		
		Closure<RuntimeResult> activeClosure = this.getTalismanRef().getActiveClosure();
		if (activeClosure != null) {
			activeClosure.call(player, this);
			if(this.lastHandleTime == 0){
				this.lastHandleTime = System.currentTimeMillis();
			}
		}
		
		return ret;
	}

	public RuntimeResult unactive(Player player) {
		RuntimeResult ret = RuntimeResult.OK();
		
		state = MGTalismanState.Inactive_State;
		
		Closure<RuntimeResult> unActiveClosure = this.getTalismanRef().getUnactiveClosure();
		if (unActiveClosure != null) {
			unActiveClosure.call(player, this);
		}
		
		return ret;
	}

	public RuntimeResult acquire(Player player) {
		if(!isNotAcquire()){
			return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_ACTIVE_ACQUIRE_STATE);
		}		
		RuntimeResult ret = this.levelSmith.acquire(player);
		if(ret.isOK()){
			if(isPassiveTalisman()){
				active(player);
			}else{
				state = MGTalismanState.Inactive_State;
			}
			
		}
		
		return ret;
	}

	public byte getState() {
		return state;
	}

	public void setState(byte state) {
		this.state = state;
	}
	
	public boolean isActive(){
		return this.state == MGTalismanState.Active_State;
	}
	
	public boolean isNotActive() {
		return this.state == MGTalismanState.Inactive_State;
	}
	
	public boolean isNotAcquire() {
		return this.state == MGTalismanState.InAcquire_State;
	}
	
	public MGTalismanRef getTalismanRef() {
		return talismanRef;
	}

	public void setTalismanRef(MGTalismanRef talismanRef) {
		this.talismanRef = talismanRef;
	}

	public boolean hasListenGameEvent() {
		if (gameEventIdSet != null && gameEventIdSet.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public List<String> getListenGameEventIdSet() {
		return gameEventIdSet;
	}

	public void addListenGameEventId(String gameEventId) {
		this.gameEventIdSet.add(gameEventId);
	}

	public boolean isIsNotify() {
		return IsNotify;
	}

	public void setIsNotify(boolean isNotify) {
		IsNotify = isNotify;
	}
	public boolean isPassiveTalisman(){
		return this.getTalismanRef().isPassiveTalisman();
	}

	public int getHandleFirstTime() {
		return handleFirstTime;
	}

	public void setHandleFirstTime(int handleFirstTime) {
		this.handleFirstTime = handleFirstTime;
	}

	public List<String> getGameEventIdSet() {
		return gameEventIdSet;
	}

	public void setGameEventIdSet(List<String> gameEventIdSet) {
		this.gameEventIdSet = gameEventIdSet;
	}

	public long getLastHandleTime() {
		return lastHandleTime;
	}

	public void  updateLastHandleTime() {
		this.lastHandleTime = System.currentTimeMillis();
	}
	
	public void setLastHandleTime(long lastHandleTime) {
		this.lastHandleTime = lastHandleTime;
	}

		
}
