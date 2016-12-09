package newbee.morningGlory.mmorpg.player.activity.discount;

import java.util.LinkedList;

public class BatchMgr {
	private static LinkedList<Short> batchList = new LinkedList<Short>();

	public static LinkedList<Short> getBatchList() {
		return batchList;
	}

	public static void setBatchList(LinkedList<Short> batchList) {
		BatchMgr.batchList = batchList;
	}

	public static short getNextBatch() {
		LinkedList<Short> batchList = getBatchList();
		short batch = batchList.removeFirst();
		batchList.addLast(batch);

		return batch;
	}
}
