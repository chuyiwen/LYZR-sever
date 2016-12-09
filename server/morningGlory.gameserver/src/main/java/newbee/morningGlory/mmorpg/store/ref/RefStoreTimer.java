package newbee.morningGlory.mmorpg.store.ref;

import newbee.morningGlory.MorningGloryContext;
import sophia.mmorpg.core.timer.SFTimeChimeListener;

public class RefStoreTimer implements SFTimeChimeListener {

	private float kind;
	
	public RefStoreTimer(float kind) {
		this.kind = kind;
	}

	@Override
	public void handleServiceShutdown() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleTimeChimeCancel() {
		// TODO Auto-generated method stub
	}

	@Override
	public void handleTimeChime() {
		StoreItemRefMgr refMgr = MorningGloryContext.getStoreItemRefMgr();
		refMgr.resetAllLimitCountByKind((int)kind);
		refMgr.resetPersonalLimitCountByKind((int)kind);
		refMgr.chackIfTimeOut();
	}

}
