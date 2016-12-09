package newbee.morningGlory.mmorpg.player.activity.fund.persistence;

import java.util.ArrayList;
import java.util.HashMap;

import newbee.morningGlory.mmorpg.player.activity.ActivityReadWrite;
import newbee.morningGlory.mmorpg.player.activity.fund.FundActivityComponet;
import newbee.morningGlory.mmorpg.player.activity.fund.FundDataByType;
import newbee.morningGlory.mmorpg.player.activity.fund.FundType;

import org.apache.log4j.Logger;

import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.mmorpg.player.Player;

public class FundReadWrite implements ActivityReadWrite{
	private static Logger logger = Logger.getLogger(FundReadWrite.class);
	private Player player;
	private int current_version = Default_Write_Version;

	public FundReadWrite(Player player) {
		this.player = player;
	}

	public byte[] toBytes() {
		if (current_version == 10000) {
			return toBytesVer10000();
		} else {
			logger.error("写入版本没有对应写入方法");
			return null;
		}
	}

	public void fromBytes(ByteArrayReadWriteBuffer buffer) {
		int ver = buffer.readInt();
		if (ver == 10000) {
			fromBytesVer10000(buffer);
		} else {
			logger.error("读出版本没有对应读出方法");
		}
	}

	public byte[] toBytesVer10000() {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();
		FundActivityComponet fundActivityComponent = (FundActivityComponet) player.getTagged(FundActivityComponet.Tag);
		ArrayList<FundDataByType> list = new ArrayList<>(fundActivityComponent.getFundMap().values());

		buffer.writeInt(Default_Write_Version);
		buffer.writeInt(list.size());
		for (FundDataByType data : list) {
			FundType fundType = data.getType();
			buffer.writeInt(fundType.getType());
			buffer.writeLong(data.getBuyFundTime());
			byte record[] = data.getGetRewardRecord();
			if (record != null) {
				buffer.writeInt(record.length);
				for (int j = 0; j < record.length; j++)
					buffer.writeByte(record[j]);
			} else {
				buffer.writeInt(0);
			}
		}
		return buffer.getData();
	}

	public void fromBytesVer10000(ByteArrayReadWriteBuffer buffer) {
		FundActivityComponet fundActivityComponent = (FundActivityComponet) player.getTagged(FundActivityComponet.Tag);
		HashMap<FundType, FundDataByType> map = fundActivityComponent.getFundMap();

		int size = buffer.readInt();
		for (int i = 0; i < size; i++) {
			int readint = buffer.readInt();
			FundType type = FundType.getFundType(readint);
			if (type == null) {
				logger.error("type为空 找不到：" + readint);
				break;
			}
			FundDataByType data = new FundDataByType(type);
			data.setBuyFundTime(buffer.readLong());
			int len = buffer.readInt();
			byte record[] = new byte[len];
			for (int j = 0; j < len; j++) {
				record[j] = buffer.readByte();
			}
			data.setGetRewardRecord(record);
			map.put(type, data);
		}
	}

}
