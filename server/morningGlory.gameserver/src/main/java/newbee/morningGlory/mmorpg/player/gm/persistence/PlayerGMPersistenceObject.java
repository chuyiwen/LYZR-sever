package newbee.morningGlory.mmorpg.player.gm.persistence;

import java.util.ArrayList;
import java.util.Collection;

import newbee.morningGlory.mmorpg.player.gm.MGPlayerGMMgr;

import org.apache.log4j.Logger;

import sophia.foundation.data.AbstractPersistenceObject;
import sophia.foundation.data.PersistenceParameter;

public class PlayerGMPersistenceObject extends AbstractPersistenceObject {
	private static final Logger logger = Logger.getLogger(PlayerGMPersistenceObject.class.getName());
	private static final String PlayerGMFieldName = "states";
	private MGPlayerGMMgr owner;

	public static final byte Json_Data = 1;

	public static final byte Bytes_Data = 2;

	private static final String SaveFormatParameterName = "saveFormat";

	private static final Byte saveFormatParameterValue = Bytes_Data;

	private static final String SaveDataParameterName = "states";

	private PersistenceParameter playerGMDataPersistenceParameter = new PersistenceParameter();

	private final PlayerGMReadWrite readWrite;

	public PlayerGMPersistenceObject(MGPlayerGMMgr playerGMMgr) {
		this.owner = playerGMMgr;
		readWrite = new PlayerGMReadWrite(playerGMMgr);
		persistenceParameters = new ArrayList<>(1);
		playerGMDataPersistenceParameter.setName(SaveDataParameterName);
		this.persistenceParameters.add(playerGMDataPersistenceParameter);
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
		byte[] bytes = readWrite.toBytes(owner);
		playerGMDataPersistenceParameter.setValue(bytes);
	}
}
