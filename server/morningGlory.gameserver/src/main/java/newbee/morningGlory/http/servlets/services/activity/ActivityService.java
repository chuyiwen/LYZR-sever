package newbee.morningGlory.http.servlets.services.activity;

import groovy.json.JsonOutput;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import newbee.morningGlory.http.servlets.services.AbstractService;
import newbee.morningGlory.mmorpg.operatActivities.OperatActivityMgr;
import newbee.morningGlory.mmorpg.operatActivities.OperatActivityType;

import org.apache.log4j.Logger;

import sophia.foundation.util.DebugUtil;
import sophia.foundation.util.Pair;
import sophia.mmorpg.utils.Type;

/**
 * 
 * Copyright (c) 2014 by 游爱.
 * 
 */
public class ActivityService extends AbstractService {
	private final static Logger logger = Logger.getLogger(ActivityService.class);
	String success = "1";
	String fail = "0";

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");

		PrintWriter writer = response.getWriter();
		Map<String, String> ret = new LinkedHashMap<String, String>();

		int type = Type.getInt(request.getParameter("type"), 0);
	
		OperatActivityType operatActivityType = OperatActivityType.get(type);
		if (operatActivityType == null) {
			ret.put("status", fail);
			ret.put("errorMsg", "类型参数错误：" + type);
		} else {
			Pair<Boolean, Throwable> load = OperatActivityMgr.getInstance().load(operatActivityType);
			if (load.getKey()) {
				ret.put("status", success);
			} else {
				ret.put("status", fail);
				if (load.getValue() != null) {
					String stackString = getStackString(load.getValue());
					if (stackString.length() > 800)
						stackString = stackString.substring(0, 800);
					ret.put("errorStack", stackString);
				}
			}
		}
		writer.write(JsonOutput.toJson(ret));
		writer.flush();
	}

	public static String getStackString(Throwable t) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		t.printStackTrace(ps);
		try {
			return new String(os.toByteArray(), "utf-8");
		} catch (UnsupportedEncodingException e) {
			logger.error(DebugUtil.printStack(e));
		}finally{
			ps.close();
			try {
				os.close();
			} catch (IOException e) {
				logger.error("", e);
			}
		}
		return "";
	}

}
