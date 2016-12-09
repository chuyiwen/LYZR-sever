package newbee.morningGlory.mmorpg.union;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import newbee.morningGlory.mmorpg.player.unionGameInstance.UnionGameInstanceMgr;
import newbee.morningGlory.mmorpg.sceneActivities.castleWar.CastleWarApplyMgr;
import newbee.morningGlory.mmorpg.union.persistence.MGUnionDAO;

import org.apache.log4j.Logger;

import sophia.game.component.AbstractComponent;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.core.timer.SFTimeChimeListener;
import sophia.mmorpg.core.timer.SFTimeUnit;
import sophia.mmorpg.core.timer.SFTimer;
import sophia.mmorpg.core.timer.SFTimerCreater;

public class UnionSystemComponent extends AbstractComponent {

	private static Logger logger = Logger.getLogger(UnionSystemComponent.class);
	private final MGUnionMgr unionMgr = MGUnionMgr.getInstance();
	private UnionGameInstanceMgr unionGameInstanceMgr = new UnionGameInstanceMgr();
	private SFTimer gameInstanceOpenTimer;
	private SFTimer gameInstanceCloseTimer;

	@Override
	public void ready() {

		SFTimerCreater timerCreater = MMORPGContext.getTimerCreater();
		gameInstanceOpenTimer = timerCreater.calendarChime(new SFTimeChimeListener() {

			@Override
			public void handleTimeChimeCancel() {

			}

			@Override
			public void handleTimeChime() {				
				unionGameInstanceMgr.open();
			}

			@Override
			public void handleServiceShutdown() {

			}
		}, SFTimeUnit.HOUR, 15);
		gameInstanceCloseTimer = timerCreater.calendarChime(new SFTimeChimeListener() {

			@Override
			public void handleTimeChimeCancel() {

			}

			@Override
			public void handleTimeChime() {
				unionGameInstanceMgr.close();
				
			}

			@Override
			public void handleServiceShutdown() {

			}
		}, SFTimeUnit.HOUR, 16);

		if (logger.isDebugEnabled()) {
			logger.debug(" GameInstnceTick was running.");
			logger.debug(" GameInstnceCacheTick was running");
		}

	}

	@Override
	public void suspend() {
		if (gameInstanceOpenTimer != null) {
			gameInstanceOpenTimer.cancel();
		}

		if (gameInstanceCloseTimer != null) {
			gameInstanceCloseTimer.cancel();
		}
		if (logger.isDebugEnabled()) {
			logger.debug(" GameInstnceTick was terminated.");
			logger.debug(" GameInstnceCacheTick was terminated.");
		}
	}

	
	public MGUnionMgr getUnionMgr() {
		return unionMgr;
	}

	public UnionGameInstanceMgr getUnionGameInstanceMgr() {
		return unionGameInstanceMgr;
	}

	public void setUnionGameInstanceMgr(UnionGameInstanceMgr unionGameInstanceMgr) {
		this.unionGameInstanceMgr = unionGameInstanceMgr;
	}

	public void configUnionData() {
		UnionGameInstanceMgr applyUnionGameInstance = getUnionGameInstanceMgr();
		ConcurrentHashMap<String, MGUnion> nameToUnionMap = MGUnionDAO.getInstance().selectData();
		List<String> canWarUnion = new ArrayList<String>();
		List<MGUnion> gameInstanceUnions = new ArrayList<MGUnion>();
		for (Entry<String, MGUnion> entry : nameToUnionMap.entrySet()) {
			if (entry.getValue().isSignup() || entry.getValue().getKingCityType() == MGUnionConstant.Is_KingCity) {
				if (logger.isDebugEnabled()) {
					logger.debug("可以参战公会名字:" + entry.getKey());
					logger.debug("可以参战公会标记:" + entry.getValue().isSignup());
					logger.debug("可以参战公会王城类型:" + entry.getValue().getKingCityType());
				}
				canWarUnion.add(entry.getValue().getName());
			}
			if(entry.getValue().isApplyGameInstance()){
				gameInstanceUnions.add(entry.getValue());
			}
			unionMgr.addUnion(entry.getValue());
		}
		
		unionMgr.sortUnion();
		CastleWarApplyMgr.getInstance().setCanWarUnion(canWarUnion);
		applyUnionGameInstance.setGameInstanceUnions(gameInstanceUnions);
	}
}
