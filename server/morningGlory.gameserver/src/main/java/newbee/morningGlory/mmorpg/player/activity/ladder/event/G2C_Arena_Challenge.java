package newbee.morningGlory.mmorpg.player.activity.ladder.event;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.property.PropertyDictionary;

public class G2C_Arena_Challenge extends ActionEventBase {
	private static Logger logger = Logger.getLogger(G2C_Arena_Challenge.class);
	private byte fightResult;
	private int goldNum;
	private int meritNum;
	private int fightRandomValue;
	private int targetRandomValue;
	private PropertyDictionary property;
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		logger.debug("G2C_Arena_Challenge 返回");
		buffer.put(fightResult);
		buffer.putInt(goldNum);
		buffer.putInt(meritNum);
		buffer.putInt(fightRandomValue);
		buffer.putInt(targetRandomValue);
		byte[] pdData = property.toByteArray();
		buffer.putInt(pdData.length);
		buffer.put(pdData);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {

	}

	public void setFightResult(byte fightResult) {
		this.fightResult = fightResult;
	}

	public void setFightRandomValue(int fightRandomValue) {
		this.fightRandomValue = fightRandomValue;
	}

	public void setTargetRandomValue(int targetRandomValue) {
		this.targetRandomValue = targetRandomValue;
	}

	public void setProperty(PropertyDictionary property) {
		this.property = property;
	}

	public void setGoldNum(int goldNum) {
		this.goldNum = goldNum;
	}

	public void setMeritNum(int meritNum) {
		this.meritNum = meritNum;
	}

}
