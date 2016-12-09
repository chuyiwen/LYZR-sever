package newbee.morningGlory.mmorpg.player.talisman;

import java.util.HashMap;
import java.util.Map;

public class MGTalismanStatistics {
	
	public static final long Default_Talisman_Record = 0;
	
	public static final byte Total_AtkSpeedPer = 1;
	public static final byte Total_MoveSpeedPer = 2;
	public static final byte Total_Gold = 3;
	public static final byte Total_Exp = 4;
	public static final byte Total_BaoXiang = 5;
	public static final byte Total_ShenQiExp = 6;
	public static final byte Total_Benumb = 7;
	public static final byte Total_Hurt = 8;
	public static final byte Total_Revive= 9;
	public static final byte Total_Message = 10;

	private Map<Byte,Long> statistics = new HashMap<>(10);
	
	public MGTalismanStatistics() {
		for(byte i=1 ;i <= 10 ; i++ ){
			statistics.put(i, MGTalismanStatistics.Default_Talisman_Record);
		}
	}
	
	public void addTalismanStatistics(byte type,long value){
		if(value < 0){
			return;
		}
		long total = statistics.get(type);
		total = total + value;
		statistics.put(type, total);
	}
	
	public void subTalismanStatistics(byte type,long value){
		if(value < 0){
			return;
		}
		long total = statistics.get(type);
		total = total - value;
		statistics.put(type, total);
	}
	
	public Map<Byte,Long> getStatistics(){
		return statistics;
	}
}
