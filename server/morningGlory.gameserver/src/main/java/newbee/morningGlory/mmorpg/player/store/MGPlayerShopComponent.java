package newbee.morningGlory.mmorpg.player.store;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import newbee.morningGlory.code.MGErrorCode;
import newbee.morningGlory.mmorpg.player.activity.discount.DiscountTimeMgr;
import newbee.morningGlory.mmorpg.player.activity.discount.RecordManager;
import newbee.morningGlory.mmorpg.player.store.event.C2G_Discount_GetShopList;
import newbee.morningGlory.mmorpg.player.store.event.C2G_Store_BuyItemReq;
import newbee.morningGlory.mmorpg.player.store.event.C2G_Store_ItemListReq;
import newbee.morningGlory.mmorpg.player.store.event.C2G_Store_LimitItemReq;
import newbee.morningGlory.mmorpg.player.store.event.C2G_Store_VersonReq;
import newbee.morningGlory.mmorpg.player.store.event.G2C_Discount_BeginOrEndNotify;
import newbee.morningGlory.mmorpg.player.store.event.G2C_Discount_GetShopList;
import newbee.morningGlory.mmorpg.player.store.event.G2C_Store_BuyItemResp;
import newbee.morningGlory.mmorpg.player.store.event.G2C_Store_ItemListResp;
import newbee.morningGlory.mmorpg.player.store.event.G2C_Store_LimitItemResp;
import newbee.morningGlory.mmorpg.player.store.event.G2C_Store_VersonResp;
import newbee.morningGlory.mmorpg.player.store.event.StoreEventDefines;
import newbee.morningGlory.mmorpg.store.StoreMgr;
import newbee.morningGlory.mmorpg.store.ref.MallItemRef;
import newbee.morningGlory.mmorpg.store.ref.ShopItemRef;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.authentication.Identity;
import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.foundation.property.PropertyDictionary;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.base.scene.GameSceneHelper;
import sophia.mmorpg.base.scene.mgr.NpcMgrComponent;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.npc.Npc;
import sophia.mmorpg.npc.ref.JobType;
import sophia.mmorpg.npc.ref.NpcShop;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.gameEvent.EnterWorld_SceneReady_GE;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.RuntimeResult;

public class MGPlayerShopComponent extends ConcreteComponent<Player> {
	private static Logger logger = Logger.getLogger(MGPlayerShopComponent.class);
	public static final String Tag = "MGPlayerShopComponent";
	private static final String EnterWorld_SceneReady_GE_ID = EnterWorld_SceneReady_GE.class.getSimpleName();
	private StoreMgr storeMgr = new StoreMgr();
	public static final byte Discount_End = 0;
	public static final byte Discount_Begin = 1;

	// byte 0为失败 1为成功 2为打折商品卖完了或者数目不足
	public static final byte Fail = 0;
	public static final byte Succeed = 1;
	public static final byte NotEnought = 2;

	private Player player;
	
	public StoreMgr getStoreMgr() {
		return storeMgr;
	}

	@Override
	public void ready() {
		player = getConcreteParent();
		addActionEventListener(StoreEventDefines.C2G_Store_VersonReq);
		addActionEventListener(StoreEventDefines.C2G_Store_ItemListReq);
		addActionEventListener(StoreEventDefines.C2G_Store_LimitItemReq);
		addActionEventListener(StoreEventDefines.C2G_Store_BuyItemReq);
		addActionEventListener(StoreEventDefines.C2G_Discount_GetShopList);
		addInterGameEventListener(EnterWorld_SceneReady_GE_ID);

		super.ready();
	}

	@Override
	public void suspend() {
		removeActionEventListener(StoreEventDefines.C2G_Store_VersonReq);
		removeActionEventListener(StoreEventDefines.C2G_Store_ItemListReq);
		removeActionEventListener(StoreEventDefines.C2G_Store_LimitItemReq);
		removeActionEventListener(StoreEventDefines.C2G_Store_BuyItemReq);
		removeActionEventListener(StoreEventDefines.C2G_Discount_GetShopList);
		removeInterGameEventListener(EnterWorld_SceneReady_GE_ID);
		super.suspend();
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {
		if (event.isId(EnterWorld_SceneReady_GE_ID)) {
			if (DiscountTimeMgr.getRemainTime() > 0) {
				sendDiscountBeginOrEndMsg(Discount_Begin);
			}
		}
		super.handleGameEvent(event);
	}

	public void sendDiscountBeginOrEndMsg(byte type) {
		G2C_Discount_BeginOrEndNotify res = (G2C_Discount_BeginOrEndNotify) MessageFactory.getMessage(StoreEventDefines.G2C_Discount_BeginOrEndNotify);
		res.setType(type);
		logger.debug("discount button type =" + type);
		GameRoot.sendMessage(player.getIdentity(), res);

		G2C_Discount_GetShopList listResp = MessageFactory.getConcreteMessage(StoreEventDefines.G2C_Discount_GetShopList);
		listResp.setPlayerId(player.getId());
		GameRoot.sendMessage(player.getIdentity(), listResp);
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
		short actionEventId = event.getActionEventId();
		Identity identity = event.getIdentity();
		switch (actionEventId) {
		case StoreEventDefines.C2G_Store_VersonReq:
			handle_Store_VersonReq((C2G_Store_VersonReq) event, actionEventId, identity);
			break;
		case StoreEventDefines.C2G_Store_ItemListReq:
			handle_Store_ItemListReq((C2G_Store_ItemListReq) event, actionEventId, identity);
			break;
		case StoreEventDefines.C2G_Store_LimitItemReq:
			handle_Store_LimitItemReq((C2G_Store_LimitItemReq) event, actionEventId, identity);
			break;
		case StoreEventDefines.C2G_Store_BuyItemReq:
			handle_Shop_BuyItemReq((C2G_Store_BuyItemReq) event, actionEventId, identity);
			break;
		case StoreEventDefines.C2G_Discount_GetShopList:
			handle_Discount_GetShopList((C2G_Discount_GetShopList) event, actionEventId, identity);
			break;
		}
		
		super.handleActionEvent(event);
	}

	private void handle_Discount_GetShopList(C2G_Discount_GetShopList event, short actionEventId, Identity identity) {
		G2C_Discount_GetShopList listResp = MessageFactory.getConcreteMessage(StoreEventDefines.G2C_Discount_GetShopList);
		listResp.setPlayerId(player.getId());
		GameRoot.sendMessage(identity, listResp);
	}

	private void handle_Store_VersonReq(C2G_Store_VersonReq event, short actionEventId, Identity identity) {
		G2C_Store_VersonResp versonResp = MessageFactory.getConcreteMessage(StoreEventDefines.G2C_Store_VersonResp);
		if (storeMgr.getMallItemList().size() == 0) {
			storeMgr.initMallItemList();
		}
		int version = 0;
		if (storeMgr.compareMallItemRef()) {
			version = storeMgr.getListVersion();
		} else {
			version = storeMgr.getNewListVersion();
		}
		versonResp.setVerson(version);
		GameRoot.sendMessage(identity, versonResp);
	}

	private void handle_Store_ItemListReq(C2G_Store_ItemListReq event, short actionEventId, Identity identity) {
		Map<String, MallItemRef> mallItemList = storeMgr.getMallItemList();
		G2C_Store_ItemListResp listResp = MessageFactory.getConcreteMessage(StoreEventDefines.G2C_Store_ItemListResp);
		listResp.setMallItemList(mallItemList);
		GameRoot.sendMessage(identity, listResp);
	}

	private void handle_Store_LimitItemReq(C2G_Store_LimitItemReq event, short actionEventId, Identity identity) {
		G2C_Store_LimitItemResp limitItemResp = MessageFactory.getConcreteMessage(StoreEventDefines.G2C_Store_LimitItemResp);
		Map<String, Short> allLimitItem = storeMgr.getAllLimitList();
		Map<String, Short> personalLimitItem = storeMgr.getPersonalLimitList(player);
		limitItemResp.setAllLimit(allLimitItem);
		limitItemResp.setPersonLimit(personalLimitItem);

		String storeType = event.getStoreType();
		limitItemResp.setStoreType(storeType);

		if (storeType.equals("mall")) {
			Map<String, MallItemRef> mallItemList = storeMgr.getMallItemList();
			limitItemResp.setMallItemList(mallItemList);
		} else {
			Map<String, ShopItemRef> shopItemList = getShopItemListFrom(storeType);
			limitItemResp.setShopItemList(shopItemList);
		}
		GameRoot.sendMessage(identity, limitItemResp);
	}

	private Map<String, ShopItemRef> getShopItemListFrom(String shopId) {
		if (storeMgr.getShopItemList().size() == 0) {
			storeMgr.initShopItemList();
		}
		Map<String, ShopItemRef> shopItemList = storeMgr.getShopItemList();
		Map<String, ShopItemRef> list = new HashMap<>();
		Set<String> keys = shopItemList.keySet();
		for (String key : keys) {
			PropertyDictionary shopPD = shopItemList.get(key).getProperty();
			if (shopId.equals(MGPropertyAccesser.getStoreType(shopPD))) {
				list.put(key, shopItemList.get(key));
			}
		}
		return list;
	}

	private void handle_Shop_BuyItemReq(C2G_Store_BuyItemReq event, short actionEventId, Identity identity) {
		String storeId = event.getStoreId();
		int count = event.getCount();
		//Preconditions.checkArgument(count > 0, "buyItem error, count=" + count + ", player=" + player);

		G2C_Store_BuyItemResp buyItem = MessageFactory.getConcreteMessage(StoreEventDefines.G2C_Store_BuyItemResp);
		
		if (count <= 0) {
			ResultEvent.sendResult(player.getIdentity(), actionEventId, MGErrorCode.CODE_STORE_INVALID_PARAM);
			return;
		}
		
		byte storeType = StoreMgr.getStoreType(storeId);
		String storeItemId = event.getItemRefId();
		boolean checkLimitTime = storeMgr.checkLimitTime(storeType, player, storeItemId, actionEventId);
		
		if (storeType == StoreMgr.Discount_type) {
			RecordManager.getInstance().discountBuyItem(storeItemId, (short)count, player);
			return;
		}
		
		if (checkLimitTime) {
			if (storeType == StoreMgr.Mall_type) {
				mallBuyItem(storeType, storeItemId, count, actionEventId, buyItem);
				
			} /*else if (storeType == StoreMgr.Discount_type) {
				DiscountMgr.getinstance().discountBuyItem(storeItemId, count, player);
				
			} */else if (storeType == StoreMgr.Shop_type) {
				shopBuyItem(storeType, event.getShopId(), event.getNpcRefId(), storeItemId, count, actionEventId, buyItem);
			}
		} else {
			buyItem.setTemp(Fail);
		}
		
		GameRoot.sendMessage(identity, buyItem);
	}

	private void mallBuyItem(byte storeType, String itemRefId, int count, short actionEventId, G2C_Store_BuyItemResp buyItem) {
		if (count <= 0) {
			ResultEvent.sendResult(player.getIdentity(), actionEventId, MGErrorCode.CODE_STORE_INVALID_PARAM);
			return;
		}
		RuntimeResult runtimeResult = storeMgr.buyItemAndPayMoney(storeType, itemRefId, count, player, actionEventId);
		if (runtimeResult.isOK()) {
			buyItem.setTemp(Succeed);
		} else {
			buyItem.setTemp(Fail);
			ResultEvent.sendResult(player.getIdentity(), actionEventId, runtimeResult.getCode());
		}
	}

	private void shopBuyItem(byte storeType, String shopId, String npcRefId, String itemRefId, int count, short actionEventId, G2C_Store_BuyItemResp buyItem) {
		if (!StringUtils.equals(shopId, StoreMgr.EQUIP_STR)) {
			NpcMgrComponent npcMgrComponent = player.getCrtScene().getNpcMgrComponent();
			Npc npc = npcMgrComponent.getNpc(npcRefId);
			if (npc == null) {
				logger.error("client has send a non-existent NpcRefId.");
				return;
			}
			NpcShop job = (NpcShop) npc.getNpcJobManager().getNpcJob(JobType.Job_Type_Store);
			List<String> shopList = job.getShopList();
			if (shopList.contains(shopId)) {
				int distance = GameSceneHelper.distance(player.getCrtScene(), player.getCrtPosition(), npc.getCrtPosition());
				if (distance <= 5) {
					RuntimeResult runtimeResult = storeMgr.buyItemAndPayMoney(storeType, itemRefId, count, player, actionEventId);
					if (runtimeResult.isOK()) {
						buyItem.setTemp(Succeed);
					} else {
						buyItem.setTemp(Fail);
						ResultEvent.sendResult(player.getIdentity(), actionEventId, runtimeResult.getCode());
					}
				} else {
					ResultEvent.sendResult(player.getIdentity(), actionEventId, MGErrorCode.CODE_STORE_TOO_FAR_AWAY);
				}
			} else {
				ResultEvent.sendResult(player.getIdentity(), actionEventId, MGErrorCode.CODE_STORE_ERROR_STORE);
			}
		} else {
			RuntimeResult runtimeResult = storeMgr.buyItemAndPayMoney(storeType, itemRefId, count, player, actionEventId);
			if (runtimeResult.isOK()) {
				buyItem.setTemp(Succeed);
			} else {
				buyItem.setTemp(Fail);
				ResultEvent.sendResult(player.getIdentity(), actionEventId, runtimeResult.getCode());
			}
		}
	}
}
