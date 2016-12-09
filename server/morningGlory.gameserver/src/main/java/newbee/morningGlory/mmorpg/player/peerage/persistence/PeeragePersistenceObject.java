package newbee.morningGlory.mmorpg.player.peerage.persistence;

import java.util.ArrayList;
import java.util.Collection;

import newbee.morningGlory.mmorpg.player.peerage.MGPeerageEffectMgr;
import newbee.morningGlory.mmorpg.player.peerage.MGPeerageRef;
import newbee.morningGlory.mmorpg.player.peerage.MGPeerageRefMgr;
import newbee.morningGlory.mmorpg.player.peerage.MGPlayerPeerageComponent;

import org.apache.log4j.Logger;

import sophia.foundation.data.AbstractPersistenceObject;
import sophia.foundation.data.PersistenceParameter;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.equipment.persistence.EquipmentPersistenceObject;

public class PeeragePersistenceObject extends AbstractPersistenceObject {
	private static Logger logger = Logger.getLogger(EquipmentPersistenceObject.class);

	public static final byte Json_Data = 1;

	public static final byte Bytes_Data = 2;

	private static final String SaveDataParameterName = "peerageData";

	private PersistenceParameter peeragePersistenceParameter = new PersistenceParameter();

	private final PeerageReadWrite readWrite;

	private MGPeerageRefMgr peerageRefMgr;
	private Player player;

	public PeeragePersistenceObject(Player player) {
		this.player = player;
		MGPlayerPeerageComponent peerageComponent = (MGPlayerPeerageComponent) player.getTagged(MGPlayerPeerageComponent.Tag);
		this.peerageRefMgr = peerageComponent.getPeeragerefMgr();
		readWrite = new PeerageReadWrite(peerageRefMgr);
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
		// 恢复爵位增加效果
		MGPeerageRef peerageRef = peerageRefMgr.getCrtPeerageRef();
		MGPeerageEffectMgr peerageEffectMgr = new MGPeerageEffectMgr(player);
		if (peerageRef != null) {
			peerageEffectMgr.restore(peerageRef);
		}
	}

	@Override
	public void snapshot() {
		byte[] bytes = readWrite.toBytes(peerageRefMgr);
		peeragePersistenceParameter.setValue(bytes);
	}

	@Override
	public Collection<PersistenceParameter> getPersistenceParameters() {
		return persistenceParameters;
	}

}
