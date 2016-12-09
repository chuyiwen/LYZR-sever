package newbee.morningGlory.mmorpg.player.offLineAI.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

/**
 * 玩家领取离线背包奖励
 */
public class G2C_DrawOffLineAIReward extends ActionEventBase {

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {

	}

}
