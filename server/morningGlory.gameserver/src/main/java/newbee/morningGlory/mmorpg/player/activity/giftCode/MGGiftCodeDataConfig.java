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
package newbee.morningGlory.mmorpg.player.activity.giftCode;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

public final class MGGiftCodeDataConfig  {
	
	public static final String GiftCode_Id = "MGGiftCodeDataConfig_Id";
	

	private static Set<MGGiftCodeDataTypeRef> giftCodeSet = new HashSet<MGGiftCodeDataTypeRef>();

	public static Set<MGGiftCodeObject> dataSet = Collections.newSetFromMap(new ConcurrentHashMap<MGGiftCodeObject, Boolean>());
	
	public MGGiftCodeDataConfig() {
		
	}

	public static void addGiftCodeRef(MGGiftCodeDataTypeRef ref){
		giftCodeSet.add(ref);
	}
	
	public static Set<MGGiftCodeDataTypeRef> getGiftCodeSet(){
		return giftCodeSet;
	}
	
	public static int getKeyCodeCount(String keyCode){
		int count = 0;
		for(MGGiftCodeObject obj : dataSet){
			if(StringUtils.equals(keyCode, obj.getKeyCode())){
				count++;
			}
		}
		return count;
	}
	public static int getGroupAndNameCount(String groupId,String name){
		int count = 0;
		for(MGGiftCodeObject obj : dataSet){
			if(StringUtils.equals(groupId, obj.getGroupId())&&StringUtils.equals(name, obj.getName())){
				count++;
			}
		}
		return count;
	}
	public static int getKeyCodeAndNameCount(String keyCode,String name){
		int count = 0;
		for(MGGiftCodeObject obj : dataSet){
			if(StringUtils.equals(keyCode, obj.getKeyCode())&&StringUtils.equals(name, obj.getName())){
				count++;
			}
		}
		return count;
	}
	
}
