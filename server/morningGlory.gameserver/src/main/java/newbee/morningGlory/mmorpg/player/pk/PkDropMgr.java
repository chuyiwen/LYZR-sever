package newbee.morningGlory.mmorpg.player.pk;

import java.util.List;
import java.util.Map.Entry;

import newbee.morningGlory.mmorpg.player.pk.ref.MGInvasionPair;
import newbee.morningGlory.mmorpg.player.pk.ref.MGScenePKDropRef;

import org.apache.commons.lang3.StringUtils;

import sophia.game.ref.GameRefObject;
import sophia.mmorpg.loot.Loot;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.utils.SFRandomUtils;
import sophia.mmorpg.utils.Type;

public class PkDropMgr {

	/**
	 * 怪物攻城掉落
	 * 
	 * @param ref
	 */
	public static void monsterInvasionDrop(Player player,Player owner, GameRefObject refObj) {

		MGScenePKDropRef ref = (MGScenePKDropRef)refObj;
		long[] args = new long[16];
		args[0] = 0;
		int i = 1;
		int dropNumber = 0;
		String itemRefId = "";
		byte bindStatus = 1;
		for (Entry<String, List<MGInvasionPair>> entry : ref.getInvasionMap().entrySet()) {
			String key = entry.getKey();
			itemRefId = entry.getValue().get(0).getItemRefId();
			int number = ItemFacade.getNumber(player, itemRefId);
			String[] range = key.split("\\|");
			int min = Type.getInt(range[0], 0);
			int max = Type.getInt(range[1], 0);
			if (number >= min && number <= max) {
				int random = SFRandomUtils.random100w();
				for (MGInvasionPair pair : entry.getValue()) {
					int probability = pair.getProbability();
					args[i] = args[i - 1] + probability;
					if (random > args[i - 1] && random <= args[i]) {
						dropNumber = pair.getNumber();
						bindStatus = pair.getBindStatus();
					}
					i++;
				}
			}
		}
		List<Loot> tmpLootList = null;
		if(dropNumber > 0 && StringUtils.isNotEmpty(itemRefId)){
			ItemPair itemPair = new ItemPair( itemRefId, dropNumber, bindStatus);
			tmpLootList = ItemFacade.dropItem(player, owner, itemPair,ItemOptSource.Pk);
		}
		MGPlayerPKComponent<?> component = (MGPlayerPKComponent<?>)player.getTagged(MGPlayerPKComponent.Tag);
		component.addKilledLootInfo(tmpLootList);
	}
}
