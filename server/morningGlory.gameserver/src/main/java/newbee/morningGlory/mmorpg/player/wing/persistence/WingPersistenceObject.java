package newbee.morningGlory.mmorpg.player.wing.persistence;

import java.util.ArrayList;
import java.util.Collection;

import newbee.morningGlory.mmorpg.player.wing.MGPlayerWing;
import newbee.morningGlory.mmorpg.player.wing.MGPlayerWingComponent;
import newbee.morningGlory.mmorpg.player.wing.MGPlayerWingRef;
import newbee.morningGlory.mmorpg.player.wing.MGWingEffectMgr;
import sophia.foundation.data.AbstractPersistenceObject;
import sophia.foundation.data.PersistenceParameter;
import sophia.mmorpg.player.Player;

public class WingPersistenceObject extends AbstractPersistenceObject{
	private MGPlayerWing playerWing;
	
	public static final byte Json_Data = 1;
	
	public static final byte Bytes_Data = 2;
	
	private static final String SaveFormatParameterName = "saveFormat";
	
	private static final Byte saveFormatParameterValue = Bytes_Data;
	
	private static final String SaveDataParameterName = "wingData";
	
	private PersistenceParameter WingDataPersistenceParameter = new PersistenceParameter();
	
	private Player player;
	
	private WingReadWrite wingReadWrite;
	
	MGPlayerWingComponent wingComponent;
	
	public WingPersistenceObject(Player player) {
		this.player = player;
		wingReadWrite = new WingReadWrite(); 
		wingComponent = (MGPlayerWingComponent) player.getTagged(MGPlayerWingComponent.Tag);
		this.playerWing = wingComponent.getPlayerWing();
		this.persistenceParameters = new ArrayList<>(1);
		WingDataPersistenceParameter.setName(SaveDataParameterName);
		persistenceParameters.add(WingDataPersistenceParameter);
	}
	
	@Override
	public void snapshot() {
		byte[] wingBytes = wingReadWrite.toBytes(playerWing);
		WingDataPersistenceParameter.setValue(wingBytes);
	}
	
	
	public void setDataFrom2(Collection<PersistenceParameter> persistenceParameters) {
		for(PersistenceParameter persistenceParameter : persistenceParameters)
		{
			String name = persistenceParameter.getName();
			if (name.equals(SaveDataParameterName)){
				byte[] bytes = (byte[])persistenceParameter.getValue();
				MGPlayerWing wing = wingReadWrite.fromBytes(bytes);
				if(null != wing) {
					MGPlayerWingRef playerWingRef = wing.getPlayerWingRef();
					playerWing.setPlayerWingRef(playerWingRef);
					MGWingEffectMgr wingEffectMgr = new MGWingEffectMgr(player);
					wingEffectMgr.restore(playerWing);
				}
			}
		}
	}
	
	@Override
	public void setDataFrom(Collection<PersistenceParameter> persistenceParameters) {
		for(PersistenceParameter persistenceParameter : persistenceParameters)
		{
			String name = persistenceParameter.getName();
			if (name.equals(SaveDataParameterName)){
				byte[] bytes = (byte[])persistenceParameter.getValue();
				MGPlayerWing wing = wingReadWrite.fromBytes(bytes);
				if(null != wing) {
					MGPlayerWingRef playerWingRef = wing.getPlayerWingRef();
					playerWing.setPlayerWingRef(playerWingRef);
					playerWing.setExp(wing.getExp());
					wingComponent.getWingManager().setPlayerWing(playerWing);
					MGWingEffectMgr wingEffectMgr = new MGWingEffectMgr(player);
					wingEffectMgr.restore(playerWing);
					
				}
			}
		}
	}

}
