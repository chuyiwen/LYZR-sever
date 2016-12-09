package newbee.morningGlory.mmorpg.player.activity.persistence;

import java.util.ArrayList;
import java.util.Collection;

import sophia.foundation.data.AbstractPersistenceObject;
import sophia.foundation.data.PersistenceParameter;
import sophia.mmorpg.player.Player;

public class ActivityPersistenceObject extends AbstractPersistenceObject {
	public static final byte Json_Data = 1;

	public static final byte Bytes_Data = 2;

	private static final String SaveDataParameterName = "activityData";

	private PersistenceParameter peeragePersistenceParameter = new PersistenceParameter();

	private ActivityAllReadWrite readWrite;
	private Player player;

	public ActivityPersistenceObject(Player player) {
		this.player = player;
		readWrite = new ActivityAllReadWrite(player);
		persistenceParameters = new ArrayList<>(1);
		peeragePersistenceParameter.setName(SaveDataParameterName);
		this.persistenceParameters.add(peeragePersistenceParameter);
	}

	@Override
	public void setDataFrom(Collection<PersistenceParameter> persistenceParameters) {
		for (PersistenceParameter persistenceParameter : persistenceParameters) {
			String name = persistenceParameter.getName();
			if (SaveDataParameterName.equals(name)) {
				readWrite.fromBytes((byte[]) persistenceParameter.getValue());
			}
		}
	}

	@Override
	public void snapshot() {
		byte[] bytes = readWrite.toBytes(player);
		peeragePersistenceParameter.setValue(bytes);
	}

}
