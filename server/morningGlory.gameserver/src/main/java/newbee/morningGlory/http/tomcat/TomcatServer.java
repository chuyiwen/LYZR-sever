package newbee.morningGlory.http.tomcat;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;

import newbee.morningGlory.MorningGloryContext;
import newbee.morningGlory.http.ServletServer;

import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.deploy.FilterDef;
import org.apache.catalina.deploy.FilterMap;
import org.apache.catalina.startup.Tomcat;
import org.apache.log4j.Logger;

import sophia.foundation.util.PropertiesWrapper;

/**
 * Copyright (c) 2011 by 游爱.
 * 
 * @author XieEEE 2013-3-23 上午11:24:19
 */

public class TomcatServer implements ServletServer {

	private static final Logger logger = Logger.getLogger(TomcatServer.class);
	
	private volatile boolean started = false;

	private static String httpHost;
	private static int httpPort;

	private static String tomcatDir;
	private static String webAppDir;
	private static String webPath;
	private static String webDoc;
	private Tomcat tomcat;
	private List<HttpServlet> servlets = new ArrayList<HttpServlet>();
	private List<String> mappingPatterns = new ArrayList<String>();
	private LinkedHashMap<Filter, String> filters = new LinkedHashMap<Filter, String>();

	private void init() {
		PropertiesWrapper properties = MorningGloryContext.getProperties();
		httpHost = properties.getProperty("newbee.morningGlory.http.HttpService.httpHost", "localhost");
		httpPort = properties.getIntProperty("newbee.morningGlory.http.HttpService.httpPort", 80);

		String projectPath = new File("").getAbsolutePath();
		File file = new File(projectPath + "/webapps/game");
		if (!file.exists()) {
			file.mkdirs();
		}

		tomcatDir = projectPath;
		webAppDir = tomcatDir + "/webapps";
		webPath = "/game";
		webDoc = "game";

		if (logger.isDebugEnabled()) {
			logger.debug("Embed Tomcat @ httpHost:" + httpHost);
			logger.debug("Embed Tomcat @ httpPort:" + httpPort);
			logger.debug("Embed Tomcat @ tomcatDir:" + tomcatDir);
			logger.debug("Embed Tomcat @ webAppDir:" + webAppDir);
			logger.debug("Embed Tomcat @ webPath:" + webPath);
			logger.debug("Embed Tomcat @ webDoc:" + webDoc);
		}
	}

	@Override
	public void start() {

		if (logger.isDebugEnabled()) {
			logger.debug("Embed Tomcat @ ... 开始启动 ...");
		}

		init();
		
		//设置tomcat启动失败抛出异常  
		System.setProperty("org.apache.catalina.startup.EXIT_ON_INIT_FAILURE", "true");
		
		tomcat = new Tomcat();
		// tomcat.setHostname(httpHost);
		tomcat.setPort(httpPort);

		// tomcat目录
		tomcat.setBaseDir(tomcatDir);
		// 设置程序的目录信息
		tomcat.getHost().setAppBase(webAppDir);

		// Add AprLifecycleListener
		StandardServer server = (StandardServer) tomcat.getServer();
		AprLifecycleListener listener = new AprLifecycleListener();
		server.addLifecycleListener(listener);
		// 注册关闭端口以进行关闭
		// tomcat.getServer().setPort(90);

		// 加载上下文
		StandardContext standardContext = new StandardContext();
		standardContext.setPath(webPath);// contextPath
		standardContext.setDocBase(webDoc);// 文件目录位置
		standardContext.addLifecycleListener(new Tomcat.DefaultWebXmlListener());// 保证已经配置好了。
		standardContext.addLifecycleListener(new Tomcat.FixContextListener());
		standardContext.setSessionCookieName("t-session");

		tomcat.getHost().addChild(standardContext);

		for (int i = 0; i < servlets.size(); i++) {
			HttpServlet servlet = servlets.get(i);
			String mappingPattern = mappingPatterns.get(i);

			Tomcat.addServlet(standardContext, servlet.getClass().getName(), servlet);
			standardContext.addServletMapping(mappingPattern, servlet.getClass().getName());
		}

		for (Filter filter : filters.keySet()) {
			FilterDef def = new FilterDef();
			def.setFilter(filter);
			def.setFilterName(filter.getClass().getName());
			def.setFilterClass(filter.getClass().getName());
			standardContext.addFilterDef(def);

			FilterMap filterMap = new FilterMap();
			filterMap.setFilterName(filter.getClass().getName());
			filterMap.addURLPattern(filters.get(filter));

			standardContext.addFilterMap(filterMap);
		}

		try {
			standardContext.init();
		} catch (Exception ex) {
			logger.error("ERROR! Tomcat 加载上下文异常!", ex);
		}

		try {
			tomcat.start();
			started = true;
		} catch (Exception ex) {
			logger.error("ERROR! Tomcat 启动异常!", ex);
			System.exit(0);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Embed Tomcat @ ... 启动完毕");
		}

		tomcat.getServer().await();
	}

	@Override
	public void stop() {

		if (logger.isDebugEnabled()) {
			logger.debug("Embed Tomcat @ ... 开始关闭 ... ");
		}

		try {
			tomcat.getServer().stop();
		} catch (Exception ex) {
			logger.error("ERROR! Tomcat 关闭异常!", ex);
		}

	}

	@Override
	public void addServlet(HttpServlet httpServlet, String mappingPattern) {
		servlets.add(httpServlet);
		mappingPatterns.add(mappingPattern);
	}

	@Override
	public void addFilter(Filter filter, String urlPattern) {
		filters.put(filter, urlPattern);
	}

	public boolean isStarted() {
		return started;
	}
}
