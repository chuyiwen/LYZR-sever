package sophia.mmorpg.player.chat.sysytem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.game.GameRoot;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.chat.Bricks;
import sophia.mmorpg.player.chat.ChannelType;
import sophia.mmorpg.player.chat.event.ChatEventDefines;
import sophia.mmorpg.player.chat.event.G2C_Chat_System;
import sophia.mmorpg.player.chat.event.G2C_System_Prompt;
import sophia.mmorpg.player.property.PlayerConfig;
import sophia.mmorpg.world.ActionEventFacade;

import com.google.common.base.Preconditions;

/**
 * 
 * 各种不同位置的系统提示：如：走马灯 使用方法请参考getExp()方法
 * 
 */
public class SystemPromptFacade {

	private static final Logger logger = Logger.getLogger(SystemPromptFacade.class);

	/**
	 * 参考例子 （获取经验提示）
	 * 
	 * @param player
	 * @param exp
	 */
	public static final void getExp(Player player, long exp) {
		String systemPromptConfigRefId = "system_prompt_config_1";// systemPromptConfig.json
		String content = Bricks.getContents(systemPromptConfigRefId, String.valueOf(exp));
		if (logger.isDebugEnabled()) {
			logger.debug(content);
		}
		sendMsgSpecialEffects(player, content, SystemPromptPosition.POSITION_TOP_SCROLL_1, SpecialEffectsType.SPECIAL_EFFECTS_TYPE_YELLOW);
	}

	/**
	 * 使用物品提示
	 * 
	 * @param player
	 * @param needSotNumber
	 */
	public static final void sendUseItemTips(Player player, String itemName) {
		String systemPromptConfigRefId = "system_prompt_config_3";// systemPromptConfig.json
		String content = Bricks.getContents(systemPromptConfigRefId, itemName);
		if (logger.isDebugEnabled()) {
			logger.debug(content);
		}
		sendMsgSpecialEffects(player, content, SystemPromptPosition.POSITION_MIDDLE_1, SpecialEffectsType.SPECIAL_EFFECTS_TYPE_YELLOW);
	}

	/**
	 * 
	 * 背包剩余格子不足m个，清先清理背包
	 * 
	 * @param player
	 */
	public static final void itemBagForGift(Player player, int m) {
		String systemPromptConfigRefId = "system_prompt_config_4";// systemPromptConfig.json
		String content = Bricks.getContents(systemPromptConfigRefId, m);
		if (logger.isDebugEnabled()) {
			logger.debug(content);
		}
		sendMsgSpecialEffects(player, content, SystemPromptPosition.POSITION_MIDDLE_1, SpecialEffectsType.SPECIAL_EFFECTS_TYPE_YELLOW);
	}

	/**
	 * 背包已满,提示获得物品已发邮件
	 * 
	 * @param player
	 */
	public static final void itemBagFullSendMail(Player player, String source) {
		String systemPromptConfigRefId = "system_prompt_config_6";// systemPromptConfig.json
		String content = Bricks.getContents(systemPromptConfigRefId, source);
		if (logger.isDebugEnabled()) {
			logger.debug(content);
		}
		sendMsgSpecialEffects(player, content, SystemPromptPosition.POSITION_MIDDLE_1, SpecialEffectsType.SPECIAL_EFFECTS_TYPE_YELLOW);
	}

	/**
	 * "挖宝仓库位置不足，请提取后再次挖宝
	 * 
	 * @param player
	 */
	public static final void sendDigHouseFullTips(Player player) {
		String systemPromptConfigRefId = "system_prompt_config_5";// systemPromptConfig.json
		String content = Bricks.getContents(systemPromptConfigRefId);
		if (logger.isDebugEnabled()) {
			logger.debug(content);
		}
		sendMsgSpecialEffects(player, content, SystemPromptPosition.POSITION_MIDDLE_1, SpecialEffectsType.SPECIAL_EFFECTS_TYPE_YELLOW);
	}

	/**
	 * 走马灯
	 * 
	 * @param player
	 * @param exp
	 */
	public static final void scroll(Player player, String content) {
		if (logger.isDebugEnabled()) {
			logger.debug(content);
		}
		sendMsgSpecialEffects(player, content, SystemPromptPosition.POSITION_TOP_SCROLL_1, SpecialEffectsType.SPECIAL_EFFECTS_TYPE_DEFAULT);
	}

	// =============================================================================================
	/**
	 * 
	 * @param receiver
	 *            接受的玩家
	 * @param content
	 *            信息内容
	 * @param position
	 *            信息位置
	 */
	public static final void sendMsgText(Player receiver, String content, byte position) {
		G2C_System_Prompt res = getRes(content, position, SpecialEffectsType.SPECIAL_EFFECTS_TYPE_DEFAULT);
		GameRoot.sendMessage(receiver.getIdentity(), res);
	}

	/**
	 * 
	 * @param receiver
	 *            接受的玩家
	 * @param content
	 *            信息内容
	 * @param position
	 *            信息位置
	 * @param specialEffects
	 *            特效的类型
	 */
	public static final void sendMsgSpecialEffects(Player receiver, String content, byte position, byte specialEffects) {
		G2C_System_Prompt res = getRes(content, position, specialEffects);
		GameRoot.sendMessage(receiver.getIdentity(), res);
	}

	/**
	 * 
	 * @param sender
	 * @param receivers
	 *            接受的玩家
	 * @param content
	 *            信息内容
	 * @param position
	 *            信息位置
	 */
	public static final void sendMsgText(Player sender, Collection<Player> receivers, String content, byte position) {
		sendMsgSpecialEffects(sender, receivers, content, position, SpecialEffectsType.SPECIAL_EFFECTS_TYPE_DEFAULT);
	}

	/**
	 * 
	 * @param sender
	 * @param receivers
	 *            接受的玩家
	 * @param content
	 *            信息内容
	 * @param position
	 *            信息位置
	 * @param specialEffects
	 *            特效的类型
	 */
	public static final void sendMsgSpecialEffects(Player sender, Collection<Player> receivers, String content, byte position, byte specialEffects) {
		Preconditions.checkNotNull(receivers);
		Preconditions.checkNotNull(content);
		G2C_System_Prompt res = getRes(content, position, specialEffects);
		for (Player receiver : receivers) {
			try {
				GameRoot.sendMessage(receiver.getIdentity(), res);
			} catch (Throwable t) {
				logger.error(t);
			}
		}
	}

	public static final G2C_System_Prompt getRes(String content, byte position, byte specialEffects) {
		G2C_System_Prompt res = MessageFactory.getConcreteMessage(ChatEventDefines.G2C_System_Prompt);
		res.setPosition(position);
		res.setMsg(content);
		res.setSpecialEffectsType(specialEffects);
		return res;
	}

	public static final void sendSystemPromptToWorld(String content, byte position, byte specialEffects) {
		ActionEventBase actionEvent = getRes(content, position, specialEffects);
		ActionEventFacade.sendMessageToWorld(actionEvent);
	}

	public static final void sendSystemPromptToCrtScene(String content, byte position, byte specialEffects, GameScene gameScene) {
		ActionEventBase actionEvent = getRes(content, position, specialEffects);
		Map<String, Player> playerMaps = gameScene.getPlayerMgrComponent().getPlayerMap();
		Collection<Player> players = new ArrayList<Player>(playerMaps.size());
		for (Entry<String, Player> entry : playerMaps.entrySet()) {
			players.add(entry.getValue());
		}
		ActionEventFacade.sendMessageToPart(actionEvent, players);
	}

	/**
	 * 世界小偷广播
	 */
	public static final void broadWorldThiefDead(GameScene gameScene) {
		String systemPromptConfigRefId = "system_prompt_config_26"; // TODO 加//
																	// systemPromptConfig.json
		String content = Bricks.getContents(systemPromptConfigRefId);
		if (logger.isDebugEnabled()) {
			logger.debug(content);
		}
		sendSystemPromptToCrtScene(content, SystemPromptPosition.POSITION_TOP_SCROLL_1, SpecialEffectsType.SPECIAL_EFFECTS_TYPE_RED, gameScene);
	}

	/**
	 * BOSS 刷新广播
	 */
	public static final void broadWorldBossRefresh(String sceneName, String monsterName) {
		String systemPromptConfigRefId = "system_prompt_config_17"; // TODO 加
																	// systemPromptConfig.json
		String content = Bricks.getContents(systemPromptConfigRefId, monsterName, sceneName);
		if (logger.isDebugEnabled()) {
			logger.debug(content);
		}
		sendSystemPromptToWorld(content, SystemPromptPosition.POSITION_TOP_SCROLL_1, SpecialEffectsType.SPECIAL_EFFECTS_TYPE_RED);
	}

	/**
	 * 攻城战结束广播
	 * 
	 * @param unionName
	 */
	public static final void broadCastNewKingUnion(String unionName) {
		String systemPromptConfigRefId;
		String content;
		if (StringUtils.isEmpty(unionName)) {
			systemPromptConfigRefId = "system_prompt_config_16";// systemPromptConfig.json
			content = Bricks.getContents(systemPromptConfigRefId);
		} else {
			systemPromptConfigRefId = "system_prompt_config_7";// systemPromptConfig.json
			content = Bricks.getContents(systemPromptConfigRefId, unionName);
		}
		if (logger.isDebugEnabled()) {
			logger.debug(content);
		}
		sendSystemPromptToWorld(content, SystemPromptPosition.POSITION_TOP_SCROLL_1, SpecialEffectsType.SPECIAL_EFFECTS_TYPE_RED);
	}

	/**
	 * 攻城战预开始广播
	 */
	public static final void broadCastCastleWarPreStart() {
		String systemPromptConfigRefId = "system_prompt_config_8";// systemPromptConfig.json
		String content = Bricks.getContents(systemPromptConfigRefId);
		if (logger.isDebugEnabled()) {
			logger.debug(content);
		}
		sendSystemPromptToWorld(content, SystemPromptPosition.POSITION_TOP_SCROLL_1, SpecialEffectsType.SPECIAL_EFFECTS_TYPE_RED);
	}

	/**
	 * 攻城战开始广播
	 */
	public static final void broadCastCastleWarStart() {
		String systemPromptConfigRefId = "system_prompt_config_9";// systemPromptConfig.json
		String content = Bricks.getContents(systemPromptConfigRefId);
		if (logger.isDebugEnabled()) {
			logger.debug(content);
		}
		sendSystemPromptToWorld(content, SystemPromptPosition.POSITION_TOP_SCROLL_1, SpecialEffectsType.SPECIAL_EFFECTS_TYPE_RED);
	}

	/**
	 * 玩家击杀麒麟广播
	 */
	public static final void broadCastCastleWarMonsterDead(String unionName, String playerName) {
		String systemPromptConfigRefId = "system_prompt_config_21";// systemPromptConfig.json
		String content = Bricks.getContents(systemPromptConfigRefId, unionName, playerName);
		if (logger.isDebugEnabled()) {
			logger.debug(content);
		}
		sendSystemPromptToWorld(content, SystemPromptPosition.POSITION_TOP_SCROLL_1, SpecialEffectsType.SPECIAL_EFFECTS_TYPE_RED);
	}

	/**
	 * 挖矿活动预结束
	 */
	public static final void broadCastMiningPreEnd() {
		String systemPromptConfigRefId = "system_prompt_config_12";// systemPromptConfig.json
		String content = Bricks.getContents(systemPromptConfigRefId);
		if (logger.isDebugEnabled()) {
			logger.debug(content);
		}
		sendSystemPromptToWorld(content, SystemPromptPosition.POSITION_TOP_SCROLL_1, SpecialEffectsType.SPECIAL_EFFECTS_TYPE_RED);
	}

	/*
	 * 挖矿活动结束
	 */
	public static final void broadCastMiningStart() {
		String systemPromptConfigRefId = "system_prompt_config_18";// systemPromptConfig.json
		String content = Bricks.getContents(systemPromptConfigRefId);
		if (logger.isDebugEnabled()) {
			logger.debug(content);
		}
		sendSystemPromptToWorld(content, SystemPromptPosition.POSITION_TOP_SCROLL_1, SpecialEffectsType.SPECIAL_EFFECTS_TYPE_RED);
	}

	/**
	 * 挖矿活动结束广播
	 */
	public static final void broadCastMiningEnd() {
		String systemPromptConfigRefId = "system_prompt_config_13";// systemPromptConfig.json
		String content = Bricks.getContents(systemPromptConfigRefId);
		if (logger.isDebugEnabled()) {
			logger.debug(content);
		}
		sendSystemPromptToWorld(content, SystemPromptPosition.POSITION_TOP_SCROLL_1, SpecialEffectsType.SPECIAL_EFFECTS_TYPE_RED);
	}

	/**
	 * 多倍经验活动开始
	 */
	public static final void broadCastMultiExpStart() {
		String systemPromptConfigRefId = "system_prompt_config_27";// systemPromptConfig.json
		String content = Bricks.getContents(systemPromptConfigRefId);
		if (logger.isDebugEnabled()) {
			logger.debug(content);
		}
		sendSystemPromptToWorld(content, SystemPromptPosition.POSITION_TOP_SCROLL_1, SpecialEffectsType.SPECIAL_EFFECTS_TYPE_RED);
	}

	/**
	 * 多倍经验活动结束广播
	 */
	public static final void broadCastMultiExpEnd() {
		String systemPromptConfigRefId = "system_prompt_config_28";// systemPromptConfig.json
		String content = Bricks.getContents(systemPromptConfigRefId);
		if (logger.isDebugEnabled()) {
			logger.debug(content);
		}
		sendSystemPromptToWorld(content, SystemPromptPosition.POSITION_TOP_SCROLL_1, SpecialEffectsType.SPECIAL_EFFECTS_TYPE_RED);
	}

	/**
	 * 怪物入侵开始广播
	 */
	public static final void broadCastMonsterInvasionStart() {
		String systemPromptConfigRefId = "system_prompt_config_14";// systemPromptConfig.json
		String content = Bricks.getContents(systemPromptConfigRefId);
		if (logger.isDebugEnabled()) {
			logger.debug(content);
		}
		sendSystemPromptToWorld(content, SystemPromptPosition.POSITION_TOP_SCROLL_1, SpecialEffectsType.SPECIAL_EFFECTS_TYPE_RED);
	}

	public static final void broadCastMonsterInvasionPreEnd() {
		String systemPromptConfigRefId = "system_prompt_config_20";// systemPromptConfig.json
		String content = Bricks.getContents(systemPromptConfigRefId);
		if (logger.isDebugEnabled()) {
			logger.debug(content);
		}
		sendSystemPromptToWorld(content, SystemPromptPosition.POSITION_TOP_SCROLL_1, SpecialEffectsType.SPECIAL_EFFECTS_TYPE_RED);
	}

	/**
	 * 怪物入侵结束广播
	 */
	public static final void broadCastMonsterInvasionEnd() {
		String systemPromptConfigRefId = "system_prompt_config_15";// systemPromptConfig.json
		String content = Bricks.getContents(systemPromptConfigRefId);
		if (logger.isDebugEnabled()) {
			logger.debug(content);
		}
		sendSystemPromptToWorld(content, SystemPromptPosition.POSITION_TOP_SCROLL_1, SpecialEffectsType.SPECIAL_EFFECTS_TYPE_RED);
	}

	/**
	 * 职业第一名广播
	 */
	public static final void broadCastBestPlayerLogin(Player player) {
		String name = player.getName();
		String systemPromptConfigRefId = "";
		if (PlayerConfig.isWarrior(player.getProfession())) {
			systemPromptConfigRefId = "system_prompt_config_22";
		} else if (PlayerConfig.isEnchanter(player.getProfession())) {
			systemPromptConfigRefId = "system_prompt_config_23";
		} else if (PlayerConfig.isWarlock(player.getProfession())) {
			systemPromptConfigRefId = "system_prompt_config_24";
		}
		String content = Bricks.getContents(systemPromptConfigRefId, name);
		if (logger.isDebugEnabled()) {
			logger.debug(content);
		}
		sendSystemPromptToWorld(content, SystemPromptPosition.POSITION_TOP_SCROLL_1, SpecialEffectsType.SPECIAL_EFFECTS_TYPE_DEFAULT);
	}

	/**
	 * 王城公会会长登陆
	 */
	public static final void broadCastKingCityCreaterLogin(String playerName) {
		String systemPromptConfigRefId = "system_prompt_config_25";// systemPromptConfig.json
		String content = Bricks.getContents(systemPromptConfigRefId, playerName);
		if (logger.isDebugEnabled()) {
			logger.debug(content);
		}
		sendSystemPromptToWorld(content, SystemPromptPosition.POSITION_TOP_SCROLL_1, SpecialEffectsType.SPECIAL_EFFECTS_TYPE_DEFAULT);
	}

	/**
	 * 玩家升级黄金VIP
	 */
	public static final void broadVip(String playerName,String playerId, String vipStr, String vip) {
		String systemPromptConfigRefId = "system_prompt_config_34";// systemPromptConfig.json
		String content = Bricks.getContents(systemPromptConfigRefId, playerName,playerId, vipStr, vip);
		if (logger.isDebugEnabled()) {
			logger.debug(content);
		}
		broadCastSystemTips(content);
	}

	/**
	 * 玩家获得法宝
	 */
	public static final void broadGetTalisman(String playerName,String playerId, String talismanStr, String talisman) {
		String systemPromptConfigRefId = "system_prompt_config_35";// systemPromptConfig.json
		String content = Bricks.getContents(systemPromptConfigRefId, playerName,playerId, talismanStr, talisman);
		if (logger.isDebugEnabled()) {
			logger.debug(content);
		}
		broadCastSystemTips(content);
	}

	/**
	 * 玩家升级坐骑
	 */
	public static final void broadLevelUpMount(String playerName,String playerId, String mountStr,String mountName, String mount) {
		String systemPromptConfigRefId = "system_prompt_config_36";// systemPromptConfig.json
		String content = Bricks.getContents(systemPromptConfigRefId, playerName,playerId, mountStr, mountName,mount);
		if (logger.isDebugEnabled()) {
			logger.debug(content);
		}
		broadCastSystemTips(content);
	}

	/**
	 * 玩家升级翅膀
	 */
	public static final void broadLevelUpWing(String playerName,String playerId, String wingStr,String wingName, String wing) {
		String systemPromptConfigRefId = "system_prompt_config_37";// systemPromptConfig.json
		String content = Bricks.getContents(systemPromptConfigRefId,playerName,playerId, wingStr,wingName, wing);
		if (logger.isDebugEnabled()) {
			logger.debug(content);
		}
		broadCastSystemTips(content);
	}

	/**
	 * 后台发送走马灯公告
	 * 
	 */
	public static final void broadCastScrollNotice(String content) {
		sendSystemPromptToWorld(content, SystemPromptPosition.POSITION_TOP_SCROLL_1, SpecialEffectsType.SPECIAL_EFFECTS_TYPE_DEFAULT);
	}

	/**
	 * 后台发送系统频道公告
	 * 
	 */
	public static final void broadCastSystemNotice(String content) {
		G2C_Chat_System res = MessageFactory.getConcreteMessage(ChatEventDefines.G2C_Chat_System);
		res.setType((byte) 2);
		res.setMsg(content);
		ActionEventFacade.sendMessageToWorld(res);
	}
	
	/**
	 * 发送系统通知
	 * 
	 */
	public static final void broadCastSystemTips(String content) {
		G2C_Chat_System res = MessageFactory.getConcreteMessage(ChatEventDefines.G2C_Chat_System);
		res.setType(ChannelType.SystemTips);
		res.setMsg(content);
		ActionEventFacade.sendMessageToWorld(res);
	}
}
