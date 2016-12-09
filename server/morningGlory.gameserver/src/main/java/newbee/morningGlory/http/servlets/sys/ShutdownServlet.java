package newbee.morningGlory.http.servlets.sys;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import newbee.morningGlory.GameApp;
import newbee.morningGlory.MorningGloryContext;
import newbee.morningGlory.http.HttpService;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.util.PropertiesWrapper;
import sophia.mmorpg.MMORPGContext;

import com.google.common.util.concurrent.Service.State;

/**
 * Copyright (c) 2011 by 游爱.
 * 
 * @author XieEEE 2013-3-22 下午2:47:48
 */

public class ShutdownServlet extends HttpServlet {
	private static final long serialVersionUID = 5620975982758879763L;
	private static final Logger logger = Logger.getLogger(ShutdownServlet.class.getName());
	/** 关服密码 */
	private String Shutdown_Game_Server_Password;
	// 是否正在关服
	private volatile boolean isShutdown = false;

	@Override
	public void init() throws ServletException {
		super.init();
		PropertiesWrapper properties = MorningGloryContext.getProperties();
		Shutdown_Game_Server_Password = properties.getProperty("newbee.morningGlory.http.HttpService.shut.down.password", "123456");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html");
		response.getWriter().print("<div>Shutdown Game Server</div>");
		response.setCharacterEncoding("utf-8");

		response.getWriter().print("<form action='#' method='POST'>");
		response.getWriter().print("关服确认密码:<input type='text' name='pwd' value=''>");
		response.getWriter().print("强制关服:<input type='text' name='force' value='false'>");
		response.getWriter().print("<input type='submit' value='确定关服' >");
		response.getWriter().print("</form>");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");

		String pwd = request.getParameter("pwd");
		if (!Shutdown_Game_Server_Password.equals(pwd)) {
			response.getWriter().println("密码验证错误！！！！");
			return;
		}
		
		String strForce = request.getParameter("force");
		boolean force = false;
		if (StringUtils.equals(strForce, "true")) {
			logger.info("force to shutdown server");
			force = true;
		}
		
		if (!force && isShutdown == true) {
			response.getWriter().println("已经发送关服指令！！！！");
			return;
		}
		
		isShutdown = true;
		MMORPGContext.setServerRunning(false);

		logger.info("接收到游戏服务器关闭请求，即将关闭游戏服务器...");

		State state = GameApp.shutDown(force);
		if (state == State.TERMINATED) {
			logger.info("游戏服务器关闭成功...");
			response.getWriter().println("游戏服务器已经成功关闭...");
			response.getWriter().flush();
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						HttpService.shutdown();
					} catch (Exception e) {
						logger.error(e);
					}
					System.exit(0);
				}
			}).start();
		} else {
			logger.info("服务器关闭失败...");
			response.getWriter().println("游戏服务器关闭失败，请登陆物理服务器查看日志...");
		}
	}
}
