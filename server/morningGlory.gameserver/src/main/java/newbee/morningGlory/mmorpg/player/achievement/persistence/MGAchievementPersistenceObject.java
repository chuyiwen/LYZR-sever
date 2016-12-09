package newbee.morningGlory.mmorpg.player.achievement.persistence;

import java.util.ArrayList;
import java.util.Collection;

import newbee.morningGlory.mmorpg.player.achievement.MGPlayerAchievementComponent;
import newbee.morningGlory.mmorpg.player.achievement.MGPlayerAchievementMgr;

import org.apache.log4j.Logger;

import sophia.foundation.data.AbstractPersistenceObject;
import sophia.foundation.data.PersistenceParameter;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.equipment.persistence.EquipmentPersistenceObject;

public class MGAchievementPersistenceObject extends AbstractPersistenceObject {
	private static Logger logger = Logger.getLogger(EquipmentPersistenceObject.class);

	public static final byte Json_Data = 1;

	public static final byte Bytes_Data = 2;

	private static final String SaveFormatParameterName = "saveFormat";

	private static final Byte saveFormatParameterValue = Bytes_Data;

	private static final String SaveDataParameterName = "achievementData";

	private PersistenceParameter achievementPersistenceParameter = new PersistenceParameter();

	private final AchievementReadWrite readWrite;

	private MGPlayerAchievementMgr achievementMgr;

	private Player player;

	public MGAchievementPersistenceObject(Player player) {
		MGPlayerAchievementComponent achievementComponent = (MGPlayerAchievementComponent) player.getTagged(MGPlayerAchievementComponent.Tag);
		this.achievementMgr = achievementComponent.getAchievementMgr();
		readWrite = new AchievementReadWrite(achievementMgr);
		persistenceParameters = new ArrayList<>(1);
		achievementPersistenceParameter.setName(SaveDataParameterName);
		this.persistenceParameters.add(achievementPersistenceParameter);
	}

	@Override
	public void setDataFrom(Collection<PersistenceParameter> persistenceParameters) {
		for (PersistenceParameter persistenceParameter : persistenceParameters) {
			String name = persistenceParameter.getName();
			if (logger.isDebugEnabled()) {
				logger.debug(name);
			}
			if (SaveDataParameterName.equals(name)) {
				readWrite.fromBytes((byte[]) persistenceParameter.getValue());
			}
		}

	}

	@Override
	public void snapshot() {
		byte[] bytes = readWrite.toBytes(achievementMgr);
		achievementPersistenceParameter.setValue(bytes);
	}

}
