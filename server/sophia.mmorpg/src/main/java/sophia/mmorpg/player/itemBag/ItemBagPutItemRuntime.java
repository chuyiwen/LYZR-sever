/**
 * 
 */
package sophia.mmorpg.player.itemBag;

import sophia.mmorpg.item.Item;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.utils.RuntimeResult;

/**
 * @author Administrator
 *
 */
public interface ItemBagPutItemRuntime {
	
	RuntimeResult putItem(Player player, Item item,int index);
	RuntimeResult putMerit(Player player,int number);
	RuntimeResult putAchievement(Player player,int number);
}
