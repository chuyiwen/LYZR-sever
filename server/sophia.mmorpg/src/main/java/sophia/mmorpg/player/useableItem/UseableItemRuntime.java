package sophia.mmorpg.player.useableItem;

import sophia.mmorpg.item.Item;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.utils.RuntimeResult;

public interface UseableItemRuntime {
	RuntimeResult useTo(Player player, Item item,int number);

}