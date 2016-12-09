package newbee.morningGlory.http.servlets.services.sys;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import newbee.morningGlory.GameApp;
import newbee.morningGlory.MorningGloryContext;
import newbee.morningGlory.http.HttpService;
import newbee.morningGlory.http.servlets.services.AbstractService;
import newbee.morningGlory.http.util.MD5;

import org.apache.log4j.Logger;

import sophia.foundation.util.DebugUtil;
import sophia.foundation.util.PropertiesWrapper;
import sophia.mmorpg.MMORPGContext;

import com.google.common.util.concurrent.Service.State;

public class ShutDownService extends AbstractService {
	private static final Logger logger = Logger.getLogger(ShutDownService.class.getName());
	/** 关服密码 */
	private String Shutdown_Game_Server_Password;
	// 是否正在关服
	private volatile boolean isShutdown = false;
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		PropertiesWrapper properties = MorningGloryContext.getProperties();
		Shutdown_Game_Server_Password = properties.getProperty("newbee.morningGlory.http.HttpService.shut.down.password", "123456");
		
		String pwd = request.getParameter("pwd");
		if (!MD5.digest(Shutdown_Game_Server_Password + HttpService.HttpCommunicationKey).equals(pwd)) {
			response.getWriter().println("密码验证错误！！！！");
			return;
		}
		
		if (isShutdown == true) {
			response.getWriter().println("已经发送关服指令！！！！");
			return;
		}
		
		isShutdown = true;
		MMORPGContext.setServerRunning(false);

		logger.info("接收到游戏服务器关闭请求，即将关闭游戏服务器...");

		State state = GameApp.shutDown(false);
		if (state == State.TERMINATED) {
			logger.info("游戏服务器关闭成功...");
			response.getWriter().print("success");
			response.getWriter().flush();
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						HttpService.shutdown();
					} catch (Exception e) {
						logger.error(DebugUtil.printStack(e));
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
