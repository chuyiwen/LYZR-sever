package newbee.morningGlory.mmorpg.player.funStep.persistence;

import java.util.List;

import newbee.morningGlory.mmorpg.player.funStep.FunStepComponent;
import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.mmorpg.player.Player;

public class FunStepPersistenceObject {
	private static FunStepPersistenceObject instance = new FunStepPersistenceObject();
	private static final int Default_version = 10000;
	public static FunStepPersistenceObject getInstance() {
		return instance;
	}
	
	private FunStepPersistenceObject() {
		
	}

	public byte[] ToBytes(Player player) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();
		FunStepComponent funStepComponent  = (FunStepComponent)player.getTagged(FunStepComponent.Tag);
		List<String> list = funStepComponent.getCompleteStepList();
		buffer.writeInt(Default_version);
		buffer.writeInt(list.size());
		for(String stepId : list){
			buffer.writeString(stepId);
		}
		return buffer.getData();
	}
	
	public void FromBytes(byte[] persistence,Player player) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer(persistence);
		FunStepComponent funStepComponent  = (FunStepComponent)player.getTagged(FunStepComponent.Tag);
		funStepComponent.getCompleteStepList().clear();
		int version  = buffer.readInt();
		int size = buffer.readInt();
		for(int i= 0 ;i< size;i++){
			String stepId = buffer.readString();
			funStepComponent.getCompleteStepList().add(stepId);		
		}
		
	}
}
