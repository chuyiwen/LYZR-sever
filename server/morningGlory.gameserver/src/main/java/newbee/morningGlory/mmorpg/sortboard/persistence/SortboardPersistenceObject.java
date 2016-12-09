package newbee.morningGlory.mmorpg.sortboard.persistence;

import java.util.ArrayList;
import java.util.List;

import newbee.morningGlory.mmorpg.sortboard.SortboardData;
import newbee.morningGlory.mmorpg.sortboard.SortboardScoreData;
import sophia.foundation.util.ByteArrayReadWriteBuffer;

public class SortboardPersistenceObject {
	private static SortboardPersistenceObject instance = new SortboardPersistenceObject();

	public static SortboardPersistenceObject getInstance() {
		return instance;
	}
	
	private SortboardPersistenceObject() {
		
	}

	public byte[] sortboardInfoToBytes(SortboardData sortboardData) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();
		List<SortboardScoreData> scoreDatas = sortboardData.getScoreData();
		buffer.writeInt(scoreDatas.size());
		for (SortboardScoreData scoreData : scoreDatas) {
			buffer.writeString(scoreData.getPlayerId());
			buffer.writeString(scoreData.getName());
			buffer.writeInt(scoreData.getProfession());
			buffer.writeInt(scoreData.getScore());
		}
		return buffer.getData();
	}
	
	public SortboardData sortboardInfoFromBytes(byte[] sortboardData) {
		SortboardData boardData = new SortboardData();
		List<SortboardScoreData> scoreDataList = new ArrayList<>();
		ByteArrayReadWriteBuffer sortboardBuffer = new ByteArrayReadWriteBuffer(sortboardData);
		int count = sortboardBuffer.readInt();
		for (int i = 0; i < count; i++) {
			String playerId = sortboardBuffer.readString();
			String name = sortboardBuffer.readString();
			int profession = sortboardBuffer.readInt();
			int score = sortboardBuffer.readInt();
			SortboardScoreData scoreData = new SortboardScoreData(playerId, name, profession, score);
			scoreDataList.add(scoreData);
		}
		boardData.setScoreData(scoreDataList);
		return boardData;
		
	}
}
