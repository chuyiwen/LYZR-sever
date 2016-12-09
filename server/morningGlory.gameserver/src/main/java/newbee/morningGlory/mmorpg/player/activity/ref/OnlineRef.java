package newbee.morningGlory.mmorpg.player.activity.ref;

import java.util.List;

import sophia.game.GameRoot;
import sophia.game.ref.AbstractGameRefObjectBase;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class OnlineRef extends AbstractGameRefObjectBase implements Comparable<OnlineRef>{
	private static final long serialVersionUID = -3124718312081909577L;
	private List<ItemPair> itemPairs;

	public OnlineRef() {

	}

	public List<ItemPair> getItemPairs() {
		return itemPairs;
	}

	public void setItemPairs(List<ItemPair> itemPairs) {
		this.itemPairs = itemPairs;
	}

	/**
	 * 下一个ref
	 */

	public OnlineRef getOnlineNextRef() {
		String nextRefId = MGPropertyAccesser.getOnlineNextRefId(getProperty());
		return (OnlineRef) GameRoot.getGameRefObjectManager().getManagedObject(nextRefId);
	}
	
	/**
	 * 上一个ref
	 */
	public OnlineRef getOnlineRefPreRef() {
		String preRefId = MGPropertyAccesser.getOnlinePreRefId(getProperty());

		return (OnlineRef) GameRoot.getGameRefObjectManager().getManagedObject(preRefId);
	}
	

	/**
	 * 本次倒计时时间
	 */
	public int getOnlineSeconds() {
		return MGPropertyAccesser.getOnlineSecond(getProperty());
	}

	@Override
	public int compareTo(OnlineRef o) {
		String crtRefId = this.getId();
		String targetRefId = o.getId();
		
		// online_1 online_2
		int crtIndex = Integer.parseInt(crtRefId.substring(crtRefId.length() - 1));
		
		int targetIndex = Integer.parseInt(targetRefId.substring(targetRefId.length() - 1));
		
		return crtIndex - targetIndex;
	}
}
