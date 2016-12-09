package newbee.morningGlory.mmorpg.player.peerage.persistence;

import newbee.morningGlory.mmorpg.player.peerage.MGPeerageRef;
import newbee.morningGlory.mmorpg.player.peerage.MGPeerageRefMgr;
import newbee.morningGlory.mmorpg.player.peerage.MGPlayerPeerageComponent;
import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.game.GameRoot;
import sophia.mmorpg.player.persistence.AbstractPersistenceObjectReadWrite;
import sophia.mmorpg.player.persistence.PersistenceObjectReadWrite;

public class PeerageReadWrite extends AbstractPersistenceObjectReadWrite<MGPeerageRefMgr> implements PersistenceObjectReadWrite<MGPeerageRefMgr> {
	private MGPeerageRefMgr peerageRefMgr;

	public PeerageReadWrite(MGPeerageRefMgr peerageRefMgr) {
		this.peerageRefMgr = peerageRefMgr;
	}

	@Override
	public byte[] toBytes(MGPeerageRefMgr persistenceObject) {
		// TODO Auto-generated method stub
		return toBytesVer1000(persistenceObject);
	}

	@Override
	public MGPeerageRefMgr fromBytes(byte[] persistenceBytes) {
		// TODO Auto-generated method stub
		return fromBytesVer1000(persistenceBytes);
	}

	@Override
	public String toJsonString(MGPeerageRefMgr persistenceObject) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MGPeerageRefMgr fromJsonString(String persistenceJsonString) {
		return null;
	}

	public byte[] toBytesVer1000(MGPeerageRefMgr persistenceObject) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();
		MGPlayerPeerageComponent peerageComponent = (MGPlayerPeerageComponent)persistenceObject.getPlayer().getTagged(MGPlayerPeerageComponent.Tag);
		int merit = peerageComponent.getMeritManager().getMerit();
		MGPeerageRef crtPeerageRef = persistenceObject.getCrtPeerageRef();
		String crtPeerageRefId = null;
		if(null != crtPeerageRef){
			crtPeerageRefId = crtPeerageRef.getId();
		}
		String dateTime = persistenceObject.getDateTime();
		buffer.writeInt(Default_Write_Version);
		buffer.writeString(crtPeerageRefId);
		buffer.writeString(dateTime);
		buffer.writeInt(merit);

		return buffer.getData();
	}

	public MGPeerageRefMgr fromBytesVer1000(byte[] persistenceBytes) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer(persistenceBytes);
		int version = buffer.readInt();
		String crtPeerageRefId = buffer.readString();
		String dateTime = buffer.readString();
		int merit = buffer.readInt();		
		
		MGPeerageRef crtPeerageRef = (MGPeerageRef)GameRoot.getGameRefObjectManager().getManagedObject(crtPeerageRefId);
		peerageRefMgr.setCrtPeerageRef(crtPeerageRef);
		peerageRefMgr.setDateTime(dateTime);
		MGPlayerPeerageComponent peerageComponent = (MGPlayerPeerageComponent)peerageRefMgr.getPlayer().getTagged(MGPlayerPeerageComponent.Tag);
		peerageComponent.getMeritManager().setMerit(merit);
		return peerageRefMgr;
	}

	public MGPeerageRefMgr getPeerageRefMgr() {
		return peerageRefMgr;
	}

	public void setPeerageRefMgr(MGPeerageRefMgr peerageRefMgr) {
		this.peerageRefMgr = peerageRefMgr;
	}

}
