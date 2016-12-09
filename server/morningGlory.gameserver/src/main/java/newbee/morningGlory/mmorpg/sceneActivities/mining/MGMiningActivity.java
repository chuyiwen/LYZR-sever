package newbee.morningGlory.mmorpg.sceneActivities.mining;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import newbee.morningGlory.mmorpg.player.activity.mining.MGPlayerMiningComponent;
import newbee.morningGlory.mmorpg.player.activity.mining.event.G2C_Mining_Open;
import newbee.morningGlory.mmorpg.player.activity.mining.event.G2C_Mining_RemainTime;
import newbee.morningGlory.mmorpg.player.activity.mining.event.MGMiningEventDefines;
import newbee.morningGlory.mmorpg.sceneActivities.SceneActivity;
import newbee.morningGlory.mmorpg.sceneActivities.SceneActivityRef;
import newbee.morningGlory.mmorpg.sceneActivities.mining.ref.MGMiningRefConfigRef;

import org.apache.log4j.Logger;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.gameArea.GameArea;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.chat.sysytem.SystemPromptFacade;
import sophia.mmorpg.world.ActionEventFacade;

public class MGMiningActivity extends SceneActivity {
	private static Logger logger = Logger.getLogger(MGMiningActivity.class);

	private static final int Default_Hour_Interval = 1;
	
	private static final long Max_Mining_Interval = Default_Hour_Interval * 3600 * 1000L;
	
	public MGMiningRefConfigRef getMGMiningRefConfigRef() {
		return getRef().getComponentRef(MGMiningRefConfigRef.class);
	}

	@Override
	public boolean checkEnter(Player player) {
		GameScene fromScene = player.getCrtScene();
		if (fromScene == null) {
			return false;
		}

		if (getCrtActivityState() != SceneActivity.ACTIVITY_START) {
			logger.debug("活动未开启");
			return false;
		}
		return true;
	}

	@Override
	public boolean checkLeave(Player player) {
		return true;
	}

	@Override
	public boolean onPreStart() {
		if (logger.isDebugEnabled()) {
			logger.debug("onpreStart--preActivityState = " + getPreActivityState());
		}
		
		G2C_Mining_RemainTime res = getRemainMillsEvent();
		ActionEventFacade.sendMessageToWorld(res);
		return true;
	}

	@Override
	public boolean onPreEnd() {
		SystemPromptFacade.broadCastMiningPreEnd();
		return true;
	}

	@Override
	public boolean onStart() {
		if (logger.isDebugEnabled()) {
			logger.debug("onStart--crtActivityState = " + getCrtActivityState());
		}
		
		G2C_Mining_RemainTime millsres =  getRemainMillsEvent();
		ActionEventFacade.sendMessageToWorld(millsres);
		SystemPromptFacade.broadCastMiningStart();
		// 活动开启通知全服在线玩家
		G2C_Mining_Open res = MessageFactory.getConcreteMessage(MGMiningEventDefines.G2C_Mining_Open);
		res.setOpenState(G2C_Mining_Open.Open);
		ActionEventFacade.sendMessageToWorld(res);
		return true;
	}

	@Override 
	public boolean onCheckEnd() {
		
		GameArea gameArea = MMORPGContext.getGameAreaComponent().getGameArea();
		GameScene gameScene = gameArea.getSceneById(getRef().getSceneRefId());
		if (null == gameScene) {
			return false;
		}

		if(gameScene.getPlayerMgrComponent().getPlayerMap().isEmpty()){
			return false;
		}
		
		for (Player owner : gameScene.getPlayerMgrComponent().getPlayerMap().values()) {
			MGPlayerMiningComponent miningComponent = (MGPlayerMiningComponent) owner.getTagged(MGPlayerMiningComponent.Tag);
			miningComponent.miningEnd();
		}
		
		return true;
	}
	
	@Override
	public boolean onEnd() {
		SystemPromptFacade.broadCastMiningEnd();
		// 广播给全服在线玩家活动结束消息
		G2C_Mining_Open res = MessageFactory.getConcreteMessage(MGMiningEventDefines.G2C_Mining_Open);
		res.setOpenState(G2C_Mining_Open.Close);
		ActionEventFacade.sendMessageToWorld(res);
		
		GameArea gameArea = MMORPGContext.getGameAreaComponent().getGameArea();
		GameScene gameScene = gameArea.getSceneById(getRef().getSceneRefId());
		if (null == gameScene) {
			return false;
		}

		for (Player owner : gameScene.getPlayerMgrComponent().getPlayerMap().values()) {
			MGPlayerMiningComponent miningComponent = (MGPlayerMiningComponent) owner.getTagged(MGPlayerMiningComponent.Tag);
			miningComponent.miningEnd();
		}
		
		G2C_Mining_RemainTime remainRes = getRemainMillsEvent();
		ActionEventFacade.sendMessageToWorld(remainRes);
		
		return true;
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {

	}

	@Override
	public void handleActionEvent(ActionEventBase event) {

	}
	
	public G2C_Mining_RemainTime getRemainMillsEvent() {
		G2C_Mining_RemainTime res = MessageFactory.getConcreteMessage(MGMiningEventDefines.G2C_Mining_RemainTime);
		long startRemainMills = 0;
		long endRemainMills = 0;
		if(getPreActivityState() == ACTIVITY_PRE_START) {
			startRemainMills = getActivityRemainingStartTime();
			startRemainMills = startRemainMills < 0 ? 0 : startRemainMills;
		} 
		
		if (getCrtActivityState() == ACTIVITY_START){
			endRemainMills = getActivityRemainingEndTime();
			endRemainMills = endRemainMills < 0 ? 0 : endRemainMills;
		}
		res.setStartRemainMills(startRemainMills);
		res.setEndRemainMills(endRemainMills);
		
		if(logger.isDebugEnabled()) {
			logger.debug("PreActivityState = " + getPreActivityState() + "," + " CrtActivityState = " + getCrtActivityState());
			logger.debug("startRemainMills = " + startRemainMills + "," + "endRemainMills = " + endRemainMills);
		}
		return res;
	}

	public String getNextMiningTimeString() {
		SceneActivityRef sceneRef = getRef();
		String time = sceneRef.getDurationTime();
		String[] timeArray = time.split("&");
		String[] nextTimeArray1 = timeArray[0].split("\\|");
		String[] nextTimeArray2 = timeArray[1].split("\\|");
		
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		String now = format.format(new Date());
		if (now.compareTo(nextTimeArray1[1]) > 0 && now.compareTo(nextTimeArray2[1]) < 0) {
			return nextTimeArray2[0];
		}

		return nextTimeArray1[0];
	}

	/**
	 * 获得挖矿活动限制次数
	 * 
	 * @return
	 */
	public byte getMiningCounts() {
		return getMGMiningRefConfigRef().getLimitCount();
	}

	/**
	 * 获得挖矿活动限制等级
	 * 
	 * @return
	 */
	public int getMiningLimitLevel() {
		return getMGMiningRefConfigRef().getLevel();
	}

	/**
	 * 获得挖矿活动剩余时间
	 * 
	 * @return
	 */
	public long getLeaveTime() {
		SceneActivityRef sceneRef = getRef();
		String time = sceneRef.getDurationTime();
		String[] timeArray = time.split("&");
		String[] tmpArr = null;
		for (String tmpTime : timeArray) {
			String[] strArr = tmpTime.split("\\|");
			if (isIntervalTime(strArr)) {
				tmpArr = strArr;
				break;
			}
		}
		if (tmpArr == null) {
			return 0;
		}
		long leaveTime = readTime(tmpArr);
		return leaveTime;
	}

	private boolean isIntervalTime(String[] strArr) {
		Calendar cal = Calendar.getInstance();
		int nowHour = cal.get(Calendar.HOUR_OF_DAY);
		String[] beginStr = strArr[0].split(":");
		String[] endStr = strArr[1].split(":");
		int beginHour = Integer.parseInt(beginStr[0]);
		int endHour = Integer.parseInt(endStr[0]);
		return nowHour <= endHour && nowHour >= beginHour;
	}

	public long readTime(String[] strArr) {
		String strEndTime = strArr[1];
		long time = getTwoTimeDiffer(strEndTime, getCrtTimeStampString());
		if (time <= 0) {
			return 0;
		}
		return time;
	}

	private String getCrtTimeStampString() {
		Calendar cal = Calendar.getInstance();
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);

		return String.format("%1$d:%2$d:%3$d", hour, minute, second);
	}

	private long getTwoTimeDiffer(String bigTime, String lowTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		try {
			return (sdf.parse(bigTime).getTime() - sdf.parse(lowTime).getTime()) / 1000;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public String getCrtTimeString() {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONDAY);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int hour = cal.get(Calendar.HOUR_OF_DAY);

		String timeString = String.format("%1$d:%2$d:%3$d%4$d", year, month, day, hour);
		return timeString;
	}
	
	public boolean isTheSameMiningRound(long lastMiningMills, long currentMiningMills){
		return Math.abs(currentMiningMills - lastMiningMills) <= Max_Mining_Interval;
	}

	public void setCrtActivityState(byte temp) {
		this.crtActivityState = temp;
	}

	public void setPreActivityState(byte temp) {
		this.preActivityState = temp;
	}
	
	/**
	 * 距离活动开始时间
	 * 
	 * @return
	 */
	public long getActivityRemainingStartTime() {
		if (getPreActivityState() != ACTIVITY_PRE_START) {
			return 0;
		}
		if (getChimeList().isEmpty()) {
			return 0;
		}
		return getChimeList().get(0).getRemainStartTime();
	}
	
	/**
	 * 获取当前活动剩余时间
	 * 
	 * @return
	 */
	public long getActivityRemainingEndTime() {
		if (getCrtActivityState() != ACTIVITY_START) {
			return 0;
		}
		if (getChimeList().isEmpty()) {
			return 0;
		}
		return getChimeList().get(0).getRemainEndTime();
	}

	@Override
	public boolean onEnter(Player player) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onLeave(Player player) {
		// TODO Auto-generated method stub
		return false;
	}
}
