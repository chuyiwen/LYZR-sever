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
package newbee.morningGlory.mmorpg.player.activity.giftCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import newbee.morningGlory.code.MGErrorCode;
import newbee.morningGlory.mmorpg.player.activity.giftCode.persistence.GiftCodeDAO;
import newbee.morningGlory.mmorpg.player.store.event.C2G_ExchangeCode;
import newbee.morningGlory.mmorpg.player.store.event.G2C_ExchangeCode;
import newbee.morningGlory.mmorpg.player.store.event.StoreEventDefines;
import newbee.morningGlory.stat.MGStatFunctions;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.item.ref.ItemRef;
import sophia.mmorpg.item.ref.UnPropsItemRef;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.utils.RuntimeResult;
/**
 * 已停用
 * @author Administrator
 *
 */
public class MGGiftCodeComponent extends ConcreteComponent<Player> {
	private static final Logger logger = Logger.getLogger(MGGiftCodeComponent.class);
	public static final String Tag = "MGGiftCodeComponent";
	private boolean isSingleCode = false;

	@Override
	public void ready() {
	//	addActionEventListener(StoreEventDefines.C2G_ExchangeCode);
	}

	@Override
	public void suspend() {
	//	removeActionEventListener(StoreEventDefines.C2G_ExchangeCode);
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {
		super.handleGameEvent(event);
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
//		int actionEventId = event.getActionEventId();
//		switch (actionEventId) {
//		case StoreEventDefines.C2G_ExchangeCode:
//			handle_ExchangeCode(event);
//			break;
//
//		default:
//			break;
//		}
	}

	private void handle_ExchangeCode(ActionEventBase event) {

		G2C_ExchangeCode res = MessageFactory.getConcreteMessage(StoreEventDefines.G2C_ExchangeCode);
		byte ret = 1;
		RuntimeResult result = exchange(event);
		if (!result.isOK()) {
			ret = 0;
			ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), result.getApplicationCode());
		}
		res.setType(ret);
		GameRoot.sendMessage(event.getIdentity(), res);
		MGStatFunctions.GiftCodeStat(getConcreteParent(), StringUtils.lowerCase(((C2G_ExchangeCode)event).getKeyCode()), ret);
	}

	private RuntimeResult exchange(ActionEventBase event) {
		C2G_ExchangeCode code = (C2G_ExchangeCode) event;
		Player player = getConcreteParent();
		String name = player.getName();
		String keyCode = code.getKeyCode();
		keyCode = StringUtils.lowerCase(keyCode);
		MGGiftCodeDataTypeRef ref = getKeyCodeRef(keyCode);
		
		if (ref == null) {
			return RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_STORE_EXCHANGE_NOT_EXIST);
		}
		
		byte codeType = ref.getCodeType();
		String groupId = ref.getId();
		
		
		if (codeType == MGGiftCodeType.Single) { // 如果是单人专用的
	//		int keyCount = GiftCodeDAO.getInstance().selectData(keyCode);
			int keyCount = MGGiftCodeDataConfig.getKeyCodeCount(keyCode);
			if(keyCount > 0){
				if (logger.isDebugEnabled()) {
					logger.debug("该单人专用礼包码:[" + keyCode + "]已使用");
				}
				return RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_STORE_EXCHANGE_EXPIRED);
			}
	//		int count = GiftCodeDAO.getInstance().selectDataByGroup(groupId, name);	
			int count = MGGiftCodeDataConfig.getGroupAndNameCount(groupId, name);
			if (count > 0) {
				if (logger.isDebugEnabled()) {
					logger.debug("该组单人专用礼包码:[" + keyCode + "]已使用");
				}
				return RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_STORE_EXCHANGE_EXPIRED);
			}
			
		}else if(codeType == MGGiftCodeType.Double){
	//		int keyCount = GiftCodeDAO.getInstance().selectData(name, keyCode);
			int keyCount = MGGiftCodeDataConfig.getKeyCodeAndNameCount(keyCode, name);
			if (keyCount > 0) {
				if (logger.isDebugEnabled()) {
					logger.debug("礼包码:[" + keyCode + "]已使用");
				}
				return RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_STORE_EXCHANGE_EXPIRED);
			}
		} 

		if (!timeValid(ref)) {
			if (logger.isDebugEnabled()) {
				logger.debug("礼包码:[" + keyCode + "]已过期");
			}
			return RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_STORE_EXCHANGE_EXPIRED);
		}
		List<ItemPair> rewards = ref.getRewards();
		List<ItemPair> items = new ArrayList<ItemPair>();
		List<ItemPair> unpsitems = new ArrayList<ItemPair>();
		for(ItemPair itemPair : rewards){
			String itemRefId = itemPair.getItemRefId();
			GameRefObject gameRefObject = GameRoot.getGameRefObjectManager().getManagedObject(itemRefId);
			if(gameRefObject instanceof ItemRef){
				items.add(itemPair);
			}else if(gameRefObject instanceof UnPropsItemRef){
				unpsitems.add(itemPair);
			}
		}
		RuntimeResult result = ItemFacade.addItem(getConcreteParent(), items, ItemOptSource.giftCode); 
																							
		if (!result.isOK()) {
			if (logger.isDebugEnabled()) {
				logger.debug("礼包码兑换失败，背包已满");
			}
			ResultEvent.sendResult(player.getIdentity(), event.getActionEventId(), result.getApplicationCode());
			return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_ITEM_SLOT_ENOUGH);
		}
		ItemFacade.addItem(getConcreteParent(), unpsitems, ItemOptSource.giftCode);
		
		
		MGGiftCodeObject giftCodeObject = new MGGiftCodeObject(name, keyCode, groupId);
		MGGiftCodeDataConfig.dataSet.add(giftCodeObject);
		GiftCodeDAO.getInstance().saveImmediateData(giftCodeObject);
		return RuntimeResult.OK();
	}
	
	private boolean timeValid(MGGiftCodeDataTypeRef ref) {
		long now = System.currentTimeMillis();
		return now >= ref.getOpenTime() && now <= ref.getExpiredTime();
	}

	private MGGiftCodeDataTypeRef getKeyCodeRef(String keyCode) {
		Set<MGGiftCodeDataTypeRef> giftCodeSet = MGGiftCodeDataConfig.getGiftCodeSet();
		if (giftCodeSet.isEmpty()) {
			return null;
		}
		for (MGGiftCodeDataTypeRef ref : giftCodeSet) {
			byte codeType = ref.getCodeType();
			if(codeType == MGGiftCodeType.Double){		//如果是多人公用
				String doubleKeyCode = ref.getDoubleGiftKeyCode();
				if(StringUtils.equals(keyCode, doubleKeyCode)){
					return ref;
				}
			}else if(codeType == MGGiftCodeType.Single){	//如果是单人专用
				Set<String> singleSet = ref.getGiftCodeSingleSet();
				if(singleSet == null){
					continue;
				}
				for(String singleCode : singleSet){
					if(StringUtils.equals(keyCode, singleCode)){
						return ref;
					}
				}
			}
		}
		

		return null;
	}
}
