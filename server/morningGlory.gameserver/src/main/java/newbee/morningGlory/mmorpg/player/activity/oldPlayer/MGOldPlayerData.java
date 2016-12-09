/**
 * 
 */
package newbee.morningGlory.mmorpg.player.activity.oldPlayer;

import java.util.HashSet;
import java.util.Set;

/**
 * @author yinxinglin
 *
 */
public class MGOldPlayerData {
	
	private static Set<String> oldPlayers = new HashSet<>();
	
	public static boolean addOldPlayer(String identityId){
		return oldPlayers.add(identityId);
	}
	
	public static boolean isContain(String identityId){
		return oldPlayers.contains(identityId);
	}
}
