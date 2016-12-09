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
package sophia.mmorpg.player.exp;

import java.text.DecimalFormat;

import org.apache.log4j.Logger;

import sophia.foundation.property.PropertyDictionary;
import sophia.foundation.util.DebugUtil;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.chat.sysytem.SpecialEffectsType;
import sophia.mmorpg.player.chat.sysytem.SystemPromptFacade;
import sophia.mmorpg.player.chat.sysytem.SystemPromptPosition;
import sophia.mmorpg.player.exp.gameevent.PlayerLevelUp_GE;
import sophia.mmorpg.player.property.PlayerConfig;
import sophia.mmorpg.player.ref.PlayerProfessionLevelData;
import sophia.mmorpg.player.ref.PlayerProfessionRef;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.stat.StatFunctions;

public final class PlayerExpComponent extends ConcreteComponent<Player> {

	private static final Logger logger = Logger.getLogger(PlayerExpComponent.class);

	private PropertyDictionary property = new PropertyDictionary();

	private PropertyDictionary propertyExp = new PropertyDictionary();

	private float multipleExp = 1.0f;

	private int level;

	private long exp;

	private int deadExp = 0;

	public PlayerExpComponent() {
		MGPropertyAccesser.setOrPutExp(propertyExp, 0);
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {
		super.handleGameEvent(event);
	}

	/**
	 * 初始加载玩家数据，纯设置玩家的经验方法
	 * 
	 * @param value
	 */
	public synchronized void setExp(long value) {
		if (value <= 0)
			return;
		this.exp = value;
		Player player = getConcreteParent();
		MGPropertyAccesser.setOrPutExp(player.getProperty(), exp);
	}

	/**
	 * 增加玩家经验
	 * 
	 * @param delta
	 */
	public void addExp(int delta) {
		if (getConcreteParent().isDead()) {
			synchronized (this) {
				this.deadExp += delta;
			}
			return;
		}

		if (addExpThreadImpl(delta)) {
			modifyAndNotifyPlayerPropery();
		} else {
			notifyExpProperty();
		}
	}

	public void setLevel(int value) {
		setLevelImpl(value);
	}

	/**
	 * 通过debug指令设置玩家等级
	 * 
	 * @param newLevel
	 */
	public synchronized void setLevelFromDebug(int newLevel) {
		Player player = getConcreteParent();
		setLevel(newLevel);
		setExp(0);
		MGPropertyAccesser.setOrPutExp(player.getProperty(), 0);
		// use new ref data and notify client
		PlayerConfig.configFightPropertiesTo(player);
		// fire PlayerLevelUp_GE
		PlayerLevelUp_GE ge = PlayerLevelUp_GE.pool.obtain();
		ge.setCurLevel(newLevel);
		ge.setPlayer(getConcreteParent());
		sendGameEvent(PlayerLevelUp_GE.class.getSimpleName(), ge);
		PlayerLevelUp_GE.pool.recycle(ge);
		modifyPropertyByLevelChanged();

		StatFunctions.levelStat(player, newLevel, getExp());
		SystemPromptFacade.sendMsgSpecialEffects(player, "GM修改了您的角色等级，请您谅解！", SystemPromptPosition.POSITION_MIDDLE_1, SpecialEffectsType.SPECIAL_EFFECTS_TYPE_YELLOW);
	}

	public synchronized void incrementLevel() {
		setLevel(level + 1);
	}

	public synchronized long getExp() {
		return exp;
	}

	public synchronized int getLevel() {
		return level;
	}

	public void revive() {
		addExp(deadExp);
		this.deadExp = 0;
	}

	// --------------------------------------------------------------------

	/**
	 * 
	 * @param delta
	 * @return 如果等级变动返回 true,否则返回flase
	 */
	private synchronized boolean addExpThreadImpl(int delta) {
		boolean ret = false;

		int oldLevel = getLevel();
		setExpImpl(exp + delta);
		int newLevel = getLevel();
		Player player = getConcreteParent();
		MGPropertyAccesser.setOrPutExp(player.getProperty(), exp);
		if (newLevel > oldLevel) {
			ret = true;
		}

		return ret;
	}

	/**
	 * 当玩家等级发生变化时，通知玩家等级数据
	 */
	private void modifyAndNotifyPlayerPropery() {
		modifyPropertyByLevelChanged();

		Player player = getConcreteParent();
		PropertyDictionary fightProperty = player.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotByNew().getPropertyDictionary();
		player.notifyPorperty(fightProperty);
		player.notifyPorperty(property);

		notifyLevelModifyGameEvent();

		StatFunctions.levelStat(getConcreteParent(), level, getExp());
	}

	private void notifyLevelModifyGameEvent() {
		// fire PlayerLevelUp_GE
		PlayerLevelUp_GE ge = PlayerLevelUp_GE.pool.obtain();
		ge.setCurLevel(level);
		ge.setPlayer(getConcreteParent());
		sendGameEvent(PlayerLevelUp_GE.class.getSimpleName(), ge);
		PlayerLevelUp_GE.pool.recycle(ge);
	}

	/**
	 * 通知玩家经验值变化
	 */
	private void notifyExpProperty() {
		notifyPropertyJustExp();
		StatFunctions.levelStat(getConcreteParent(), level, getExp());
	}

	private synchronized void setExpImpl(long value) {
		if (value <= 0)
			return;

		long deltaExp = value;
		int oldLevel = getLevel();

		while (deltaExp > 0) {
			long maxExp = maxExp(getLevel());
			if (deltaExp >= maxExp) {
				if (getLevel() < maxLevel()) {
					deltaExp -= maxExp;
					if (deltaExp == 0) {
						this.exp = deltaExp;
					}
					incrementLevel();
				} else {
					this.exp = maxExp(maxLevel());
					deltaExp = 0;
				}
			} else {
				if (getLevel() >= maxLevel()) {
					this.exp = maxExp(maxLevel());
				} else {
					this.exp = deltaExp;
				}
				deltaExp = 0;
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("setExpImpl :" + oldLevel + " -> " + getLevel());
		}

	}

	private synchronized void modifyPropertyByLevelChanged() {
		Player player = getConcreteParent();

		PlayerConfig.configFightPropertiesTo(player);

		MGPropertyAccesser.setOrPutExp(property, getExp());
		MGPropertyAccesser.setOrPutLevel(property, getLevel());
		MGPropertyAccesser.setOrPutHP(property, player.getHP());
		MGPropertyAccesser.setOrPutMP(property, player.getMP());
		MGPropertyAccesser.setOrPutMaxHP(property, player.getHPMax());
		MGPropertyAccesser.setOrPutMaxMP(property, player.getMPMax());
	}

	public void notifyPropertyJustExp() {
		Player player = getConcreteParent();
		synchronized (this) {
			MGPropertyAccesser.setOrPutExp(propertyExp, getExp());
		}
		player.notifyPorperty(propertyExp);
	}

	private synchronized void setLevelImpl(int value) {
		if (value < 1)
			value = 1;
		if (value > maxLevel())
			value = maxLevel();

		this.level = value;
		Player player = getConcreteParent();
		MGPropertyAccesser.setOrPutLevel(player.getProperty(), value);
	}

	// -------------------------------------------------------------------------------------------------------
	public long maxExp(int level) {
		Player parent = getConcreteParent();
		PlayerProfessionRef professionRef = parent.getPlayerProfessionRef();
		PlayerProfessionLevelData playerClassLevelData = professionRef.getPlayerClassLevelData(level);
		PropertyDictionary levelProperties = playerClassLevelData.getLevelProperties();

		return MGPropertyAccesser.getMaxExp(levelProperties);
	}

	private int maxLevel() {
		Player parent = getConcreteParent();
		PlayerProfessionRef professionRef = parent.getPlayerProfessionRef();
		int maxLevel = professionRef.maxLevel();
		maxLevel = maxLevel > 80 ? 80 : maxLevel;
		return maxLevel;
	}

	public PropertyDictionary getProperty() {
		return property;
	}

	public void setProperty(PropertyDictionary property) {
		this.property = property;
	}

	// ------------------------------------------------------------------------------------------------

	public void addExpMultiple(float expMultiple) {
		Player player = getConcreteParent();
		float newExpMultiple = this.multipleExp + expMultiple;
		if (newExpMultiple <= 0) {
			logger.error("addExpMultiple , expMultiple =" + expMultiple + " ," + DebugUtil.printStack(new RuntimeException("经验倍数少于 0")));
		}
		if (newExpMultiple > 15) {
			newExpMultiple = 15;
			logger.error("addExpMultiple , expMultiple =" + expMultiple + " ," + DebugUtil.printStack(new RuntimeException("经验倍数大于 15")));
		}		
		this.multipleExp = newExpMultiple;
		logger.info("addExpMultiple , player:" + player.getName() + "当前经验倍数:" + multipleExp);
	}

	public void subExpMultiple(float expMultiple) {
		Player player = getConcreteParent();
		float newExpMultiple = this.multipleExp - expMultiple;
		if (newExpMultiple <= 0) {
			logger.error("subExpMultiple , expMultiple =" + expMultiple + " ," + DebugUtil.printStack(new RuntimeException("经验倍数少于 0")));
		}
		if (newExpMultiple > 15) {
			newExpMultiple = 15;
			logger.error("subExpMultiple , expMultiple =" + expMultiple + " ," + DebugUtil.printStack(new RuntimeException("经验倍数大于 15")));
		}
		newExpMultiple = newExpMultiple < 1 ? 1 : newExpMultiple;
		DecimalFormat df = new DecimalFormat("#.0");
		this.multipleExp = Float.valueOf(df.format(newExpMultiple));
		logger.info("subExpMultiple , player:" + player.getName() + "当前经验倍数:" + multipleExp);
	}

	public void setExpMultiple(float expMultiple) {
		this.multipleExp = expMultiple;
	}

	public float getExpMultiple() {
		return this.multipleExp;
	}
}
