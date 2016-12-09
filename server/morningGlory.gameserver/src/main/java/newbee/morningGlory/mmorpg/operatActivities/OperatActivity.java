package newbee.morningGlory.mmorpg.operatActivities;

import java.util.Date;
import java.util.List;

import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.utils.RuntimeResult;

/**
 * 运营活动接口<br>
 */
public abstract class OperatActivity {
	private boolean opened = true;

	/**
	 * 活动引用对象
	 * 
	 * @return
	 */
	public abstract OperatActivityRef getRef();

	/**
	 * 修改数据<br>
	 * 比如首充礼包的玩家数据（0-未充值，1-已充值未领取，2-已领取）
	 * 
	 * @param objs
	 *            外部传入的任意参数，请根据不同的运营活动做参数
	 */
	public abstract void modify(Object... objs);

	/**
	 * 是否可以领取奖励
	 * 
	 * @param objs
	 *            外部传入的任意参数，请根据不同的运营活动做参数
	 * @return
	 */
	public abstract boolean canReceiveAward(Object... objs);

	/**
	 * 领取奖励
	 * 
	 * @param objs
	 *            外部传入的任意参数，请根据不同的运营活动做参数
	 */
	public abstract void receiveAward(Object... objs);

	/**
	 * 运营活动开始事件
	 */
	public abstract void onOperatActivityStart();

	public final void onOperatActivityStart0() {
		onOperatActivityStart();
		OperatActivityRef ref = getRef();
		if (ref != null) {
			OperatActivityMgr.getInstance().onOperatActivityStart(ref.getType());
		}
	}

	/**
	 * 运营活动结束事件
	 */
	public abstract void onOperatActivityEnd();

	public final void onOperatActivityEnd0() {
		onOperatActivityEnd();
		OperatActivityRef ref = getRef();
		if (ref != null) {
			OperatActivityMgr.getInstance().onOperatActivityEnd(ref.getType());
			OperatActivityMgr.getInstance().load(ref.getType());// 活动结束后重新载入数据
		}
	}

	/**
	 * 进入新的一天
	 */
	public abstract void onEnterNewDay();

	/**
	 * 进入新的一周
	 */
	public abstract void onEnterNewWeek();

	/**
	 * 进入新的一月
	 */
	public abstract void onEnterNewMonth();

	/**
	 * 引用数据加载完成前
	 */
	public void onBeforeLoadRef() {

	}

	/**
	 * 引用数据加载完成后
	 */
	public void onAfterLoadRef() {

	}

	/**
	 * 分钟定时事件
	 */
	public final void onMinute0() {
		checkOpenStatus();
		if (getRef() != null)
			onMinute();
	}

	public void checkOpenStatus() {
		OperatActivityRef ref = getRef();
		if (ref == null) {
			if (isOpened()) {
				setOpened(false);
			}
			return;
		}
		if (isOpened()) {// 已经开启
			if (!isOpening()) {
				setOpened(false);
				Date endTime = ref.getEndTime();
				if (Math.abs(System.currentTimeMillis() - endTime.getTime()) <= 1000 * 60 * 1) {
					onOperatActivityEnd0();
				}
			}
		} else {// 没有开启
			if (isOpening()) {
				setOpened(true);
				Date openTime = ref.getOpenTime();
				if (Math.abs(System.currentTimeMillis() - openTime.getTime()) <= 1000 * 60 * 1) {
					onOperatActivityStart0();
				}
			}
		}
	}

	/**
	 * 清理玩家数据
	 * 
	 * @param playerCharacter
	 */
	public void clearPlayerCharacterData(Player playerCharacter) {

	}

	/**
	 * 分钟定时事件
	 */
	public abstract void onMinute();

	/**
	 * 登陆完成后
	 */
	public void onPlayerPostLogin(Player playerCharacter) {
	}

	/**
	 * 登陆完成前
	 */
	public void onPlayerPreLogin(Player playerCharacter) {
	}

	/**
	 * 奖励活动物品<br>
	 * 
	 * @param player
	 * @param items
	 * @return
	 */
	public int rewardItems(Player player, List<ItemPair> items,byte source) {
		
		if (items.size() > 0) {
			RuntimeResult rs = ItemFacade.addItemCompareSlot(player, items,source);
			if(!rs.isOK()){
				return rs.getApplicationCode();
			}

		}
		return 0;
	}

	public boolean isOpening(Player playerCharacter) {
		return true;
	}
	
	public boolean isOpening(long time) {
		OperatActivityRef ref = getRef();
		if (ref == null)
			return false;
//		if (ref.getData().getInt("status") == 0)
//			return false;
		Date openTime = ref.getOpenTime();
		Date endTime = ref.getEndTime();
		if (openTime != null) {
			if (time < openTime.getTime())
				return false;
		}
		if (endTime != null) {
			if (time > endTime.getTime())
				return false;
		}
		return true;
	}

	public boolean isOpening() {
		return isOpening(System.currentTimeMillis());
	}

	public boolean isOpened() {
		return opened;
	}

	public void setOpened(boolean opened) {
		this.opened = opened;
	}
}
