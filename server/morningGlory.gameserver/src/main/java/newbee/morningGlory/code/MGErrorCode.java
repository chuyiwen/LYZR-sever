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
package newbee.morningGlory.code;

import sophia.mmorpg.code.CodeContext;

public final class MGErrorCode {

	public static final int CODE_AUTH = 0;
	// public static final int CODE_AUTH_FAILURE_MD5 = addCode(CODE_AUTH + 1,
	// "非法请求.md5校验失败");
	// public static final int CODE_AUTH_FAILURE_TIMESTAMP = addCode(CODE_AUTH +
	// 2, "非法请求.时戳过期");
	// public static final int CODE_AUTH_FAILURE_PARAM = addCode(CODE_AUTH + 3,
	// "非法请求.参数错误");
	public static final int CODE_AUTH_END = CODE_AUTH + 3;
	
	// 角色
	public static final int CODE_PLAYER = CODE_AUTH_END;
	public static final int CODE_NAME_LENGTH_SHORTEST = addCode(CODE_PLAYER + 1, "昵称太短，昵称取2-6中文字符，4-12英文字符，区分大小写");
	public static final int CODE_NAME_LENGTH_LONGEST = addCode(CODE_PLAYER + 2, "昵称太长，昵称取2-6中文字符，4-12英文字符，区分大小写");
	public static final int CODE_NAME_ALREADY_EXSIT = addCode(CODE_PLAYER + 3, "昵称已经存在");
	public static final int CODE_PLAYER_ALREADY_EXSIT = addCode(CODE_PLAYER + 4, "角色已经创建");
	public static final int CODE_PLAYER_INVALID = addCode(CODE_PLAYER + 5, "找不到角色");
	public static final int CODE_PLAYER_PROFESSION_INVALID = addCode(CODE_PLAYER + 6, "职业非法");
	public static final int CODE_PLAYER_LIMITCOUNT = addCode(CODE_PLAYER + 7, "角色最多为3个");
	public static final int CODE_PLAYER_DISABLED = addCode(CODE_PLAYER + 8, "封号中");
	public static final int CODE_PLAYER_INVALID_CHARNAME = addCode(CODE_PLAYER + 9, "角色名含有非法字符");
	public static final int CODE_PLAYER_ONLINE_LIMIT = addCode(CODE_PLAYER + 10, "本服太火，在线人数爆棚了，请登陆其他不繁忙的服务器，谢谢!");
	public static final int CODE_PLAYER_DATAERROR = addCode(CODE_PLAYER + 11, "很抱歉，加载数据出错，请联系客服!!!");
	public static final int CODE_PLAYER_END = CODE_PLAYER + 11;
	
	public static final int CODE_INVALID = 200;
	public static final int CODE_PARAM_INVALID = addCode(CODE_INVALID + 1, "非法数据");

	// ////////////////// MorningGlory Error Code Range [2000, Short.MAX_VALUE)
	
	public static final int CODE_GM = 2000;
	public static final int CODE_GM_ERROR = addCode(CODE_GM + 1, "错误的GM指令");

	public static final int CODE_GAME_INSTANCE_BEGIN = 2100;
	public static final int CODE_GAME_INSTANCE_NOT_OPEN = addCode(CODE_GAME_INSTANCE_BEGIN + 1, "副本未开放");
	public static final int CODE_GAME_INSTANCE_NOT_NEXT_LAYER = addCode(CODE_GAME_INSTANCE_BEGIN + 2, "副本进入条件不足");
	public static final int CODE_GAME_INSTANCE_NOT_EXIST_NEXT_LAYER = addCode(CODE_GAME_INSTANCE_BEGIN + 3, "副本不存在下一层");
	public static final int CODE_GAME_INSTANCE_NOT_ENOUGH_ZHENMOLING = addCode(CODE_GAME_INSTANCE_BEGIN + 4, "镇魔令不足");
	public static final int CODE_GAME_INSTANCE_ENTER_DAY_LIMIT = addCode(CODE_GAME_INSTANCE_BEGIN + 5, "您今天进入副本的次数已满");
	public static final int CODE_GAME_INSTANCE_ENTER_WEEK_LIMIT = addCode(CODE_GAME_INSTANCE_BEGIN + 6, "您本周进入副本的次数已满");
	public static final int CODE_GAME_INSTANCE_NOT_ENOUGH_LEVEL = addCode(CODE_GAME_INSTANCE_BEGIN + 7, "您的等级不足");
	public static final int CODE_GAME_INSTANCE_NEED_ACCEPT_MAINQUEST = addCode(CODE_GAME_INSTANCE_BEGIN + 8, "需要接受主线任务才能进此副本");
	public static final int CODE_GAME_INSTANCE_NOT_CURRENT_LAYER = addCode(CODE_GAME_INSTANCE_BEGIN + 9, "当前没有在副本里面");
	public static final int CODE_GAME_INSTANCE_EXIST_MONSTER_CURRENT_LAYER = addCode(CODE_GAME_INSTANCE_BEGIN + 10, "您没有击败足够的怪物");
	public static final int CODE_GAME_INSTANCE_ALREAD_IN = addCode(CODE_GAME_INSTANCE_BEGIN + 11, "玩家已在副本中，请勿重复请求。");
	public static final int CODE_GAME_INSTANCE_NOT_EXIST = addCode(CODE_GAME_INSTANCE_BEGIN + 12, "副本不存在。");
	public static final int CODE_GAME_INSTANCE_NOT_TRANS = addCode(CODE_GAME_INSTANCE_BEGIN + 13, "当前状态无法传送。");

	// 爵位
	public static final int CODE_PEERAGE_BEGIN = 2300;
	public static final int CODE_PEERAGE_HAVENOPEERAGE = addCode(CODE_PEERAGE_BEGIN + 1, "当前玩家没有爵位等级");
	public static final int CODE_PEERAGE_ALREADYHIGHEST = addCode(CODE_PEERAGE_BEGIN + 2, "玩家当前爵位等级已经达到最高");
	public static final int CODE_PEERAGE_NOTENOUGHPEERAGELEVEL = addCode(CODE_PEERAGE_BEGIN + 3, "玩家等级不够");
	public static final int CODE_PEERAGE_NOTENOUGHMERIT = addCode(CODE_PEERAGE_BEGIN + 4, "玩家功勋不够");
	public static final int CODE_PEERAGE_ALREAYRECEIVE = addCode(CODE_PEERAGE_BEGIN + 5, "今天已经领取过奖励了");

	// 成就
	public static final int CODE_ACHIEVE_BEGIN = 2400;
	public static final int CODE_ACHIEVE_NOTCOMPLETE = addCode(CODE_ACHIEVE_BEGIN + 1, "成就还没有完成");
	public static final int CODE_ACHIEVE_ALREADYHIGHEST = addCode(CODE_ACHIEVE_BEGIN + 2, "成就已经达到最高等级");
	public static final int CODE_ACHIEVE_ALREADYGETREWARD = addCode(CODE_ACHIEVE_BEGIN + 3, "已经领取过奖励了");
	public static final int CODE_MEDAL_ALREADYOWNMEDAL = addCode(CODE_ACHIEVE_BEGIN + 4, "已经有勋章了,不能兑换");
	public static final int CODE_MEDAL_ACHIEVEPOINTNOTENOUGH = addCode(CODE_ACHIEVE_BEGIN + 5, "成就点不够");
	public static final int CODE_MEDAL_HASNOMEDAL = addCode(CODE_ACHIEVE_BEGIN + 6, "没有勋章");
	public static final int CODE_MEDAL_ALREADYHIGHEST = addCode(CODE_ACHIEVE_BEGIN + 7, "勋章已经达到最高等级");

	// 公会
	public static final int CODE_UNION_BEGIN = 2500;
	public static final int CODE_UNION_CrtPlayerNotOnLine = addCode(CODE_UNION_BEGIN + 1, "玩家不在线");
	public static final int CODE_UNION_CrtPlayerLevelToLow = addCode(CODE_UNION_BEGIN + 2, "等级不够");
	public static final int CODE_UNION_CrtPlayerItemNotEnough = addCode(CODE_UNION_BEGIN + 3, "公会令牌不足");
	public static final int CODE_UNION_CrtPlayerMoneyNotEnogh = addCode(CODE_UNION_BEGIN + 4, "金币不够");
	public static final int CODE_UNION_UnionNameTooLong = addCode(CODE_UNION_BEGIN + 5, "公会名字太长");
	public static final int CODE_UNION_UnionNameTooShort = addCode(CODE_UNION_BEGIN + 6, "公会名字太短");
	public static final int CODE_UNION_UnionNameExist = addCode(CODE_UNION_BEGIN + 7, "公会名字已经存在");
	public static final int CODE_UNION_AlreadyInOneUnion = addCode(CODE_UNION_BEGIN + 8, "已经加入公会");
	public static final int CODE_UNION_AlreadyApplyOneUnion = addCode(CODE_UNION_BEGIN + 9, "已经申请过加入公会");
	public static final int CODE_UNION_UnionIsFull = addCode(CODE_UNION_BEGIN + 10, "公会已满");
	public static final int CODE_UNION_HasNOAuthority = addCode(CODE_UNION_BEGIN + 11, "权限不够");
	public static final int CODE_UNION_ViceChairmanCountISFull = addCode(CODE_UNION_BEGIN + 12, "副会长人数达到上限，不能继续任命");
	public static final int CODE_UNION_Interface_Expired = addCode(CODE_UNION_BEGIN + 13, "用户界面过期");
	public static final int CODE_UNION_ApplyAlreadyQuit = addCode(CODE_UNION_BEGIN + 14, "申请已经取消");
	public static final int CODE_UNION_AlreadyBeKickOut = addCode(CODE_UNION_BEGIN + 15, "已经被踢出公会");
	public static final int CODE_UNION_UnionAlreadyNotExist = addCode(CODE_UNION_BEGIN + 16, "公会已解散");
	public static final int CODE_UNION_PlayerAlreadyQuit = addCode(CODE_UNION_BEGIN + 17, "已经退出公会");
	public static final int CODE_UNION_ApplyAlreadyBeHandle = addCode(CODE_UNION_BEGIN + 18, "申请已经被处理");
	public static final int CODE_UNION_CANTRepeatInvite = addCode(CODE_UNION_BEGIN + 19, "不能重复邀请");
	public static final int CODE_UNION_MessageTooLong = addCode(CODE_UNION_BEGIN + 20, "输入公告内容太长");
	public static final int CODE_UNION_UnionNameValid = addCode(CODE_UNION_BEGIN + 21, "公会名非法");
	public static final int CODE_UNION_UnionIsNotExist = addCode(CODE_UNION_BEGIN + 22, "公会不存在");
	public static final int CODE_UNION_ApplyNumberUpperLimit = addCode(CODE_UNION_BEGIN + 23, "该公会申请人员已达上限");
	public static final int CODE_UNION_InviteNumberUpperLimit = addCode(CODE_UNION_BEGIN + 24, "该玩家被邀请已达上限");

	// 商城
	public static final int CODE_STORE_BEGIN = 2800;
	public static final int CODE_STORE_BUYERROR = addCode(CODE_STORE_BEGIN + 1, "购买失败");
	public static final int CODE_STORE_TOO_FAR_AWAY = addCode(CODE_STORE_BEGIN + 2, "购买失败：超出合法购买距离");
	public static final int CODE_STORE_ERROR_STORE = addCode(CODE_STORE_BEGIN + 3, "购买失败： NPC不存在该商店");
	public static final int CODE_STORE_OUT_OF_DATE = addCode(CODE_STORE_BEGIN + 4, "购买失败： 商品已过期");
	public static final int CODE_STORE_NUM_PERSON_LIMIT = addCode(CODE_STORE_BEGIN + 5, "购买失败： 个人限购物品剩余数量不足");
	public static final int CODE_STORE_NUM_SERVER_LIMIT = addCode(CODE_STORE_BEGIN + 6, "购买失败： 全服限购物品剩余数量不足");
	public static final int CODE_STORE_WRONT_ITEM_ID = addCode(CODE_STORE_BEGIN + 7, "购买失败： 该物品不存在");
	public static final int CODE_STORE_NOT_ENOUGHT_BINDGOLD = addCode(CODE_STORE_BEGIN + 8, "购买失败： 绑定元宝不足");
	public static final int CODE_STORE_NOT_ENOUGHT_UNBINDGOLD = addCode(CODE_STORE_BEGIN + 9, "购买失败：元宝不足");
	public static final int CODE_STORE_NOT_ENOUGHT_GOLD = addCode(CODE_STORE_BEGIN + 10, "购买失败：金币不足");
	public static final int CODE_STORE_NOT_ENOUGHT_ITEM = addCode(CODE_STORE_BEGIN + 11, "购买失败：所需道具数量不足");
	public static final int CODE_STORE_DISCOUNTSHOP_SELL_OVER = addCode(CODE_STORE_BEGIN + 12, "购买失败： 淘折扣商品已经售完");
	public static final int CODE_STORE_DISCOUNTSHOP_OUT_OF_TIME = addCode(CODE_STORE_BEGIN + 13, "购买失败： 淘折扣商品活动结束");
	public static final int CODE_STORE_INVALID_PARAM = addCode(CODE_STORE_BEGIN + 14, "购买失败：非法参数");
	public static final int CODE_STORE_EXCHANGE_EXPIRED = addCode(CODE_STORE_BEGIN + 15, "礼包码已过期");
	public static final int CODE_STORE_EXCHANGE_NOT_EXIST = addCode(CODE_STORE_BEGIN + 16, "礼包码错误");

	// 礼包
	public static final int CODE_Activity_BEGIN = 2900;
	public static final int CODE_Award_AwardNOTExist = addCode(CODE_Activity_BEGIN + 1, "礼包不存在");
	public static final int CODE_Activity_LevelTooLower = addCode(CODE_Activity_BEGIN + 2, "竞技场35级开启");
	public static final int CODE_Award_AwardAlreadyBennGet = addCode(CODE_Activity_BEGIN + 3, "礼包已被领取过");
	public static final int CODE_Award_BagAlreadyFull = addCode(CODE_Activity_BEGIN + 4, "背包已满，请清理背包");
	public static final int CODE_Award_TypeError = addCode(CODE_Activity_BEGIN + 5, "礼包奖励领取错误");

	// 签到、在线时长、进阶等活动
	public static final int CODE_Sign_RepeatSameDay = addCode(CODE_Activity_BEGIN + 6, "重复签到同一天");
	public static final int CODE_Sign_NOT_ENOUGHT_GOLD = addCode(CODE_Activity_BEGIN + 7, "元宝不足");
	public static final int CODE_Sign_NONeedSign = addCode(CODE_Activity_BEGIN + 8, "本月签到已满,不能补签");
	public static final int CODE_Advanced_CantGetReward = addCode(CODE_Activity_BEGIN + 9, "没有领取可以奖励");
	public static final int CODE_LimitTimeRank_TimeNotOver = addCode(CODE_Activity_BEGIN + 10, "活动未结束,不能领奖");
	public static final int CODE_Award_DataErorr = addCode(CODE_Activity_BEGIN + 11, "非法数据");
	// 天梯
	public static final int CODE_Ladder_CDTimeNotCooling = addCode(CODE_Activity_BEGIN + 12, "CD时间未冷却");
	public static final int CODE_Ladder_ChallengeCountIsZero = addCode(CODE_Activity_BEGIN + 13, "今日挑战次数用完");
	public static final int CODE_Ladder_CDTimeNotCode = addCode(CODE_Activity_BEGIN + 14, "CD时间未冷却");
	public static final int CODE_Ladder_UnbinedGoldNotEnough = addCode(CODE_Activity_BEGIN + 15, "元宝不够");
	public static final int CODE_Ladder_InBattle = addCode(CODE_Activity_BEGIN + 16, "战斗中");
	public static final int CODE_TimeNotOver = addCode(CODE_Activity_BEGIN + 17, "倒计时未结束");
	public static final int CODE_Ladder_NotLadderMember = addCode(CODE_Activity_BEGIN + 18, "你还不是天梯成员，不能挑战");
	public static final int CODE_Sign_ValidTime = addCode(CODE_Activity_BEGIN + 19, "不能早于角色创建时间和开服时间");
	public static final int CODE_Ladder_Invalid_Challenge_Rank = addCode(CODE_Activity_BEGIN + 20, "挑战对象不存在");
	public static final int CODE_Advanced_AlreadyGet = addCode(CODE_Activity_BEGIN + 21, "已领取");
	public static final int CODE_Activity_IsOver = addCode(CODE_Activity_BEGIN + 22, "活动已结束");
	public static final int CODE_Activity_ItemBagIsFull = addCode(CODE_Activity_BEGIN + 23, "您的背包已满，请先清理背包");
	public static final int CODE_Ladder_CannotChallenge_Self = addCode(CODE_Activity_BEGIN + 24, "不能挑战自己");

	// 活动礼包
	private static final int CODE_OA = 3000;
	public static final int CODE_OA_INVALID = addCode(CODE_OA + 1, "活动礼包已失效");
	public static final int CODE_OA_CANNOT_RECEIVE = addCode(CODE_OA + 2, "礼包还不能领取");
	public static final int CODE_OA_HAD_RECEIVED = addCode(CODE_OA + 3, "礼包已经领取过了");
	public static final int CODE_OA_NO_BUY = addCode(CODE_OA + 4, "未购买无权限领取礼包");
	public static final int CODE_OA_OVERDAY = addCode(CODE_OA + 5, "时间未到");

	// 场景活动
	private static final int SCENE_ACTIVITY = 3200;
	public static final int SCENE_ACTIVITY_ENTER_NO = addCode(SCENE_ACTIVITY + 1, "没达到活动进入条件，无法进入。");

	// 攻城战
	private static final int CODE_CASTLEWAR = 3600;
	public static final int CODE_CASTLEWAR_NOT_START = addCode(CODE_CASTLEWAR + 1, "申请失败，攻城战尚未开始申请。");
	public static final int CODE_CASTLEWAR_LIST_ENOUGHT = addCode(CODE_CASTLEWAR + 2, "申请失败，本周申请攻城名额已满，请下周再来。");
	public static final int CODE_CASTLEWAR_APPLY_TWICE = addCode(CODE_CASTLEWAR + 3, "申请失败，贵公会已在申请列表中请勿重复申请。");
	public static final int CODE_CASTLEWAR_NOT_KINGCITY = addCode(CODE_CASTLEWAR + 4, "领取礼包失败，贵公会并不是王城公会，请检测后重试。");
	public static final int CODE_CASTLEWAR_GIFT_TWICE = addCode(CODE_CASTLEWAR + 5, "领取礼包失败，一天只能领取一次王城礼包。");
	public static final int CODE_CASTLEWAR_GIFT_SUCCEED = addCode(CODE_CASTLEWAR + 6, "成功领取王城礼包");
	public static final int CODE_CASTLEWAR_INSTANCE_OT = addCode(CODE_CASTLEWAR + 7, "今天进入王城副本次数已用完，请明天重试。");
	public static final int CODE_CASTLEWAR_KINGCITY = addCode(CODE_CASTLEWAR + 8, "您所属的公会是王城公会，默认参加攻城战，无需申请。");
	public static final int CODE_CASTLEWAR_NOTKINGCITY = addCode(CODE_CASTLEWAR + 9, "您所属的公会不是王城公会，无法进入王城副本。");
	public static final int CODE_CASTLEWAR_NO_UNION = addCode(CODE_CASTLEWAR + 10, "操作失败，玩家没有加入公会。");
	public static final int CODE_CASTLEWAR_NO_MONEY = addCode(CODE_CASTLEWAR + 11, "申请失败，金币不足。");
	public static final int CODE_CASTLEWAR_NO_ITEM = addCode(CODE_CASTLEWAR + 12, "申请失败，申请道具不足。");
	public static final int CODE_CASTLEWAR_STARTED = addCode(CODE_CASTLEWAR + 13, "攻城战进行中，非参战公会玩家无法进入场景，请在攻城战结束后重试。");
	public static final int CODE_CASTLEWAR_NO_KINGCITY = addCode(CODE_CASTLEWAR + 14, "攻城战即将进行，非参王城公会玩家，无法进入。");
	public static final int CODE_CASTLEWAR_STARTED_CANT_APPLY = addCode(CODE_CASTLEWAR + 15, "攻城战正在进行，无法申请参战。");
	public static final int CODE_CASTLEWAR_CANT_GET_GIFT = addCode(CODE_CASTLEWAR + 16, "攻城战正在进行，无法领取王城礼包。");

	private static final int CODE_INVASIOIN = 3700;
	public static final int CODE_BUFF_BETTER = addCode(CODE_INVASIOIN + 1, "您已附加了更好BUFF效果");
	public static final int CODE_INVASIOIN_WRONGTIME = addCode(CODE_INVASIOIN + 2, "非场景活动时间，请在下一次怪物入侵时请求。");
	public static final int CODE_INVASIOIN_ALREAD_IN = addCode(CODE_INVASIOIN + 3, "您已在活动场景内，请勿重复请求。");
	public static final int CODE_INVASIOIN_NOT_ENOUGHT_LEVEL = addCode(CODE_INVASIOIN + 4, "40级以上才能参加活动。");
	public static final int CODE_INVASIOIN_ALREAD_LEAVE = addCode(CODE_INVASIOIN + 5, "您已离开活动场景，请勿重复请求。");
	public static final int CODE_INVASIOIN_WRONG_SCENE = addCode(CODE_INVASIOIN + 6, "您请求进入的场景错误，无法传送。");
	//付费地宫
	public static final int CODE_PAYONPALACE_NOT_ENOUGH_ITEM = addCode(CODE_INVASIOIN + 7, "神殿令牌不足，无法进入。");
	public static final int CODE_PAYONPALACE_NOT_START = addCode(CODE_INVASIOIN + 8, "付费地宫活动未开启。");

	
	// 挖矿
	private static final int CODE_MINING = 3800;
	public static final int CODE_MINING_LVERROR = addCode(CODE_MINING + 1, "等级未达到40级");
	public static final int CODE_MINING_COUNT_UP = addCode(CODE_MINING + 2, "当天采矿次数已满");
	public static final int CODE_MINING_NOT_START = addCode(CODE_MINING + 3, "采矿活动未开启");
	public static final int CODE_Mining_SceneLoadError = addCode(CODE_MINING + 4, "场景加载失败");
	public static final int CODE_Mining_AlreadyInScene = addCode(CODE_MINING + 5, "已经在挖矿地图中");
	public static final int CODE_Mining_FromSceneValid = addCode(CODE_MINING + 6, "非法场景类型");
	public static final int CODE_Mining_TransferError = addCode(CODE_MINING + 7, "传送失败");

	//
	private static final int CODE_VIP = 3900;
	public static final int  CODE_VIP_LOTTERTY_NOT_ENOUGH = addCode(CODE_VIP + 1, "抽奖次数不足");
	public static final int  CODE_VIP_HIGHER_CARD = addCode(CODE_VIP + 2, "只能使用同等级或更高等级的VIP卡");
	public static final int  CODE_VIP_NO_VIP = addCode(CODE_VIP + 3, "您不是VIP");
	
	private static final int CODE_UNIONFUBEN = 4000;
	public static final int CODE_UNIONFUBEN_NOT_UNIONLEARDER =  addCode(CODE_UNIONFUBEN + 1, "您不是公会会长，不能报名开启公会副本");
	public static final int CODE_UNIONFUBEN_HAD_BAOMING =  addCode(CODE_UNIONFUBEN + 2, "您的公会已经报名");
	public static final int CODE_UNIONFUBEN_NOT_ENOUGH =  addCode(CODE_UNIONFUBEN + 3, "您的公会成员不足10人");
	public static final int CODE_UNIONFUBEN_NOT_GOLD =  addCode(CODE_UNIONFUBEN + 4, "您的金币不足，无法报名");
	public static final int CODE_UNIONFUBEN_NOT_TIME_BAO =  addCode(CODE_UNIONFUBEN + 5, "当前时间不不能报名公会副本");
	public static final int CODE_UNIONFUBEN_NOT_JOIN_UNION =  addCode(CODE_UNIONFUBEN + 6, "您还未加入任何公会");
	public static final int CODE_UNIONFUBEN_NOT_TIME_OPEN =  addCode(CODE_UNIONFUBEN + 7, "公会副本开放时间未到");
	public static final int CODE_UNIONFUBEN_NOT_BAOMING =  addCode(CODE_UNIONFUBEN + 8, "您的公会未报名此次公会副本活动");
	
	
	public static void initialize() {

	}

	public static int addCode(int code, String desc) {
		return CodeContext.addErrorCode(code, desc);
	}
}
