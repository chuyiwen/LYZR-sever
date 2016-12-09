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
package sophia.mmorpg.player.money;

import sophia.foundation.property.PropertyDictionary;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemCode;
import sophia.mmorpg.player.money.gameEvent.ConsumeUnBindedGold_GE;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.stat.StatFunctions;
import sophia.mmorpg.stat.logs.StatMoney;

public final class PlayerMoneyComponent extends ConcreteComponent<Player> {

	private PropertyDictionary property = new PropertyDictionary();

	private int gold;

	private int bindGold;

	private int unbindGold;

	public synchronized int getUnbindGold() {
		return unbindGold;
	}

	public synchronized void setUnbindGold(int unbindGold) {
		if (unbindGold < 0)
			unbindGold = 0;
		this.unbindGold = unbindGold;

	}

	public synchronized int getBindGold() {
		return bindGold;
	}

	public synchronized void setBindGold(int bindGold) {
		if (bindGold < 0)
			bindGold = 0;
		this.bindGold = bindGold;
	}

	public synchronized int getGold() {
		return gold;
	}

	public synchronized void setGold(int gold) {
		if (gold < 0)
			gold = 0;
		this.gold = gold;
	}

	private synchronized void setGoldImpl(int gold) {
		if (gold < 0)
			gold = 0;
		this.gold = gold;
		notifyProperty();
	}

	private synchronized void setBindGoldImpl(int bindGold) {
		if (bindGold < 0)
			bindGold = 0;
		this.bindGold = bindGold;
		notifyProperty();
	}

	private synchronized void setUnbindGoldImpl(int unbindGold) {
		if (unbindGold < 0)
			unbindGold = 0;
		this.unbindGold = unbindGold;
		notifyProperty();
	}
	/**
	 * 不通知玩家
	 * @param gold
	 */
	private synchronized void setGoldNotNoticeImpl(int gold) {
		if (gold < 0)
			gold = 0;
		this.gold = gold;
	}

	private synchronized void setBindGoldNotNoticeImpl(int bindGold) {
		if (bindGold < 0)
			bindGold = 0;
		this.bindGold = bindGold;
	}

	private synchronized void setUnbindGoldNotNoticeImpl(int unbindGold) {
		if (unbindGold < 0)
			unbindGold = 0;
		this.unbindGold = unbindGold;
	}
	
	/**
	 * 货币是否足够
	 * @param money
	 * @param currencyUnit
	 * 		 CurrencyUnit.GOLD = 金币|CurrencyUnit.BINDED_GOLD = 绑定元宝|CurrencyUnit.UNBINDED_GOLD = 元宝
	 * @return
	 */
	public synchronized boolean isMoneyEnough(int money , byte currencyUnit){
		boolean isMoneyEnough = false;
		switch (currencyUnit) {
		case CurrencyUnit.GOLD:
			isMoneyEnough = getGold() >= money;
			break;
		case CurrencyUnit.BINDED_GOLD:
			isMoneyEnough = getBindGold() >= money;
			break;
		case CurrencyUnit.UNBINDED_GOLD:
			isMoneyEnough = getUnbindGold() >= money;
			break;
		default:
			break;
		}
		
		return isMoneyEnough;
	}
	
	/**
	 * 货币是否足够
	 * @param money
	 * @param moneyRefId
	 * 		 ItemCode.Gold_ID = 金币|ItemCode.BindedGold_ID = 绑定元宝|ItemCode.UnBindedGold_ID = 元宝
	 * @return
	 */
	public synchronized boolean isMoneyEnough(int money , String moneyRefId){
		boolean isMoneyEnough = false;
		switch (moneyRefId) {
		case ItemCode.Gold_ID:
			isMoneyEnough = getGold() >= money;
			break;
		case ItemCode.BindedGold_ID:
			isMoneyEnough = getBindGold() >= money;
			break;
		case ItemCode.UnBindedGold_ID:
			isMoneyEnough = getUnbindGold() >= money;
			break;
		default:
			break;
		}
		
		return isMoneyEnough;
	}
	
	/**
	 * 通过货币单位获取指定类型货币的数量
	 * @param currencyUnit
	 * 		 CurrencyUnit.GOLD = 金币|CurrencyUnit.BINDED_GOLD = 绑定元宝|CurrencyUnit.UNBINDED_GOLD = 元宝
	 * @return
	 */
	public synchronized int getMoneyByCurrencyUnit(byte currencyUnit){
		int money = 0;
		switch (currencyUnit) {
		case CurrencyUnit.GOLD:
			money = getGold();
			break;
		case CurrencyUnit.BINDED_GOLD:
			money = getBindGold();
			break;
		case CurrencyUnit.UNBINDED_GOLD:
			money = getUnbindGold();
			break;
		default:
			break;
		}
		
		return money;
	}
	
	/**
	 * 取得指定货币RefId的货币数量
	 * @param moneyRefId
	 * 		 ItemCode.Gold_ID = 金币|ItemCode.BindedGold_ID = 绑定元宝|ItemCode.UnBindedGold_ID = 元宝
	 * @return
	 */
	public synchronized int getMoneyByMoneyRefID(String moneyRefId){
		int money = 0;
		switch (moneyRefId) {
		case ItemCode.Gold_ID:
			money = getGold() ;
			break;
		case ItemCode.BindedGold_ID:
			money = getBindGold() ;
			break;
		case ItemCode.UnBindedGold_ID:
			money = getUnbindGold() ;
			break;
		default:
			break;
		}		
		return money;
	}
	/**
	 * 增加金币
	 * 
	 * @param value
	 * @return
	 */
	public synchronized boolean addGold(int value,byte source) {
		if (value < 0)
			return false;
		setGoldImpl(this.gold + value);
		StatFunctions.MoneyStat(getConcreteParent(), StatMoney.Add, getGold(), value, StatMoney.Gold,source);
		return true;
	}

	/**
	 * 增加绑定元宝
	 * 
	 * @param value
	 * @return
	 */
	public synchronized boolean addBindGold(int value,byte source) {
		if (value < 0)
			return false;
		setBindGoldImpl(this.bindGold + value);
		StatFunctions.MoneyStat(getConcreteParent(), StatMoney.Add, getBindGold(), value, StatMoney.BindGold,source);
		return true;
	}

	/**
	 * 增加元宝
	 * 
	 * @param value
	 * @return
	 */
	public synchronized boolean addUnbindGold(int value,byte source) {
		if (value < 0)
			return false;
		setUnbindGoldImpl(this.unbindGold + value);
		StatFunctions.MoneyStat(getConcreteParent(), StatMoney.Add, getUnbindGold(), value, StatMoney.UnBindGold,source);
		return true;
	}

	/**
	 * 减少金币
	 * 
	 * @param value
	 * @return
	 */
	public synchronized boolean subGold(int value,byte source) {
		if (value <= 0 || value > getGold())
			return false;
		setGoldImpl(this.gold - value);
		StatFunctions.MoneyStat(getConcreteParent(), StatMoney.Sub, getGold(), value, StatMoney.Gold,source);
		return true;
	}

	/**
	 * 减少绑定元宝
	 * 
	 * @param value
	 * @return
	 */
	public synchronized boolean subBindGold(int value,byte source) {
		if (value <= 0 || value > getBindGold())
			return false;
		setBindGoldImpl(this.bindGold - value);
		sendConsumeEvent(0, value,source);
		StatFunctions.MoneyStat(getConcreteParent(), StatMoney.Sub, getBindGold(), value, StatMoney.BindGold,source);
		return true;
	}

	/**
	 * 减少元宝
	 * 
	 * @param value
	 * @return
	 */
	public synchronized boolean subUnbindGold(int value,byte source) {
		if (value <= 0 || value > getUnbindGold())
			return false;
		setUnbindGoldImpl(this.unbindGold - value);
		sendConsumeEvent(value, 0,source);
		StatFunctions.MoneyStat(getConcreteParent(), StatMoney.Sub, getUnbindGold(), value, StatMoney.UnBindGold,source);
		return true;
	}

	
	
	/**
	 * 增加金币不通知玩家
	 * 
	 * @param value
	 * @return
	 */
	public synchronized boolean addGoldNotNotice(int value, byte source) {
		if (value < 0)
			return false;
		setGoldNotNoticeImpl(this.gold + value);
		StatFunctions.MoneyStat(getConcreteParent(), StatMoney.Add, getGold(), value, StatMoney.Gold,source);
		return true;
	}

	/**
	 * 增加绑定元宝不通知玩家
	 * 
	 * @param value
	 * @return
	 */
	public synchronized boolean addBindGoldNotNotice(int value,byte source) {
		if (value < 0)
			return false;
		setBindGoldNotNoticeImpl(this.bindGold + value);
		StatFunctions.MoneyStat(getConcreteParent(), StatMoney.Add, getBindGold(), value, StatMoney.BindGold,source);
		return true;
	}

	/**
	 * 增加元宝不通知玩家
	 * 
	 * @param value
	 * @return
	 */
	public synchronized boolean addUnbindGoldNotNotice(int value,byte source) {
		if (value < 0)
			return false;
		setUnbindGoldNotNoticeImpl(this.unbindGold + value);
		StatFunctions.MoneyStat(getConcreteParent(), StatMoney.Add, getUnbindGold(), value, StatMoney.UnBindGold,source);
		return true;
	}

	/**
	 * 减少金币 不通知玩家
	 * 
	 * @param value
	 * @return
	 */
	public synchronized boolean subGoldNotNotice(int value,byte source) {
		if (value <= 0 || value > getGold())
			return false;
		setGold(this.gold - value);
		StatFunctions.MoneyStat(getConcreteParent(), StatMoney.Sub, getGold(), value, StatMoney.Gold,source);
		return true;
	}

	/**
	 * 减少绑定元宝不通知玩家
	 * 
	 * @param value
	 * @return
	 */
	public synchronized boolean subBindGoldNotNotice(int value,byte source) {
		if (value <= 0 || value > getBindGold())
			return false;
		setBindGoldNotNoticeImpl(this.bindGold - value);
		StatFunctions.MoneyStat(getConcreteParent(), StatMoney.Sub, getBindGold(), value, StatMoney.BindGold,source);
		return true;
	}

	/**
	 * 减少元宝不通知玩家
	 * 
	 * @param value
	 * @return
	 */
	public synchronized boolean subUnbindGoldNotNotice(int value,byte source) {
		if (value <= 0 || value > getUnbindGold())
			return false;
		setUnbindGoldNotNoticeImpl(this.unbindGold - value);
		StatFunctions.MoneyStat(getConcreteParent(), StatMoney.Sub, getUnbindGold(), value, StatMoney.UnBindGold,source);
		return true;
	}
	
	public void notifyProperty() {
		MGPropertyAccesser.setOrPutGold(getProperty(), getGold());
		MGPropertyAccesser.setOrPutBindedGold(getProperty(), getBindGold());
		MGPropertyAccesser.setOrPutUnbindedGold(getProperty(), getUnbindGold());
		getConcreteParent().notifyPorperty(getProperty());
	}

	public PropertyDictionary getProperty() {
		return property;
	}

	public void setProperty(PropertyDictionary property) {
		this.property = property;
	}

	private void sendConsumeEvent(int unBindedGold, int bindedGold,byte source) {
		ConsumeUnBindedGold_GE consumeUnBindedGold_GE = new ConsumeUnBindedGold_GE(unBindedGold, bindedGold,source);
		GameEvent<ConsumeUnBindedGold_GE> ge = (GameEvent<ConsumeUnBindedGold_GE>) GameEvent.getInstance(ConsumeUnBindedGold_GE.class.getSimpleName(), consumeUnBindedGold_GE);
		sendGameEvent(ge, getConcreteParent().getId());

	}

}
