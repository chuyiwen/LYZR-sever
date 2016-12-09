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
package newbee.morningGlory.mmorpg.player.activity.sevenLogin;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map.Entry;

import newbee.morningGlory.mmorpg.player.activity.sevenLogin.event.C2G_SevenLogin_HadReceive;
import newbee.morningGlory.mmorpg.player.activity.sevenLogin.event.C2G_SevenLogin_HaveReceive;
import newbee.morningGlory.mmorpg.player.activity.sevenLogin.event.C2G_SevenLogin_ReceiveState;
import newbee.morningGlory.mmorpg.player.activity.sevenLogin.event.G2C_SevenLogin_HaveReceive;
import newbee.morningGlory.mmorpg.player.activity.sevenLogin.event.G2C_SevenLogin_ReceiveState;
import newbee.morningGlory.mmorpg.player.activity.sevenLogin.event.MGSevenLoginDefines;
import newbee.morningGlory.mmorpg.vip.MGPlayerVipComponent;

import org.apache.log4j.Logger;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.itemBag.ItemPair;
/**
 * 此七日登录代码暂时废弃
 */
public class MGPlayerSevenLoginComponent extends ConcreteComponent<Player> {
	private static final Logger logger = Logger.getLogger(MGPlayerVipComponent.class);
	public static final String Tag = "MGPlayerSevenLoginComponent";
	private MGSevenLoginRecord record = new MGSevenLoginRecord();
	private static final String SevenLoginRef = "sevenlogin_";

	public MGPlayerSevenLoginComponent() {
	}

	@Override
	public void ready() {
//		addActionEventListener(MGSevenLoginDefines.C2G_SevenLogin_ReceiveState);
//		addActionEventListener(MGSevenLoginDefines.C2G_SevenLogin_HadReceive);
//		addActionEventListener(MGSevenLoginDefines.C2G_SevenLogin_HaveReceive);
	}

	@Override
	public void suspend() {
//		removeActionEventListener(MGSevenLoginDefines.C2G_SevenLogin_ReceiveState);
//		removeActionEventListener(MGSevenLoginDefines.C2G_SevenLogin_HadReceive);
//		removeActionEventListener(MGSevenLoginDefines.C2G_SevenLogin_HaveReceive);
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {
		
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
		short actionEventId = event.getActionEventId();

		switch (actionEventId) {
		case MGSevenLoginDefines.C2G_SevenLogin_ReceiveState:
			handle_SevenLogin_ReceiveState((C2G_SevenLogin_ReceiveState) event);
			break;
		case MGSevenLoginDefines.C2G_SevenLogin_HadReceive:
			handle_SevenLogin_Receive((C2G_SevenLogin_HadReceive) event);
			break;
		case MGSevenLoginDefines.C2G_SevenLogin_HaveReceive:
			handle_SevenLogin_HaveReceive((C2G_SevenLogin_HaveReceive) event);
			break;
		default:
			break;
		}

	}
	private void handle_SevenLogin_HaveReceive(C2G_SevenLogin_HaveReceive event){
		int whichDay = whichDay();
		if(logger.isDebugEnabled()){
			logger.debug("今天已开服第几天："+whichDay);
		}
		if(whichDay > this.getRecord().getSevenLoginMaps().size()){
			return;
		}
		if (this.getRecord().getSevenLoginMaps().get(whichDay) != MGSevenLoginRewardType.HadReceive) {
			this.getRecord().updateRewardStatus(whichDay, MGSevenLoginRewardType.NoReceive);
		}
		byte ret = haveRewardDays();
		G2C_SevenLogin_HaveReceive res = MessageFactory.getConcreteMessage(MGSevenLoginDefines.G2C_SevenLogin_HaveReceive);
		res.setRet(ret);
		GameRoot.sendMessage(getConcreteParent().getIdentity(), res);
//		// 测试代码， 需删掉
//		long openServer = this.getRecord().getOpenServerDate();
//		openServer -= 3600 * 24 * 1000 * 1l;
//		this.getRecord().setOpenServerDate(openServer);
	}
	private void handle_SevenLogin_Receive(C2G_SevenLogin_HadReceive event) {

		int whichDay = event.getWhichDay();
		if (logger.isDebugEnabled()) {
			logger.debug("请求领取 第" + whichDay + "天的奖励");
		}
		if (this.getRecord().isHadReceiveThisDay(whichDay)) {
			if (logger.isDebugEnabled()) {
				logger.debug("第" + whichDay + "天已领取过奖励!");
			}
			return;
		}
		String sevenRefId = SevenLoginRef + whichDay;
		MGSevenLoginRef ref = (MGSevenLoginRef) GameRoot.getGameRefObjectManager().getManagedObject(sevenRefId);
		if (ref == null) {
			return;
		}
		List<ItemPair> rewardList = ref.getRewardList();
		for (ItemPair itemPair : rewardList) {
			ItemFacade.addItem(getConcreteParent(), itemPair,ItemOptSource.SevenLogin);
		}

		this.getRecord().updateRewardStatus(whichDay, MGSevenLoginRewardType.HadReceive);

	}

	private void handle_SevenLogin_ReceiveState(C2G_SevenLogin_ReceiveState event) {
		if (logger.isDebugEnabled()) {
			logger.debug("请求七日登录状态");
		}
		sendReceiveState();
	}

	public void sendReceiveState() {
		if (logger.isDebugEnabled()) {
			logger.debug("发送七日登录状态");
		}
		long openTime = this.getRecord().getOpenServerDate();
	       
		long overTime = openTime + 3600 * 1000 * 24 * 7l;
	           
		String duration = getDateString(openTime) +"-"+getDateString(overTime);
		
		int whichDay = whichDay();
		G2C_SevenLogin_ReceiveState res = MessageFactory.getConcreteMessage(MGSevenLoginDefines.G2C_SevenLogin_ReceiveState);
		res.setDuration(duration);
		res.setWhichDay(whichDay);
		res.setRecord(getRecord());
		GameRoot.sendMessage(getConcreteParent().getIdentity(), res);
	}

	public byte haveRewardDays() {
		byte ret = 0;
		for (Entry<Integer, Byte> entry : this.getRecord().getSevenLoginMaps().entrySet()) {
			if (entry.getValue() == MGSevenLoginRewardType.NoReceive) {
				ret++;
			}
		}
		return ret;
	}
	public String getDateString(long crtTime){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(crtTime);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
		return format.format(cal.getTime());
	}
	/**
	 * 距离开服过了多少天
	 * 
	 * @return
	 */
	public int whichDay() {
		long crtTime = System.currentTimeMillis();
		long openServerTime = getRecord().getOpenServerDate();
		long totaltime = crtTime - getLongTimeOfToday(openServerTime);
		int whichDay = (int) (totaltime) / (3600 * 1000 * 24) + 1;
		return whichDay;
	}

	/**
	 * 取得指定时间某天开始时的毫秒数
	 * 
	 * @return
	 */
	public long getLongTimeOfToday(long time) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return cal.getTime().getTime();
	}

	public MGSevenLoginRecord getRecord() {
		return record;
	}

	public void setRecord(MGSevenLoginRecord record) {
		this.record = record;
	}

}
