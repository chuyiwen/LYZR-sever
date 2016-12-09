package newbee.morningGlory.http.servlets.services.gm;

import groovy.json.JsonOutput;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import newbee.morningGlory.http.HttpService;
import newbee.morningGlory.http.servlets.services.AbstractService;
import newbee.morningGlory.http.util.MD5;
import newbee.morningGlory.http.util.StringUtil;
import newbee.morningGlory.http.util.TimeStampUtil;

import org.apache.log4j.Logger;

import sophia.foundation.util.DebugUtil;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.persistence.immediatelySave.PlayerImmediateDaoFacade;
import sophia.mmorpg.player.state.PlayerStateMgr;

public class AccountService extends AbstractService {

	private static final Logger logger = Logger.getLogger(AccountService.class.getName());

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// localhost/game/services?action=account&playerName=123456&identityName=&states=14&tstamp=10&sign=AFC4B86DE1D85E42967AA68E5CB4607A&opType=2
		// localhost/game/services?action=account&playerName=123456&identityName=&states=13&tstamp=10&sign=26CC5D9D46D0480ADB791B0EBD39CAB2&opType=forbid
		// Disabled=13
		// DisallowTalk=11
		if (logger.isDebugEnabled()) {
			logger.debug(JsonOutput.toJson(request.getParameterMap()));
		}
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		Player player = null;

		try {
			String playerName = request.getParameter("playerName");
			byte states = Byte.parseByte(request.getParameter("states"));
			long tstamp = Long.parseLong(request.getParameter("tstamp"));
			String sign = request.getParameter("sign");
			String cutGoldCountStr = request.getParameter("cutGoldCount");
			String cutBindGoldCountStr = request.getParameter("cutBindGoldCount");
			int cutGoldCount = 0;
			int cutBindGoldCount = 0;
			if(StringUtil.isNumber(cutGoldCountStr)){
				cutGoldCount = Integer.parseInt(cutGoldCountStr);
			}
			
			if(StringUtil.isNumber(cutBindGoldCountStr)){
				cutBindGoldCount = Integer.parseInt(cutBindGoldCountStr);
			}
			
			if (!TimeStampUtil.check(tstamp, TimeStampUtil.MiddleOffset)) {
				response.getWriter().println("{ code:3,description:\"非法请求(时间戳过期)\"}");
				return;
			}

			if (!MD5.digest(playerName + states + tstamp + HttpService.HttpCommunicationKey).equals(sign)) {
				response.getWriter().println("{ code:3,description:\"非法请求(密钥错误)\"}");
				return;
			}
			
			if(cutGoldCount < 0){
				response.getWriter().println("{ code:3,description:\"非法请求(非绑定元宝只能扣除不能增加)\"}");
				return;
			}
			
			if(cutBindGoldCount < 0){
				response.getWriter().println("{ code:3,description:\"非法请求(绑定元宝只能扣除不能增加)\"}");
				return;
			}
		
			if (!StringUtil.isEmpty(playerName)) {
				player = MMORPGContext.getPlayerComponent().getPlayerManager().getPlayerByName(playerName);
			}

			if (player == null) {
				response.getWriter().println("{ code:1,description:\"无法找到玩家或者玩家不在线\"}");
				return;
			}
			
			boolean result = true;

			if (cutBindGoldCount > 0) {
				if (player.isOnline()) {
					result = player.getPlayerMoneyComponent().subBindGold(cutBindGoldCount,ItemOptSource.GMSend);
				} else {
					result = player.getPlayerMoneyComponent().subBindGoldNotNotice(cutBindGoldCount,ItemOptSource.GMSend);
				}
				
				if (!result) {
					response.getWriter().println(String.format("{ code:3,description:\"%s\"}", "扣除绑定元宝失败"));
					return;
				}
			}

			if (cutGoldCount > 0) {
				if (player.isOnline()) {
					result = player.getPlayerMoneyComponent().subUnbindGold(cutGoldCount,ItemOptSource.GMSend);
				} else {
					result = player.getPlayerMoneyComponent().subUnbindGoldNotNotice(cutGoldCount,ItemOptSource.GMSend);
				}
				
				if (!result) {
					response.getWriter().println(String.format("{ code:3,description:\"%s\"}", "扣除非绑定元宝失败"));
					return;
				}
			}

			if (result) {
				exeAccountOP(player, states);
				response.getWriter().println("{ code:0,description:\"\"}");
			}
			
			MMORPGContext.getPlayerComponent().getSaveService().saveImmediateData(player);
			PlayerImmediateDaoFacade.update(player);
		} catch (Throwable ex) {
			logger.error(DebugUtil.printStack(ex));
			response.getWriter().println("{ code:2,description:\"" + URLEncoder.encode(ex.toString()) + "\"}");
			return;
		}
	}

	private void exeAccountOP(Player player, byte states) {
		if (logger.isDebugEnabled()) {
			logger.debug("接收到服务器账号操作请求");
		}
	
		PlayerStateMgr playerStateMgr = player.getPlayerStateMgr();
		
		// 解除禁言
		if (playerStateMgr.hasState(PlayerStateMgr.DisallowTalk) && playerStateMgr.hasState(states, PlayerStateMgr.AllowTalk)) {
			playerStateMgr.setState(PlayerStateMgr.AllowTalk);
		}
		
		// 禁言
		if (playerStateMgr.hasState(PlayerStateMgr.AllowTalk) && playerStateMgr.hasState(states, PlayerStateMgr.DisallowTalk)) {
			playerStateMgr.setState(PlayerStateMgr.DisallowTalk);
		}
		
		// 解除封号
		if (playerStateMgr.hasState(PlayerStateMgr.Disabled) && playerStateMgr.hasState(states, PlayerStateMgr.Enabled)) {
			playerStateMgr.setState(PlayerStateMgr.Enabled);
		}
		
		// 封号
		if (playerStateMgr.hasState(PlayerStateMgr.Enabled) && playerStateMgr.hasState(states, PlayerStateMgr.Disabled)) {
			playerStateMgr.setState(PlayerStateMgr.Disabled);
		}
		
		// 踢下线
		if (playerStateMgr.hasState(PlayerStateMgr.Online) && playerStateMgr.hasState(states, PlayerStateMgr.OffLine)) {
			kickOutPlayer(player);
		}

	}

	private void kickOutPlayer(Player player) {
		if (logger.isDebugEnabled()) {
			logger.debug("接收到踢玩家下线请求,name:" + player.getName());
		}

		// 踢下线
		if (player.isOnline()) {
			PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
			playerManager.kickoutPlayerCharacter(player);
			if (logger.isInfoEnabled()) {
				logger.info("kickOut, GM account option, playerName=" + player.getName());
			}
		}
	}

}
