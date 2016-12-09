package newbee.morningGlory.http.servlets.services.gm;

import groovy.json.JsonOutput;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import newbee.morningGlory.http.HttpService;
import newbee.morningGlory.http.servlets.services.AbstractService;
import newbee.morningGlory.http.util.MD5;
import newbee.morningGlory.http.util.TimeStampUtil;
import newbee.morningGlory.http.util.Type;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.util.DebugUtil;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.itemBag.ItemPair;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class RewardService extends AbstractService {
	private static final Logger logger = Logger.getLogger(RewardService.class.getName());

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// localhost/game/services?action=reward&playerName=123456&identityName=&tstamp=10&reward={gold:100,bindedGold:10,unbindedGold:1,itemRefId:item_2exp,itemNum:10}
		if (logger.isDebugEnabled()) {
			logger.debug(JsonOutput.toJson(request.getParameterMap()));
		}
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		Player player = null;

		try {
			String playerName = request.getParameter("playerName");
			String identityName = request.getParameter("identityName");
			long tstamp = Long.parseLong(request.getParameter("tstamp"));
			String sign = request.getParameter("sign");
			String reward = request.getParameter("reward");
			if (!TimeStampUtil.check(tstamp, TimeStampUtil.MiddleOffset)) {
				response.getWriter().println("{ code:3,description:\"非法请求(时间戳过期)\"}");
				return;
			}

			if (!MD5.digest(identityName + playerName + tstamp + HttpService.HttpCommunicationKey).equals(sign)) {
				response.getWriter().println("{ code:3,description:\"非法请求(密钥错误)\"}");
				return;
			}

			if (!StringUtils.isEmpty(playerName)&&!StringUtils.isEmpty(identityName)) {
				player = MMORPGContext.getPlayerComponent().getPlayerManager().getPlayerByName(playerName);
			}
			if (player == null) {
				response.getWriter().println("{ code:1,description:\"无法找到玩家\"}");
				return;
			} else {
				if (StringUtils.isNotEmpty(reward) && !"null".equals(reward)) {
					Gson gson = new Gson();
					Map<String, String> rewardMap = gson.fromJson(reward, new TypeToken<Map<String, String>>() {
					}.getType());
					int gold = Type.getInt(rewardMap.get("gold"), 0);
					int bindedGold = Type.getInt(rewardMap.get("bindedGold"), 0);
					int unbindedGold = Type.getInt(rewardMap.get("unbindedGold"), 0);
					if (gold > 0) {
						player.getPlayerMoneyComponent().addGold(gold,ItemOptSource.GMSend);
					}
					if (bindedGold > 0) {
						player.getPlayerMoneyComponent().addBindGold(bindedGold,ItemOptSource.GMSend);
					}
					if (unbindedGold > 0) {
						player.getPlayerMoneyComponent().addUnbindGold(unbindedGold,ItemOptSource.GMSend);
					}
					if (logger.isDebugEnabled()) {
						logger.debug("addMoney 金币：" + gold + ",绑定元宝:" + bindedGold + ",元宝:" + unbindedGold);
					}

					String refId = rewardMap.get("itemRefId");
					int itemNumber = Type.getInt(rewardMap.get("itemNum"), 0);
					if (itemNumber > 0 && StringUtils.isNotEmpty(reward)) {
						ItemFacade.addItem(player, new ItemPair(refId, itemNumber, false),ItemOptSource.GMSend);
						if (logger.isDebugEnabled()) {
							logger.debug("addItem " + refId + "数量:" + itemNumber);
						}

					}

				}
				response.getWriter().println("{ code:1,description:\"发送奖励成功\"}");
				return;
			}
		} catch (Throwable ex) {
			logger.error(DebugUtil.printStack(ex));
			response.getWriter().println("{ code:2,description:发送奖励失败\"" + URLEncoder.encode(ex.toString()) + "\"}");
			return;
		}
	}
}
