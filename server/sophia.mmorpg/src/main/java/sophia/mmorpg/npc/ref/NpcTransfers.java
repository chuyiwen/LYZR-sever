package sophia.mmorpg.npc.ref;

import java.util.List;

import sophia.foundation.communication.core.ActionEventBase;

public final class NpcTransfers implements NpcJob{
	private List<SingleTransfer> transferData;//传送数据
	
	/**
	 * 根据ID返回传送数据
	 * @param id
	 * @return
	 */
	public SingleTransfer getTransferEntry(int id)
	{
		for(SingleTransfer transfer : this.transferData)
		{
			if (transfer.getId() == id)
				return transfer;
		}
		return null;
	}
	
	/**
	 * 返回传送列表
	 * @return
	 */
	public List<SingleTransfer> getTransferData() {
		return transferData;
	}

	public void setTransferData(List<SingleTransfer> transferData) {
		this.transferData = transferData;
	}

	@Override
	public int compareTo(NpcJob o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Short getJobType() {
		return JobType.Job_Type_Transfer;
	}

	@Override
	public int priority() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void doJob(ActionEventBase actionEvent) {
		// TODO Auto-generated method stub
		
	}
}
