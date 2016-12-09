package newbee.morningGlory.http.servlets.sys;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * Copyright (c) 2011 by 游爱.
 * 
 * @author XieEEE 2013-3-23 上午10:26:22
 */

public class ReloadLog4jServlet extends HttpServlet {
	private static final long serialVersionUID = -3424086022384309414L;

	private static final Logger logger = Logger.getLogger(ReloadLog4jServlet.class.getName());

	public static final String ReloadType_ParamName = "";
	public static final String GameRefObjectClass_ParamName = "";

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		response.getWriter().print("<table border=1><tr><td>GameRef数据类型</td><td>操作</td><td>操作</td></tr>");

		// TODO FIXME 暂时不实现
		// for( Class<? extends GameRefObject> cls :
		// GameRoot.getGameRefObjectDataManager().get )
		// {
		// response.getWriter().print("<tr>");
		// response.getWriter().print("<td>"+cls.getName()+"</td>");
		// response.getWriter().print("<td>");
		// response.getWriter().print("<form action=# method=POST >");
		// response.getWriter().print("<input type=hidden name=className value="
		// + cls.getName() + ">");
		// response.getWriter().print("<input type=hidden name=reloadType value=modify>");
		// response.getWriter().print("<input type=submit value='Reload By Modified' >");
		// response.getWriter().print("</form>");
		// response.getWriter().print("</td>");
		// response.getWriter().print("<td>");
		// response.getWriter().print("<form action=# method=POST >");
		// response.getWriter().print("<input type=hidden name=className value="
		// + cls.getName() + ">");
		// response.getWriter().print("<input type=hidden name=reloadType value=add>");
		// response.getWriter().print("<input type=submit value='Reload By Added' >");
		// response.getWriter().print("</form>");
		// response.getWriter().print("</td>");
		// response.getWriter().print("</tr>");
		// }

		response.getWriter().print("</table>");

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("utf-8");

		String className = request.getParameter("className");
		String reloadType = request.getParameter("reloadType");
		String ids = "all"; // FIXME:需要支持指定条目的方式

		if (logger.isDebugEnabled()) {
			logger.debug("接收到游戏引用数据更新请求:" + reloadType + " " + ids);
			logger.debug(className);
		}

		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		response.getWriter().print("<font style='color:blue'>更新完成 @ " + reloadType + ">" + ids + " : " + className + "</font>");
		doGet(request, response);
		return;
	}
}
