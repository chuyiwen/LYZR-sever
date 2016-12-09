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
package newbee.morningGlory.character;

import newbee.morningGlory.character.event.CharacterEventDefines;
import newbee.morningGlory.character.event.G2C_CharacterLogin;
import newbee.morningGlory.code.MGErrorCode;
import newbee.morningGlory.code.MGSuccessCode;
import newbee.morningGlory.stat.MGStatFunctions;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.authentication.Identity;
import sophia.foundation.communication.core.MessageFactory;
import sophia.foundation.property.PropertyDictionary;
import sophia.game.GameRoot;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.auth.AuthIdentity;
import sophia.mmorpg.code.CodeContext;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.player.state.PlayerStateMgr;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.DateTimeUtil;

public final class CharacterLogin {

	private static final Logger logger = Logger.getLogger(CharacterLogin.class);
	public static final int SECONDS_IN_DAY = 60 * 60 * 24;
	public static final long MILLIS_IN_DAY = 1000L * SECONDS_IN_DAY;

	public static int characterLogin(Identity identity, String charId) {
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();

		Player player = playerManager.getPlayer(charId);
		if (player == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("characterLogin error, invalid identityId=" + identity.getId());
			}

			return MGErrorCode.CODE_PLAYER_INVALID;
		}

		if (!StringUtils.equals(player.getIdentity().getId(), identity.getId())) {
			if (logger.isDebugEnabled()) {
				logger.debug("characterLogin error, invalid charId=" + charId + ", identityId=" + identity.getId());
			}

			return MGErrorCode.CODE_PLAYER_INVALID;
		}

		return characterLogin(identity, player);
	}

	public static int characterLogin(Identity identity, Player player) {
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		String charId = player.getId();

		// 封号
		if (player.getPlayerStateMgr().hasState(PlayerStateMgr.Disabled)) {
			if (logger.isDebugEnabled()) {
				logger.debug("characterLogin 被封号中. player=" + player);
			}

			return MGErrorCode.CODE_PLAYER_DISABLED;
		}

		AuthIdentity authIdentity = (AuthIdentity) identity;
		player.setIdentity(identity);
		int code = playerManager.enterWorld(player);
		if (code == MMORPGErrorCode.CODE_PLAYER_ALREADY_ENTERWORLD || code == MMORPGErrorCode.CODE_PLAYER_ALREADY_ONLINE) {
			authIdentity.setCharId(charId);
			if (logger.isDebugEnabled()) {
				logger.debug("characterLogin already enterWorld, player=" + player);
			}

			return MGSuccessCode.CODE_SUCCESS;
		} else if (code != MGSuccessCode.CODE_SUCCESS) {
			if (logger.isDebugEnabled()) {
				logger.debug("characterLogin error, code=" + CodeContext.description(code) + ", player=" + player);
			}
			return code;
		}

		authIdentity.setCharId(charId);

		setPropertyLoginData(player);
		// 登录成功返回
		G2C_CharacterLogin res = MessageFactory.getConcreteMessage(CharacterEventDefines.G2C_CharacterLogin);
		res.setPlayer(player);
		GameRoot.sendMessage(identity, res);

		return MGSuccessCode.CODE_SUCCESS;
	}

	private static void setPropertyLoginData(Player player) {
		PropertyDictionary playerPd = player.getProperty();
		int playerLoginTimes = MGPropertyAccesser.getPlayerLoginTimes(playerPd);
		if (playerLoginTimes < 0) {
			playerLoginTimes = 0;
		}

		long lastLoginTime = MGPropertyAccesser.getLastLoginTime(playerPd);
		if (lastLoginTime < 0) {
			lastLoginTime = 0;
		}

		int playerOnlineDays = MGPropertyAccesser.getPlayerOnlineDays(playerPd);
		if (playerOnlineDays < 0) {
			playerOnlineDays = 0;
		}

		long now = System.currentTimeMillis();
		if (!DateTimeUtil.isTheSameDay(lastLoginTime, now)) {
			playerOnlineDays++;
		}

		// lastLoginTime在CharacterCreate时已设置，仅isTheSameDay不足以判定，这边对于当前创建，当天登录的情况进行处理
		if (playerOnlineDays == 0) {
			playerOnlineDays = 1;
		}

		// 设置玩家登陆次数
		MGPropertyAccesser.setOrPutPlayerLoginTimes(playerPd, ++playerLoginTimes);
		// 设置最后登录时间
		MGPropertyAccesser.setOrPutLastLoginTime(player.getProperty(), now);
		// 设置玩家在线天数
		MGPropertyAccesser.setOrPutPlayerOnlineDays(playerPd, playerOnlineDays);
		// 设置默认登出时间
		if (MGPropertyAccesser.getLastLogoutTime(playerPd) <= 0) {
			MGPropertyAccesser.setOrPutLastLogoutTime(playerPd, now);
		}

		MGStatFunctions.loginStat(player, playerLoginTimes, playerOnlineDays, now);
	}
}
