package newbee.morningGlory.http;

import newbee.morningGlory.MorningGloryContext;
import newbee.morningGlory.http.servlets.RoleFilter;
import newbee.morningGlory.http.servlets.TestServlet;
import newbee.morningGlory.http.servlets.services.ServicesServlet;
import newbee.morningGlory.http.servlets.sys.BeanShellServlet;
import newbee.morningGlory.http.servlets.sys.LogServlet;
import newbee.morningGlory.http.servlets.sys.LoginServlet;
import newbee.morningGlory.http.servlets.sys.NotifyShutDownServlet;
import newbee.morningGlory.http.servlets.sys.ReloadDataServlet;
import newbee.morningGlory.http.servlets.sys.ShutdownServlet;
import newbee.morningGlory.http.tomcat.TomcatServer;

import org.apache.log4j.Logger;

import sophia.foundation.util.PropertiesWrapper;

public class HttpService {

	public static final String HttpCommunicationKey;

	private static final Logger logger = Logger.getLogger(HttpService.class);

	private static ServletServer servletServer = new TomcatServer();

	static {

		PropertiesWrapper properties = MorningGloryContext.getProperties();
		HttpCommunicationKey = properties.getProperty("newbee.morningGlory.http.HttpService.HttpCommunicationKey", "");
		// -----------不需要验证的url-------------
		servletServer.addServlet(new ServicesServlet(), "/services");
		servletServer.addServlet(new LoginServlet(), "/login");

		// -----------需要权限才能访问的路径
		servletServer.addServlet(new TestServlet(), "/adm/test");
		servletServer.addServlet(new LogServlet(), "/adm/log");
		servletServer.addServlet(new ReloadDataServlet(), "/adm/reload");
		servletServer.addServlet(new ShutdownServlet(), "/adm/shutdown");
		servletServer.addServlet(new NotifyShutDownServlet(), "/adm/notifyshutdown");
		servletServer.addServlet(new BeanShellServlet(), "/adm/bsh");

		// ------添加过滤器------
		servletServer.addFilter(new RoleFilter(), "/adm/*");

	}

	public static void startup() {
		if (logger.isDebugEnabled()) {
			logger.debug("Http Service @ ... 开始启动 ...");
		}
		servletServer.start();
	}

	public static void shutdown() {
		if (logger.isDebugEnabled()) {
			logger.debug("Http Service @ ... 开始关闭 ...");
		}
		servletServer.stop();
	}
	
	public static boolean isHttpStarted() {
		return servletServer.isStarted();
	}

}
