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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import sophia.game.GameRoot;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemCode;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.DateTimeUtil;
import sophia.mmorpg.utils.RuntimeResult;
import sophia.mmorpg.utils.SFRandomUtils;

import com.google.common.base.Preconditions;

/**
 * 玩家-法宝管理
 */
public final class MGPlayerCitta {

	private MGCittaRef cittaRef;

	public static final int MaxLevel = 60;

	public static final int TalismanNumber = 10;

	public static final int MaxRewardLimit = 7 * 24 * 3600 * 1000;

	private int wingQuestStatus = 0;

	private int mountQuestStatus = 0;

	public int goldReward = 0;

	public int expReward = 0;

	private long lastRewardTime = 0;

	public List<ItemPair> baoXiangs = new ArrayList<ItemPair>();;

	public int stoneReward = 0;

	private List<MGTalismanContains> talismanContainList = new ArrayList<MGTalismanContains>();

	private MGTalisman crtActiveTalisman;

	public MGPlayerCitta() {

	}

	public MGPlayerCitta(MGCittaRef cittaRef) {
		this.cittaRef = cittaRef;
	}

	public MGTalisman getCrtActiveTalisman() {
		return crtActiveTalisman;
	}

	public void emptyCrtActiveTalisman() {
		this.crtActiveTalisman = null;
	}

	public void setCrtActiveTalisman(MGTalisman crtActiveTalisman) {
		this.crtActiveTalisman = crtActiveTalisman;
	}

	/**
	 * 索取到指定位置上的法宝
	 * 
	 * @param index
	 * @return
	 */
	public MGTalisman getTalisman(int index) {
		for (MGTalismanContains talismanContains : talismanContainList) {
			if (talismanContains.getIndex() == index) {
				return talismanContains.getTalisman();
			}
		}

		return null;
	}

	/**
	 * 索取到指定的法宝
	 * 
	 * @param index
	 * @return
	 */
	public MGTalisman getTalisman(String talismanRefId) {
		for (MGTalismanContains talismanContains : talismanContainList) {
			if (StringUtils.equals(talismanContains.getTalisman().getTalismanRef().getId(), talismanRefId)) {
				return talismanContains.getTalisman();
			}
		}
		return null;
	}

	/**
	 * 增加法宝
	 * 
	 * @param talismanContains
	 */
	public void addTalisman(MGTalismanContains talismanContains) {
		Preconditions.checkNotNull(talismanContains);
		if (talismanContainList.size() < TalismanNumber) {
			talismanContainList.add(talismanContains);
		}
	}

	public int getTalismanSystemActiveState() {
		int state = 0;
		if (this.getCittaRef() != null) {
			state = 1;
		}
		return state;
	}

	public int getLevel() {
		if (this.getCittaRef() == null) {
			return 0;
		}
		return MGPropertyAccesser.getTalisManLevel(this.cittaRef.getProperty());

	}

	/**
	 * 心法升级
	 * 
	 * @param player
	 * @return
	 */
	public RuntimeResult levelUp(Player player, MGTalismanEffectMgr effectMgr) {
		RuntimeResult ret = RuntimeResult.OK();

		RuntimeResult validRet = valid(player);

		if (validRet.isOK()) {
			MGCittaRef oldCittaRef = this.cittaRef;
			int probability = MGPropertyAccesser.getProbability(this.cittaRef.getProperty());
			int random = SFRandomUtils.random100();
			if (random <= probability) {
				effectMgr.detachAndSnapshot(oldCittaRef);
				String nextRefId = this.cittaRef.getNextRefId();
				if (nextRefId == null) {
					return RuntimeResult.ParameterError();
				}
				MGCittaRef cittaRef = (MGCittaRef) GameRoot.getGameRefObjectManager().getManagedObject(nextRefId);
				setCittaRef(cittaRef);
				effectMgr.attach(cittaRef);
				for (MGTalismanContains talismanContains : talismanContainList) {
					levelUp(talismanContains.getTalisman(), player, cittaRef.getCittaLevel());
				}
			} else {
				ret = RuntimeResult.RuntimeError();

			}
			ItemFacade.removeItem(player, ItemCode.JinJieShi, MGPropertyAccesser.getUseMaterialCount(oldCittaRef.getProperty()), true, ItemOptSource.Talisman);
		} else {
			ret = validRet;
		}

		return ret;
	}

	/**
	 * 心法升级校验
	 * 
	 * @param player
	 * @return
	 */
	public RuntimeResult valid(Player player) { // 升级条件判断
		RuntimeResult ret = RuntimeResult.OK();
		int useMaterialCount = MGPropertyAccesser.getUseMaterialCount(this.cittaRef.getProperty());
		int haveMaterialCount = ItemFacade.getNumber(player, ItemCode.JinJieShi);

		if (getLevel() >= MGPlayerCitta.MaxLevel) {
			ret = RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_ACTIVE_MAX_LEVEL);
		} else if (haveMaterialCount < useMaterialCount) {
			ret = RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_ITEM_NOENOUGH);
		}
		return ret;
	}

	/**
	 * 法宝升级
	 * 
	 * @param talisman
	 * @return
	 */
	private RuntimeResult levelUp(MGTalisman talisman, Player player, int level) {
		RuntimeResult result = RuntimeResult.OK();

		boolean isActiveBeforeLevelUp = false;
		if (talisman.isActive()) {
			talisman.setIsNotify(false);
			talisman.unactive(player);
			isActiveBeforeLevelUp = true;
		}
		talisman.levelUp(player, level);

		if (isActiveBeforeLevelUp) {
			talisman.active(player);
		}

		return result;
	}

	/**
	 * 领取法宝奖励
	 * 
	 * @param talisman
	 * @param player
	 * @return
	 */
	public RuntimeResult reward(Player player) {
		RuntimeResult result = RuntimeResult.OK();
		int gold = getGoldReward();
		int exp = getExpReward();
		int stoneNumber = getStoneReward();
		int baoxiangs = getBaoXiangsCount();
		ItemPair stonePair = new ItemPair(ItemCode.JinJieShi, stoneNumber, false);
		List<ItemPair> itemPairs = new ArrayList<ItemPair>();
		itemPairs.add(stonePair);
		itemPairs.addAll(getBaoXiangs());
		result = ItemFacade.addItem(player, itemPairs, ItemOptSource.Talisman);
		if (result.isOK()) {
			player.getPlayerMoneyComponent().addGold(gold, ItemOptSource.Talisman);
			player.getExpComponent().addExp(exp);
			clearReward();
			setLastRewardTime(System.currentTimeMillis());
			MGPlayerTalismanComponent component = (MGPlayerTalismanComponent) player.getTagged(MGPlayerTalismanComponent.Tag);
			component.getStatistics().addTalismanStatistics(MGTalismanStatistics.Total_BaoXiang, baoxiangs);
			component.getStatistics().addTalismanStatistics(MGTalismanStatistics.Total_Gold, gold);
			component.getStatistics().addTalismanStatistics(MGTalismanStatistics.Total_Exp, exp);
			component.getStatistics().addTalismanStatistics(MGTalismanStatistics.Total_ShenQiExp, stoneNumber);
		}
		return result;
	}

	public void speciaTalismanHandle(MGTalisman talisman, Player player) {
		if (StringUtils.equals("title_3", talisman.getTalismanRef().getId()) || StringUtils.equals("title_5", talisman.getTalismanRef().getId())
				|| StringUtils.equals("title_6", talisman.getTalismanRef().getId())) {

			long lastLogoutTime = MGPropertyAccesser.getLastLogoutTime(player.getProperty());
			long lastHandleTime = talisman.getLastHandleTime();
			if (lastRewardTime == 0) {
				lastRewardTime = lastHandleTime;
			}
			long time = lastLogoutTime - lastRewardTime;
			long canRewardTime = MaxRewardLimit - time; // 结算时间不超过7天
			canRewardTime = canRewardTime < 0 ? 0 : canRewardTime;
			long crtTime = System.currentTimeMillis();
			canRewardTime = crtTime - lastHandleTime > canRewardTime ? canRewardTime : crtTime - lastHandleTime;

			int hours = (int) canRewardTime / MGTalisman.DEFAULT_HANDLE_TIME;

			int lastHandleHour = (int) (crtTime - lastHandleTime) / MGTalisman.DEFAULT_HANDLE_TIME;
			// 更新最后执行为上次执行 +此时距离上次更新的小时数 的毫秒数
			talisman.setLastHandleTime(lastHandleTime + lastHandleHour * MGTalisman.DEFAULT_HANDLE_TIME);

			if (StringUtils.equals("title_3", talisman.getTalismanRef().getId())) {
				sendTitleThree(talisman, hours, player);
			} else if (StringUtils.equals("title_5", talisman.getTalismanRef().getId())) {
				sendTitleFive(talisman, lastHandleTime, crtTime, player);
			} else {
				sendTitleSix(talisman, hours, player);
			}
		}
	}

	public int getTotalTalismanCount() {
		int count = 0;
		for (MGTalismanContains talistmanContains : talismanContainList) {
			MGTalisman talisman = talistmanContains.getTalisman();
			if (talisman.isNotAcquire()) {
				continue;
			}

			count++;
		}

		return count;
	}

	// 聚宝盆
	private void sendTitleFive(MGTalisman talisman, long endTime, long startTime, Player player) {
		if (startTime < endTime) {
			return;
		}
		talisman.updateLastHandleTime();
		long between = (startTime - DateTimeUtil.getLongTimeOfToday(endTime)) / 1000;
		long day = between / (24 * 3600);
		if (day <= 0) {
			return;
		}
		int number = MGPropertyAccesser.getNumber(talisman.getTalismanRef().getEffectData()) * (int) day;
		String itemRefId = MGPropertyAccesser.getItemRefId(talisman.getTalismanRef().getEffectData());
		if (number <= 0) {
			return;
		}
		ItemPair baoXiang = new ItemPair(itemRefId, number, true);
		getBaoXiangs().add(baoXiang);
	}

	// 金手指
	private void sendTitleThree(MGTalisman talisman, int hours, Player player) {
		if (hours <= 0) {
			return;
		}
		int gold = MGPropertyAccesser.getGold(talisman.getTalismanRef().getEffectData()) * hours;
		setGoldReward(getGoldReward() + gold);
	}

	// 聚灵壶
	private void sendTitleSix(MGTalisman talisman, int hours, Player player) {
		if (hours <= 0) {
			return;
		}
		int number = MGPropertyAccesser.getNumber(talisman.getTalismanRef().getEffectData()) * hours;
		if (number <= 0) {
			return;
		}
		setStoneReward(getStoneReward() + number);
	}

	public MGCittaRef getCittaRef() {
		return cittaRef;
	}

	public void setCittaRef(MGCittaRef cittaRef) {
		this.cittaRef = cittaRef;
	}

	public int getWingQuestStatus() {
		return wingQuestStatus;
	}

	public int getMountQuestStatus() {
		return mountQuestStatus;
	}

	public List<MGTalismanContains> getTalismanList() {
		return talismanContainList;
	}

	public void setTalismanList(List<MGTalismanContains> mgTalismanList) {
		this.talismanContainList = mgTalismanList;
	}

	public boolean IsWingQuestCompleted() {
		return wingQuestStatus == 1;
	}

	public boolean IsMountQuestCompleted() {
		return mountQuestStatus == 1;
	}

	public void setWingQuestStatus(int wingQuestStatus) {
		this.wingQuestStatus = wingQuestStatus;
	}

	public void setMountQuestStatus(int mountQuestStatus) {
		this.mountQuestStatus = mountQuestStatus;
	}

	public int getGoldReward() {
		return goldReward;
	}

	public void setGoldReward(int goldReward) {
		this.goldReward = goldReward;
	}

	public void addGoldReward(int goldReward) {
		if (isMoreThanSevenDay()) {
			return;
		}
		this.goldReward += goldReward;
	}

	public int getExpReward() {
		return expReward;
	}

	public void setExpReward(int expReward) {
		this.expReward = expReward;
	}

	public void addExpReward(int expReward) {
		if (isMoreThanSevenDay()) {
			return;
		}
		this.expReward += expReward;
	}

	public int getStoneReward() {
		return stoneReward;
	}

	public void setStoneReward(int stoneReward) {
		this.stoneReward = stoneReward;
	}

	public void addStoneReward(int stoneReward) {
		if (isMoreThanSevenDay()) {
			return;
		}
		this.stoneReward += stoneReward;
	}

	public List<ItemPair> getBaoXiangs() {
		return baoXiangs;
	}

	public int getBaoXiangsCount() {
		int count = 0;
		for (ItemPair pair : baoXiangs) {
			count += pair.getNumber();
		}
		return count;
	}

	public void setBaoXiangs(List<ItemPair> baoXiangs) {
		this.baoXiangs = baoXiangs;
	}

	public void addBaoXiangs(ItemPair baoXiang) {
		if (isMoreThanSevenDay()) {
			return;
		}
		this.baoXiangs.add(baoXiang);
	}

	public void clearReward() {
		setGoldReward(0);
		setExpReward(0);
		setStoneReward(0);
		getBaoXiangs().clear();
	}

	public long getLastRewardTime() {
		return lastRewardTime;
	}

	public void setLastRewardTime(long lastRewardTime) {
		this.lastRewardTime = lastRewardTime;
	}

	public boolean isMoreThanSevenDay() {
		if (lastRewardTime == 0) {
			lastRewardTime = System.currentTimeMillis();
			return false;
		}
		long now = System.currentTimeMillis();
		return (now - lastRewardTime) > MaxRewardLimit;
	}

	
}
