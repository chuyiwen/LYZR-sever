package newbee.morningGlory.http;

import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;

/**
 * Copyright (c) 2011 by 游爱.
 * @author XieEEE 2013-3-23 上午10:30:08
 */

public interface ServletServer {
	void addFilter(Filter filter,String urlPattern);
	
	public void addServlet(HttpServlet httpServlet,String mappingPattern);
	
	public void start();
	
	public void stop();
	
	public boolean isStarted();
	
}
