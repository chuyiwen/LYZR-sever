package newbee.morningGlory.http.servlets.sys;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import newbee.morningGlory.MorningGloryContext;
import sophia.foundation.util.PropertiesWrapper;

public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 3956122161958850765L;
	public static Map<String, String> users = new HashMap<String, String>();

	@Override
	public void init() throws ServletException {
		super.init();
		PropertiesWrapper properties = MorningGloryContext.getProperties();
		String password = properties.getProperty("newbee.morningGlory.http.HttpService.login.password", "123456");
		users.put("管理员", password);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		resp.setCharacterEncoding("utf-8");
		resp.setContentType("text/html");
		resp.getWriter().print("<html xmlns='http://www.w3.org/1999/xhtml' xml:lang='zh-CN' dir='ltr'>");
		resp.getWriter().print("<head>");
		resp.getWriter().print("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/>");
		resp.getWriter().print("</head>");
		resp.getWriter().print("<body>");
		resp.getWriter().print("<form action=# method=POST >");
		resp.getWriter().print("账号：<input type=text name='user' value='' /><br/>");
		resp.getWriter().print("密码：<input type=password name='pwd' value='' /><br/>");
		resp.getWriter().print("<input type=submit value=' 提交 ' >");
		resp.getWriter().print("</form>");
		resp.getWriter().print("</body></html>");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		resp.setCharacterEncoding("utf-8");
		String user = req.getParameter("user");
		String pwd = req.getParameter("pwd");
		String string = users.get(user);
		if (string == null || !string.equals(pwd)) {
			resp.sendRedirect("/game/login");
			return;
		}
		req.getSession().setAttribute("user", user);
		resp.sendRedirect("/game/adm/test");
	}

}
