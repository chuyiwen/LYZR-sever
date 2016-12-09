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
package sophia.mmorpg.equipmentSmith.smith.fenJie;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import sophia.game.GameRoot;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.GameObjectFactory;
import sophia.mmorpg.Mail.MailMgr;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.item.ref.ItemRef;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.stat.StatFunctions;
import sophia.mmorpg.utils.SFRandomUtils;
import sophia.mmorpg.utils.Type;

import com.google.gson.Gson;

/**
 * 装备-分解
 */
public final class MGFenJieEquipmentSmith {
	private static final MGFenJieEquipmentConfig fenJieConfig, fenJieScrollConfig;
	private int totalGold;
	private int totalBindGold;
	private int totalUnBindGold;

	private long totalExp;
	private short itemCount;
	private short count = 2;

	public MGFenJieEquipmentSmith() {

	}

	static {
		fenJieConfig = (MGFenJieEquipmentConfig) GameRoot.getGameRefObjectManager().getManagedObject(MGFenJieEquipmentConfig.FenJie_Id);
		fenJieScrollConfig = (MGFenJieEquipmentConfig) GameRoot.getGameRefObjectManager().getManagedObject(MGFenJieEquipmentConfig.FenJieScroll_Id);
	}

	public boolean fenJie(Player delegate, List<Item> equipments) {

		List<Item> items = new ArrayList<Item>();
		for (Item equipment : equipments) {

			byte bindStatus = equipment.getBindStatus();

			ItemRef equipmentRef = equipment.getItemRef();

			StatFunctions.FenJieStat(delegate, equipmentRef.getId());

			int pingJiaLevel = MGPropertyAccesser.getEvaluateLevel(equipmentRef.getProperty()) < 0 ? 0 : MGPropertyAccesser.getEvaluateLevel(equipmentRef.getProperty());
			int strengtheningLevel = MGPropertyAccesser.getStrengtheningLevel(equipment.getProperty()) < 0 ? 0 : MGPropertyAccesser.getStrengtheningLevel(equipment.getProperty());

			String rangeKey = getRangeKey(pingJiaLevel);
			MGFenJieRef mgFenJieRef = fenJieConfig.getFenJieConfigMap().get(rangeKey);

			checkArgument(mgFenJieRef != null, "Resolve Eqiupment Data Key " + rangeKey + " is not Exist.");

			List<MGFenJieItem> fenJieItems = mgFenJieRef.getFenJieItem();
			int saleCurrency = equipment.getSaleCurrency(); // 价格单位
			int fenJieGold = equipment.getSalePrice(); // 从装备表里读出售价格

			long fenJieExp = MGPropertyAccesser.getExp(mgFenJieRef.getProperty());

			for (MGFenJieItem fenJieItem : fenJieItems) {
				Item item = createItem(fenJieItem.getItemRefId(), fenJieItem.getProbability(), fenJieItem.getNumber());
				if (item != null) {
					item.setBindStatus(bindStatus);
					items.add(item);
				}
			}
			if (strengtheningLevel > 0) {
				MGFenJieScrollRef jieScrollRef = fenJieScrollConfig.getFenJieScrollConfigMap().get(strengtheningLevel);
				String scrollItemRefId = MGPropertyAccesser.getItemRefId(jieScrollRef.getProperty());
				Item item = GameObjectFactory.getItem(scrollItemRefId);
				item.setBindStatus(bindStatus);
				items.add(item);
			}

			if (saleCurrency == 1)
				totalGold += fenJieGold;
			else if (saleCurrency == 2)
				totalBindGold += fenJieGold;
			else if (saleCurrency == 3)
				totalUnBindGold += fenJieGold;
			totalExp = totalExp + fenJieExp;
		}

		for (Item equipment : equipments) {
			ItemFacade.removeItemById(delegate, equipment.getId(), 1, ItemOptSource.FenJie);
		}
		addAndCountItem(delegate, items, totalGold, totalBindGold, totalUnBindGold, totalExp);
		return true;
	}

	private void addAndCountItem(Player delegate, List<Item> items, int totalGold, int totalBindGold, int totalUnBindGold, long totalExp) {

		if (items.size() > 0) {
			List<Item> canPutItems = new ArrayList<Item>();
			int i = 0;
			while (delegate.getItemBagComponent().getItemBag().canPut(items) && items.size() > 0) {
				canPutItems.add(items.remove(i));
			}
			ItemFacade.addItems(delegate, canPutItems, ItemOptSource.FenJie);
			if (items.size() > 0) {
				sendEmail(delegate, items); // 发送邮件
			}

		}
		if (totalGold > 0) {
			delegate.getPlayerMoneyComponent().addGold(totalGold, ItemOptSource.FenJie);
		}
		if (totalBindGold > 0) {
			delegate.getPlayerMoneyComponent().addBindGold(totalBindGold, ItemOptSource.FenJie);
		}
		if (totalUnBindGold > 0) {
			delegate.getPlayerMoneyComponent().addUnbindGold(totalUnBindGold, ItemOptSource.FenJie);
		}
		delegate.getExpComponent().addExp((int) totalExp);

	}

	private void sendEmail(Player delegate, List<Item> items) {

		String firstStr = "您本次分解装备，获得";
		StringBuffer content = new StringBuffer(firstStr);
		List<ItemPair> list = new ArrayList<ItemPair>();
		for (int j = 0; j < items.size(); j++) {
			Item item = items.get(j);
			list.add(new ItemPair(item.getItemRefId(), item.getNumber(), false));

		}
		for (ItemPair itemPair : list) {
			String key = itemPair.getItemRefId();
			GameRefObject gameRefObject = GameRoot.getGameRefObjectManager().getManagedObject(key);
			String itemName = MGPropertyAccesser.getName(gameRefObject.getProperty());
			int count = itemPair.getNumber();
			content.append(itemName + "X" + count + ",");
		}
		content.append("金币X" + this.totalGold);
		content.append("由于背包已满，部分开出道具请在邮箱查收");
		String json = (new Gson()).toJson(list);
		MailMgr.sendMailById(delegate.getId(), content.toString(), (byte) 0, json, 0, 0, 0);
	}

	private Item createItem(String itemRefId, int probability, int number) {

		Item item = null;
		for (int i = 0; i < number; i++) {
			int probobly = SFRandomUtils.random100w();
			if (probobly <= probability) { // 概率获得物品
				if (item == null) {
					item = GameObjectFactory.getItem(itemRefId);
				} else {

					item.setNumber(item.getNumber() + 1);

				}
			}
		}

		return item;
	}

	private String getRangeKey(int pingJiaLevel) {

		String rangeKey = "";
		Map<String, MGFenJieRef> maps = fenJieConfig.getFenJieConfigMap();
		for (Entry<String, MGFenJieRef> entry : maps.entrySet()) {
			String key = entry.getKey();
			String rkey = key.substring(1, key.length() - 1);
			String[] range = rkey.split(",");
			int min = Type.getInt(range[0], 0);
			int max = Type.getInt(range[1], 20);
			if (pingJiaLevel <= max && pingJiaLevel >= min) {
				return key;
			}
		}

		return rangeKey;
	}

	public int getTotalGold() {
		return totalGold;
	}

	public void setTotalGold(int totalGold) {
		this.totalGold = totalGold;
	}

	public long getTotalExp() {
		return totalExp;
	}

	public void setTotalExp(long totalExp) {
		this.totalExp = totalExp;
	}

	public short getItemCount() {
		return itemCount;
	}

	public void setItemCount(short itemCount) {
		this.itemCount = itemCount;
	}

	public short getCount() {
		return count;
	}

	public void setCount(short count) {
		this.count = count;
	}

	public int getTotalBindGold() {
		return totalBindGold;
	}

	public void setTotalBindGold(int totalBindGold) {
		this.totalBindGold = totalBindGold;
	}

	public int getTotalUnBindGold() {
		return totalUnBindGold;
	}

	public void setTotalUnBindGold(int totalUnBindGold) {
		this.totalUnBindGold = totalUnBindGold;
	}

	public void reset() {
		this.itemCount = 0;
		this.totalExp = 0;
		this.totalGold = 0;
	}
}
