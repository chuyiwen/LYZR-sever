package newbee.morningGlory.http.servlets.services.gm;

import groovy.json.JsonOutput;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import newbee.morningGlory.debug.DebugComponent;
import newbee.morningGlory.http.HttpService;
import newbee.morningGlory.http.servlets.services.AbstractService;
import newbee.morningGlory.http.util.MD5;
import newbee.morningGlory.http.util.StringUtil;

import org.apache.log4j.Logger;

import sophia.foundation.util.DebugUtil;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.persistence.immediatelySave.PlayerImmediateDaoFacade;

public class PlayerChangeStateService extends AbstractService {
	private static final Logger logger = Logger.getLogger(PlayerChangeStateService.class.getName());
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if(logger.isDebugEnabled()){
			logger.debug(JsonOutput.toJson(request.getParameterMap()));
		}
		
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		
		Player player = null ;
		
		try{
			String playerName = request.getParameter("playerName");
			String sign = request.getParameter("sign");
			String cmdstr = request.getParameter("cmdstr");
			if(StringUtil.isEmpty(cmdstr)){
				response.getWriter().println("{ code:3,description:\"cmdstr is error\"}");
			}
			
			if (!MD5.digest(playerName + cmdstr + HttpService.HttpCommunicationKey).equals(sign)) {
				response.getWriter().println("{ code:3,description:\"非法请求(密钥错误)\"}");
				return;
			}

			if (!StringUtil.isEmpty(playerName)) {
				player = MMORPGContext.getPlayerComponent().getPlayerManager().getPlayerByName(playerName);
			}
			if (player == null) {
				response.getWriter().println("{ code:1,description:\"无法找到玩家或者玩家不在线\"}");
				return;
			}
				
			DebugComponent debugComponent = (DebugComponent) player.getTagged(DebugComponent.Tag);
			
			if(debugComponent.execDebug(cmdstr)){
				MMORPGContext.getPlayerComponent().getSaveImmediateService().saveImmediateData(player);
				PlayerImmediateDaoFacade.update(player);
				response.getWriter().println("{ code:0,description:\"\"}");
			}else{
				response.getWriter().println("{ code:1,description:\"cmdstr is error"+cmdstr+"\"}");
			}
			
			
		}catch(Throwable ex){
			logger.error(DebugUtil.printStack(ex));
			response.getWriter().println("{ code :2 ,description:\"" + URLEncoder.encode(ex.toString()) + "\"}");
		}
	}

}
