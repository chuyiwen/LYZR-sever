package newbee.morningGlory.mmorpg.player.offLineAI.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

/**
 * 玩家查看离线背包
 */
public class C2G_ViewOffLineAIReward extends ActionEventBase{

	@Override
	public void unpackBody(IoBuffer buffer) {
	}
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		return buffer;
	}
	

}
