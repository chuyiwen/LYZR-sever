/**
 * 
 */
package sophia.foundation.util;

import java.util.Calendar;
import java.util.Date;


public class DateWrapper {

	public static long getDelayTime(Date currentDate,int dayOfWeek,int hourOfDay)
	{
		Calendar crtCalendar=Calendar.getInstance();
		
		crtCalendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
		crtCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
		crtCalendar.set(Calendar.MINUTE, 0);
		crtCalendar.set(Calendar.SECOND, 0);
		
		Date crtDate=crtCalendar.getTime();
		
		if(crtDate.before(currentDate))
		{
			crtDate=addDay(crtDate,7);
		}
		
		long delay =crtDate.getTime()- currentDate.getTime();
		
		return delay;
	}
	
	private static final Date addDay(Date date,int dayNum)
	{
		Calendar oldCanlendar=Calendar.getInstance();
		oldCanlendar.setTime(date);
		oldCanlendar.add(Calendar.DAY_OF_MONTH, dayNum);
		return oldCanlendar.getTime();
	}
	
}
