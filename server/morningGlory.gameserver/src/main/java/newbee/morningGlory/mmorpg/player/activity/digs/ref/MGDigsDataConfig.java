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
package newbee.morningGlory.mmorpg.player.activity.digs.ref;

import java.util.Map;

import sophia.game.ref.AbstractGameRefObjectBase;

public class MGDigsDataConfig extends AbstractGameRefObjectBase {
	
	private static final long serialVersionUID = -8192192444176572654L;
	
	public static final String  DigsReward_Id = "digsReward_Id"; 
	
	public static final String  DigsType_Id = "digsType_Id"; 
	
	private Map<String,MGDigsRewardRef> digsRewardMaps ;
	
	private Map<String,MGDigsTypeRef> digsTypeMaps ;
	
	
	
	public Map<String, MGDigsRewardRef> getDigsRewardMaps() {
		return digsRewardMaps;
	}



	public void setDigsRewardMaps(Map<String, MGDigsRewardRef> digsRewardMaps) {
		this.digsRewardMaps = digsRewardMaps;
	}



	public Map<String, MGDigsTypeRef> getDigsTypeMaps() {
		return digsTypeMaps;
	}



	public void setDigsTypeMaps(Map<String, MGDigsTypeRef> digsTypeMaps) {
		this.digsTypeMaps = digsTypeMaps;
	}



	public MGDigsDataConfig() {
	}
	
	
}
