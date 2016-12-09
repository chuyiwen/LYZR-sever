package newbee.morningGlory.mmorpg.player.dailyQuest.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import newbee.morningGlory.mmorpg.player.dailyQuest.MGDailyQuest;
import newbee.morningGlory.mmorpg.player.dailyQuest.MGDailyQuestManager;
import sophia.foundation.data.AbstractPersistenceObject;
import sophia.foundation.data.PersistenceParameter;
import sophia.foundation.util.ByteArrayReadWriteBuffer;

public class DailyQuestPersistenceObject extends AbstractPersistenceObject {
	private MGDailyQuestManager owner;
	
	public static final byte Json_Data = 1;
	
	public static final byte Bytes_Data = 2;
	
	private static final String SaveFormatParameterName = "saveFormat";
	
	private static final Byte saveFormatParameterValue = Bytes_Data;
	
	private static final String SaveDataParameterName = "dailyQuestData";
	
	private PersistenceParameter DailyquestDataPersistenceParameter = new PersistenceParameter();
	
	private final DailyQuestPersistenceReadWrite dailyReadWrite = new DailyQuestPersistenceReadWrite();
	
	public DailyQuestPersistenceObject(MGDailyQuestManager owner) {
		this.owner = owner;
		this.persistenceParameters = new ArrayList<>(1);
		// persistenceParameters.add(new PersistenceParameter(SaveFormatParameterName, saveFormatParameterValue));
		DailyquestDataPersistenceParameter.setName(SaveDataParameterName);
		persistenceParameters.add(DailyquestDataPersistenceParameter);
	}
	
	@Override	
	public void snapshot() {
		List<MGDailyQuest> crtQuestList = owner.getCrtQuestList();
		ByteArrayReadWriteBuffer br = new ByteArrayReadWriteBuffer();
		int count = crtQuestList.size();
		br.writeInt(count);
		for (MGDailyQuest dailyQuest : crtQuestList){
			byte[] dqbyte = dailyReadWrite.toBytes(dailyQuest);
			br.writeInt(dqbyte.length);
			br.writeBytes(dqbyte);
		}
		DailyquestDataPersistenceParameter.setValue(br.getData());
	}
	
	@Override
	public void setDataFrom(Collection<PersistenceParameter> persistenceParameters) {
		for(PersistenceParameter persistenceParameter : persistenceParameters)
		{
			String name = persistenceParameter.getName();
			if (name.equals(SaveDataParameterName)){
				owner.getCrtQuestList().clear();
				byte[] bytes = (byte[])persistenceParameter.getValue();
				ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer(bytes);
				int size = buffer.readInt();
				for (int i = 0; i < size;i++){
					int len = buffer.readInt();
					MGDailyQuest crtQuest = dailyReadWrite.fromBytes(buffer.readBytes(len));
					owner.addCrtQuest(crtQuest);
				}
			}
		}
	}
}
