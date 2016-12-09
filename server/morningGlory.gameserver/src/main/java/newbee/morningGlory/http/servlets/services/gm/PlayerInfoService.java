package newbee.morningGlory.http.servlets.services.gm;

import groovy.json.JsonOutput;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import newbee.morningGlory.http.HttpService;
import newbee.morningGlory.http.servlets.services.AbstractService;
import newbee.morningGlory.http.util.MD5;
import newbee.morningGlory.http.util.TimeStampUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.util.DebugUtil;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.player.Player;

public class PlayerInfoService extends AbstractService {
	private static final Logger logger = Logger.getLogger(PlayerInfoService.class.getName());

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// localhost/game/services?action=playerInfo&playerName=123456&identityName=&tstamp=10
		if (logger.isDebugEnabled()) {
			logger.debug(JsonOutput.toJson(request.getParameterMap()));
		}
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		Player player = null;

		try {
			String playerName = request.getParameter("playerName");
			String identityName = request.getParameter("identityName");
			long tstamp = Long.parseLong(request.getParameter("tstamp"));
			String sign = request.getParameter("sign");
			if (!TimeStampUtil.check(tstamp, TimeStampUtil.MiddleOffset)) {
				response.getWriter().println("{ code:3,description:\"非法请求(时间戳过期)\"}");
				return;
			}

			if (!MD5.digest(identityName + playerName + tstamp + HttpService.HttpCommunicationKey).equals(sign)) {
				response.getWriter().println("{ code:3,description:\"非法请求(密钥错误)\"}");
				return;
			}

			if (!StringUtils.isEmpty(playerName)&&!StringUtils.isEmpty(identityName)) {
				player = MMORPGContext.getPlayerComponent().getPlayerManager().getPlayerByName(playerName);
			}
			if (player == null) {
				response.getWriter().println("{ code:1,description:\"无法找到玩家\"}");
				return;
			} else {
				response.getWriter().println(player.getProperty().toString());
				return;
			}
		} catch (Throwable ex) {
			logger.error(DebugUtil.printStack(ex));
			response.getWriter().println("{ code:2,description:\"" + URLEncoder.encode(ex.toString()) + "\"}");
			return;
		}
	}
}
