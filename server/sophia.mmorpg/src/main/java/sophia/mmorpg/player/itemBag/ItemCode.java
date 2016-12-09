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
package sophia.mmorpg.player.itemBag;

public final class ItemCode {
	
	private static final int UPDATE_CODE = 0;
	/** 物品更新部分**/
	public static final byte ADD_UPDATE = UPDATE_CODE + 1; //物品增加
	public static final byte DEL_UPDATE = UPDATE_CODE + 2; //物品删除
	public static final byte MODIFY_UPDATE = UPDATE_CODE + 3; //物品改写
	
	/** 锻造部分 **/
	public static final byte TOTAL_PD_UPDATE = UPDATE_CODE + 1; //总属性pd更新
	public static final byte WASH_PD_UPDATE = UPDATE_CODE + 2; //洗练属性pd更新
	
	/** 普通道具refid */
	public static final String TransferStone = "item_moveto_3";
	public static final String FlyShoes = "item_feixie";
	public static final String  JinJieShi="item_shenqiExp";
	public static final String  AllCanSuiPian = "item_suipian";
	public static final String  QiangHuaShi = "item_qianghuashi";
	public static final String MOUNT_EXP_REF_ID = "item_zuoqiExp";
	/** 属性道具 refId*/
	public static final String 	Gold_ID = "gold";
	public static final String 	UnBindedGold_ID = "unbindedGold";
	public static final String 	BindedGold_ID = "bindedGold";
	public static final String 	Exp_ID = "exp";
	public static final String 	Merit_ID = "merit";
	public static final String 	Achievement_ID = "achievement";
}
