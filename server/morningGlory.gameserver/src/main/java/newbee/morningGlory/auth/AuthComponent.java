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
package newbee.morningGlory.auth;

import java.net.URLDecoder;

import newbee.morningGlory.http.HttpService;
import newbee.morningGlory.http.util.MD5;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.Connection;
import sophia.foundation.util.DebugUtil;
import sophia.game.component.AbstractComponent;
import sophia.game.plugIns.gateWay.Authenticate;
import sophia.mmorpg.auth.AuthIdentity;
import sophia.mmorpg.auth.event.AuthEventDefines;
import sophia.mmorpg.auth.event.C2G_AuthEvent;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.code.MMORPGSuccessCode;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.stat.StatFunctions;
import sophia.mmorpg.stat.StatService;

import com.google.common.base.Strings;

public class AuthComponent extends AbstractComponent implements Authenticate {
	
	private static final Logger logger = Logger.getLogger(AuthComponent.class);

	@Override
	public void ready() {
		super.ready();
		addActionEventListener(AuthEventDefines.C2G_AuthEvent);
	}

	@Override
	public void destroy() {
		super.destroy();
		removeActionEventListener(AuthEventDefines.C2G_AuthEvent);
	}

	@Override
	public ActionEventBase verify(ActionEventBase actionEvent, Connection connection) {
		ResultEvent res = ResultEvent.getInstance();
		res.setMsgId(AuthEventDefines.C2G_AuthEvent);
		C2G_AuthEvent req = (C2G_AuthEvent) actionEvent;
		StatService.getInstance().getStatOnlineTicker().onLogged(req.getIdentityId());
		// TODO:参数的校验
		boolean verify = true;
		String identityId = req.getIdentityId();
		String identityName = "";
		try {
			identityName = URLDecoder.decode(req.getIdentityName());
		} catch (Exception e) {
			logger.error(DebugUtil.printStack(e));
		}
		
		do {
			if (StringUtils.isEmpty(identityId) || identityId.length() > 60) {
				if (logger.isDebugEnabled()) {
					logger.debug("verify error, invalid identityId=" + identityId);
				}
				verify = false;
				break;
			}
			
			if (StringUtils.isEmpty(identityName) || identityName.length() > 60) {
				if (logger.isDebugEnabled()) {
					logger.debug("verify error, invalid identityName=" + identityName);
				}
				verify = false;
				break;
			}
			
			if (StringUtils.isEmpty(req.getSign())) {
				if (logger.isDebugEnabled()) {
					logger.debug("verify error, invalid sign=" + req.getSign() + ", identityId=" + identityId);
				}
				verify = false;
				break;
			}
			
			if (StringUtils.isEmpty(req.getUuid())) {
				if (logger.isDebugEnabled()) {
					logger.debug("verify error, invalid uuid=" + req.getUuid() + ", identityId=" + identityId);
				}
				verify = false;
				break;
			}
		} while (false);
	
		if (!verify) {
			res.setCode(MMORPGErrorCode.CODE_AUTH_FAILURE_PARAM);
			if (!Strings.isNullOrEmpty(identityId) && !Strings.isNullOrEmpty(identityName)) {
				StatFunctions.authStat(identityId, identityName, res.getCode(), req.getQdCode1(), req.getQdCode2(), connection.getIP());
			}
			
			return res;
		}
		
		// 测试机器人不需要校验
		boolean isRobot = false; 
		if (StringUtils.equals(req.getUuid(), "NewBeeRobot")) {
			isRobot = true;
		}

		if (!isRobot) {
			// TODO: md5的校验..
			// boolean verifyMD5 = true;
			boolean verifyMD5 = req.getSign().equals(MD5.digest(identityId + req.getTimeStamp() + HttpService.HttpCommunicationKey));
			if (!verifyMD5) {
				if (logger.isDebugEnabled()) {
					logger.debug("verify error, invalid md5, identityId=" + identityId);
				}
				res.setCode(MMORPGErrorCode.CODE_AUTH_FAILURE_MD5);
				StatFunctions.authStat(identityId, identityName, res.getCode(), req.getQdCode1(), req.getQdCode2(), connection.getIP());
				return res;
			}

			// TODO: 时戳的校验
			boolean verifyTimeStamp = true;
			// boolean verifyTimeStamp = TimeStampUtil.check(req.getTimeStamp(),
			// TimeStampUtil.BigOffset);
			if (!verifyTimeStamp) {
				if (logger.isDebugEnabled()) {
					logger.debug("verify error, invalid timestamp, identityId=" + identityId);
				}
				res.setCode(MMORPGErrorCode.CODE_AUTH_FAILURE_TIMESTAMP);
				StatFunctions.authStat(identityId, identityName, res.getCode(), req.getQdCode1(), req.getQdCode2(), connection.getIP());
				return res;
			}
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("auth success identityId=" + identityId);
		}

		AuthIdentity identity = new AuthIdentity(identityId, identityName, req.getUuid(), req.getQdCode1(), req.getQdCode2());
		res.setIdentity(identity);
		res.setCode(MMORPGSuccessCode.CODE_AUTH_SUCCESS);
		
		StatFunctions.authStat(identityId, identityName, res.getCode(), req.getQdCode1(), req.getQdCode2(), connection.getIP());
		StatService.getInstance().getStatOnlineTicker().onConnected(connection.getIP());
		return res;
	}
}
