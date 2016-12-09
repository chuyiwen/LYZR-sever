package newbee.morningGlory.http.actionEvent;

import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.player.itemBag.ItemPair;

public class GetGiftsCardEvent extends ActionEventBase {

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
	}

	public List<ItemPair> itemPairs = null;

	public List<ItemPair> getItemPairs() {
		return itemPairs;
	}

	public void setItemPairs(List<ItemPair> itemPairs) {
		this.itemPairs = itemPairs;
	}
}
