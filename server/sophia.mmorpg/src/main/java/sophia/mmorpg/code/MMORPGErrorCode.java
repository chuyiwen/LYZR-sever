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
package sophia.mmorpg.code;

public final class MMORPGErrorCode {
	// 认证
	public static final int CODE_AUTH = 0;
	public static final int CODE_AUTH_FAILURE_MD5 = addCode(CODE_AUTH + 1, "非法请求.md5校验失败");
	public static final int CODE_AUTH_FAILURE_TIMESTAMP = addCode(CODE_AUTH + 2, "非法请求.时戳过期");
	public static final int CODE_AUTH_FAILURE_PARAM = addCode(CODE_AUTH + 3, "非法请求.参数错误");
	public static final int CODE_AUTH_END = CODE_AUTH + 3;
	
	public static final int CODE_SESSION_SPEEDUP = addCode(CODE_AUTH + 15, "非法请求.包速过快");
	public static final int CODE_MESSAGE_FILTER = addCode(CODE_AUTH + 16, "很抱歉，暂时屏蔽此功能!!!");
	public static final int CODE_MESSAGE_ILLEGAL = addCode(CODE_AUTH + 17, "非法请求，包速过快！！！");
	public static final int CODE_MESSAGE_DECODE_ERROR = addCode(CODE_AUTH + 18, "非法的消息包");
	public static final int CODE_MESSAGE_SERVER_STOPPED = addCode(CODE_AUTH + 19, "非法请求，服务器正在启动中，请稍后再试!!!");
	
	// 玩家
	private static final int CODE_PLAYER = 100;
	public static final int CODE_PLAYER_ALREADY_ENTERWORLD = addCode(CODE_PLAYER + 1, "玩家正在进入世界");
	public static final int CODE_PLAYER_ALREADY_ONLINE = addCode(CODE_PLAYER + 2, "玩家已经进入世界");
	public static final int CODE_PLAYER_ONLINEPLAYER_LIMIT = addCode(CODE_PLAYER + 3, "服务器爆满，进入时间失败，请稍后再试");
	public static final int CODE_PLAYER_ENTERWORLD_EXCEPTION = addCode(CODE_PLAYER + 4, "进入世界异常，请联系GM");

	// //////////////////MMORPG Error Code Range [300, 2000)
	// ///////////////////////
	public static final int CODE_SKILL_BEGIN = 300;
	public static final int CODE_SKILL_NOT_LEARNED = addCode(CODE_SKILL_BEGIN + 1, "技能未学习");
	public static final int CODE_SKILL_IN_CD = addCode(CODE_SKILL_BEGIN + 2, "技能CD中");
	public static final int CODE_SKILL_TARGET_NULL = addCode(CODE_SKILL_BEGIN + 3, "技能目标为空");
	public static final int CODE_SKILL_NOT_ENOUGH_MP = addCode(CODE_SKILL_BEGIN + 4, "技能所需魔法不够");
	public static final int CODE_SKILL_DATA_LOGIC_ERROR = addCode(CODE_SKILL_BEGIN + 5, "技能数据逻辑错误");
	public static final int CODE_SKILL_TARGET_TOO_FAR = addCode(CODE_SKILL_BEGIN + 6, "技能目标不在施法距离之内");
	public static final int CODE_SKILL_TARGET_DEAD = addCode(CODE_SKILL_BEGIN + 7, "技能作用目标已经死亡");
	public static final int CODE_SKILL_ATTACKER_DEAD = addCode(CODE_SKILL_BEGIN + 8, "技能发起者已经死亡");
	public static final int CODE_SKILL_TARGET_IN_SAFE_ZONE = addCode(CODE_SKILL_BEGIN + 9, "技能作用目标在安全区");
	public static final int CODE_SKILL_TARGET_NOT_IN_SAME_SCENE = addCode(CODE_SKILL_BEGIN + 10, "技能作用目标和技能发起者不在同一场景");
	public static final int CODE_SKILL_TARGET_OFFLINE = addCode(CODE_SKILL_BEGIN + 11, "技能作用目标已经离线");
	public static final int CODE_SKILL_TARGET_OWNMONSTER = addCode(CODE_SKILL_BEGIN + 12, "技能作用目标不能为自己的召唤兽");
	public static final int CODE_SKILL_CASTER_NULL = addCode(CODE_SKILL_BEGIN + 13, "技能发起者为空");
	public static final int CODE_SKILL_SKILL_NULL = addCode(CODE_SKILL_BEGIN + 14, "技能为空");
	public static final int CODE_SKILL_CASTER_IN_SAFE_ZONE = addCode(CODE_SKILL_BEGIN + 15, "技能释放者在安全区");

	public static final int CODE_NPC_BEGIN = 350;
	public static final int CODE_NPC_TRANSFER_NOT_SAME_SCENE = addCode(CODE_NPC_BEGIN + 1, "玩家与传送NPC不在同一场景，不能传送。");
	public static final int CODE_NPC_TRANSFER_DISTANCE = addCode(CODE_NPC_BEGIN + 2, "玩家与传送NPC距离太远，无法传送。");

	public static final int CODE_PLAYER_BEGIN = 400;
	public static final int CODE_PLAYER_REVIVE_INVALID = addCode(CODE_PLAYER_BEGIN + 1, "非法的复活请求");
	public static final int CODE_PLAYER_REVIVE_UNBINDGOLD_NOT_ENOUGH = addCode(CODE_PLAYER_BEGIN + 2, "原地复活失败，金币不足");
	public static final int CODE_PLAYER_CANNOT_FIND = addCode(CODE_PLAYER_BEGIN + 3, "找不到此玩家");
	public static final int CODE_PLAYER_REVIVE_INTERVAL = addCode(CODE_PLAYER_BEGIN + 4, "原地复活间隔是9s,请耐心等待");
	public static final int CODE_PLAYER_REVIVE_INVALID_NOT_DEAD = addCode(CODE_PLAYER_BEGIN + 5, "非法的复活请求，玩家已复活");
	public static final int CODE_PLAYER_REVIVE_INVALID_IN_FUBEN = addCode(CODE_PLAYER_BEGIN + 6, "非法的复活情况，副本不可以原地复活");
	
	public static final int CODE_SCENE = 500;
	public static final int CODE_SCENE_CANNOT_FINDSPRITE = addCode(CODE_SCENE + 1, "当前场景不存在该精灵");

	public static final int CODE_QUEST = 600;
	public static final int CODE_QUEST_NO_LEVEL_VISIT = addCode(CODE_QUEST + 1, "玩家不够等级查看任务");
	public static final int CODE_QUEST_NO_LEVEL_ACCEPT = addCode(CODE_QUEST + 2, "玩家不够等级接取任务");
	public static final int CODE_QUEST_WRONG_ID = addCode(CODE_QUEST + 3, "任务ID错误");
	public static final int CODE_QUEST_WRONG_QUEST = addCode(CODE_QUEST + 4, "提交任务错误，该任务并非正在进行中的任务。");
	public static final int CODE_QUEST_REWARDED = addCode(CODE_QUEST + 5, "任务奖励已领取， 请勿重复提交。");
	public static final int CODE_QUEST_PLUCK_NOT_ENOUGHT = addCode(CODE_QUEST + 6, "采集物数量不足，请检测背包采集物数量。");
	public static final int CODE_QUEST_START_MAX = addCode(CODE_QUEST + 7, "当前任务级别已达最高级。");
	public static final int CODE_GOLD_NOT_ENOUGHT = addCode(CODE_QUEST + 8, "金币不足");
	public static final int CODE_QUEST_NOT_ENOUGHT_SPACE = addCode(CODE_QUEST + 9, "背包已满，请清理后再提交任务。");
	public static final int CODE_QUEST_WRONG_MESSAGE = addCode(CODE_QUEST + 10, "日常任务领奖倍数有误，请稍后重试。");
	public static final int CODE_QUEST_NOT_FINISH = addCode(CODE_QUEST + 11, "当前任务未完成，无法接取下一任务。");
	public static final int CODE_QUEST_NOT_SUTE_COURSE = addCode(CODE_QUEST + 12, "没有符合玩家当前等级的任务目标，请提高等级后重试。");
	public static final int CODE_QUEST_LAST_RING = addCode(CODE_QUEST + 13, "当前日常任务的可接取次数为0，任务接取失败。");

	public static final int CODE_ITEM = 800;
	public static final int CODE_ITEM_USERSLOT = addCode(CODE_ITEM + 1, "非法背包格位置");
	public static final int CODE_ITEM_NOTEXIEST = addCode(CODE_ITEM + 2, "背包格位置上没有物品");
	public static final int CODE_ITEM_NOENOUGH = addCode(CODE_ITEM + 3, "物品不足");
	public static final int CODE_ITEM_USELEVEL = addCode(CODE_ITEM + 4, "玩家未能达到使用等级");
	public static final int CODE_ITEM_CANTUSE = addCode(CODE_ITEM + 5, "该物品不可使用");
	public static final int CODE_ITEM_CANTSALE = addCode(CODE_ITEM + 6, "该物品不可出售");
	public static final int CODE_ITEM_CANTDROP = addCode(CODE_ITEM + 7, "该物品不可丢弃");
	public static final int CODE_ITEM_NUMBERWORNG = addCode(CODE_ITEM + 8, "物品数量有误");
	public static final int CODE_ITEM_USEKNIGHT = addCode(CODE_ITEM + 9, "玩家爵位未能达到使用等级");
	public static final int CODE_ITEM_FULL = addCode(CODE_ITEM + 10, "背包已满");
	public static final int CODE_ITEM_USECOUNTTODAY = addCode(CODE_ITEM + 11, "该物品今日使用次数已满");
	public static final int CODE_ITEM_IN_CD = addCode(CODE_ITEM + 12, "物品冷却中");
	public static final int CODE_ITEM_GIFT_TIME_NOT = addCode(CODE_ITEM + 13, "礼包开启时间未到");
	public static final int CODE_ITEM_GIFT_NOT_RECHARGE = addCode(CODE_ITEM + 14, "您还未冲过值，暂不能开启礼包");
	public static final int CODE_ITEM_ITEMS_COLLECTION_EMPTY = addCode(CODE_ITEM + 15, "物品集合为空");
	public static final int CODE_ITEM_CAPATICY_NOT_ENOUGH = addCode(CODE_ITEM + 16, "背包格不足");
	public static final int CODE_ITEM_ADD_FAILED = addCode(CODE_ITEM + 17, "由于某种原因，物品存放失败");
	public static final int CODE_ITEM_USE_HP_FULL = addCode(CODE_ITEM + 18, "当前生命值已满");
	public static final int CODE_ITEM_USE_MP_FULL = addCode(CODE_ITEM + 19, "当前魔法值已满");
	public static final int CODE_ITEM_WEAPON_EMPTY = addCode(CODE_ITEM + 20, "请先装备一把武器");
	public static final int CODE_ITEM_LUCK__1 = addCode(CODE_ITEM + 21, "恭喜您，武器幸运+1");
	public static final int CODE_ITEM_LUCK_NO = addCode(CODE_ITEM + 22, "幸好，武器幸运不变");
	public static final int CODE_ITEM_LUCK_FAILED = addCode(CODE_ITEM + 23, "sorry,武器幸运-1");
	public static final int CODE_ITEM_LUCK__2 = addCode(CODE_ITEM + 24, "恭喜您,武器幸运+2");
	public static final int CODE_ITEM_LUCK__3 = addCode(CODE_ITEM + 25, "恭喜您,武器幸运+3");
	public static final int CODE_ITEM_LUCK__4 = addCode(CODE_ITEM + 26, "恭喜您,武器幸运+4");
	public static final int CODE_ITEM_LUCK__5 = addCode(CODE_ITEM + 27, "恭喜您,武器幸运+5");
	public static final int CODE_ITEM_LUCK__6 = addCode(CODE_ITEM + 28, "恭喜您,武器幸运+6");
	public static final int CODE_ITEM_LUCK__7 = addCode(CODE_ITEM + 29, "恭喜您,武器幸运+7");
	public static final int CODE_ITEM_LUCK_FULL = addCode(CODE_ITEM + 30, "通过祝福油可附加的幸运已达上限7");
	public static final int CODE_ITEM_FEIXIE = addCode(CODE_ITEM + 31, "飞鞋不足，无法传送");
	public static final int CODE_ITEM_USE_MPHP_FULL = addCode(CODE_ITEM + 32, "当前生命和魔法值已满");
	public static final int CODE_ITEM_SLOT_ENOUGH = addCode(CODE_ITEM + 33, "背包已满，请先清理背包");
	public static final int CODE_ITEM_CNAT_SEE = addCode(CODE_ITEM + 34, "物品当前不可看");
	public static final int CODE_ITEM_TRANSFERSTONE_NOTENOUGH = addCode(CODE_ITEM + 35, "传送石不足");
	public static final int CODE_ITEM_PLAYER_DEAD = addCode(CODE_ITEM + 36, "死亡状态下不可使用");
	public static final int CODE_ITEM_SendBYMail = addCode(CODE_ITEM + 37, "背包已满，物品已通过邮件发送");
	public static final int CODE_ITEM_REMOVE_FAILED = addCode(CODE_ITEM + 38, "由于某种原因，物品删除失败");
	public static final int CODE_ITEM_DEPOT_NOT_EXIST = addCode(CODE_ITEM + 39, "仓库不存在该物品");
	public static final int CODE_ITEM_DEPOT_NOT_ENOUGH_CAPACITY = addCode(CODE_ITEM + 40, "仓库已满格");
	public static final int CODE_ITEM_UpGRADEEQUIP_LACK_MATERIAL = addCode(CODE_ITEM + 41, "进阶材料不足，无法进阶");
	public static final int CODE_ITEM_UpGRADEEQUIP_HIGHEST = addCode(CODE_ITEM + 42, "该装备已是最高阶装备，无法继续进阶");
	public static final int CODE_ITEM_UpGRADEEQUIP_LACK_GOLD = addCode(CODE_ITEM + 43, "金币不足，无法进阶");

	// 聊天
	public static final int CODE_CHAT = 900;
	public static final int CODE_CHAT_LENGTH_LONGEST = addCode(CODE_CHAT + 1, "聊天内容太长，内容取2-6中文字符，4-12英文字符，区分大小写");
	public static final int CODE_CHAT_LENGTH_SHORTEST = addCode(CODE_CHAT + 2, "聊天内容太短，内容取2-6中文字符，4-12英文字符，区分大小写");
	public static final int CODE_CHAT_TIME_LONGEST = addCode(CODE_CHAT + 3, "你说话太快了，请休息几秒再说话");
	public static final int CODE_CHAT_USER_NOT_EXIST = addCode(CODE_CHAT + 4, "玩家不在线或者不存在");
	public static final int CODE_CHAT_BLACK_LIST = addCode(CODE_CHAT + 5, "你已被加入对方的黑名单");
	public static final int CODE_CHAT_TALISMAN = addCode(CODE_CHAT + 6, "请先激活千里传音法宝");
	public static final int CODE_CHAT_USER_NOT_ONLIE = addCode(CODE_CHAT + 7, "玩家不在线，无法查看该物品");
	public static final int CODE_CHAT_USER_DISALLOW_TALK = addCode(CODE_CHAT + 8, "你被禁言了");
	public static final int CODE_CHAT_TALISMAN_CDTIME = addCode(CODE_CHAT + 9, "千里传音法宝尚未冷却");
	public static final int CODE_CHAT_CantWatchYourself = addCode(CODE_CHAT + 10, "不能查看自己的信息");
	public static final int CODE_CHAT_PlayerIsNotExist = addCode(CODE_CHAT + 11, "玩家不存在");
	public static final int CODE_CHAT_BLACK_LIST_ADD = addCode(CODE_CHAT + 12, "你在对方的黑名单中，无法加入黑名单以外的列表中。");
	public static final int CODE_CHAT_IN_OTHER_LIST = addCode(CODE_CHAT + 13, "对方已存在于其他列表中，无法加入");
	public static final int CODE_CHAT_NOT_IN_LIST = addCode(CODE_CHAT + 14, "对方不存在于列表中");
	public static final int CODE_CHAT_ADD_SELF = addCode(CODE_CHAT + 15, "无法将自己加为好友");
	public static final int CODE_CHAT_CANT_CHAT_SELF = addCode(CODE_CHAT + 16, "不能和自己聊天");
	public static final int CODE_CHAT_CANT_REPEATADD = addCode(CODE_CHAT + 17, "玩家已经在该列表中");
	public static final int CODE_CHAT_IN_BLACKLIST = addCode(CODE_CHAT + 18, "不能跟黑名单中的玩家聊天");
	public static final int CODE_CHAT_IN_OTHERBLACKLIST = addCode(CODE_CHAT + 19, "你在对方的黑名单中,不能聊天");
	
	// 装备
	public static final int CODE_EQUIPMENT = 1000;
	public static final int CODE_EQUIPMENT_BODYCANTEQUIP = addCode(CODE_EQUIPMENT + 1, "该部位不能穿戴装备");
	public static final int CODE_EQUIPMENT_CANTMATCHBODYID = addCode(CODE_EQUIPMENT + 2, "装备穿戴的部位和身体的部位不匹配");
	public static final int CODE_EQUIPMENT_CANTGETEQUIPMENT = addCode(CODE_EQUIPMENT + 3, "指定部位没有拿到装备");
	public static final int CODE_EQUIPMENT_CANTMATCHGENDER = addCode(CODE_EQUIPMENT + 4, "玩家性别与装备穿戴性别要求不匹配");
	public static final int CODE_EQUIPMENT_CANTMATCHPROFESSION = addCode(CODE_EQUIPMENT + 5, "玩家职业与装备穿戴职业要求不匹配");
	public static final int CODE_EQUIPMENT_CANTMATCHLEVEL = addCode(CODE_EQUIPMENT + 6, "玩家等级不够");
	public static final int CODE_EQUIPMENT_CANTMATCHKNIGHT = addCode(CODE_EQUIPMENT + 7, "玩家爵位等级不够");

	// 坐骑
	public static final int CODE_MOUNT = 1100;
	public static final int CODE_MOUNT_UP_WRONG = addCode(CODE_MOUNT + 1, "上马失败");
	public static final int CODE_MOUNT_UPED = addCode(CODE_MOUNT + 2, "不能重复上马");
	public static final int CODE_MOUNT_DOWN_WRONG = addCode(CODE_MOUNT + 3, "下马失败");
	public static final int CODE_MOUNT_DOWNED = addCode(CODE_MOUNT + 4, "不能重复上马");
	public static final int CODE_MOUNT_NOT_EXIST = addCode(CODE_MOUNT + 5, "坐骑不存在");
	public static final int CODE_MOUNT_ITEMREFID_WRONG = addCode(CODE_MOUNT + 6, "该物品不能喂养坐骑");
	public static final int CODE_MOUNT_NUM_WRONG = addCode(CODE_MOUNT + 7, "参数有误");
	public static final int CODE_MOUNT_FEED_WRONG = addCode(CODE_MOUNT + 8, "喂养坐骑出错");
	public static final int CODE_MOUNT_CD = addCode(CODE_MOUNT + 9, "CD未冷却");
	public static final int CODE_MOUNT_PVP_CD = addCode(CODE_MOUNT + 10, "战斗状态中不能上马");
	// 邮件
	private static final int CODE_MAIL = 1200;

	public static final int CODE_MAIL_PICKUP_ERROR = addCode(CODE_MAIL + 1, "拾取附件失败");
	public static final int CODE_MAIL_IS_READ = addCode(CODE_MAIL + 2, "邮件已读取");
	public static final int CODE_MAIL_NOT_EXIST = addCode(CODE_MAIL + 3, "邮件不存在");
	public static final int CODE_MAIL_NOT_ITEM = addCode(CODE_MAIL + 4, "没有可拾取的附件");
	public static final int CODE_SEND_CUSTOMER_MAIL_FAIL = addCode(CODE_MAIL + 5, "发送客服邮件失败");
	public static final int CODE_SEND_CUSTOMER_MAIL_FAST = addCode(CODE_MAIL + 6, "发送客服邮件间隔必须大于3分钟");
	public static final int CODE_SEND_CUSTOMER_MAIL_CONENT_ERROR = addCode(CODE_MAIL + 7, "发送客服邮件内容为空或者过长");
	public static final int CODE_MAIL_FETCH_NO_ENOUGHT_TIME= addCode(CODE_MAIL + 8, "请在倒计时结束后拾取附件");
	public static final int CODE_MAIL_NOREAD_HALF_FULL= addCode(CODE_MAIL + 9, "您的邮箱快要满了，邮箱满后会自动删除最早的邮件，\\n为避免您的损失，请尽快读取未读邮件");
	public static final int CODE_MAIL_NOREAD_FULL= addCode(CODE_MAIL + 10, "您的邮箱已满，已自动删除最早获得的邮件，\\n请尽快读取未读邮件");

	// 采集
	private static final int CODE_PLUCK = 1300;
	public static final int CODE_PLUCK_NOT_PLUCK_NPC = addCode(CODE_PLUCK + 1, "不是采集物");
	public static final int CODE_PLUCK_NOT_ENOUGHT_LEVEL = addCode(CODE_PLUCK + 2, "未满足采集等级");
	public static final int CODE_PLUCK_BEING_PLUCKED = addCode(CODE_PLUCK + 3, "该采集物正在被他人采集");
	public static final int CODE_PLUCK_OutOfDistance = addCode(CODE_PLUCK + 4, "采集距离不够");

	// 拾取
	private static final int CODE_PICKUP = 1350;
	public static final int CODE_PICKUP_GUARDTIME = addCode(CODE_PICKUP + 1, "拾取失败,掉落物还在保护时间");
	public static final int CODE_PICKUP_ITEMBAG_FULL = addCode(CODE_PICKUP + 2, "拾取失败,背包已满");
	public static final int CODE_PICKUP_ALREADY = addCode(CODE_PICKUP + 3, "拾取失败,该掉落物已被拾取");

	// 法宝
	private static final int CODE_TALISMAN = 1400;
	public static final int CODE_ACTIVE_IN_CD = addCode(CODE_TALISMAN + 1, "不能激活该法宝,还在保护时间");
	public static final int CODE_ACTIVE_MAX_LEVEL = addCode(CODE_TALISMAN + 2, "心法已经升到最高等级");
	public static final int CODE_ACTIVE_UNACTIVE_STATE = addCode(CODE_TALISMAN + 3, "该法宝处于未激活状态，不能取消");
	public static final int CODE_ACTIVE_ACTIVE_STATE = addCode(CODE_TALISMAN + 4, "该法宝处于激活状态，不能重复激活");
	public static final int CODE_ACTIVE_ACQUIRE_STATE = addCode(CODE_TALISMAN + 5, "该法宝已获取");
	public static final int CODE_ACTIVE_UNACQUIRE_STATE = addCode(CODE_TALISMAN + 6, "该法宝处于未获取状态，不能激活");

	// 组队
	private static final int CODE_TEAM = 1600;
	public static final int CODE_TEAM_FAIL = addCode(CODE_TEAM + 1, "不能对自己操作");// 0x80000641
	public static final int CODE_TEAM_EXIST = addCode(CODE_TEAM + 2, "对方已有队伍");
	public static final int CODE_TEAM_JOIN_FAIL = addCode(CODE_TEAM + 3, "加入队伍失败");
	public static final int CODE_TEAM_KICKEDOUT_FAIL = addCode(CODE_TEAM + 4, "踢出队伍失败");
	public static final int CODE_TEAM_LEAVE_FAIL = addCode(CODE_TEAM + 5, "离开队伍失败");
	public static final int CODE_TEAM_HANDEROVER_FAIL = addCode(CODE_TEAM + 6, "转让队长失败");
	public static final int CODE_TEAM_DISBAND_FAIL = addCode(CODE_TEAM + 7, "解散队伍失败");
	public static final int CODE_TEAM_INVITE_FAIL = addCode(CODE_TEAM + 8, "邀请失败");
	public static final int CODE_TEAM_INVITE_EXIST = addCode(CODE_TEAM + 9, "不能重复邀请");
	public static final int CODE_TEAM_NO_INVITE = addCode(CODE_TEAM + 10, "队员不能邀请");
	public static final int CODE_TEAM_ME_EXIST = addCode(CODE_TEAM + 11, "自己已有队伍");
	public static final int CODE_TEAM_REQUEST_EXIST = addCode(CODE_TEAM + 12, "不能重复申请");
	public static final int CODE_TEAM_REQUEST_FAIL = addCode(CODE_TEAM + 13, "申请失败");
	public static final int CODE_TEAM_LIMIT = addCode(CODE_TEAM + 14, "队伍已满员，加入失败");
	public static final int CODE_TEAM_NOT_ONLINE = addCode(CODE_TEAM + 15, "目标不在线，邀请失败");
	public static final int CODE_TEAM_NOT_TEAM_EXIT = addCode(CODE_TEAM + 16, "当前不属于组队状态，无法操作");
	public static final int CODE_TEAM_NOT_TEAMLEADER = addCode(CODE_TEAM + 17, "您不是队长，无法操作");
	public static final int CODE_TEAM_NOT_ONLINE_JOIN = addCode(CODE_TEAM + 18, "目标不在线，加入队伍失败");
	public static final int CODE_TEAM_NOT_ACTIVITY_TIME = addCode(CODE_TEAM + 19, "活动尚未开始，无法主动创建队伍。");

	// 传送
	private static final int CODE_TRANSFER = 1700;
	public static final int CODE_TRANSFER_INVALID = addCode(CODE_TRANSFER + 1, "非法传送");
	public static final int CODE_TRANSFER_NOT_ENOUGHT_LEVEL = addCode(CODE_TRANSFER + 2, "玩家等级未达到地图开放等级");
	public static final int CODE_TRANSFER_WRONG_STATE = addCode(CODE_TRANSFER + 3, "玩家当前状态无法传送");
	public static final int CODE_TRANSFER_SCENE_NOT_READY = addCode(CODE_TRANSFER + 4, "场景未准备完毕，无法传送。");
	public static final int CODE_TRANSFER_WRONG_POSITION = addCode(CODE_TRANSFER + 5, "传送位置非法，无法传送。");	
	public static final int CODE_TRANSFER_ACTIVITY_BAND_TRANSFER = addCode(CODE_TRANSFER + 6, "当前场景有活动正在进行，禁止传送。");
	public static final int CODE_TRANSFER_NOT_ENOUGHT_FIGHTPOWER = addCode(CODE_TRANSFER + 7, "玩家战力未满足目标地图的开放条件，无法传送！");
	
	// 怪物
	private static final int CODE_MONSTER_BEGIN = 1800;
	public static final int CODE_MONSTER_NULL = addCode(CODE_MONSTER_BEGIN + 1, "怪物不存在");
	
	// 拍卖行
	private static final int CODE_AUCTION_BEGIN = 1900;
	public static final int CODE_AUCTION_ITEM_NOT_EXIST = addCode(CODE_AUCTION_BEGIN + 1, "该物品已被其他玩家购买");
	public static final int CODE_AUCTION_NOT_ENOUGH_MONEY = addCode(CODE_AUCTION_BEGIN + 2, "您的元宝不足");
	public static final int CODE_AUCTION_ITEM_NOT_ENOUGH = addCode(CODE_AUCTION_BEGIN + 3, "没有足够的该物品来拍卖");
	public static final int CODE_AUCTION_ITEM_BINDED = addCode(CODE_AUCTION_BEGIN + 4, "该物品已经绑定");
	public static final int CODE_AUCTION_DATA_ERROR = addCode(CODE_AUCTION_BEGIN + 5, "协议数据错误");
	public static final int CODE_AUCIION_INVALID_PRICE = addCode(CODE_AUCTION_BEGIN + 6, "请输入1~999999之间的数字");
	public static final int CODE_AUCTION_TIMES_LIMITED = addCode(CODE_AUCTION_BEGIN + 7, "您在拍卖行同时出售的物品数量已达最大值，无法继续出售");
	public static final int CODE_AUCTION_ITEM_STOLEN = addCode(CODE_AUCTION_BEGIN + 8, "非法取消拍卖");

	public static final void initialize() {

	}

	public static int addCode(int code, String desc) {
		return CodeContext.addErrorCode(code, desc);
	}
}
