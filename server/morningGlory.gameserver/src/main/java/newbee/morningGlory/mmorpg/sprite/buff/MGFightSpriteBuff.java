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
package newbee.morningGlory.mmorpg.sprite.buff;

import org.apache.log4j.Logger;

import sophia.foundation.property.PropertyDictionary;
import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.base.sprite.state.action.IdleState;
import sophia.mmorpg.utils.RuntimeResult;

public class MGFightSpriteBuff {
	
	private static final Logger logger = Logger.getLogger(MGFightSpriteBuff.class);
	
	private MGFightSpriteBuffRef fightSpriteBuffRef;
	/** 创建的时间 */
	private long createTime;
	/** 当前剩余持续作用的时间 */
	private long duration;
	/** 绝对的持续作用时间 */
	private long absoluteDuration;
	/** 到期的时间 */
	private volatile long expiredTime;
	/** BUFF的间隔周期(可选,在ref数据里定义) */
	private long affectIntervalDuration;
	/** 最近一次的BUFF作用时间 */
	private long lastAffectTime;
	
	private PropertyDictionary specialProperty = new PropertyDictionary();
	
	private FightSprite owner;

	private FightSprite attachFightSprite;
	
	private boolean isSendAttackEvent = true;	//buff被附加时 是否要发送AfterAttack 事件
	
	private boolean isNotify = true;			//buff被附加时是否要发送客户端通知

	private short attachedState = IdleState.IdleState_Id;

	public MGFightSpriteBuff() {

	}

	public MGFightSpriteBuff(MGFightSpriteBuffRef fightSpriteBuffRef, FightSprite from, FightSprite to) {
		this.duration = fightSpriteBuffRef.getDuration();
		this.absoluteDuration = fightSpriteBuffRef.getDuration();
		this.affectIntervalDuration = fightSpriteBuffRef.getPeriodAffectTime();
		createTime = System.currentTimeMillis();
		expiredTime = createTime + duration;
		this.owner = from;
		this.attachFightSprite = to;
		this.fightSpriteBuffRef = fightSpriteBuffRef;
	}
	
	public MGFightSpriteBuff(MGFightSpriteBuffRef fightSpriteBuffRef, FightSprite from, FightSprite to,long duration) {
		this.fightSpriteBuffRef = fightSpriteBuffRef;
		this.duration = duration;
		this.absoluteDuration = duration;
		this.affectIntervalDuration = fightSpriteBuffRef.getPeriodAffectTime();
		createTime = System.currentTimeMillis();
		expiredTime = createTime + duration;
		this.owner = from;
		this.attachFightSprite = to;

	}
	
	public RuntimeResult attach() {
		RuntimeResult ret = RuntimeResult.OK();

		if (fightSpriteBuffRef.getAttachClosure() != null) {
			ret = fightSpriteBuffRef.getAttachClosure().call(owner, attachFightSprite, this);
			this.lastAffectTime = System.currentTimeMillis();
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug(fightSpriteBuffRef + ", attach fightSprite=" + attachFightSprite);
		}

		return ret;
	}
	
	public RuntimeResult detach() {
		RuntimeResult ret = RuntimeResult.OK();

		if (fightSpriteBuffRef.getDetachClosure() != null) {
			ret = fightSpriteBuffRef.getDetachClosure().call(owner, attachFightSprite, this);
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug(fightSpriteBuffRef + ", detach fightSprite=" + attachFightSprite);
		}

		return ret;
	}

	public void tick() {
		if (System.currentTimeMillis() - this.lastAffectTime >= this.affectIntervalDuration) {
			effectTo();
		}
	}
	
	public boolean effectTo() {
		if (fightSpriteBuffRef.isPeriodAffectBuff()) {
			if (fightSpriteBuffRef.getTickAffectClosure() != null) {
				this.lastAffectTime = System.currentTimeMillis();
				fightSpriteBuffRef.getTickAffectClosure().call(owner, attachFightSprite, this);
				if (logger.isDebugEnabled()) {
					logger.debug(fightSpriteBuffRef + ", effectTo fightSprite=" + attachFightSprite);
				}
				return true;
			}
		}
		
		return false;
	}

	public boolean isFinished() {
		boolean ret = false;
		if(!this.getFightSpriteBuffRef().isDependOnDurationTime()){
			return false;
		}
		
		long currentTime = System.currentTimeMillis();
		if (currentTime > expiredTime)
			return true;
		
		return ret;
	}

	/** 增加绝对的持续时间
	 * @param delta
	 */
	public synchronized void addAbsoluteDuration(long delta) {
		this.absoluteDuration += delta;
	}
	
	/**
	 * 当前剩余持续时间(玩家退出登录时调用)
	 * @return
	 */
	public synchronized long getCrtDuration() {
		long d = expiredTime - System.currentTimeMillis();
		return d;
	}
	
	/**
	 * 增加过期时间
	 * @param delta
	 */
	public synchronized void addExpiration(long delta) {
		this.expiredTime += delta;
	}
	/**
	 * 增加持续时间
	 * @param duration
	 */
	public synchronized void addDuration(long duration) {
		this.duration += duration;
	}
	/**
	 * 重置持续时间
	 * @param duration
	 */
	public synchronized void reSetDuration( ) {		
		this.duration = getCrtDuration();
	}
	
	/**
	 * 初始的持续时间
	 * @return
	 */
	public synchronized long getDuration() {
		return this.duration;
	}
	public synchronized void setDuration(long duration) {
		this.duration = duration;
	}
	public synchronized long getExpiration() {
		return expiredTime;
	}	
	public synchronized void setExpiration(long expiration) {
		this.expiredTime = expiration;
	}
	public long getAbsoluteDuration() {
		return absoluteDuration;
	}

	public void setAbsoluteDuration(long absoluteDuration) {
		this.absoluteDuration = absoluteDuration;
	}

	public long getAffectIntervalDuration() {
		return affectIntervalDuration;
	}
	public long getLastAffectTime() {
		return lastAffectTime;
	}

	public void setLastAffectTime(long lastAffectTime) {
		this.lastAffectTime = lastAffectTime;
	}

	public FightSprite getOwner() {
		return owner;
	}

	public void setOwner(FightSprite owner) {
		this.owner = owner;
	}
	
	public FightSprite getAttachFightSprite() {
		return attachFightSprite;
	}

	public void setAttachFightSprite(FightSprite attachFightSprite) {
		this.attachFightSprite = attachFightSprite;
	}
	
	public MGFightSpriteBuffRef getFightSpriteBuffRef() {
		return fightSpriteBuffRef;
	}

	public void setFightSpriteBuffRef(MGFightSpriteBuffRef fightSpriteBuffRef) {
		this.fightSpriteBuffRef = fightSpriteBuffRef;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attachFightSprite == null) ? 0 : attachFightSprite.hashCode());
		result = prime * result + (int) (createTime ^ (createTime >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MGFightSpriteBuff other = (MGFightSpriteBuff) obj;
		if (attachFightSprite == null) {
			if (other.attachFightSprite != null)
				return false;
		} else if (!attachFightSprite.equals(other.attachFightSprite))
			return false;
		if (createTime != other.createTime)
			return false;
		return true;
	}

	public PropertyDictionary getSpecialProperty() {
		return specialProperty;
	}

	public void setSpecialProperty(PropertyDictionary specialProperty) {
		this.specialProperty = specialProperty;
	}
	public boolean isNotify() {
		return isNotify;
	}

	public void setNotify(boolean isNotify) {
		this.isNotify = isNotify;
	}

	public boolean isSendAttackEvent() {
		return isSendAttackEvent;
	}

	public void setSendAttackEvent(boolean isSendAttackEvent) {
		this.isSendAttackEvent = isSendAttackEvent;
	}

	public short getAttachedState() {
		return attachedState;
	}

	public void setAttachedState(short attachedState) {
		this.attachedState = attachedState;
	}
	
	public void resetAttachedState(){
		this.attachedState = IdleState.IdleState_Id;
	}
	
}
