package newbee.morningGlory.http.servlets.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import newbee.morningGlory.http.servlets.services.activity.ActivityService;
import newbee.morningGlory.http.servlets.services.gm.AccountService;
import newbee.morningGlory.http.servlets.services.gm.ChatService;
import newbee.morningGlory.http.servlets.services.gm.ComponentSwitchService;
import newbee.morningGlory.http.servlets.services.gm.MailService;
import newbee.morningGlory.http.servlets.services.gm.NoticeService;
import newbee.morningGlory.http.servlets.services.gm.PlayerChangePropertyService;
import newbee.morningGlory.http.servlets.services.gm.PlayerChangeStateService;
import newbee.morningGlory.http.servlets.services.gm.PlayerInfoService;
import newbee.morningGlory.http.servlets.services.gm.RewardService;
import newbee.morningGlory.http.servlets.services.pay.GiftsCardService;
import newbee.morningGlory.http.servlets.services.pay.PayService;
import newbee.morningGlory.http.servlets.services.sys.ShutDownService;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.mmorpg.MMORPGContext;

/**
 * Copyright (c) 2011 by 游爱.
 * 
 * @author XieEEE 2013-7-29 下午4:33:41
 */

public class ServicesServlet extends HttpServlet {

	private static final long serialVersionUID = 288358202151249052L;

	private static final MailService mailService = new MailService();
	private static final AccountService accountService = new AccountService();
	private static final PayService payService = new PayService();
	private static final GiftsCardService giftsCardService = new GiftsCardService();
	private static final ShutDownService shutDownService = new ShutDownService();
	private static final NoticeService noticeService = new NoticeService();
	private static final PlayerInfoService playerInfoService = new PlayerInfoService();
	private static final RewardService rewardService = new RewardService();
	private static final ActivityService activityService = new ActivityService();
	private static final ChatService chatService = new ChatService();
	private static final PlayerChangePropertyService changePropertyService = new PlayerChangePropertyService();
	private static final PlayerChangeStateService changeStateService = new PlayerChangeStateService();
	private static final ComponentSwitchService componentSwitchService = new ComponentSwitchService();
	private static final Logger logger = Logger.getLogger(ServicesServlet.class.getName());

	// TODO FIXME 调试用
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("action:" + request.getParameter("action"));
		}
		if (!MMORPGContext.isServerRunning()) {
			response.getWriter().println("{ code:1,description:\"失败，服务器正在关闭中...\"}");
			return;
		}
		String action = request.getParameter("action");
		if ("account".equals(action)) {
			accountService.doPost(request, response);
		} else if ("pay".equals(action)) {
			payService.doPost(request, response);
		} else if ("mail".equals(action)) {
			mailService.doPost(request, response);
		} else if ("giftsCard".equals(action)) {
			giftsCardService.doPost(request, response);
		} else if ("shutDown".equals(action)) {
			shutDownService.doPost(request, response);
		} else if ("notice".equals(action)) {
			noticeService.doPost(request, response);
		} else if ("playerInfo".equals(action)) {
			playerInfoService.doPost(request, response);
		}else if ("scrollNotice".equals(action)) {
			chatService.doPost(request, response);
		}else if ("reward".equals(action)) {
			rewardService.doPost(request, response);
		} else if(StringUtils.equals("activity", action)){
			activityService.doPost(request, response);
		} else if("playerChangeProperty".equals(action)){
			changePropertyService.doPost(request, response);
		} else if("playerChangeState".equals(action)){  
			changeStateService.doPost(request, response);
		} else if("componentSwitch".equals(action)) {
			componentSwitchService.doPost(request, response);
		}
	}

}
