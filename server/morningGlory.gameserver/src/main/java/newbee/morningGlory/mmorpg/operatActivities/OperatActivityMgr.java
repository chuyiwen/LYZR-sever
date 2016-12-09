package newbee.morningGlory.mmorpg.operatActivities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import newbee.morningGlory.mmorpg.operatActivities.impl.FirstRechargeGift;

import org.apache.log4j.Logger;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.util.Pair;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public abstract class OperatActivityMgr {
	protected static final Logger logger = Logger.getLogger(OperatActivityMgr.class);
	public static final String LoadUrl = "newbee.morningGlory.http.HttpService.HttpWebUrl";

	/** 引用数据 */
	public static Map<OperatActivityGroup, Collection<OperatActivityRef>> refMap = new HashMap<OperatActivityGroup, Collection<OperatActivityRef>>();
	/** 模拟仿真数据 */
	public static Map<OperatActivityType, OperatActivity> simulateMap = new HashMap<OperatActivityType, OperatActivity>();

	private static OperatActivityMgr instance = null;

	public static OperatActivityMgr getInstance() {
		if (instance == null) {
			update();
		}
		return instance;
	}

	public static void update() {
		try {
			String className = "newbee.morningGlory.mmorpg.operatActivities.OperatActivityMgrImpl";
			// / Class<?> clazz = GameRoot.classFactory.reloadClass(className);
			Class<?> clazz = Class.forName(className);
			instance = (OperatActivityMgr) clazz.newInstance();
			instance.onInit();
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	@SuppressWarnings("unchecked")
	public static void update(OperatActivityType type) {
		Class<? extends OperatActivity> clazz = type.getClazz();
		if (clazz != null) {
			try {
				// clazz = (Class<? extends OperatActivity>)
				// GameRoot.classFactory.reloadClass(clazz.getName());
				clazz = (Class<? extends OperatActivity>) Class.forName(clazz.getName());
				type.setClazz(clazz);
				simulateMap.put(type, clazz.newInstance());
			} catch (Exception e) {
				logger.error("", e);
			}
		}
	}

	/**
	 * 修改数据
	 * 
	 * @param type
	 *            活动类型
	 * @param objs
	 *            外部传入的任意参数，请根据不同的运营活动做参数
	 */
	public void modifyOperatActivity(OperatActivityType type, Object... objs) {
		OperatActivity operatActivity = simulateMap.get(type);
		if (operatActivity != null && operatActivity.isOpened())
			operatActivity.modify(objs);
	}

	/**
	 * 是否可以领取奖励
	 * 
	 * @param type
	 *            活动类型
	 * @param objs
	 *            外部传入的任意参数，请根据不同的运营活动做参数
	 * @return
	 */
	public boolean canReceiveAward(OperatActivityType type, Object... objs) {
		OperatActivity operatActivity = simulateMap.get(type);
		if (operatActivity != null)
			return operatActivity.isOpened() && operatActivity.canReceiveAward(objs);
		return false;
	}

	/**
	 * 领取奖励
	 * 
	 * @param type
	 *            活动类型
	 * @param objs
	 *            外部传入的任意参数，请根据不同的运营活动做参数
	 */
	public void receiveAward(OperatActivityType type, Object... objs) {
		OperatActivity operatActivity = simulateMap.get(type);
		if (operatActivity != null) {
			if (operatActivity.isOpened() && operatActivity.canReceiveAward(objs))
				operatActivity.receiveAward(objs);
		}
		sendAllCanReceiveMsg(objs);
	}

	public void sendAllCanReceiveMsg(Object... objs) {
		if (objs != null && objs.length > 0) {
			Object o = objs[0];
			if (o instanceof Player) {
				Player playerCharacter = (Player) o;

				List<Integer> canReceiveSet = new ArrayList<Integer>();
				for (OperatActivityType tp : OperatActivityType.values()) {
					if (OperatActivityMgr.getInstance().getRef(tp) != null)
						if (canReceiveAward(tp, playerCharacter)) {
							canReceiveSet.add(tp.getValue());
						}
				}
				
				byte firstRecharge = MGPropertyAccesser.getIsFirstRecharge(playerCharacter.getProperty());
				if(firstRecharge == FirstRechargeGift.NotRecharge || firstRecharge == FirstRechargeGift.RechargeButNotReceive){
					if(!canReceiveSet.contains(OperatActivityType.FirstRechargeGift.getValue())){
						canReceiveSet.add(OperatActivityType.FirstRechargeGift.getValue());
					}
				}
				OperatActivityType[] canReceiveArr = new OperatActivityType[canReceiveSet.size()];
				for (int i = 0; i < canReceiveSet.size(); i++) {	
					canReceiveArr[i] = OperatActivityType.get(canReceiveSet.get(i));	
				}
				
				sendCanReceiveMsg(playerCharacter, canReceiveArr);
			}
		}
	}

	public Collection<OperatActivityRef> getRefsByGroup(OperatActivityGroup group) {
		return refMap.get(group);
	}

	public OperatActivityRef getRef(OperatActivityType type) {
		for (Collection<OperatActivityRef> collection : refMap.values()) {
			synchronized (collection) {
				for (OperatActivityRef ref : collection) {
					if (ref.getType() == type)
						return ref;
				}
			}
		}
		return null;
	}

	public Collection<OperatActivityRef> removeRefsByGroup(OperatActivityGroup group) {
		return refMap.remove(group);
	}

	public OperatActivityRef removeRef(OperatActivityType type) {
		for (Collection<OperatActivityRef> collection : refMap.values()) {
			synchronized (collection) {
				Iterator<OperatActivityRef> it = collection.iterator();
				while (it.hasNext()) {
					OperatActivityRef next = it.next();
					if (next.getType() == type) {
						it.remove();
						return next;
					}
				}
			}
		}
		return null;
	}

	public abstract void onEnterDay();

	public abstract void onMinute();

	public boolean isOpening(OperatActivityType type) {
		OperatActivity operatActivity = simulateMap.get(type);
		if (operatActivity != null)
			return operatActivity.isOpening();
		return false;
	}

	/**
	 * 管理器初始化事件
	 */
	protected void onInit() {
	}

	/**
	 * 引用数据初始化事件
	 * 
	 * @param ref
	 * @return
	 */
	public abstract boolean onRefInit(OperatActivityRef ref);

	/**
	 * 加载全部引用数据
	 * 
	 * @return
	 */
	public abstract boolean loadAll();

	/**
	 * 加载指定引用数据
	 * 
	 * @param type
	 * @return
	 */
	public abstract Pair<Boolean, Throwable> load(OperatActivityType type);

	/**
	 * 加载指定引用数据
	 * 
	 * @param type
	 * @param sendLoad
	 *            是否触发加载事件
	 * @return
	 */
	public abstract Pair<Boolean, Throwable> load(OperatActivityType type, boolean sendLoad);

	protected abstract void handleEvent(ActionEventBase actionEvent);

	public abstract void onPlayerPreLogin(Player playerCharacter);

	public abstract void onPlayerPostLogin(Player playerCharacter);

	public abstract void sendCanReceiveMsg(Player playerCharacter, OperatActivityType... types);

	public abstract void sendOpeningMsg(Player playerCharacter, OperatActivityType... types);

	public abstract void sendClosingMsg(Player playerCharacter, OperatActivityType... types);

	public abstract void sendOAStart(OperatActivityType type);

	public abstract void sendOAEnd(OperatActivityType type);

	public abstract void onOperatActivityStart(OperatActivityType type);

	public abstract void onOperatActivityEnd(OperatActivityType type);
}
