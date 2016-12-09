/**
 * 
 */
package newbee.morningGlory.mmorpg.player.activity.QuickRecharge;

import java.util.HashSet;
import java.util.Set;

public final class QuickRechargeMgr {

	public static final Set<String> quickRefIds = new HashSet<>();
	private Set<String> quickRechargeSet = new HashSet<>();

	public void add(String quickRechargeRefId) {
		quickRechargeSet.add(quickRechargeRefId);
	}

	public boolean isNeverRecharge(String quickRechargeRefId) {
		return !quickRechargeSet.contains(quickRechargeRefId);
	}

	public Set<String> getQuickRechargeSet() {
		return quickRechargeSet;
	}

}
