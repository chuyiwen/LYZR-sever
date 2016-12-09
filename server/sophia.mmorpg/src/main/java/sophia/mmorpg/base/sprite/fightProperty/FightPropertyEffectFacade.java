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

import org.apache.log4j.Logger;

import sophia.foundation.property.PropertyDictionary;
import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.core.PropertyDictionaryModifyPhase;
import sophia.mmorpg.player.Player;

public final class FightPropertyEffectFacade {

	private static final Logger logger = Logger.getLogger(FightPropertyEffectFacade.class);
	private static FightPropertyHelper propertyHelper;

	private static void checkArgument(Player player, PropertyDictionary pd) {
		if (player == null || pd == null) {
			return;
		}
	}

	/**
	 * 附加战斗属性，不计算 <br>
	 * 只用于玩家登陆加载战斗属性数据附加，被setDataFrom调用
	 * 
	 * @param player
	 * @param pd
	 */
	public static void attachWithoutSnapshot(Player player, PropertyDictionary pd) {
		checkArgument(player, pd);
		FightPropertyMgr fightPropertyMgr = player.getFightPropertyMgrComponent().getFightPropertyMgr();
		PropertyDictionaryModifyPhase appendPhase = FightPropertyMgr.getModifyPhaseFromPool();

		try {
			appendPhase.modify(pd);
			if (logger.isDebugEnabled()) {
				logger.debug("attachWithoutSnapshot " + appendPhase);
			}
			fightPropertyMgr.attach(null, appendPhase);
		} finally {
			FightPropertyMgr.recycleModifyPhaseToPool(appendPhase);
		}

	}

	/**
	 * 附加战斗属性，计算并且通知属性更新
	 * 
	 * @param player
	 * @param pd
	 */
	public static void attachAndNotify(Player player, PropertyDictionary pd) {
		checkArgument(player, pd);
		FightPropertyMgr fightPropertyMgr = player.getFightPropertyMgrComponent().getFightPropertyMgr();
		PropertyDictionaryModifyPhase attachPhase = FightPropertyMgr.getModifyPhaseFromPool();
		PropertyDictionaryModifyPhase modifyPhase = null;
		try {
			attachPhase.modify(pd);
			if (logger.isDebugEnabled()) {
				logger.debug("attachAndNotify " + attachPhase);
			}
			modifyPhase = fightPropertyMgr.attachAndSnapshot(null, null, attachPhase);
			if (modifyPhase != null) {
				player.notifyPorperty(modifyPhase.getModifiedAttributes());
			}
		} finally {
			FightPropertyMgr.recycleModifyPhaseToPool(attachPhase);
			FightPropertyMgr.recycleSnapshotToPool(modifyPhase);
		}

		if (logger.isDebugEnabled()) {
			propertyHelper.checkAttributeValidity(player);
		}
	}

	/**
	 * 移除战斗属性，计算但不通知属性更新 <br>
	 * 一般用于技能升级、身上装备强化等
	 * 
	 * @param player
	 * @param pd
	 */
	public static void detachAndSnapshot(Player player, PropertyDictionary pd) {
		checkArgument(player, pd);
		FightPropertyMgr fightPropertyMgr = player.getFightPropertyMgrComponent().getFightPropertyMgr();
		PropertyDictionaryModifyPhase detachPhase = FightPropertyMgr.getModifyPhaseFromPool();
		PropertyDictionaryModifyPhase modifyPhase = null;
		try {
			detachPhase.modify(pd);
			if (logger.isDebugEnabled()) {
				logger.debug("detachAndSnapshot " + detachPhase);
			}
			modifyPhase = fightPropertyMgr.detachAndSnapshot(null, detachPhase);
		} finally {
			FightPropertyMgr.recycleModifyPhaseToPool(detachPhase);
			FightPropertyMgr.recycleSnapshotToPool(modifyPhase);
		}
	}

	/**
	 * 移除战斗属性，计算并通知属性更新
	 * 
	 * @param player
	 * @param pd
	 */
	public static void detachAndNotify(Player player, PropertyDictionary pd) {
		checkArgument(player, pd);
		FightPropertyMgr fightPropertyMgr = player.getFightPropertyMgrComponent().getFightPropertyMgr();
		PropertyDictionaryModifyPhase detachPhase = FightPropertyMgr.getModifyPhaseFromPool();
		PropertyDictionaryModifyPhase modifyPhase = null;
		try {
			detachPhase.modify(pd);
			if (logger.isDebugEnabled()) {
				logger.debug("detachAndNotify " + detachPhase);
			}
			modifyPhase = fightPropertyMgr.detachAndSnapshot(null, detachPhase);
			if (modifyPhase != null) {
				player.notifyPorperty(modifyPhase.getModifiedAttributes());
			}
		} finally {
			FightPropertyMgr.recycleModifyPhaseToPool(detachPhase);
			FightPropertyMgr.recycleSnapshotToPool(modifyPhase);
		}

	}

	public static void detachAndNotifySprite(FightSprite sprite, PropertyDictionary pd) {

		FightPropertyMgr fightPropertyMgr = sprite.getFightPropertyMgrComponent().getFightPropertyMgr();
		PropertyDictionaryModifyPhase detachPhase = FightPropertyMgr.getModifyPhaseFromPool();
		PropertyDictionaryModifyPhase modifyPhase = null;
		try {
			detachPhase.modify(pd);
			if (logger.isDebugEnabled()) {
				logger.debug("detachAndNotify " + detachPhase);
			}
			modifyPhase = fightPropertyMgr.detachAndSnapshot(null, detachPhase);
			if (sprite instanceof Player && modifyPhase != null) {
				Player player = (Player) sprite;
				player.notifyPorperty(modifyPhase.getModifiedAttributes());
			}
		} finally {
			FightPropertyMgr.recycleModifyPhaseToPool(detachPhase);
			FightPropertyMgr.recycleSnapshotToPool(modifyPhase);
		}

	}

	public static void attachAndNotifySprite(FightSprite sprite, PropertyDictionary pd) {

		FightPropertyMgr fightPropertyMgr = sprite.getFightPropertyMgrComponent().getFightPropertyMgr();
		PropertyDictionaryModifyPhase attachPhase = FightPropertyMgr.getModifyPhaseFromPool();
		PropertyDictionaryModifyPhase modifyPhase = null;
		try {
			attachPhase.modify(pd);
			modifyPhase = fightPropertyMgr.attachAndSnapshot(null, null, attachPhase);
			if (sprite instanceof Player && modifyPhase != null) {
				Player player = (Player) sprite;
				player.notifyPorperty(modifyPhase.getModifiedAttributes());
			}
		} finally {
			FightPropertyMgr.recycleModifyPhaseToPool(attachPhase);
			FightPropertyMgr.recycleSnapshotToPool(modifyPhase);
		}

		if (sprite instanceof Player) {
			Player player = (Player) sprite;
			if (logger.isDebugEnabled()) {
				propertyHelper.checkAttributeValidity(player);
			}
		}
	}

	public static FightPropertyHelper getPropertyHelper() {
		return propertyHelper;
	}

	public static void setPropertyHelper(FightPropertyHelper propertyHelper) {
		FightPropertyEffectFacade.propertyHelper = propertyHelper;
	}
}
