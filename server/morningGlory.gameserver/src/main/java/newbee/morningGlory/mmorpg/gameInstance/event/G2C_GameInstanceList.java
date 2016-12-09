package newbee.morningGlory.mmorpg.gameInstance.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_GameInstanceList extends ActionEventBase {

	private String[] crtGameInstanceIds;
	private int[] countsInDays;// 当天剩余的次数
	private int[] countsInWeeks;// 当周剩余的次数
	private String[] crtGameInstanceRefIds;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		int length = crtGameInstanceIds.length;
		buffer.put((byte) length);
		for (int i = 0; i < length; i++) {
			putString(buffer, crtGameInstanceRefIds[i]);
			putString(buffer, crtGameInstanceIds[i]);
			buffer.put((byte)countsInDays[i]);
			buffer.putShort((short)countsInWeeks[i]);
		}
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		byte length = buffer.get();
		crtGameInstanceIds = new String[length];
		crtGameInstanceRefIds = new String[length];
		countsInDays = new int[length];
		countsInWeeks = new int[length];
		for (int i = 0; i < length; i++) {
			crtGameInstanceRefIds[i] = getString(buffer);
			crtGameInstanceIds[i] = getString(buffer);
			countsInDays[i] = buffer.getInt();
			countsInWeeks[i] = buffer.getInt();
		}
	}

	public void setCrtGameInstanceIds(String[] crtGameInstanceIds) {
		this.crtGameInstanceIds = crtGameInstanceIds;
	}

	public void setCountsInDays(int[] countsInDays) {
		this.countsInDays = countsInDays;
	}

	public void setCountsInWeeks(int[] countsInWeeks) {
		this.countsInWeeks = countsInWeeks;
	}

	public String[] getCrtGameInstanceIds() {
		return crtGameInstanceIds;
	}

	public int[] getCountsInDays() {
		return countsInDays;
	}

	public int[] getCountsInWeeks() {
		return countsInWeeks;
	}

	public String[] getCrtGameInstanceRefIds() {
		return crtGameInstanceRefIds;
	}

	public void setCrtGameInstanceRefIds(String[] crtGameInstanceRefIds) {
		this.crtGameInstanceRefIds = crtGameInstanceRefIds;
	}

}
