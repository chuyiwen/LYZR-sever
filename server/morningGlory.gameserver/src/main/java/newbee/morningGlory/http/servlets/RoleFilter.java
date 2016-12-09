package newbee.morningGlory.http.servlets;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class RoleFilter implements Filter {
	private static Logger logger = Logger.getLogger(RoleFilter.class);

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest hreq = (HttpServletRequest) req;
		HttpServletResponse hresp = (HttpServletResponse) resp;
		hreq.setCharacterEncoding("utf-8");
		hresp.setCharacterEncoding("utf-8");
		Object user = hreq.getSession().getAttribute("user");
		if (user == null) {
			hresp.sendRedirect("/game/login");
			return;
		}
		chain.doFilter(req, resp);
		logger.info(user + " visit url：" + hreq.getRequestURI() + "?" + queryString(hreq));
	}

	String queryString(HttpServletRequest req) {
		StringBuffer s = new StringBuffer();
		Map<String, String[]> parameterMap = req.getParameterMap();
		for (Entry<String, String[]> e : parameterMap.entrySet()) {
			s.append(e.getKey()).append("=");
			String[] value = e.getValue();
			if (value != null && value.length >= 0)
				s.append(value[0]);
			s.append("&");
		}
		String string = s.toString();
		if (string.endsWith("&"))
			string = string.substring(0, string.length() - 1);
		return string;
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

}
