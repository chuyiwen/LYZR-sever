/**
 *   Copyright 2013-2015 Sophia
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
*/
package newbee.morningGlory.ref.loader.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import newbee.morningGlory.mmorpg.sceneActivities.SceneActivityRef;
import newbee.morningGlory.mmorpg.sceneActivities.chime.Chime;
import newbee.morningGlory.mmorpg.sceneActivities.chime.DateChime;
import newbee.morningGlory.mmorpg.sceneActivities.chime.DateWeekChime;
import newbee.morningGlory.mmorpg.sceneActivities.chime.DayChime;
import newbee.morningGlory.mmorpg.sceneActivities.chime.WeekChime;
import newbee.morningGlory.ref.loader.AbstractGameRefObjectLoader;

import com.google.gson.JsonObject;

public class AbstractSceneActivityRefLoader extends AbstractGameRefObjectLoader<SceneActivityRef> {
	
	public AbstractSceneActivityRefLoader() {}
	
	public AbstractSceneActivityRefLoader(String jsonKey) {
		super(jsonKey);
	}
	
	@Override
	protected SceneActivityRef create() {
		return new SceneActivityRef();
	}

	@Override
	protected void fillNonPropertyDictionary(SceneActivityRef ref, JsonObject refData) {
		String sceneRefId = refData.get("sceneRefId").getAsString();
		JsonObject timeJsonObject = refData.get("time").getAsJsonObject();
		String durationTime = timeJsonObject.get("duration").getAsString();
		int preStartTime = timeJsonObject.get("preStartTime").getAsInt();
		int preEndTime = timeJsonObject.get("preEndTime").getAsInt();
		List<Chime> chimeList = parseDurationTime(durationTime);
		ref.setDurationTime(durationTime);
		ref.setChimeList(chimeList);
		ref.setSceneRefId(sceneRefId);
		ref.setPreStartTime(preStartTime);
		ref.setPreEndTime(preEndTime);
		super.fillNonPropertyDictionary(ref, refData);
	}
	
	public List<Chime> parseDurationTime(String durationTime) {
		if(durationTime==null || durationTime.equals(""))
			return null;
		
		List<Chime> chimeList = new ArrayList<>();
		// WeekChime/DateWeekChime 格式  1|12:00:00|18:00:00
		if (durationTime.indexOf("|") <= 1) {
			// 多个时间
			String[] strArr = durationTime.split("&");
			for (String s : strArr) {	
				String[] subStrArr = s.split("\\|");
				String strStartTime = subStrArr[1];
				
				Chime chime;
				if (strStartTime.contains("-")) {
					chime = readDateWeekChime(subStrArr);
				} else {
					chime = readWeekChime(subStrArr);
				}
				
				if (chime != null) {
					chimeList.add(chime);
				}
			}
		}
		// DateChime 格式   2013-01-20 18:00:00|2013-01-20 22:00:00
		else if (durationTime.contains("-")) {
			String[] strArr = durationTime.split("&");
			for (String s : strArr) {
				String[] subStr = s.split("\\|");
				Chime chime = readDateChime(subStr);
				if (chime != null) {
					chimeList.add(chime);
				}
			}
		}
		// DayChime 格式 	18:00:00|22:00:00
		else {
			String[] strArr = durationTime.split("&");
			for (String s : strArr) {
				String[] subStr = s.split("\\|");
				Chime chime = readDayChime(subStr);
				if (chime != null) {
					chimeList.add(chime);
				}
			}
		}
		
		return chimeList;
	}
	
	public static Chime readDateWeekChime(String[] strArr) {
		byte dayInWeek = Byte.parseByte(strArr[0]);
		String strStartTime = strArr[1];
		String strEndTime = strArr[2];
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date startTime = dateFormat.parse(strStartTime);
			Date endTime = dateFormat.parse(strEndTime);
			return new DateWeekChime(dayInWeek, startTime.getTime(), endTime.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public static Chime readWeekChime(String[] strArr) {
		byte dayInWeek = Byte.parseByte(strArr[0]);
		String strStartTime = strArr[1];
		String strEndTime = strArr[2];
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		
		try {
			Date startTime = dateFormat.parse(strStartTime);
			Date endTime = dateFormat.parse(strEndTime);
			Calendar startCalendar = Calendar.getInstance();
			startCalendar.setTime(startTime);
			Calendar endCalendar = Calendar.getInstance();
			endCalendar.setTime(endTime);
			return new WeekChime(dayInWeek, startCalendar, endCalendar);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static Chime readDateChime(String[] strArr) {
		String strStartTime = strArr[0];
		String strEndTime = strArr[1];
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		try {
			Date startTime = dateFormat.parse(strStartTime);
			Date endTime = dateFormat.parse(strEndTime);
			return new DateChime(startTime.getTime(), endTime.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public Chime readDayChime(String[] strArr) {
		String strStartTime = strArr[0];
		String strEndTime = strArr[1];
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		
		try {
			Date startTime = dateFormat.parse(strStartTime);
			Date endTime = dateFormat.parse(strEndTime);
			Calendar startCalendar = Calendar.getInstance();
			startCalendar.setTime(startTime);
			Calendar endCalendar = Calendar.getInstance();
			endCalendar.setTime(endTime);
			return new DayChime(startCalendar, endCalendar);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
