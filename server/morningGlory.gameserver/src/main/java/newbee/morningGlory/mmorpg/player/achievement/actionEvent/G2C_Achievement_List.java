package newbee.morningGlory.mmorpg.player.achievement.actionEvent;

import java.util.List;

import newbee.morningGlory.mmorpg.player.achievement.MGPlayerAchievement;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Achievement_List extends ActionEventBase {
	private static final Logger logger = Logger.getLogger(G2C_Achievement_List.class);
	private List<MGPlayerAchievement> crtAchievements;

	public G2C_Achievement_List(){
		ziped = (byte)1;
	}
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		short count = (short) crtAchievements.size();
		if (logger.isDebugEnabled()) {
			logger.debug("公会列表长度:" + count);
		}
		buffer.putShort(count);
		for (MGPlayerAchievement playerAchievement : crtAchievements) {
			String refId = playerAchievement.getAchievementRef().getId();
			byte getReward = playerAchievement.getSuccess();
			putString(buffer, refId);
			buffer.put(getReward);
		}
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {

	}

	public List<MGPlayerAchievement> getCrtAchievements() {
		return crtAchievements;
	}

	public void setCrtAchievements(List<MGPlayerAchievement> crtAchievements) {
		this.crtAchievements = crtAchievements;
	}

}
