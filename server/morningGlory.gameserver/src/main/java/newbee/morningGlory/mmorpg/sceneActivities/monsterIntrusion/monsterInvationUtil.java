package newbee.morningGlory.mmorpg.sceneActivities.monsterIntrusion;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class monsterInvationUtil {

	private static Random random = new Random();

	/**
	 * 返回随机的index，[0,size-1]
	 * 
	 * @param size
	 * @return
	 */
	public static int randomIndex(int size) {
		if (size <= 0) {
			throw new IndexOutOfBoundsException("无法获取index，数组越界！");
		}
		return random.nextInt(size);
	}

	/**
	 * 返回区间内的一个随机数（包括边界值）
	 * 
	 * @param min
	 *            区间的下限
	 * @param max
	 *            区间的上限
	 * @return 随机数
	 */
	public static int random(int min, int max) {
		if (min > max) {
			int temp = min;
			min = max;
			max = temp;
		}
		return random.nextInt(max - min + 1) + min;
	}

	public static boolean randomBoolean() {
		int var = random(1, 2);
		return var == 1;
	}

	/**
	 * 返回区间内的一个随机数（不包括边界值）
	 * 
	 * @param min
	 *            区间的下限
	 * @param max
	 *            区间的上限
	 * @return 随机数
	 */
	public static int randomWithoutMax(int min, int max) {
		if (min > max) {
			int temp = min;
			min = max;
			max = temp;
		}
		return random.nextInt(max - min - 1) + min + 1;
	}

	public static long getTwoTimeCha(String bigTime, String lowTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		try {
			return (sdf.parse(bigTime).getTime() - sdf.parse(lowTime).getTime()) / 1000;
		} catch (ParseException e) {
			e.printStackTrace();
		}// 毫秒
		return 0;
	}

	public static String getNowTime(){
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		Date d = new Date();
		return sdf.format(d);
	}
	
	public static long getNowTimeMill(){
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		Date d = new Date();
		try {
			return (sdf.parse(sdf.format(d))).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * 通过格式化获取时间的字符串（HH:mm:ss）
	 * @return
	 */
	public static String getTimeString(long time){
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		return sdf.format(time);
	}
	
	/**
	 * 通过格式化获取时间的字符串（yyyy-MM-dd HH:mm:ss）
	 * @return
	 */
	public static String getTimeString2(long time){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(time);
	}	
}
