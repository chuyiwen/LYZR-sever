package newbee.morningGlory.mmorpg.player.store.event;

import java.util.Map;
import java.util.Map.Entry;

import newbee.morningGlory.mmorpg.player.activity.discount.DiscountTimeMgr;
import newbee.morningGlory.mmorpg.player.activity.discount.RecordManager;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.util.IoBufferUtil;

public class G2C_Discount_GetShopList extends ActionEventBase {
	private String playerId;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {

		long remainTime = DiscountTimeMgr.getRemainTime();
		Map<String, Short> copyAllLimitItems = RecordManager.getCopyAllLimitItems();
		
		
		buffer.putLong(remainTime);
		buffer.putShort((short) copyAllLimitItems.size());

		for (Entry<String, Short> entry : copyAllLimitItems.entrySet()) {
			String discountRefId = entry.getKey();
			short number = entry.getValue();

			IoBufferUtil.putString(buffer, discountRefId);
			buffer.putShort(number);

			short personalCanBuyCount = RecordManager.getPersonalCanBuyCount(playerId, discountRefId);
			buffer.putShort(personalCanBuyCount);
		}

		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {

	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

}
