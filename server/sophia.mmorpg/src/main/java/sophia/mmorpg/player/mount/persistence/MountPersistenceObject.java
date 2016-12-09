package sophia.mmorpg.player.mount.persistence;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import sophia.foundation.data.AbstractPersistenceObject;
import sophia.foundation.data.PersistenceParameter;
import sophia.mmorpg.player.mount.Mount;
import sophia.mmorpg.player.mount.MountManager;

public class MountPersistenceObject extends AbstractPersistenceObject {
	private static final Logger logger = Logger.getLogger(MountPersistenceObject.class.getName());
	private static final String MountFieldName = "mountData";
	private MountManager owner;

	public static final byte Json_Data = 1;

	public static final byte Bytes_Data = 2;

	private static final String SaveFormatParameterName = "saveFormat";

	private static final Byte saveFormatParameterValue = Bytes_Data;

	private static final String SaveDataParameterName = "mountData";

	private PersistenceParameter mountDataPersistenceParameter = new PersistenceParameter();

	private final MountReadWrite readWrite;

	public MountPersistenceObject(MountManager mountManager) {
		this.owner = mountManager;
		readWrite = new MountReadWrite();
		persistenceParameters = new ArrayList<>(1);
		mountDataPersistenceParameter.setName(SaveDataParameterName);
		this.persistenceParameters.add(mountDataPersistenceParameter);
	}

	@Override
	public void snapshot() {
		logger.debug("==========  MountPersistence.snapshot().  =========");
		if (owner.getCrtMount() == null) {
			logger.debug("==========  MountPersistence.snapshot().null  =========");
			return;
		}
		byte[] bytes = readWrite.toBytes(owner.getCrtMount());
		mountDataPersistenceParameter.setValue(bytes);
	}

	@Override
	public Collection<PersistenceParameter> getPersistenceParameters() {
		return persistenceParameters;
	}

	@Override
	public void setDataFrom(Collection<PersistenceParameter> persistenceParameters) {
		for (PersistenceParameter persistenceParameter : persistenceParameters) {
			String name = persistenceParameter.getName();
			if (SaveDataParameterName.equals(name)) {
				Mount crtMount = readWrite.fromBytes((byte[]) persistenceParameter.getValue());
				if (crtMount == null){
					return;
				}
				owner.setCrtMount(crtMount);
				owner.getPlayer().getPlayerMountComponent().getMountEffectMgr().restore(crtMount);
			}
		}
	}

}
