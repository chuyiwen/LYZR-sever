package newbee.morningGlory.mmorpg.player.activity.persistence;

import newbee.morningGlory.mmorpg.operatActivities.persistence.OperatActivityRecordReadWrite;
import newbee.morningGlory.mmorpg.player.activity.QuickRecharge.QuickRechargeReadWrite;
import newbee.morningGlory.mmorpg.player.activity.fund.persistence.FundReadWrite;
import newbee.morningGlory.mmorpg.player.activity.limitTimeRank.persistence.LimitTimeRankReadWrite;
import newbee.morningGlory.mmorpg.player.activity.mgr.persistence.AdvanceReadWrite;
import newbee.morningGlory.mmorpg.player.activity.mgr.persistence.LevelupReadWrite;
import newbee.morningGlory.mmorpg.player.activity.mgr.persistence.SignReadWrite;
import newbee.morningGlory.mmorpg.player.activity.mgr.persistence.TotalOnlineReadWrite;
import newbee.morningGlory.mmorpg.player.activity.mining.persistence.MiningReadWrite;
import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.persistence.AbstractPersistenceObjectReadWrite;
import sophia.mmorpg.player.persistence.PersistenceObjectReadWrite;

public class ActivityAllReadWrite extends AbstractPersistenceObjectReadWrite<Object> implements PersistenceObjectReadWrite<Object> {

	private SignReadWrite signReadWrite;
	// private LadderReadWrite ladderReadWrite;
	private LimitTimeRankReadWrite limitTimeRankReadWrite;
	private AdvanceReadWrite advanceReadWrite;
	private LevelupReadWrite levelupReadWrite;
	private TotalOnlineReadWrite totalOnlineReadWrite;
	private FundReadWrite fundReadWrite;
	private OperatActivityRecordReadWrite operatActivityRecordReadWrite;
	private MiningReadWrite mingReadWrite;
	private QuickRechargeReadWrite quickRechargeReadWrite;

	public ActivityAllReadWrite(Player player) {
		signReadWrite = new SignReadWrite(player);
		// ladderReadWrite = new LadderReadWrite(player);
		limitTimeRankReadWrite = new LimitTimeRankReadWrite(player);
		advanceReadWrite = new AdvanceReadWrite(player);
		levelupReadWrite = new LevelupReadWrite(player);
		totalOnlineReadWrite = new TotalOnlineReadWrite(player);
		fundReadWrite = new FundReadWrite(player);
		operatActivityRecordReadWrite = new OperatActivityRecordReadWrite(player);
		mingReadWrite = new MiningReadWrite(player);
		quickRechargeReadWrite = new QuickRechargeReadWrite(player);
	}

	@Override
	public byte[] toBytes(Object persistenceObject) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();
		buffer.writeBytes(signReadWrite.toBytes());

		// buffer.writeBytes(ladderReadWrite.toBytes());

		buffer.writeBytes(limitTimeRankReadWrite.toBytes());

		buffer.writeBytes(advanceReadWrite.toBytes());

		buffer.writeBytes(levelupReadWrite.toBytes());

		buffer.writeBytes(totalOnlineReadWrite.toBytes());

		buffer.writeBytes(fundReadWrite.toBytes());

		buffer.writeBytes(operatActivityRecordReadWrite.toBytes());

		buffer.writeBytes(mingReadWrite.toBytes());

		buffer.writeBytes(quickRechargeReadWrite.toBytes());

		return buffer.getData();
	}

	@Override
	public Object fromBytes(byte[] persistenceBytes) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer(persistenceBytes);

		if (buffer.hasRemaining()) {
			signReadWrite.fromBytes(buffer);
		}

		// if (buffer.hasRemaining()) {
		// ladderReadWrite.fromBytes(buffer);
		// }

		if (buffer.hasRemaining()) {
			limitTimeRankReadWrite.fromBytes(buffer);
		}

		if (buffer.hasRemaining()) {
			advanceReadWrite.fromBytes(buffer);
		}

		if (buffer.hasRemaining()) {
			levelupReadWrite.fromBytes(buffer);
		}

		if (buffer.hasRemaining()) {
			totalOnlineReadWrite.fromBytes(buffer);
		}

		if (buffer.hasRemaining()) {
			fundReadWrite.fromBytes(buffer);
		}

		if (buffer.hasRemaining()) {
			operatActivityRecordReadWrite.fromBytes(buffer);
		}

		if (buffer.hasRemaining()) {
			mingReadWrite.fromBytes(buffer);
		}
		
		if (buffer.hasRemaining()) {
			quickRechargeReadWrite.fromBytes(buffer);
		}
		return null;
	}

	@Override
	public String toJsonString(Object persistenceObject) {
		return null;
	}

	@Override
	public Object fromJsonString(String persistenceJsonString) {
		return null;
	}
}
