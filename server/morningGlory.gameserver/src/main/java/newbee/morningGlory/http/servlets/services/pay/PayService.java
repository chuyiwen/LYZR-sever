package newbee.morningGlory.http.servlets.services.pay;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import newbee.morningGlory.http.HttpService;
import newbee.morningGlory.http.servlets.services.AbstractService;
import newbee.morningGlory.http.util.MD5;
import newbee.morningGlory.http.util.StringUtil;
import newbee.morningGlory.http.util.UrlBase64;
import newbee.morningGlory.mmorpg.operatActivities.OperatActivityMgr;
import newbee.morningGlory.mmorpg.operatActivities.OperatActivityType;
import newbee.morningGlory.mmorpg.player.activity.QuickRecharge.MGPlayerQuickRechargeComponent;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.util.DebugUtil;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.auth.AuthIdentity;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.stat.StatService;
import sophia.mmorpg.utils.Type;
import sophia.stat.StatRechargeData;

/**
 * Copyright (c) 2014 by 游爱.
 * 
 * 
 */

public class PayService extends AbstractService {

	private static final Logger logger = Logger.getLogger(PayService.class);

	@Override
	public synchronized void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("接收到服务器充值请求");
		}
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");

		Player player = null;
		String identityName = null;
		String playerName = null;
		String sign = null;
		float payMoney = 0;
		long payTime = 0l;
		int gameUnBindedGold = 0;
		try {

			identityName = request.getParameter("identityName");
			playerName = UrlBase64.decode(request.getParameter("playerName"));
			String gold = request.getParameter("gameGold");
			String money = request.getParameter("payMoney");
			String time = request.getParameter("payTime");
			String refId = request.getParameter("refId");
			if (!StringUtils.isNumeric(gold)) {
				response.getWriter().println("{ code:3,description:\"元宝 非整数\"}");
				return;
			}
			if (!StringUtil.isNumber(money)) {
				response.getWriter().println("{ code:3,description:\"充值金额 非数字\"}");
				return;
			}
			if (!StringUtils.isNumeric(time) || Type.getLong(time, 0) == 0) {
				payTime = System.currentTimeMillis();
			} else {
				payTime = Type.getLong(time, 0);
			}
			gameUnBindedGold = Integer.parseInt(gold);
			payMoney = Float.parseFloat(money);
			sign = request.getParameter("sign");
			if (!MD5.digest(identityName + playerName + gameUnBindedGold + payMoney + HttpService.HttpCommunicationKey).equals(sign)) {
				logger.error("充值异常@非法请求(密钥错误): playerName=" + playerName + " gameMoney=" + payMoney + " sign=" + sign);
				response.getWriter().println("{ code:1,description:\"非法请求(密钥错误)\"}");
				return;
			}

			if (!StringUtils.isEmpty(playerName)) {
				player = MMORPGContext.getPlayerComponent().getPlayerManager().getPlayerByName(playerName);
			}

			if (player == null) {
				logger.error("充值异常@无法找到玩家: playerName=" + playerName + " gameMoney=" + payMoney + " sign=" + sign);
				response.getWriter().println("{ code:2,description:\"无法找到玩家\"}");
				return;
			} else {
				MGPlayerQuickRechargeComponent quickRechargeComponent = (MGPlayerQuickRechargeComponent) player.getTagged(MGPlayerQuickRechargeComponent.Tag);
				if(!quickRechargeComponent.isValid(refId, gameUnBindedGold)){
					logger.error("充值异常@充值数量与refId 不一致: playerName=" + playerName + " gameMoney=" + payMoney + " sign=" + sign);
					response.getWriter().println("{ code:4,description:\"充值数量与refId 不一致\"}");
					return;
				}
				int quickRechargeReward = quickRechargeComponent.getQuickRechargeReward(gameUnBindedGold);
				player.getPlayerMoneyComponent().addUnbindGold(gameUnBindedGold + quickRechargeReward, ItemOptSource.Recharge);
				MMORPGContext.getPlayerComponent().getSaveService().saveImmediateData(player);

				OperatActivityMgr.getInstance().modifyOperatActivity(OperatActivityType.FirstRechargeGift, player);
				OperatActivityMgr.getInstance().modifyOperatActivity(OperatActivityType.TotalRechargeGift, player, gameUnBindedGold);
				OperatActivityMgr.getInstance().modifyOperatActivity(OperatActivityType.EveryDayRechargeGift, player);
				AuthIdentity authIdentity = (AuthIdentity) player.getIdentity();
				StatRechargeData data = new StatRechargeData(player.getId(), player.getName(), player.getIdentity().getName(), authIdentity.getQdCode1(),
						authIdentity.getQdCode2(), gameUnBindedGold, payMoney, payTime);
				StatService.getInstance().save(data);
				response.getWriter().println("{ code:0,description:\"\"}");
				return;
			}
		} catch (Throwable ex) {
			logger.error("充值异常: playerName=" + playerName + " gameMoney=" + payMoney + " sign=" + sign + " " + DebugUtil.printStack(ex));
			response.getWriter().println("{ code:3,description:\"" + URLEncoder.encode(ex.toString()) + "\"}");
			return;
		}
	}

}
