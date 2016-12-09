package newbee.morningGlory.mmorpg.player.castleWar.persistence;

import java.util.ArrayList;
import java.util.Collection;

import newbee.morningGlory.mmorpg.player.castleWar.MGCastleWarComponent;
import sophia.foundation.data.AbstractPersistenceObject;
import sophia.foundation.data.PersistenceParameter;
import sophia.mmorpg.player.Player;

public class MGCastleWarPersistenceObject extends AbstractPersistenceObject {
	private static final String SaveDataParameterName = "castleData";

	private MGCastleWarComponent castleWarComponent = null;
	
	private Player player;

	private PersistenceParameter castleWarDataPersistenceParameter = new PersistenceParameter();
	
	private MGCastleWarPersistenceReadWrite readWrite = new MGCastleWarPersistenceReadWrite();

	public MGCastleWarPersistenceObject(Player player) {
		this.player = player;
		MGCastleWarComponent castleWarComponent = (MGCastleWarComponent) player.getTagged(MGCastleWarComponent.Tag);
		this.persistenceParameters = new ArrayList<>(1);
		castleWarDataPersistenceParameter.setName(SaveDataParameterName);
		this.castleWarComponent = castleWarComponent;
		persistenceParameters.add(castleWarDataPersistenceParameter);
	}

	@Override
	public void snapshot() {
		byte[] bytes = readWrite.toBytes(castleWarComponent);
		castleWarDataPersistenceParameter.setValue(bytes);
	}

	@Override
	public void setDataFrom(Collection<PersistenceParameter> persistenceParameters) {
		for (PersistenceParameter persistenceParameter : persistenceParameters) {
			String name = persistenceParameter.getName();
			if (name.equals(SaveDataParameterName)) {
				byte[] bytes = (byte[]) persistenceParameter.getValue();
				MGCastleWarComponent castleWarComponent = (MGCastleWarComponent) player.getTagged(MGCastleWarComponent.Tag);
				readWrite.fromBytes(bytes, castleWarComponent);
			}
		}
	}
}
