package newbee.morningGlory.mmorpg.operatActivities.persistence;

import org.apache.log4j.Logger;

import newbee.morningGlory.mmorpg.operatActivities.OperatActivityComponent;
import newbee.morningGlory.mmorpg.operatActivities.OperatActivityRecord;
import newbee.morningGlory.mmorpg.player.activity.ActivityReadWrite;
import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.mmorpg.player.Player;

public class OperatActivityRecordReadWrite implements ActivityReadWrite{
	private static Logger logger = Logger.getLogger(OperatActivityRecordReadWrite.class);
	private Player player;
	private int current_version = Default_Write_Version + 2;

	public OperatActivityRecordReadWrite(Player player) {
		this.player = player;
	}

	public byte[] toBytes() {
		if (current_version == 10000) {
			return toBytesVer10000();
		}else if(current_version == 10001) {
			return toBytesVer10001();
		}else if(current_version == 10002) {
			return toBytesVer10002();
		}else {
			logger.error("写入版本没有对应写入方法");
			return null;
		}
	}

	public void fromBytes(ByteArrayReadWriteBuffer buffer) {
		int ver = buffer.readInt();
		if (ver == 10000) {
			fromBytesVer10000(buffer);
		}else if(ver == 10001){
			fromBytesVer10001(buffer);
		}else if(ver == 10002){
			fromBytesVer10002(buffer);
		} else {
			logger.error("读出版本没有对应读出方法");
		}
	}

	private byte[] toBytesVer10000() {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();
		OperatActivityComponent operatActivityComponent = (OperatActivityComponent) player.getTagged(OperatActivityComponent.Tag);
		OperatActivityRecord operatActivityRecord = operatActivityComponent.getOperatActivityRecord();

		buffer.writeInt(Default_Write_Version);
		/** 首充 */
		buffer.writeByte(operatActivityRecord.getIsFirstRecharge());
		/** 充值消费 */
		buffer.writeLong(operatActivityRecord.getCrtRechargeValue());
		buffer.writeString(operatActivityRecord.getHadReceiveRechargeGiftStage());
		buffer.writeLong(operatActivityRecord.getLastTotalRechargeGiftsEndTime());
		/** 日充值 */
		buffer.writeByte(operatActivityRecord.getDayRecharge());
		buffer.writeLong(operatActivityRecord.getLastDayRechargeTime());
		buffer.writeLong(operatActivityRecord.getLastDayRechargeGiftsEndTime());
		/** 周累计消费 */
		buffer.writeLong(operatActivityRecord.getCrtWeekConsumeValue());
		buffer.writeString(operatActivityRecord.getHadReceiveWeekConsumeGiftStage());
		buffer.writeLong(operatActivityRecord.getLastWeekTotalConsumeGiftsEndTime());
		buffer.writeLong(operatActivityRecord.getLastWeekConsumeStartTime());
		/** 七日登录 */
		buffer.writeLong(operatActivityRecord.getOpenServerDate());
		buffer.writeString(operatActivityRecord.getHadSevenLoginStage());
		buffer.writeString(operatActivityRecord.getHadReceiveSevenLoginStage());

		return buffer.getData();
	}

	private void fromBytesVer10000(ByteArrayReadWriteBuffer buffer) {
		OperatActivityComponent operatActivityComponent = (OperatActivityComponent) player.getTagged(OperatActivityComponent.Tag);
		OperatActivityRecord operatActivityRecord = operatActivityComponent.getOperatActivityRecord();

		/** 首充 */
		operatActivityRecord.setIsFirstRecharge(buffer.readByte());
		/** 充值消费 */
		operatActivityRecord.setCrtRechargeValue(buffer.readInt());
		operatActivityRecord.setHadReceiveRechargeGiftStage(buffer.readString());
		operatActivityRecord.setLastTotalRechargeGiftsEndTime(buffer.readLong());
		/** 日充值 */
		operatActivityRecord.setDayRecharge(buffer.readByte());
		operatActivityRecord.setLastDayRechargeTime(buffer.readLong());
		operatActivityRecord.setLastDayRechargeGiftsEndTime(buffer.readLong());
		/** 周累计消费 */
		operatActivityRecord.setCrtWeekConsumeValue(buffer.readInt());
		operatActivityRecord.setHadReceiveWeekConsumeGiftStage(buffer.readString());
		operatActivityRecord.setLastWeekTotalConsumeGiftsEndTime(buffer.readLong());
		operatActivityRecord.setLastWeekConsumeStartTime(buffer.readLong());
		/** 七日登录 */
		operatActivityRecord.setOpenServerDate(buffer.readLong());
		operatActivityRecord.setHadSevenLoginStage(buffer.readString());
		operatActivityRecord.setHadReceiveSevenLoginStage(buffer.readString());

	}
	
	
	private byte[] toBytesVer10001() {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();
		OperatActivityComponent operatActivityComponent = (OperatActivityComponent) player.getTagged(OperatActivityComponent.Tag);
		OperatActivityRecord operatActivityRecord = operatActivityComponent.getOperatActivityRecord();

		buffer.writeInt(current_version);
		/** 首充 */
		buffer.writeByte(operatActivityRecord.getIsFirstRecharge());
		buffer.writeLong(operatActivityRecord.getFirstRechargeTime());
		/** 充值消费 */
		buffer.writeLong(operatActivityRecord.getCrtRechargeValue());
		buffer.writeString(operatActivityRecord.getHadReceiveRechargeGiftStage());
		buffer.writeLong(operatActivityRecord.getLastTotalRechargeGiftsEndTime());
		buffer.writeLong(operatActivityRecord.getLastRechargeTime());				//最近一次充值时间
		buffer.writeInt(operatActivityRecord.getCrtRechargeCount());				//当前充值的总次数
		/** 日充值 */
		buffer.writeByte(operatActivityRecord.getDayRecharge());
		buffer.writeLong(operatActivityRecord.getLastDayRechargeTime());
		buffer.writeLong(operatActivityRecord.getLastDayRechargeGiftsEndTime());
		/** 周累计消费 */
		buffer.writeLong(operatActivityRecord.getCrtWeekConsumeValue());
		buffer.writeString(operatActivityRecord.getHadReceiveWeekConsumeGiftStage());
		buffer.writeLong(operatActivityRecord.getLastWeekTotalConsumeGiftsEndTime());
		buffer.writeLong(operatActivityRecord.getLastWeekConsumeStartTime());
		/** 七日登录 */
		buffer.writeLong(operatActivityRecord.getOpenServerDate());
		buffer.writeString(operatActivityRecord.getHadSevenLoginStage());
		buffer.writeString(operatActivityRecord.getHadReceiveSevenLoginStage());
		
		/** 累计消费的元宝和绑元 */
		
		buffer.writeLong(operatActivityRecord.getCrtTotalConsumeBindedGold());		//累计消费的总绑定元宝数
		buffer.writeLong(operatActivityRecord.getCrtTotalConsumeValue());			//累计消费的总元宝数
		
		return buffer.getData();
	}

	private void fromBytesVer10001(ByteArrayReadWriteBuffer buffer) {
		OperatActivityComponent operatActivityComponent = (OperatActivityComponent) player.getTagged(OperatActivityComponent.Tag);
		OperatActivityRecord operatActivityRecord = operatActivityComponent.getOperatActivityRecord();

		/** 首充 */
		operatActivityRecord.setIsFirstRecharge(buffer.readByte());
		operatActivityRecord.setFirstRechargeTime(buffer.readLong());
		/** 充值消费 */
		operatActivityRecord.setCrtRechargeValue(buffer.readLong());
		operatActivityRecord.setHadReceiveRechargeGiftStage(buffer.readString());
		operatActivityRecord.setLastTotalRechargeGiftsEndTime(buffer.readLong());
		operatActivityRecord.setLastRechargeTime(buffer.readLong());
		operatActivityRecord.setCrtRechargeCount(buffer.readInt());
		/** 日充值 */
		operatActivityRecord.setDayRecharge(buffer.readByte());
		operatActivityRecord.setLastDayRechargeTime(buffer.readLong());
		operatActivityRecord.setLastDayRechargeGiftsEndTime(buffer.readLong());
		/** 周累计消费 */
		operatActivityRecord.setCrtWeekConsumeValue(buffer.readLong());
		operatActivityRecord.setHadReceiveWeekConsumeGiftStage(buffer.readString());
		operatActivityRecord.setLastWeekTotalConsumeGiftsEndTime(buffer.readLong());
		operatActivityRecord.setLastWeekConsumeStartTime(buffer.readLong());
		/** 七日登录 */
		operatActivityRecord.setOpenServerDate(buffer.readLong());
		operatActivityRecord.setHadSevenLoginStage(buffer.readString());
		operatActivityRecord.setHadReceiveSevenLoginStage(buffer.readString());

		operatActivityRecord.setCrtTotalConsumeBindedGold(buffer.readLong());
		operatActivityRecord.setCrtTotalConsumeValue(buffer.readLong());
	}
	
	private byte[] toBytesVer10002() {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();
		OperatActivityComponent operatActivityComponent = (OperatActivityComponent) player.getTagged(OperatActivityComponent.Tag);
		OperatActivityRecord operatActivityRecord = operatActivityComponent.getOperatActivityRecord();

		buffer.writeInt(current_version);
		/** 首充 */
		/** 充值消费 */
		buffer.writeString(operatActivityRecord.getHadReceiveRechargeGiftStage());
		buffer.writeLong(operatActivityRecord.getLastTotalRechargeGiftsEndTime());
		/** 日充值 */
		buffer.writeByte(operatActivityRecord.getDayRecharge());
		buffer.writeLong(operatActivityRecord.getLastDayRechargeGiftsEndTime());
		/** 周累计消费 */
		buffer.writeString(operatActivityRecord.getHadReceiveWeekConsumeGiftStage());
		buffer.writeLong(operatActivityRecord.getLastWeekTotalConsumeGiftsEndTime());
		buffer.writeLong(operatActivityRecord.getLastWeekConsumeStartTime());
		/** 七日登录 */
		buffer.writeLong(operatActivityRecord.getOpenServerDate());
		buffer.writeString(operatActivityRecord.getHadSevenLoginStage());
		buffer.writeString(operatActivityRecord.getHadReceiveSevenLoginStage());

		return buffer.getData();
	}

	private void fromBytesVer10002(ByteArrayReadWriteBuffer buffer) {
		OperatActivityComponent operatActivityComponent = (OperatActivityComponent) player.getTagged(OperatActivityComponent.Tag);
		OperatActivityRecord operatActivityRecord = operatActivityComponent.getOperatActivityRecord();

		/** 首充 */
		/** 充值消费 */
		operatActivityRecord.setHadReceiveRechargeGiftStage(buffer.readString());
		operatActivityRecord.setLastTotalRechargeGiftsEndTime(buffer.readLong());
		/** 日充值 */
		operatActivityRecord.setDayRecharge(buffer.readByte());
		operatActivityRecord.setLastDayRechargeGiftsEndTime(buffer.readLong());
		/** 周累计消费 */
		operatActivityRecord.setHadReceiveWeekConsumeGiftStage(buffer.readString());
		operatActivityRecord.setLastWeekTotalConsumeGiftsEndTime(buffer.readLong());
		operatActivityRecord.setLastWeekConsumeStartTime(buffer.readLong());
		/** 七日登录 */
		operatActivityRecord.setOpenServerDate(buffer.readLong());
		operatActivityRecord.setHadSevenLoginStage(buffer.readString());
		operatActivityRecord.setHadReceiveSevenLoginStage(buffer.readString());

	}
	
}
