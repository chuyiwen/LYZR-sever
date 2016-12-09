/**
 *   Copyright 2013-2015 Sophia
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
*/
package newbee.morningGlory.http.servlets.services.gm;

import groovy.json.JsonOutput;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import newbee.morningGlory.http.servlets.services.AbstractService;
import newbee.morningGlory.system.ComponentShieldMgr;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.util.DebugUtil;

public class ComponentSwitchService extends AbstractService {
	
	private static final Logger logger = Logger.getLogger(ComponentSwitchService.class);

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		if (logger.isDebugEnabled()) {
			logger.debug(JsonOutput.toJson(request.getParameterMap()));
		}
		
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");

		try {
			
			String componentName = request.getParameter("name");
			if (StringUtils.isEmpty(componentName)) {
				showComponentList(request, response);
			} else {
				// 0:代表关闭 1:开启
				byte state = Byte.parseByte(request.getParameter("state"));
				boolean ret = ComponentShieldMgr.changeComponentState(componentName, state == 0);
				if (ret == true) {
					response.getWriter().println("{ code:0,description:\"修改成功\"}");
				} else {
					response.getWriter().println("{ code:1,description:\"修改失败\"}");
				}
			}

		} catch (Throwable ex) {
			logger.error(DebugUtil.printStack(ex));
			response.getWriter().println("{ code:2,description:\"" + URLEncoder.encode(ex.toString()) + "\"}");
			return;
		}
		
	}
	
	private void showComponentList(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map<String, Boolean> componentStateList = ComponentShieldMgr.getComponentStateList();
		Set<Entry<String,Boolean>> entrySet = componentStateList.entrySet();
		Iterator<Entry<String, Boolean>> iterator = entrySet.iterator();
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("[");
		while (iterator.hasNext()) {
			Entry<String, Boolean> entry = iterator.next();
			byte state = entry.getValue() == true ? (byte)1 : (byte)0;
			strBuilder.append("{name:\"").append(entry.getKey()).append("\",state:").append(state).append("}");
			if (iterator.hasNext()) {
				strBuilder.append(",");
			}
		}
		strBuilder.append("]");
		response.getWriter().println(strBuilder.toString());
	}
}
