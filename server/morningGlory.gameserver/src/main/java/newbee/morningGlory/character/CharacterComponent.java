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

import java.util.List;

import newbee.morningGlory.character.event.C2G_CharacterCreate;
import newbee.morningGlory.character.event.C2G_CharacterDelete;
import newbee.morningGlory.character.event.C2G_CharacterLogin;
import newbee.morningGlory.character.event.CharacterEventDefines;
import newbee.morningGlory.character.event.G2C_CharacterDelete;
import newbee.morningGlory.code.MGErrorCode;
import newbee.morningGlory.code.MGSuccessCode;

import org.apache.log4j.Logger;

import sophia.foundation.authentication.Identity;
import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.foundation.util.SFStringUtil;
import sophia.game.GameRoot;
import sophia.game.component.AbstractComponent;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.code.CodeContext;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.player.persistence.PlayerDAO;
import sophia.mmorpg.player.property.CharacterInfo;
import sophia.mmorpg.player.property.PlayerConfig;
import sophia.mmorpg.stat.StatFunctions;
import sophia.mmorpg.stat.StatService;
import sophia.mmorpg.utils.StringValidChecker;

import com.google.common.base.Strings;

public class CharacterComponent extends AbstractComponent {

	private static final Logger logger = Logger.getLogger(CharacterComponent.class);

	private static final int NAME_LENGTH_SHORTEST = 4;
	private static final int NAME_LENGTH_LONGEST = 12;

	@Override
	public void ready() {
		addActionEventListener(CharacterEventDefines.C2G_CharacterCreate);
		addActionEventListener(CharacterEventDefines.C2G_CharacterGet);
		addActionEventListener(CharacterEventDefines.C2G_CharacterLogin);
		//addActionEventListener(CharacterEventDefines.C2G_CharacterDelete);
		super.ready();
	}

	@Override
	public void destroy() {
		removeActionEventListener(CharacterEventDefines.C2G_CharacterCreate);
		removeActionEventListener(CharacterEventDefines.C2G_CharacterGet);
		removeActionEventListener(CharacterEventDefines.C2G_CharacterLogin);
		//removeActionEventListener(CharacterEventDefines.C2G_CharacterDelete);
		super.destroy();
	}

	@Override
	public void handleActionEvent(final ActionEventBase event) {
		short actionEventId = event.getActionEventId();
		Identity identity = event.getIdentity();
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		if (CharacterEventDefines.C2G_CharacterCreate == actionEventId) {
			if (logger.isDebugEnabled()) {
				logger.debug("CharacterCreate");
			}
			
			// 最大在线玩家限制
			if (PlayerManager.isMaxOnlinePlayerCount()) {
				ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_PLAYER_ONLINE_LIMIT);
				if (logger.isDebugEnabled()) {
					logger.debug("reach max online player limit");
				}
				return;
			}

			C2G_CharacterCreate req = (C2G_CharacterCreate) event;
			String charName = req.getCharacterName();
			int len = SFStringUtil.charCount(charName);

			// 角色名太短
			if (Strings.isNullOrEmpty(charName) || len < NAME_LENGTH_SHORTEST) {
				ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_NAME_LENGTH_SHORTEST);
				if (logger.isDebugEnabled()) {
					logger.debug(charName + " too short.");
				}
				return;
			}
			// 角色名太长
			if (len > NAME_LENGTH_LONGEST) {
				ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_NAME_LENGTH_LONGEST);
				if (logger.isDebugEnabled()) {
					logger.debug(charName + " too long.");
				}
				return;
			}
			// 非法字符
			if (!StringValidChecker.isValid(charName)) {
				ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_PLAYER_INVALID_CHARNAME);
				if (logger.isDebugEnabled()) {
					logger.debug(charName + " invalid charname.");
				}
				return;
			}
			
			// 名称已经存在
			if (!PlayerManager.addCharName(charName)) {
				ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_NAME_ALREADY_EXSIT);
				if (logger.isDebugEnabled()) {
					logger.debug(charName + " already exist.");
				}
				return;
			}

			// 角色已经存在
			List<CharacterInfo> charList = PlayerDAO.getInstance().selectPlayerListByIdentity(identity);
			if (charList.size() >= 3) {
				ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_PLAYER_LIMITCOUNT);
				logger.debug("character number has 3");
				return;
			}

			if (Strings.isNullOrEmpty(PlayerConfig.getProfessionRefId(req.getProfession()))) {
				ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_PLAYER_PROFESSION_INVALID);
				if (logger.isDebugEnabled()) {
					logger.debug("profession invalid. profession:" + req.getProfession());
				}
				return;
			}

			byte gender = req.getGender();
			if (gender != 1 && gender != 2) {
				gender = 1;
			}
			

			Player player = CharacterCreate.newPlayer(identity, charName, gender, req.getProfession());
			if (logger.isDebugEnabled()) {
				logger.debug("CharacterCreate, player=" + player);
			}
			
			CharacterLogin.characterLogin(identity, player);

			StatFunctions.createCharacterStat(player);
			StatService.getInstance().getStatOnlineTicker().addTotalNum(1);
			CharecterGMNotify.getInstance().sendCreate(player);
			CharecterGMNotify.getInstance().sendLogin(player);

		} else if (CharacterEventDefines.C2G_CharacterGet == actionEventId) {
			if (logger.isDebugEnabled()) {
				logger.debug("CharacterGet");
			}

			CharacterGet.characterGet(identity);

		} else if (CharacterEventDefines.C2G_CharacterLogin == actionEventId) {
			if (logger.isDebugEnabled()) {
				logger.debug("CharacterLogin");
			}
			
			// 最大在线玩家限制
			if (PlayerManager.isMaxOnlinePlayerCount()) {
				ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_PLAYER_ONLINE_LIMIT);
				if (logger.isDebugEnabled()) {
					logger.debug("reach max online player limit");
				}
				return;
			}

			C2G_CharacterLogin req = (C2G_CharacterLogin) event;
			String charId = req.getCharId();
			if (Strings.isNullOrEmpty(charId)) {
				logger.debug("CharacterLogin Error charId null string");
				ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_PARAM_INVALID);
				return;
			}

			int code = CharacterLogin.characterLogin(identity, charId);
			if (code != MGSuccessCode.CODE_SUCCESS) {
				if (logger.isDebugEnabled()) {
					logger.debug(CodeContext.description(code));
				}

				ResultEvent.sendResult(identity, actionEventId, code);
				return;
			}

			Player player = playerManager.getPlayer(charId);
			CharecterGMNotify.getInstance().sendLogin(player);
			if (logger.isDebugEnabled()) {
				logger.debug("角色(" + player.getName() + "@" + player.getId() + ")加载成功");
			}
		} else if (CharacterEventDefines.C2G_CharacterDelete == actionEventId) {
			C2G_CharacterDelete req = (C2G_CharacterDelete) event;
			String charId = req.getCharId();

			if (logger.isDebugEnabled()) {
				logger.debug("CharacterDelete" + charId);
			}
			if (Strings.isNullOrEmpty(charId)) {
				logger.debug("CharacterDelete Fail charId null string");
				ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_PARAM_INVALID);
				return;
			}
			
			Player player = playerManager.getPlayer(charId);
			if (player == null) {
				return;
			}
			
			playerManager.leaveWorld(charId);

			if (PlayerDAO.getInstance().deletePlayer(charId)) {
				logger.debug("CharacterDelete Success");
				G2C_CharacterDelete res = MessageFactory.getConcreteMessage(CharacterEventDefines.G2C_CharacterDelete);
				res.setCharId(charId);
				GameRoot.sendMessage(identity, res);
			} else {
				logger.debug("CharacterDelete Fail character can't find ");
				ResultEvent.sendResult(identity, actionEventId, MGErrorCode.CODE_PLAYER_INVALID);
				return;
			}
		}
	}
}
