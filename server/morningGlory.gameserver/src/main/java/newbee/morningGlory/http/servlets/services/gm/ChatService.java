package newbee.morningGlory.http.servlets.services.gm;


import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import newbee.morningGlory.http.HttpService;
import newbee.morningGlory.http.servlets.services.AbstractService;
import newbee.morningGlory.http.util.MD5;

import org.apache.log4j.Logger;

import sophia.foundation.util.DebugUtil;
import sophia.mmorpg.player.chat.sysytem.SystemPromptFacade;


/**
 * Copyright (c) 2011 by 游爱.
 * 
 * @author 李观武 Create on 2013-3-18 下午17:33:38
 * @version 1.0
 */
public class ChatService extends AbstractService {
	private static final Logger logger = Logger.getLogger(ChatService.class.getName());

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (logger.isInfoEnabled()) {
			logger.info("接收到服务器发送公告请求");
		}
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");

		try {
			// isScroll int 是否滚屏：1-是，2-否
			// sendSys int 发送系统频道：1-是，2-否
			// scrollStyle int 滚屏样式 若是滚屏 暂时默认都是普通样式，否则为-1
			// content String 公告内容 UrlEncode
			// remarks String 备注字段(用于扩展)
			// sign String sign=md5(isScroll + sendSys + gameSvrKey)
			int isScroll = Integer.parseInt(request.getParameter("isScroll"));
			int sendSys = Integer.parseInt(request.getParameter("sendSys"));
			int scrollStyle = Integer.parseInt(request.getParameter("scrollStyle"));
			String content = URLDecoder.decode(request.getParameter("content"), "UTF-8");
			String remarks = request.getParameter("remarks");
			String sign = request.getParameter("sign");

			if (!MD5.digest(String.valueOf(isScroll) + sendSys + HttpService.HttpCommunicationKey).equals(sign)) {
				response.getWriter().println("{ code:2,description:\"非法请求(密钥错误)\"}");
				return;
			}

			if (isScroll == 1) {
				SystemPromptFacade.broadCastScrollNotice(content);
			}
			if (sendSys == 1) {
				SystemPromptFacade.broadCastSystemNotice(content);
			}
			response.getWriter().println("{ code:0,description:\"\"}");
			return;
		} catch (Throwable ex) {
			logger.error(DebugUtil.printStack(ex));
			response.getWriter().println("{ code:3,description:\"" + URLEncoder.encode(ex.toString(), "UTF-8") + "\"}");
			return;
		}
	}

}
