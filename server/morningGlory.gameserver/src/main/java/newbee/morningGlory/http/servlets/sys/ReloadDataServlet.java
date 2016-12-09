package newbee.morningGlory.http.servlets.sys;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import newbee.morningGlory.checker.RefChecker;
import newbee.morningGlory.checker.RefCheckerManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.game.GameRoot;
import sophia.game.ref.GameRefObject;
import sophia.game.ref.GameRefObjectChangedListener;
import sophia.game.ref.GameRefObjectDataService;

/**
 * Copyright (c) 2011 by 游爱.
 * 
 * @author XieEEE 2013-3-23 上午10:26:22
 */

public class ReloadDataServlet extends HttpServlet {
	private static final long serialVersionUID = -3424086022384309414L;

	private static final Logger logger = Logger.getLogger(ReloadDataServlet.class.getName());

	public static final String ReloadType_ParamName = "";
	public static final String GameRefObjectClass_ParamName = "";

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.error("Reloaded by : Address: " + request.getRemoteAddr());
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		response.getWriter().print("<body>");
		response.getWriter().print("<table border=1><tr><td>描述</td><td>GameRef数据类型</td>");
		response.getWriter().print("<td>");
		response.getWriter().print("<form action=# method=POST >");
		response.getWriter().print("<input type=hidden name=reloadAll value=true>");
		response.getWriter().print("<input type=submit value='Reload All' >");
		response.getWriter().print("</form>");
		response.getWriter().print("</td>");
		response.getWriter().print("</tr>");

		for (String cls : GameRoot.getGameRefObjectDataManager().getGameRefObjectKeys()) {
			List<GameRefObject> refs = (List<GameRefObject>)GameRoot.getGameRefObjectDataManager().loadGameRefObjects(cls);
			GameRefObject ref = refs.get(0);
			Class<? extends GameRefObject> clazz = ref.getClass();
			response.getWriter().print("<tr>");
			RefChecker<?> checker = RefCheckerManager._checkersMapRef.get(clazz);
			if (checker != null) {
				response.getWriter().print("<td>" + RefCheckerManager._checkersMapRef.get(clazz).getDescription() + "</td>");
				checker.check(ref);
				logger.info("数据检验 @ " + ref.getClass().getSimpleName() + "...OK");
			} else {
				response.getWriter().print("<td></td>");
			}
			response.getWriter().print("<td>" + cls.substring(cls.lastIndexOf(".") + 1) + "</td>");
			response.getWriter().print("<td>");
			response.getWriter().print("<form action=# method=POST >");
			response.getWriter().print("<input type=hidden name=className value=" + cls + ">");
			response.getWriter().print("<input type=hidden name=reloadType value=modify>");
			response.getWriter().print("<div style='text-align:center;'><input type=submit style='height:50' value='Reload By Modified' ></div>");
			response.getWriter().print("</form>");
			response.getWriter().print("</td>");
			response.getWriter().print("</tr>");
		}

		response.getWriter().print("</table>");
		response.getWriter().print("</body>");

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("utf-8");
		String reloadAll = request.getParameter("reloadAll");
		if (StringUtils.equals("true", reloadAll)) {
			if (logger.isInfoEnabled()) {
				logger.debug("接收到更新全部游戏引用数据请求");
			}
			reloadAll();
			response.setContentType("text/html");
			response.setCharacterEncoding("utf-8");
			response.getWriter().print("<font style='color:blue'>全部游戏引用数据更新完成-_- </font>");
			doGet(request, response);
			return;
		}

		String refKey = request.getParameter("className");
		String reloadType = request.getParameter("reloadType");

		String ids = "all"; // FIXME:需要支持指定条目的方式

		if (logger.isDebugEnabled()) {
			logger.info("接收到游戏引用数据更新请求:" + reloadType + " " + ids);
			logger.info(refKey);
		}

		reloadBy(reloadType, refKey, ids);

		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		response.getWriter().print("<font style='color:blue'>更新完成 @ " + reloadType + ">" + ids + " : " + refKey + "</font>");
		doGet(request, response);
		return;
	}

	private void reloadBy(String reloadType, String className, String idSet) {

		/**
		 * reloadType: modify|add className: GameRefObject的class全名 idSet:
		 * 逗号分隔的id列表|标识所有的'all'
		 */

		String[] ids = null;
		if (!"all".equals(idSet)) {
			ids = idSet.split(",");
		}

		GameRefObjectDataService dataServive = GameRoot.getGameRefObjectDataService();
		GameRefObjectChangedListener gameRefChangedListener = dataServive.getGameRefObjectChangedListener();

		try {
			gameRefChangedListener.updateGameRefObject(className, ids);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void reloadAll() {
		GameRefObjectDataService dataServive = GameRoot.getGameRefObjectDataService();
		GameRefObjectChangedListener gameRefChangedListener = dataServive.getGameRefObjectChangedListener();
		for (String class1 : GameRoot.getGameRefObjectDataManager().getGameRefObjectKeys()) {
			gameRefChangedListener.updateGameRefObject(class1, new String[0]);
		}
	}

}
