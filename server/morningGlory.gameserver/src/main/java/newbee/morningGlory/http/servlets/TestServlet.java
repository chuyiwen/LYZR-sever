package newbee.morningGlory.http.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Copyright (c) 2011 by 游爱.
 * @author XieEEE 2013-3-22 下午2:47:48
 */

public class TestServlet extends HttpServlet {

	private static final long serialVersionUID = 1318928381210450175L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.setContentType( "text/html" );
		response.setCharacterEncoding("utf-8");
		

		response.getWriter().print("<html xmlns='http://www.w3.org/1999/xhtml' xml:lang='zh-CN' dir='ltr'>");
		response.getWriter().print("<head>");
		response.getWriter().print("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/>");
		response.getWriter().print("</head>");
		response.getWriter().print("<body>");
		
		response.getWriter().println("<div><a href='/game/adm/reload'>Reload GameRef Data</a></div>");
		response.getWriter().println("<div><a href='/game/adm/log'>Log查看</a></div>");
		response.getWriter().println("<div ><a href='/game/adm/bsh'>BSH调试器</a></div>");
		response.getWriter().println("<div ><a style='color:red' href='/game/adm/notifyshutdown'>通知在线玩家Shutdown GameServer</a></div>");
		response.getWriter().println("<div ><a style='color:red' href='/game/adm/shutdown'>Shutdown GameServer</a></div>");

		
		response.getWriter().print("</body>");
		response.getWriter().print("</html>");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    request.setCharacterEncoding("utf-8");
	    response.getWriter().print( System.currentTimeMillis() );
	    return;
	}
	

}
