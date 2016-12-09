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
package sophia.mmorpg.loot;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import sophia.mmorpg.base.scene.ref.SceneRef;
import sophia.mmorpg.base.sprite.NonFightSprite;
import sophia.mmorpg.base.sprite.SpriteTypeDefine;
import sophia.mmorpg.base.sprite.aoi.SpriteAOIComponent;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.code.MMORPGSuccessCode;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.player.team.PlayerTeam;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class Loot extends NonFightSprite {

	public static final String Loot_GameSPrite_Type = Loot.class.getSimpleName();

	// 保护时间，30秒
	public static final long SafeTime = 30 * 1000;
	// 生存时间，60秒
	public static final long LifeTime = 60 * 1000;
	// ItemPair 和 Item 不同时存在
	private ItemPair itemPair;

	private Item item;

	private long bornTime;

	private int lootKind = LootKind.SinglePlayerLoot; // 初始化为个人拾取类型

	private String ownerId;

	public Loot() {
		setId(UUID.randomUUID().toString());
		clear();
		registComponents();
	}

	public Loot(ItemPair itemPair) {
		setId(UUID.randomUUID().toString());
		clear();
		this.setItemPair(itemPair);
		registComponents();
	}

	public Loot(Item item) {
		setId(UUID.randomUUID().toString());
		clear();
		this.setItem(item);
		registComponents();
	}

	@SuppressWarnings("unchecked")
	public void registComponents() {
		setAoiComponent((SpriteAOIComponent<Loot>) createComponent(SpriteAOIComponent.class));
	}

	@Override
	public String getGameSpriteType() {
		return Loot_GameSPrite_Type;
	}

	@Override
	public byte getSpriteType() {
		return SpriteTypeDefine.GameSprite_Loot;
	}

	public long getBornTime() {
		return bornTime;
	}

	public void setBornTime(long bornTime) {
		this.bornTime = bornTime;
	}

	public void clear() {
		bornTime = System.currentTimeMillis();
		ownerId = null;
		setItemPair(null);
	}

	public synchronized int pickUp(Player player) {
		if (!isWho(player)) {
			return MMORPGErrorCode.CODE_PICKUP_GUARDTIME;
		}

		if (crtScene.getLootMgrComponent().getLoot(getId()) == null) {
			return MMORPGErrorCode.CODE_PICKUP_ALREADY;
		}

		// 物品给到player
		if (getItemPair() == null) {
			if (!ItemFacade.addItems(player, getItem(), ItemOptSource.Loot).isOK()) {
				return MMORPGErrorCode.CODE_PICKUP_ITEMBAG_FULL;
			}
		} else {
			if (!ItemFacade.addItem(player, getItemPair(), ItemOptSource.Loot).isOK()) {
				return MMORPGErrorCode.CODE_PICKUP_ITEMBAG_FULL;
			}
		}

		// 掉落清除
		crtScene.getLootMgrComponent().leaveWorld(this);

		return MMORPGSuccessCode.CODE_SUCCESS;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public ItemPair getItemPair() {
		return itemPair;
	}

	public void setItemPair(ItemPair itemPair) {
		this.itemPair = itemPair;
	}

	public boolean isWho(Player player) {
		
		// TODO:单人副本时使用，多人副本需修改
		if( player.getCrtScene().getRef().getType() == SceneRef.FuBen && this.getCrtScene().getId() == player.getCrtScene().getId() ){
			return true;
		}
				
		if (lootKind == LootKind.SinglePlayerLoot) {
			if (leftGuardSeconds() > 0 && !StringUtils.equals(ownerId, player.getId())) {
				return false;
			}
		} else if (lootKind == LootKind.TeamPlayerLoot) {
			PlayerTeam team = player.getPlayerTeamComponent().getTeam();
			if (team == null) {
				return false;
			}
			if (leftGuardSeconds() > 0 && !StringUtils.equals(ownerId, team.getId())) {
				return false;
			}
		} else if (lootKind == LootKind.UnionPlayerLoot) {
			String unionName = MGPropertyAccesser.getUnionName(player.getProperty());
			if (leftGuardSeconds() > 0 && !StringUtils.equals(unionName, ownerId)) {
				return false;
			}
		}
		return true;
	}

	public int leftGuardSeconds() {
		long leftTime = SafeTime - System.currentTimeMillis() + bornTime;
		if (leftTime < 0) {
			leftTime = 0;
		} else {
			leftTime = leftTime / 1000;
		}

		return (int) leftTime;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	@Override
	public String toString() {
		return "Loot [itemPair=" + itemPair + ", item=" + item + " " + crtPosition + ", ownerId=" + ownerId + ", getId()=" + getId() + "]";
	}

	public int getLootKind() {
		return lootKind;
	}

	public void setLootKind(int lootKind) {
		this.lootKind = lootKind;
	}
}
