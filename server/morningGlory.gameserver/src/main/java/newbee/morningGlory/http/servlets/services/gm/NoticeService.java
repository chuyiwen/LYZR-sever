package newbee.morningGlory.http.servlets.services.gm;

import groovy.json.JsonOutput;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import newbee.morningGlory.http.HttpService;
import newbee.morningGlory.http.servlets.services.AbstractService;
import newbee.morningGlory.http.util.MD5;
import newbee.morningGlory.http.util.TimeStampUtil;
import newbee.morningGlory.http.util.Type;

import org.apache.log4j.Logger;

import sophia.foundation.communication.core.MessageFactory;
import sophia.foundation.util.DebugUtil;
import sophia.mmorpg.player.chat.event.ChatEventDefines;
import sophia.mmorpg.player.chat.event.G2C_Chat_System;
import sophia.mmorpg.world.ActionEventFacade;

public class NoticeService extends AbstractService {
	private static final Logger logger = Logger.getLogger(NoticeService.class.getName());

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// http://localhost/game/services?action=notice&content=test&name=name&title=title&version=version&noticeType=1
		if (logger.isDebugEnabled()) {
			logger.debug(JsonOutput.toJson(request.getParameterMap()));
		}
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		try {
			String content = request.getParameter("content");
			String name = request.getParameter("name");
			String title = request.getParameter("title");
			String version = request.getParameter("version");
			byte noticeType = Type.getByte(request.getParameter("noticeType"), (byte) 2);
			long tstamp = Long.parseLong(request.getParameter("tstamp"));
			String sign = request.getParameter("sign");
			if (!TimeStampUtil.check(tstamp, TimeStampUtil.MiddleOffset)) {
				response.getWriter().println("{ code:3,description:\"非法请求(时间戳过期)\"}");
				return;
			}

			if (!MD5.digest(name + title + version + noticeType + tstamp + HttpService.HttpCommunicationKey).equals(sign)) {
				response.getWriter().println("{ code:3,description:\"非法请求(密钥错误)\"}");
				return;
			}

			G2C_Chat_System res = MessageFactory.getConcreteMessage(ChatEventDefines.G2C_Chat_System);
			res.setType((byte) 2);
			res.setMsg(content);
			ActionEventFacade.sendMessageToWorld(res);
			response.getWriter().print("success");
		} catch (NumberFormatException e) {
			if (logger.isInfoEnabled()) {
				logger.info("消息错误");
			}
			response.getWriter().print("failure");
			logger.error(DebugUtil.printStack(e));
		}
		return;
	}

}
