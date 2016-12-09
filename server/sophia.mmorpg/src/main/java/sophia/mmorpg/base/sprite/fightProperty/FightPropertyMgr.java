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
package sophia.mmorpg.base.sprite.fightProperty;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;

import sophia.foundation.property.PropertyDictionary;
import sophia.foundation.property.SimulatorProperty;
import sophia.foundation.property.ValueProperty;
import sophia.mmorpg.core.PropertyDictionaryModifyPhase;
import sophia.mmorpg.core.PropertyPool;

public abstract class FightPropertyMgr {
	private static final Logger logger = Logger.getLogger(FightPropertyMgr.class.getName());

	protected static final PropertyPool ModifyPhase_Pool = new PropertyPool();

	protected static final PropertyPool Snapshot_Pool = new PropertyPool();

	/** 引用的战斗属性数据。我们从这里获取基础值，做当前属性的计算；也从这里获取边界值（诸如HP,MP） */
	private PropertyDictionary ref;

	/** 当前的战斗属性数据 */
	protected PropertyDictionaryModifyPhase current;
	/** 当前效果：包括所有加点技能、装备etc. */
	protected PropertyDictionaryModifyPhase effectModifyPhase = Snapshot_Pool.obtain();

	private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	private Lock readLock = readWriteLock.readLock();
	private Lock writeLock = readWriteLock.writeLock();

	/** 当前的战斗属性数据快照 */
	protected PropertyDictionaryModifyPhase snapshot;

	private final FightPropertyModifyTransaction modifyTransaction;

	private final FightPropertyEffectTransaction effectTransaction;
	
	protected static final Set<Short> valueIds = new HashSet<Short>();
	protected static final Set<Short> rateIds = new HashSet<Short>();

	public FightPropertyMgr() {
		modifyTransaction = new FightPropertyModifyTransaction(this);
		effectTransaction = new FightPropertyEffectTransaction(this);
	}
	
	protected final static void addValueId(short id) {
		valueIds.add(id);
	}
	
	protected final static void addRateId(short id) {
		rateIds.add(id);
	}
	
	public Set<Short> getRateIds() {
		return rateIds;
	}
	
	public Set<Short> getValueIds() {
		return valueIds;
	}

	public FightPropertyModifyTransaction getModifyTransaction() {
		return modifyTransaction;
	}

	public FightPropertyEffectTransaction getEffectTransaction() {
		return effectTransaction;
	}
	
	public PropertyDictionary getRefPropertyDictionary() {
		readLock.lock();
		try {
			return ref;
		} finally {
			readLock.unlock();
		}
	}

	public void levelUp(PropertyDictionary leveRef) {
		writeLock.lock();
		PropertyDictionaryModifyPhase snapshot = null;
		try {
			this.ref = leveRef;
			snapshot = crtModifyImpl(leveRef);
			// TODO: 黄晓源 加满血|蓝
		} finally {
			writeLock.unlock();
			FightPropertyMgr.recycleSnapshotToPool(snapshot);
		}
	}

	public void setCrtPropertyDictionary(PropertyDictionary propertyDictionary) {
		writeLock.lock();
		PropertyDictionaryModifyPhase snapshot = null;
		try {
			this.ref = propertyDictionary;
			snapshot = crtModifyImpl(propertyDictionary);
		} finally {
			writeLock.unlock();
			FightPropertyMgr.recycleSnapshotToPool(snapshot);
		}
	}
	
	/**
	 * 设置当前snapshot的值，暂时用于数据库加载恢复玩家血量、魔法量
	 * @param id
	 * @param value
	 */
	public final void setSnapshotValueById(short id, int value) {
		writeLock.lock();
		try {
			this.snapshot.getPropertyDictionary().setOrPutValue(id, value);
		} finally {
			writeLock.unlock();
		}
	}
	
	public final int getSnapshotValueById(short id) {
		readLock.lock();
		try {
			return this.snapshot.getPropertyDictionary().getValue(id);
		} finally {
			readLock.unlock();
		}
	}

	public final PropertyDictionaryModifyPhase getSnapshotByNew() {
		readLock.lock();
		try {
			return new PropertyDictionaryModifyPhase(this.snapshot);
		} finally {
			readLock.unlock();
		}
	}
	
	public final PropertyDictionaryModifyPhase getSnapshotFromPool() {
		readLock.lock();
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("您拿了我一个池化对象[snapshot]，可得记得还给我。");
			}
			PropertyDictionaryModifyPhase ret = Snapshot_Pool.obtain();
			copy(this.snapshot, ret);
			return ret;
		} finally {
			readLock.unlock();
		}
	}

	@SuppressWarnings("unchecked")
	private void copy(PropertyDictionaryModifyPhase from, PropertyDictionaryModifyPhase to) {
		PropertyDictionary fromProperty = from.getPropertyDictionary();
		PropertyDictionary toProperty = to.getPropertyDictionary();
		Set<Entry<Short, SimulatorProperty<?>>> fromEntrySet = fromProperty.getDictionary().entrySet();
		for (Entry<Short, SimulatorProperty<?>> entry : fromEntrySet) {
			ValueProperty<Integer> valueProperty = (ValueProperty<Integer>) entry.getValue();
			toProperty.setOrPutValue(valueProperty.getId(), valueProperty.getValue());
		}
	}

	public static final void recycleSnapshotToPool(PropertyDictionaryModifyPhase snapshot) {
		Snapshot_Pool.recycle(snapshot);
		if (logger.isDebugEnabled()) {
			logger.debug("谢谢您还我[snaphost]。好人一生平安。");
		}
	}

	public static final PropertyDictionaryModifyPhase getModifyPhaseFromPool() {
		if (logger.isDebugEnabled()) {
			logger.debug("您拿了我一个池化对象[modify phase]，可得记得还给我。");
		}
		return ModifyPhase_Pool.obtain();
	}

	public static final void recycleModifyPhaseToPool(PropertyDictionaryModifyPhase modifyPhase) {
		ModifyPhase_Pool.recycle(modifyPhase);
		if (logger.isDebugEnabled()) {
			logger.debug("谢谢您还我[modify phase]。好人一生平安。");
		}
	}

	/**
	 * 事务更新当前战斗属性，并生成快照数据
	 * 
	 * @param modifyPhase
	 *            事务更新属性值
	 * @return 事务更新后的战斗属性快照
	 */
	public PropertyDictionaryModifyPhase modifyAndSnapshot(final PropertyDictionaryModifyPhase modifyPhase) {
		writeLock.lock();
		try {
			return modifyImpl(modifyPhase);
		} finally {
			writeLock.unlock();
		}
	}

	public PropertyDictionaryModifyPhase attachAndSnapshot(final Object attachObject, final PropertyDictionaryModifyPhase modify, final PropertyDictionaryModifyPhase effect) {
		writeLock.lock();
		try {
			return attachImpl(attachObject, modify, effect);
		} finally {
			writeLock.unlock();
		}
	}

	public PropertyDictionaryModifyPhase detachAndSnapshot(final Object detachObject, PropertyDictionaryModifyPhase effect) {
		writeLock.lock();
		try {
			return detachImpl(detachObject, effect);
		} finally {
			writeLock.unlock();
		}
	}
	
	public void attach(final Object attachObject, final PropertyDictionaryModifyPhase effect) {
		writeLock.lock();
		try {
			this.effectModifyPhase.modify(effect);
		} finally {
			writeLock.unlock();
		}
	}

	protected abstract PropertyDictionaryModifyPhase crtModifyImpl(PropertyDictionary propertyDictionary);

	protected abstract PropertyDictionaryModifyPhase modifyImpl(final PropertyDictionaryModifyPhase modifyPhase);

	protected abstract PropertyDictionaryModifyPhase attachImpl(final Object attachObject, final PropertyDictionaryModifyPhase modify, final PropertyDictionaryModifyPhase effect);

	protected abstract PropertyDictionaryModifyPhase detachImpl(final Object detachObject, PropertyDictionaryModifyPhase effect);
}
