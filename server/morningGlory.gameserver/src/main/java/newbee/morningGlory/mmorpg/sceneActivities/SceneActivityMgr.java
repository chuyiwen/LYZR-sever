package newbee.morningGlory.mmorpg.sceneActivities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.core.timer.SFTimeChimeListener;
import sophia.mmorpg.core.timer.SFTimeUnit;
import sophia.mmorpg.core.timer.SFTimer;
import sophia.mmorpg.core.timer.SFTimerCreater;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.sceneActivities.SceneActivityMgrInterface;

public class SceneActivityMgr implements SceneActivityMgrInterface {
	
	private static final Logger logger = Logger.getLogger(SceneActivityMgr.class);
	
	private static Map<SceneActivityType, SceneActivity> activityMap = new HashMap<>();
	
	// sceneRefId, sceneActivity
	private static Map<String, SceneActivity> sceneToActivity = new HashMap<>();
	
	private static long lastTickTime;
	
	private static SceneActivityMgr instance = new SceneActivityMgr();
	
	public static SceneActivityMgr getInstance() {
		return instance;
	}
	
	private static List<SFTimer> sfTimer = new ArrayList<>();
	
	public static void initialize() {
		SceneActivityType[] activites = SceneActivityType.values();
		for (SceneActivityType type : activites) {
			load(type);
		}
		
		lastTickTime = System.currentTimeMillis();
		SFTimerCreater timerCreater = MMORPGContext.getTimerCreater();
		sfTimer.add(timerCreater.secondInterval(new SFTimeChimeListener() {
			@Override
			public void handleTimeChimeCancel() {
			}
			
			@Override
			public void handleTimeChime() {
				// 5s tick once
				long now = System.currentTimeMillis(); 
				if (now - lastTickTime >= 5000) {
					lastTickTime = now;
					checkEnd();
					checkBegin();
				}
			}
			
			@Override
			public void handleServiceShutdown() {
			}
		}));
		
		// 每天零点报时
		sfTimer.add(timerCreater.calendarChime(new SFTimeChimeListener() {
			@Override
			public void handleTimeChimeCancel() {
			}
			
			@Override
			public void handleTimeChime() {
				refreshChime();
			}
			
			@Override
			public void handleServiceShutdown() {
			}
		}, SFTimeUnit.HOUR, 0));
	}
	
	public static void destoryTimer() {
		for (SFTimer timer : sfTimer) {
			if (timer != null) {
				timer.cancel();
			}
		}
	}
	
	public static void update(SceneActivityType type) {
		Class<? extends SceneActivity> clazz = type.getClazz();
		if (clazz != null) {
			try {
				//clazz = (Class<? extends SceneActivity>) GameRoot.classFactory.reloadClass(clazz.getName());
				type.setClazz(clazz);
				activityMap.put(type, clazz.newInstance());
			} catch (Exception e) {
				logger.error("", e);
			}
		}
	}
	
	public static void load(SceneActivityType type) {
		SceneActivityMgr.update(type);
		SceneActivity sceneActivity = activityMap.get(type);
		sceneActivity.setActivityType(type);
		sceneActivity.refreshChime();
		String sceneRefId = sceneActivity.getRef().getSceneRefId();
		String[] sceneStrArray = sceneRefId.split("\\|");
		for(int i=0;i<sceneStrArray.length;i++){
			sceneToActivity.put(sceneStrArray[i], sceneActivity);
		}
	}
	
	public static void refreshChime() {
		for (SceneActivity sceneActivity : activityMap.values()) {
			sceneActivity.refreshChime();
		}
	}
	
	public static void checkBegin() {
		Calendar crtCalendar = Calendar.getInstance();
		for (SceneActivity sceneActivity : activityMap.values()) {
			sceneActivity.checkBegin(crtCalendar);
		}
	}
	
	public static void checkEnd() {
		Calendar crtCalendar = Calendar.getInstance();
		for (SceneActivity sceneActivity : activityMap.values()) {
			sceneActivity.checkEnd(crtCalendar);
		}
		
	}
	
	public static boolean checkEnter(Player player, String sceneRefId) {
		SceneActivity sceneActivity = sceneToActivity.get(sceneRefId);
		if (sceneActivity == null) {
			return true;
		}
		
		return sceneActivity.checkEnter(player);
	}
	
	public static boolean checkLeave(Player player, String sceneRefId) {
		SceneActivity sceneActivity = sceneToActivity.get(sceneRefId);
		if (sceneActivity == null) {
			return true;
		}
		
		return sceneActivity.checkLeave(player);
	}	
	
	public static void handleGameEvent(GameEvent<?> event) {
		for (SceneActivity sceneActivity : activityMap.values()) {
			sceneActivity.handleGameEvent(event);
		}
	}
	
	public static void handleActionEvent(ActionEventBase event) {
		for (SceneActivity sceneActivity : activityMap.values()) {
			sceneActivity.handleActionEvent(event);
		}
	}
	
	@Override
	public SceneActivity getSceneAcitityBySceneRefId(String key){
		return sceneToActivity.get(key);
	}
	
	public SceneActivity getSceneAcitityByType(SceneActivityType type){
		return activityMap.get(type);
	}
}
