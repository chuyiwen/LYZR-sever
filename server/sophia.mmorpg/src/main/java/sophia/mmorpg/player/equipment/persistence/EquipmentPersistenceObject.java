package sophia.mmorpg.player.equipment.persistence;

import java.util.ArrayList;
import java.util.Collection;

import sophia.foundation.data.AbstractPersistenceObject;
import sophia.foundation.data.PersistenceParameter;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.equipment.EquipEffectMgr;
import sophia.mmorpg.player.equipment.PlayerEquipBody;

public class EquipmentPersistenceObject extends AbstractPersistenceObject {

	private PlayerEquipBody equipBody;

	public static final byte Json_Data = 1;

	public static final byte Bytes_Data = 2;

	private static final String SaveDataParameterName = "equipData";

	private PersistenceParameter equipmentPersistenceParameter = new PersistenceParameter();

	private final EquipmentReadWrite readWrite;

	private Player player;

	public EquipmentPersistenceObject(Player player) {
		this.player = player;
		this.equipBody = player.getPlayerEquipBodyConponent().getPlayerBody();
		readWrite = new EquipmentReadWrite(equipBody);
		persistenceParameters = new ArrayList<>(1);
		equipmentPersistenceParameter.setName(SaveDataParameterName);
		this.persistenceParameters.add(equipmentPersistenceParameter);
	}

	@Override
	public void setDataFrom(Collection<PersistenceParameter> persistenceParameters) {
		for (PersistenceParameter persistenceParameter : persistenceParameters) {
			String name = persistenceParameter.getName();
			if (SaveDataParameterName.equals(name)) {
				readWrite.fromBytes((byte[]) persistenceParameter.getValue());
			}
		}
		EquipEffectMgr equipEffectMgr = new EquipEffectMgr(player);
		equipEffectMgr.restore();
		//player.getPlayerEquipBodyConponent().getEquipEffectMgr().restore();
	}

	@Override
	public void snapshot() {
		// TODO Auto-generated method stub
		byte[] bytes = readWrite.toBytes(equipBody);
		equipmentPersistenceParameter.setValue(bytes);
	}

	@Override
	public Collection<PersistenceParameter> getPersistenceParameters() {
		return persistenceParameters;
	}
}
