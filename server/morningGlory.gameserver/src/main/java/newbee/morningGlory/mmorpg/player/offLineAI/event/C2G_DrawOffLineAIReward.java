package newbee.morningGlory.mmorpg.player.offLineAI.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

/**
 * 玩家领取离线背包奖励
 */
public class C2G_DrawOffLineAIReward extends ActionEventBase{

	@Override
	public void unpackBody(IoBuffer buffer) {
	}
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		return buffer;
	}

}
