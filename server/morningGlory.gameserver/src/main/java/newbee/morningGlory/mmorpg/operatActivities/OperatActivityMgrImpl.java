package newbee.morningGlory.mmorpg.operatActivities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import newbee.morningGlory.code.MGErrorCode;
import newbee.morningGlory.mmorpg.operatActivities.awardCond.CondType;
import newbee.morningGlory.mmorpg.operatActivities.awardContent.AwardContent;
import newbee.morningGlory.mmorpg.operatActivities.awardContent.AwardItem;
import newbee.morningGlory.mmorpg.operatActivities.awardContent.AwardSendType;
import newbee.morningGlory.mmorpg.operatActivities.event.C2G_OA_TotalRechargeGiftReceiveEvent;
import newbee.morningGlory.mmorpg.operatActivities.event.G2C_OA_CanReceiveEvent;
import newbee.morningGlory.mmorpg.operatActivities.event.G2C_OA_ClosedActivityEvent;
import newbee.morningGlory.mmorpg.operatActivities.event.G2C_OA_FirstRechargeGiftList;
import newbee.morningGlory.mmorpg.operatActivities.event.G2C_OA_OpenedActivityEvent;
import newbee.morningGlory.mmorpg.operatActivities.event.G2C_OA_OpeningEvent;
import newbee.morningGlory.mmorpg.operatActivities.event.G2C_OA_TotalRechargeGiftListEvent;
import newbee.morningGlory.mmorpg.operatActivities.event.OperatActivityDefines;
import newbee.morningGlory.mmorpg.operatActivities.event.everyRecharge.G2C_OA_EveryRechargeGiftListEvent;
import newbee.morningGlory.mmorpg.operatActivities.event.sevenLogin.C2G_OA_SevenLogin_HadReceive;
import newbee.morningGlory.mmorpg.operatActivities.event.sevenLogin.C2G_OA_SevenLogin_HaveReceive;
import newbee.morningGlory.mmorpg.operatActivities.event.sevenLogin.C2G_OA_SevenLogin_ReReceive;
import newbee.morningGlory.mmorpg.operatActivities.event.sevenLogin.G2C_OA_SevenLogin_ReceiveState;
import newbee.morningGlory.mmorpg.operatActivities.event.weekConsume.C2G_OA_WeekTotalConsumeGiftReceiveEvent;
import newbee.morningGlory.mmorpg.operatActivities.event.weekConsume.G2C_OA_WeekTotalConsumeGiftListEvent;
import newbee.morningGlory.mmorpg.operatActivities.impl.FirstRechargeGift;
import newbee.morningGlory.mmorpg.operatActivities.utils.ActivityGift;

import org.apache.commons.lang3.StringUtils;

import sophia.foundation.authentication.Identity;
import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.foundation.util.Pair;
import sophia.game.GameRoot;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.player.persistence.immediatelySave.PlayerImmediateDaoFacade;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.DateTimeUtil;
import sophia.mmorpg.utils.SFRandomUtils;
import sophia.mmorpg.utils.Type;

public class OperatActivityMgrImpl extends OperatActivityMgr {
	private OperatActivityRefChecker refChecker = new OperatActivityRefChecker();

	@Override
	protected void onInit() {
		OperatActivityType[] values = OperatActivityType.values();
		for (OperatActivityType type : values) {
			OperatActivityMgr.update(type);
		}
	}

	@Override
	public boolean loadAll() {
		boolean success = true;
		// OperatActivityType[] values = OperatActivityType.values();
		//
		// for (OperatActivityType type : values) {
		// Pair<Boolean, Throwable> pair = load(type);
		// success = pair.getKey();
		// if (!pair.getKey()) {
		// if (pair.getValue() != null)
		// try {
		// throw pair.getValue();
		// } catch (Throwable e) {
		// logger.error("OperatActivityMgrImpl loadAll error!", e);
		// return false;
		// }
		// }
		// }
		// logger.info("活动数据加载校验完成!!!");
		return success;
	}

	@Override
	public Pair<Boolean, Throwable> load(OperatActivityType type) {
		// return load(type, true);
		return null;
	}

	public Pair<Boolean, Throwable> load(OperatActivityType type, boolean sendLoad) {
		// PropertiesWrapper properties = MorningGloryContext.getProperties();
		// LoadCallBack loadCallBack = new LoadCallBack();
		//
		// String url = properties.getProperty(LoadUrl, "") +
		// "?action=activity&fid=" //
		// +
		// properties.getProperty("newbee.morningGlory.http.HttpService.serverId")
		// //
		// + "&type=" + type.getValue() + "&time=" + System.currentTimeMillis();
		//
		// if (logger.isDebugEnabled()) {
		// logger.debug(url);
		// }
		//
		// HttpConnection httpConnection = HttpConnection.create(url,
		// loadCallBack);
		// httpConnection.exec(false);
		// synchronized (loadCallBack) {
		// while (!loadCallBack.isCallback()) {
		// try {
		// loadCallBack.wait(100);
		// } catch (InterruptedException e) {
		// logger.error("", e);
		// }
		// }
		// }
		// boolean success = loadCallBack.isSuccess();
		// if (success) {
		// OperatActivityRef ref = loadCallBack.getRef();
		// try {
		// if (ref.getData().getInt("status") != 0)
		// refChecker.check(ref);
		// } catch (Throwable e) {
		// logger.error("", e);
		// success = false;
		// logger.info("load " + url + " faile!!!");
		// return new Pair<Boolean, Throwable>(success, e);
		// }
		// if (success) {
		// OperatActivity operatActivity = simulateMap.get(type);
		// if (sendLoad)
		// operatActivity.onBeforeLoadRef();
		// Collection<OperatActivityRef> collection =
		// refMap.get(ref.getGroup());
		// if (collection == null) {
		// collection = new ArrayList<OperatActivityRef>();
		// refMap.put(ref.getGroup(), collection);
		// }
		//
		// synchronized (collection) {
		// Iterator<OperatActivityRef> iterator = collection.iterator();
		// while (iterator.hasNext()) {
		// OperatActivityRef next = iterator.next();
		// if (next.getType() == ref.getType())
		// iterator.remove();
		// }
		// if (ref.getData().getInt("status") != 0) {
		// collection.add(ref);
		// operatActivity.setOpened(true);
		// sendOAStart(type);
		// } else {
		// operatActivity.setOpened(false);
		// sendOAEnd(type);
		// }
		// }
		// if (sendLoad)
		// operatActivity.onAfterLoadRef();
		// }
		// }
		// logger.info("load " + type.getValue() + " " + success + " " + url);
		// return new Pair<Boolean, Throwable>(success,
		// loadCallBack.getThrowable());
		return null;
	}

	@Override
	public boolean onRefInit(OperatActivityRef ref) {

		int status = ref.getData().getInt("status");
		if (status == 0) {
			int type = ref.getData().getInt("type");
			OperatActivityRef ref2 = getRef(OperatActivityType.get(type));
			if (ref2 != null) {
				ref.getData().set("status", status);
			}
			return true;
		}

		String qdCode = ref.getData().getString("qdCode");
		if (qdCode.length() > 0) {
			String[] arr = qdCode.split("\\|");
			Set<Integer> qds = new HashSet<Integer>();
			for (String s : arr) {
				qds.add(Type.getInt(s, 0));
			}
		}

		long openTime = ref.getData().getLong("openTime");
		if (openTime > 0) {
			ref.setOpenTime(new Date(openTime));
		}
		long endTime = ref.getData().getLong("endTime");
		if (endTime > 0) {
			ref.setEndTime(new Date(endTime));
		}

		Map<?, ?> awardContext = (Map<?, ?>) ref.getData().getMap("awardContext");
		if (awardContext != null) {
			int sendType = Type.getInt(awardContext.get("sendType"), 0);
			String desc = Type.getString(awardContext.get("desc"), "");
			String otherData = Type.getString(awardContext.get("otherData"), "");

			List<?> itemArr = Type.getList(awardContext.get("itemArr"), null);

			AwardContent awardContent = new AwardContent();
			awardContent.setDesc(desc);
			awardContent.setOtherData(otherData);
			awardContent.setSendType(AwardSendType.get(sendType));

			if (itemArr != null) {
				List<AwardItem> awardItems = new ArrayList<AwardItem>();
				for (Object o : itemArr) {
					Map<?, ?> m = (Map<?, ?>) o;
					int condType = Type.getInt(m.get("condType"), 0);
					String condValue = Type.getString(m.get("condValue"), "");
					String itemDesc = Type.getString(m.get("itemDesc"), "");
					List<?> items = Type.getList(m.get("items"), null);
					String itemOtherData = Type.getString(m.get("itemOtherData"), "");

					AwardItem awardItem = new AwardItem();
					awardItem.setId(Type.getString(m.get("id"), ""));
					awardItem.setItemDesc(itemDesc);
					awardItem.setItemOtherData(itemOtherData);
					awardItem.setCondType(CondType.get(condType));
					awardItem.setCondValue(condValue);

					List<ItemPair> _items = new ArrayList<ItemPair>();

					if (items != null) {
						for (Object obj : items) {
							if (obj == null)
								continue;
							Map<?, ?> map = (Map<?, ?>) obj;
							String itemRefId = Type.getString(map.get("itemRefId"), "");
							boolean itemIsBind = Type.getBoolean(map.get("itemIsBind"), false);
							short itemCount = Type.getShort(map.get("itemCount"), (short) 0);

							ItemPair item = new ItemPair();
							item.setBindStatus(itemIsBind);
							item.setItemRefId(itemRefId);
							item.setNumber(itemCount);

							_items.add(item);
						}
						awardItem.setItems(_items);

						awardItems.add(awardItem);
					}
				}
				awardContent.setAwardItems(awardItems);
			}
			ref.setAwardContent(awardContent);
		}
		return true;
	}

	@Override
	public void onEnterDay() {
		Calendar c = Calendar.getInstance();
		boolean bom = c.get(Calendar.DAY_OF_MONTH) == 1;// 是否进入了新的一个月
		boolean bow = c.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY;// 是都进入了新的一周

		for (OperatActivity oa : simulateMap.values()) {
			try {
				oa.onEnterNewDay();
				if (bom)
					oa.onEnterNewMonth();
				if (bow)
					oa.onEnterNewWeek();
				oa.checkOpenStatus();
			} catch (Exception e) {
				logger.error("", e);
			}
		}

		OperatActivityType[] values = OperatActivityType.values();
		for (OperatActivityType type : values) {
			try {
				load(type, false);
			} catch (Throwable e) {
				logger.error("", e);
			}
		}

	}

	@Override
	public void onMinute() {
		for (OperatActivity oa : simulateMap.values()) {
			try {
				oa.onMinute0();
			} catch (Exception e) {
				logger.error("", e);
			}
		}
	}

	@Override
	public void onPlayerPostLogin(Player playerCharacter) {
		for (OperatActivity oa : simulateMap.values()) {
			if (oa.getRef() != null)
				oa.onPlayerPostLogin(playerCharacter);
		}

		// -----------------

		List<Integer> openingSet = new ArrayList<Integer>();
		for (OperatActivityType type : OperatActivityType.values()) {
			OperatActivity activity = simulateMap.get(type);
			if (activity != null && activity.isOpening() && activity.isOpening(playerCharacter)) {
				openingSet.add(type.getValue());
			}
		}
		OperatActivityType[] openingArr = new OperatActivityType[openingSet.size()];
		for (int i = 0; i < openingSet.size(); i++) {
			openingArr[i] = OperatActivityType.get(openingSet.get(i));
		}
		sendOpeningMsg(playerCharacter, openingArr);

		// ------------------

		List<Integer> canReceiveSet = new ArrayList<Integer>();
		for (OperatActivityType type : OperatActivityType.values()) {
			if (OperatActivityMgr.getInstance().getRef(type) != null)
				if (canReceiveAward(type, playerCharacter)) {
					canReceiveSet.add(type.getValue());
				}
		}
		OperatActivityType[] canReceiveArr = new OperatActivityType[canReceiveSet.size()];
		for (int i = 0; i < canReceiveSet.size(); i++) {
			canReceiveArr[i] = OperatActivityType.get(canReceiveSet.get(i));
		}
		sendCanReceiveMsg(playerCharacter, canReceiveArr);

	}

	@Override
	public void onPlayerPreLogin(Player playerCharacter) {
		for (OperatActivity oa : simulateMap.values()) {
			if (oa.getRef() != null) {
				oa.onPlayerPreLogin(playerCharacter);
			}
			if (!oa.isOpening()) {
				oa.clearPlayerCharacterData(playerCharacter);
			}
		}
	}

	@Override
	public void sendCanReceiveMsg(Player playerCharacter, OperatActivityType... types) {
		Set<Integer> set = new HashSet<Integer>();
		for (OperatActivityType oat : types) {
			if (oat != null)
				set.add(oat.getValue());
		}

		G2C_OA_CanReceiveEvent event = MessageFactory.getConcreteMessage(OperatActivityDefines.G2C_OA_CanReceiveEvent);
		event.setTypes(set);
		GameRoot.sendMessage(playerCharacter.getIdentity(), event);

	}

	public void sendOpeningMsg(Player playerCharacter, OperatActivityType... types) {
		Set<Integer> set = new HashSet<Integer>();
		for (OperatActivityType oat : types) {
			if (oat != null)
				set.add(oat.getValue());
		}

		G2C_OA_OpeningEvent event = new G2C_OA_OpeningEvent();
		event.setTypes(set);
		GameRoot.sendMessage(playerCharacter.getIdentity(), event);
	}

	public void sendClosingMsg(Player playerCharacter, OperatActivityType... types) {
		for (OperatActivityType type : types) {
			G2C_OA_ClosedActivityEvent event = new G2C_OA_ClosedActivityEvent();
			event.setType((short) type.getValue());
			GameRoot.sendMessage(playerCharacter.getIdentity(), event);
		}
	}

	@Override
	public void onOperatActivityStart(OperatActivityType type) {
		sendOAStart(type);
	}

	@Override
	public void onOperatActivityEnd(OperatActivityType type) {
		sendOAEnd(type);
	}

	public void sendOAStart(OperatActivityType type) {
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		Collection<Player> playerList = playerManager.getPlayerList();
		if (playerList.isEmpty()) {
			return;
		}
		
		OperatActivity operatActivity = simulateMap.get(type);
		G2C_OA_OpenedActivityEvent event = new G2C_OA_OpenedActivityEvent();
		event.setType((short) type.getValue());

		for (Player player : playerList) {
			if (player.isOnline() && operatActivity.isOpening(player)) {
				GameRoot.sendMessage(player.getIdentity(), event);
			}
		}
	}

	public void sendOAEnd(OperatActivityType type) {
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		Collection<Player> playerList = playerManager.getPlayerList();
		if (playerList.isEmpty()) {
			return;
		}
		
		G2C_OA_ClosedActivityEvent event = MessageFactory.getConcreteMessage(OperatActivityDefines.G2C_OA_ClosedActivityEvent);
		event.setType((short) type.getValue());

		for (Player player : playerList) {
			if (player.isOnline()) {
				GameRoot.sendMessage(player.getIdentity(), event);
			}
		}
	}

	// 处理活动请求事件
	// ----------------------------------------------------------------------------

	@Override
	protected void handleEvent(ActionEventBase actionEvent) {
		switch (actionEvent.getActionEventId()) {
		case OperatActivityDefines.C2G_OA_FirstRechargeGiftList:// 显示首充礼包界面
			handle_FirstRechargeGiftList(actionEvent);
			break;
		case OperatActivityDefines.C2G_OA_FirstRechargeGiftReceive:// 领取首冲礼包
			handle_FirstRechargeGiftReceive(actionEvent);
			break;
		case OperatActivityDefines.C2G_OA_TotalRechargeGiftListEvent: // 显示充值礼包界面
			handle_TotalRechargeGiftListEvent(actionEvent);
			break;
		case OperatActivityDefines.C2G_OA_TotalRechargeGiftReceiveEvent: // 领取充值礼包相应的奖励
			handle_TotalRechargeGiftReceiveEvent(actionEvent);
			break;
		case OperatActivityDefines.C2G_OA_EveryRechargeGiftListEvent: // 显示充值礼包界面
			handle_EveryRechargeGiftListEvent(actionEvent);
			break;
		case OperatActivityDefines.C2G_OA_EveryRechargeGiftReceiveEvent: // 领取充值礼包相应的奖励
			handle_EveryRechargeGiftReceiveEvent(actionEvent);
			break;
		case OperatActivityDefines.C2G_OA_WeekTotalConsumeGiftListEvent: // 显示周消费累计礼包界面
			handle_WeekTotalConsumeGiftListEvent(actionEvent);
			break;
		case OperatActivityDefines.C2G_OA_WeekTotalConsumeGiftReceiveEvent: // 领取周消费累计相应的奖励
			handle_WeekTotalConsumeGiftReceiveEvent(actionEvent);
			break;
		case OperatActivityDefines.C2G_OA_SevenLogin_ReceiveState: // 开服七日领取状态列表
			handle_SevenLogin_ReceiveState(actionEvent);
			break;
		case OperatActivityDefines.C2G_OA_SevenLogin_HadReceive: // 开服七日领取
			handle_SevenLogin_Receive(actionEvent);
			break;
		case OperatActivityDefines.C2G_OA_SevenLogin_ReReceive: // 开服七日领取
			handle_SevenLogin_ReReceive(actionEvent);
			break;
		case OperatActivityDefines.C2G_OA_SevenLogin_HaveReceive: // 开服七日是否可以领取
			handle_SevenLogin_HaveReceive(actionEvent);
			break;
		case OperatActivityDefines.C2G_OA_CanReceiveEvent: // 请求哪些活动可领奖
			handle_CanReceiveEvent(actionEvent);
			break;
		default:
			break;
		}
	}

	private void handle_CanReceiveEvent(ActionEventBase actionEvent) {
		Identity identity = actionEvent.getIdentity();
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		Player player = playerManager.getOnlinePlayer(identity.getCharId());
		if (player == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("handle_CanReceiveEvent error, player is null");
			}
			return;
		}

		OperatActivityMgr.getInstance().sendAllCanReceiveMsg(player);

	}

	// 补领
	private void handle_SevenLogin_ReReceive(ActionEventBase actionEvent) {

		C2G_OA_SevenLogin_ReReceive event = (C2G_OA_SevenLogin_ReReceive) actionEvent;
		Identity identity = actionEvent.getIdentity();
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		Player player = playerManager.getOnlinePlayer(identity.getCharId());
		if (player == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("handle_SevenLogin_ReReceive error, player is null");
			}
			
			return;
		}
		
		OperatActivityComponent operatActivityComponent = (OperatActivityComponent) player.getTagged(OperatActivityComponent.Tag);
		OperatActivityRecord record = operatActivityComponent.getOperatActivityRecord();
		long openServerTime = record.getOpenServerDate();
		int whichDay = whichDay(openServerTime);
		if (logger.isDebugEnabled()) {
			logger.debug("今天已开服第几天：" + whichDay);
		}
		if (whichDay > 7) {
			return;
		}
		String stage = event.getStage();
		OperatActivityRef ref = getRef(OperatActivityType.SevenDayLoginGift);
		if(ref.getAwardContent().getAwardItem(stage) == null){
			return;
		}
		List<AwardItem> awardItems = ref.getAwardContent().getAwardItems();
		
		for (AwardItem awardItem : awardItems) {
			if (StringUtils.equals(awardItem.getId(), stage)) {
				whichDay = Type.getInt(awardItem.getCondValue(), 0);
				break;
			}

		}
		String hadSevenLoginStage = Type.getString(record.getHadSevenLoginStage(), "");
		hadSevenLoginStage = hadSevenLoginStage + "|" + whichDay;
		record.setHadSevenLoginStage(hadSevenLoginStage);
		PlayerImmediateDaoFacade.update(player);
		// handle_SevenLogin_ReceiveState(actionEvent);
	}

	private void handle_SevenLogin_HaveReceive(ActionEventBase actionEvent) {
		C2G_OA_SevenLogin_HaveReceive event = (C2G_OA_SevenLogin_HaveReceive) actionEvent;
		Identity identity = actionEvent.getIdentity();
		short actionEventId = actionEvent.getActionEventId();
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		Player player = playerManager.getOnlinePlayer(identity.getCharId());
		if (player == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("handle_SevenLogin_HaveReceive error, player is null");
			}
			
			return;
		}

		OperatActivity activity = simulateMap.get(OperatActivityType.SevenDayLoginGift);
		if (activity == null || !activity.isOpening()) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_OA_INVALID); // 活动已失效
			return;
		}
		OperatActivityComponent operatActivityComponent = (OperatActivityComponent) player.getTagged(OperatActivityComponent.Tag);
		OperatActivityRecord record = operatActivityComponent.getOperatActivityRecord();
		long openServerTime = record.getOpenServerDate();
		int whichDay = whichDay(openServerTime);
		if (logger.isDebugEnabled()) {
			logger.debug("今天已开服第几天：" + whichDay);
		}
		byte ret = 0;
		String receivedSevenLoginStage = Type.getString(record.getHadReceiveSevenLoginStage(), "");
		List<String> receivelist = Arrays.asList(receivedSevenLoginStage.split("\\|"));

		String hadsevenLoginStage = Type.getString(record.getHadSevenLoginStage(), "");
		List<String> loginList = Arrays.asList(hadsevenLoginStage.split("\\|"));
		AwardContent awardContent = activity.getRef().getAwardContent();

		if (whichDay < 8) {
			for (int i = 1; i <= whichDay; i++) {
				if (loginList.contains(i + "")) {
					for (AwardItem awardItem : awardContent.getAwardItems()) {
						if (awardItem != null && Type.getInt(awardItem.getCondValue(), 0) == i) {
							if (!receivelist.contains(awardItem.getId())) {
								ret++;

							}
						}
					}
				}
			}
		} else {
			ret = 8;
		}

		// G2C_OA_SevenLogin_HaveReceive res =
		// MessageFactory.getConcreteMessage(OperatActivityDefines.G2C_OA_SevenLogin_HaveReceive);
		// res.setRet(ret);
		// GameRoot.sendMessage(player.getIdentity(), res);

	}

	private void handle_SevenLogin_Receive(ActionEventBase actionEvent) {
		if (logger.isDebugEnabled()) {
			logger.debug("领取七日登录奖励.......");
		}
		
		Identity identity = actionEvent.getIdentity();
		short actionEventId = actionEvent.getActionEventId();
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		Player player = playerManager.getOnlinePlayer(identity.getCharId());
		if (player == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("handle_SevenLogin_Receive error, player is null");
			}
			
			return;
		}
		
		C2G_OA_SevenLogin_HadReceive event = (C2G_OA_SevenLogin_HadReceive) actionEvent;
		String stage = event.getStage();
		OperatActivityRef ref = getRef(OperatActivityType.SevenDayLoginGift);
		if(ref.getAwardContent().getAwardItem(stage) == null){
			return;
		}
		OperatActivityComponent operatActivityComponent = (OperatActivityComponent) player.getTagged(OperatActivityComponent.Tag);
		OperatActivityRecord record = operatActivityComponent.getOperatActivityRecord();

		OperatActivity activity = simulateMap.get(OperatActivityType.SevenDayLoginGift);
		if (activity == null || !activity.isOpening()) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_OA_INVALID); // 活动已失效
			return;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("开服七日登录领取第：" + stage + "天的奖励");
		}
		OperatActivityMgr.getInstance().receiveAward(OperatActivityType.SevenDayLoginGift, player, stage, whichDay(record.getOpenServerDate()));

	}

	private void handle_SevenLogin_ReceiveState(ActionEventBase actionEvent) {
		if (logger.isDebugEnabled()) {
			logger.debug("处理七日登录请求列表.......");
		}

		Identity identity = actionEvent.getIdentity();
		short actionEventId = actionEvent.getActionEventId();
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		Player player = playerManager.getOnlinePlayer(identity.getCharId());
		if (player == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("handle_SevenLogin_ReceiveState error, player is null");
			}
			
			return;
		}

		OperatActivity activity = simulateMap.get(OperatActivityType.SevenDayLoginGift);
		if (activity == null || !activity.isOpening()) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_OA_INVALID); // 活动已失效
			return;
		}

		OperatActivityRef ref = getRef(OperatActivityType.SevenDayLoginGift);

		OperatActivityComponent operatActivityComponent = (OperatActivityComponent) player.getTagged(OperatActivityComponent.Tag);
		OperatActivityRecord record = operatActivityComponent.getOperatActivityRecord();
		String receiveSevenLoginStage = record.getHadReceiveSevenLoginStage();
		List<String> receivelist = Arrays.asList(receiveSevenLoginStage.split("\\|")); // 已领取的阶段
		String hadsevenLoginStage = Type.getString(record.getHadSevenLoginStage(), "");
		List<String> loginList = Arrays.asList(hadsevenLoginStage.split("\\|"));

		List<AwardItem> awardItems = ref.getAwardContent().getAwardItems();
		List<ActivityGift> activityGifts = new ArrayList<ActivityGift>();
		long openTime = record.getOpenServerDate();
		long overTime = openTime + 3600 * 1000 * 24 * 6l;
		String duration = DateTimeUtil.getDateString(openTime) + "-" + DateTimeUtil.getDateString(overTime);
		int whichDay = whichDay(openTime);
		whichDay = whichDay > 7 ? 8 : whichDay;
		int ret = whichDay;
		for (int i = 1; i <= whichDay; i++) {
			if (loginList.contains(i + "")) {
				for (AwardItem awardItem : ref.getAwardContent().getAwardItems()) {
					if (awardItem != null && Type.getInt(awardItem.getCondValue(), 0) == i) {
						if (!receivelist.contains(awardItem.getId())) {
							ret = (byte) i;
							break;
						}
					}
				}
			}
		}

		String icon = "ride_9";
		for (AwardItem awardItem : awardItems) {
			ActivityGift gift = new ActivityGift();
			gift.setId(awardItem.getId());
			gift.setCondValue(Type.getInt(awardItem.getCondValue(), 0));
			gift.setItems(awardItem.getItems(player));
			gift.setPic(icon);
			boolean received = receivelist.contains(awardItem.getId());
			byte status = received ? ActivityGift.Status_Has_Received
					: canReceiveAward(OperatActivityType.SevenDayLoginGift, player, awardItem.getId(), whichDay) ? ActivityGift.Status_Can_Receive
							: ActivityGift.Status_Cannot_Receive;
			if (gift.getCondValue() < whichDay && ActivityGift.Status_Cannot_Receive == status) {
				status = ActivityGift.Status_Have_Expired;
			}
			gift.setStatus(status);
			activityGifts.add(gift);
		}

		G2C_OA_SevenLogin_ReceiveState res = MessageFactory.getConcreteMessage(OperatActivityDefines.G2C_OA_SevenLogin_ReceiveState);
		res.setDuration(duration);
		res.setActivityGifts(activityGifts);
		res.setWhichDay(ret);
		GameRoot.sendMessage(identity, res);

	}

	private void handle_WeekTotalConsumeGiftReceiveEvent(ActionEventBase actionEvent) {
		if (logger.isDebugEnabled()) {
			logger.debug("领取周消费累计奖励.......");
		}
		C2G_OA_WeekTotalConsumeGiftReceiveEvent event = (C2G_OA_WeekTotalConsumeGiftReceiveEvent) actionEvent;
		Identity identity = actionEvent.getIdentity();
		short actionEventId = actionEvent.getActionEventId();
		String stage = event.getStage() + "";
		OperatActivityRef ref = getRef(OperatActivityType.WeekTotalConsumeGift);
		if(ref.getAwardContent().getAwardItem(stage) == null){
			return;
		}
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		Player player = playerManager.getOnlinePlayer(identity.getCharId());
		if (player == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("handle_WeekTotalConsumeGiftReceiveEvent error, player is null");
			}
			
			return;
		}

		OperatActivity activity = simulateMap.get(OperatActivityType.WeekTotalConsumeGift);
		if (activity == null || !activity.isOpening()) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_OA_INVALID); // 活动已失效
			return;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("领取周消费礼包相应的奖励：" + stage);
		}
		OperatActivityMgr.getInstance().receiveAward(OperatActivityType.WeekTotalConsumeGift, player, stage, actionEventId);

	}

	private void handle_WeekTotalConsumeGiftListEvent(ActionEventBase actionEvent) {
		if (logger.isDebugEnabled()) {
			logger.debug("领取每周消费奖励列表.......");
		}

		Identity identity = actionEvent.getIdentity();
		short actionEventId = actionEvent.getActionEventId();
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		Player player = playerManager.getOnlinePlayer(identity.getCharId());
		if (player == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("handle_WeekTotalConsumeGiftListEvent error, player is null");
			}
			
			return;
		}

		OperatActivity activity = simulateMap.get(OperatActivityType.WeekTotalConsumeGift);
		if (activity == null || !activity.isOpening()) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_OA_INVALID); // 活动已失效
			return;
		}
		OperatActivityComponent operatActivityComponent = (OperatActivityComponent) player.getTagged(OperatActivityComponent.Tag);
		OperatActivityRecord record = operatActivityComponent.getOperatActivityRecord();
		OperatActivityRef ref = getRef(OperatActivityType.WeekTotalConsumeGift);
		long now = System.currentTimeMillis();
		long beginTime = ref.getOpenTime().getTime();
		long endTime = ref.getEndTime().getTime();
		long leaveTime = endTime - now;
		long crtValue = MGPropertyAccesser.getCrtWeekConsume(player.getProperty());
		if (crtValue < 0) {
			crtValue = 0;
			MGPropertyAccesser.setOrPutCrtWeekConsume(player.getProperty(), 0);
		}
		String receiveWeekConsumeGifts = Type.getString(record.getHadReceiveWeekConsumeGiftStage(), "");
		if (logger.isDebugEnabled()) {
			logger.debug("已领取的阶段:" + receiveWeekConsumeGifts);
		}
		List<String> receivedGiftStage = Arrays.asList(receiveWeekConsumeGifts.split("\\|")); // 已领取的阶段

		List<ActivityGift> activityGifts = new ArrayList<ActivityGift>();
		List<AwardItem> awardItems = ref.getAwardContent().getAwardItems();
		int worth = 18888; 
		for (AwardItem awardItem : awardItems) {
			ActivityGift gift = new ActivityGift();
			gift.setId(awardItem.getId());
			gift.setItems(awardItem.getItems());
			gift.setCondValue(Type.getInt(awardItem.getCondValue(), 0));
			
			gift.setWorth(worth);
			boolean received = receivedGiftStage.contains(awardItem.getId());
			byte status = received ? ActivityGift.Status_Has_Received
					: canReceiveAward(OperatActivityType.WeekTotalConsumeGift, player, awardItem.getId()) ? ActivityGift.Status_Can_Receive : ActivityGift.Status_Cannot_Receive;
			gift.setStatus(status);

			activityGifts.add(gift);
			worth = 28888; 
		}
		String weekStartEndTime = "";
		SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		c.setFirstDayOfWeek(Calendar.MONDAY);
		c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		weekStartEndTime += format.format(c.getTime()) + "-";
		c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		weekStartEndTime += format.format(c.getTime());

		G2C_OA_WeekTotalConsumeGiftListEvent res = MessageFactory.getConcreteMessage(OperatActivityDefines.G2C_OA_WeekTotalConsumeGiftListEvent);
		res.setBeginTime(beginTime);
		res.setEndTime(endTime);
		res.setLaveTime(leaveTime);
		res.setWeekStartEndTime(weekStartEndTime);
		res.setCrtTotalRechargeValue((int) crtValue);
		res.setActivityGifts(activityGifts);
		GameRoot.sendMessage(identity, res);
	}

	private void handle_EveryRechargeGiftReceiveEvent(ActionEventBase actionEvent) {
		if (logger.isDebugEnabled()) {
			logger.debug("领取每日充值奖励.......");
		}
		Identity identity = actionEvent.getIdentity();
		short actionEventId = actionEvent.getActionEventId();
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		Player player = playerManager.getOnlinePlayer(identity.getCharId());
		if (player == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("handle_EveryRechargeGiftReceiveEvent error, player is null");
			}
			return;
		}

		OperatActivity activity = simulateMap.get(OperatActivityType.EveryDayRechargeGift);
		if (activity == null || !activity.isOpening()) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_OA_INVALID); // 活动已失效
			return;
		}

		OperatActivityMgr.getInstance().receiveAward(OperatActivityType.EveryDayRechargeGift, player, actionEventId);

	}

	private void handle_EveryRechargeGiftListEvent(ActionEventBase actionEvent) {
		if (logger.isDebugEnabled()) {
			logger.debug("处理每日礼包请求列表.......");
		}

		Identity identity = actionEvent.getIdentity();
		short actionEventId = actionEvent.getActionEventId();
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		Player player = playerManager.getOnlinePlayer(identity.getCharId());
		if (player == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("handle_EveryRechargeGiftListEvent error, player is null");
			}
			return;
		}

		OperatActivity activity = simulateMap.get(OperatActivityType.EveryDayRechargeGift);
		if (activity == null || !activity.isOpening()) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_OA_INVALID); // 活动已失效
			return;
		}

		OperatActivityComponent operatActivityComponent = (OperatActivityComponent) player.getTagged(OperatActivityComponent.Tag);
		OperatActivityRecord record = operatActivityComponent.getOperatActivityRecord();
		OperatActivityRef ref = getRef(OperatActivityType.EveryDayRechargeGift);
		List<ItemPair> list = new ArrayList<ItemPair>();
		if (StringUtils.isEmpty(record.getEveryDayrewardId())) {
			int random = SFRandomUtils.random(1, 5);
			int count = 1;
			List<AwardItem> awardItems = ref.getAwardContent().getAwardItems();

			for (AwardItem item : awardItems) {
				if (count == random) {
					list.addAll(item.getItems());
					record.setEveryDayrewardId(item.getId() + "|" + 1);
					break;
				}
				count++;
			}
		} else {
			String id = record.getEveryDayrewardId().split("\\|")[0];
			AwardItem awardItem = ref.getAwardContent().getAwardItem(id);
			list.addAll(awardItem.getItems());
		}
		byte dayRecharge = record.getDayRecharge();
		int worth = 1888;
		G2C_OA_EveryRechargeGiftListEvent res = MessageFactory.getConcreteMessage(OperatActivityDefines.G2C_OA_EveryRechargeGiftListEvent);
		res.setWorth(worth);
		res.setStatus(dayRecharge);
		res.setItems(list);
		GameRoot.sendMessage(identity, res);

	}

	/**
	 * 领取充值礼包相应的奖励
	 * 
	 * @param actionEvent
	 */
	private void handle_TotalRechargeGiftReceiveEvent(ActionEventBase actionEvent) {

		C2G_OA_TotalRechargeGiftReceiveEvent event = (C2G_OA_TotalRechargeGiftReceiveEvent) actionEvent;
		Identity identity = actionEvent.getIdentity();
		short actionEventId = actionEvent.getActionEventId();
		String stage = event.getStage() + "";
		OperatActivityRef ref = getRef(OperatActivityType.TotalRechargeGift);
		if(ref.getAwardContent().getAwardItem(stage) == null){
			return;
		}
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		Player player = playerManager.getOnlinePlayer(identity.getCharId());
		if (player == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("handle_TotalRechargeGiftReceiveEvent error, player is null");
			}
			
			return;
		}

		OperatActivity activity = simulateMap.get(OperatActivityType.TotalRechargeGift);
		if (activity == null || !activity.isOpening()) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_OA_INVALID); // 活动已失效
			return;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("领取充值礼包相应的奖励：" + stage);
		}
		OperatActivityMgr.getInstance().receiveAward(OperatActivityType.TotalRechargeGift, player, stage, actionEventId);

	}

	/**
	 * 处理充值礼包列表请求
	 * 
	 * @param actionEvent
	 */
	private void handle_TotalRechargeGiftListEvent(ActionEventBase actionEvent) {
		if (logger.isDebugEnabled()) {
			logger.debug("处理充值礼包列表请求");
		}
		Identity identity = actionEvent.getIdentity();
		short actionEventId = actionEvent.getActionEventId();
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		Player player = playerManager.getOnlinePlayer(identity.getCharId());
		if (player == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("handle_TotalRechargeGiftListEvent error, player is null");
			}
			
			return;
		}

		OperatActivity activity = simulateMap.get(OperatActivityType.TotalRechargeGift);
		if (activity == null || !activity.isOpening()) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_OA_INVALID); // 活动已失效
			return;
		}
		OperatActivityComponent operatActivityComponent = (OperatActivityComponent) player.getTagged(OperatActivityComponent.Tag);
		OperatActivityRecord record = operatActivityComponent.getOperatActivityRecord();
		OperatActivityRef ref = getRef(OperatActivityType.TotalRechargeGift);
		long now = System.currentTimeMillis();
		long beginTime = ref.getOpenTime().getTime();
		long endTime = ref.getEndTime().getTime();
		long leaveTime = endTime - now;
		int crtValue = MGPropertyAccesser.getTotalRecharge(player.getProperty());
		if (crtValue < 0) {
			crtValue = 0;
			MGPropertyAccesser.setOrPutTotalRecharge(player.getProperty(), 0);
		}
		String receiveTotalRechargeGifts = Type.getString(record.getHadReceiveRechargeGiftStage(), "");
		if (logger.isDebugEnabled()) {
			logger.debug("已领取的阶段:" + receiveTotalRechargeGifts);
		}
		List<String> receivedGiftStage = Arrays.asList(receiveTotalRechargeGifts.split("\\|")); // 已领取的阶段

		List<ActivityGift> activityGifts = new ArrayList<ActivityGift>();
		List<AwardItem> awardItems = ref.getAwardContent().getAwardItems();
		for (AwardItem awardItem : awardItems) {
			ActivityGift gift = new ActivityGift();
			gift.setId(awardItem.getId());
			gift.setItems(awardItem.getItems());
			gift.setCondValue(Type.getInt(awardItem.getCondValue(), 0));
			boolean received = receivedGiftStage.contains(awardItem.getId());
			byte status = received ? ActivityGift.Status_Has_Received
					: canReceiveAward(OperatActivityType.TotalRechargeGift, player, awardItem.getId()) ? ActivityGift.Status_Can_Receive : ActivityGift.Status_Cannot_Receive;
			gift.setStatus(status);

			activityGifts.add(gift);
		}

		G2C_OA_TotalRechargeGiftListEvent res = MessageFactory.getConcreteMessage(OperatActivityDefines.G2C_OA_TotalRechargeGiftListEvent);
		res.setBeginTime(beginTime);
		res.setEndTime(endTime);
		res.setLaveTime(leaveTime);
		res.setCrtTotalRechargeValue(crtValue);
		res.setActivityGifts(activityGifts);
		GameRoot.sendMessage(identity, res);

	}

	/**
	 * 领取首冲
	 * 
	 * @param actionEvent
	 */
	private void handle_FirstRechargeGiftReceive(ActionEventBase actionEvent) {
		if (logger.isDebugEnabled()) {
			logger.debug("领取首冲奖励.......");
		}
		Identity identity = actionEvent.getIdentity();
		short actionEventId = actionEvent.getActionEventId();
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		Player player = playerManager.getOnlinePlayer(identity.getCharId());
		if (player == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("handle_FirstRechargeGiftReceive error, player is null");
			}
			return;
		}

		OperatActivity activity = simulateMap.get(OperatActivityType.FirstRechargeGift);
		if (activity == null || !activity.isOpening()) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_OA_INVALID); // 活动已失效
			return;
		}

		OperatActivityMgr.getInstance().receiveAward(OperatActivityType.FirstRechargeGift, player, actionEventId);
	}

	/**
	 * 处理首冲礼包请求列表
	 * 
	 * @param actionEvent
	 */
	private void handle_FirstRechargeGiftList(ActionEventBase actionEvent) {
		if (logger.isDebugEnabled()) {
			logger.debug("处理首冲礼包请求列表.......");
		}

		Identity identity = actionEvent.getIdentity();
		short actionEventId = actionEvent.getActionEventId();
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		Player player = playerManager.getOnlinePlayer(identity.getCharId());
		if (player == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("handle_FirstRechargeGiftList error, player is null");
			}
			return;
		}

		OperatActivity activity = simulateMap.get(OperatActivityType.FirstRechargeGift);
		if (activity == null || !activity.isOpening()) {
			ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_OA_INVALID); // 活动已失效
			return;
		}

		OperatActivityRef ref = getRef(OperatActivityType.FirstRechargeGift);

		List<AwardItem> awardItems = ref.getAwardContent().getAwardItems();
		List<ItemPair> list = new ArrayList<ItemPair>();
		String itemOtherData = null;
		for (AwardItem item : awardItems) {
			list.addAll(item.getItems());
			itemOtherData = item.getItemOtherData();
		}
		List<String> weapons = Arrays.asList(itemOtherData.split("\\|"));
		String weaponRefId = null;
		if (weapons.size() > 0) {
			if (weapons.size() == 1) {
				weaponRefId = weapons.get(0);
			} else if (weapons.size() == 3) {
				weaponRefId = weapons.get(player.getProfession() - 1);
			}
			ItemPair weaponPair = new ItemPair(weaponRefId, 1, false);
			list.add(weaponPair);
		}

		byte firstRecharge = MGPropertyAccesser.getIsFirstRecharge(player.getProperty());
		firstRecharge = firstRecharge < 0 ? FirstRechargeGift.NotRecharge : firstRecharge;
		int worth = 18888;
		G2C_OA_FirstRechargeGiftList res = MessageFactory.getConcreteMessage(OperatActivityDefines.G2C_OA_FirstRechargeGiftList);
		res.setWorth(worth);
		res.setStatus(firstRecharge);
		res.setItems(list);
		GameRoot.sendMessage(identity, res);

	}

	/**
	 * 距离开服过了多少天
	 * 
	 * @return
	 */
	public int whichDay(long openServerTime) {		
		int whichDay = DateTimeUtil.getIntervalDays(System.currentTimeMillis(),openServerTime) + 1;
		return whichDay;
	}

	

	

}
