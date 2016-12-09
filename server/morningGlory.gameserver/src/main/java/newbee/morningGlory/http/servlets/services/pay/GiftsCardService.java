package newbee.morningGlory.http.servlets.services.pay;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import newbee.morningGlory.http.HttpService;
import newbee.morningGlory.http.actionEvent.GetGiftsCardEvent;
import newbee.morningGlory.http.servlets.services.AbstractService;
import newbee.morningGlory.http.util.MD5;
import newbee.morningGlory.http.util.TimeStampUtil;
import newbee.morningGlory.http.util.UrlBase64;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.util.DebugUtil;
import sophia.game.GameRoot;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.Mail.Mail;
import sophia.mmorpg.Mail.MailMgr;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.utils.RuntimeResult;
import sophia.mmorpg.utils.Type;

import com.google.gson.Gson;

public class GiftsCardService extends AbstractService {
	private static final Logger logger = Logger.getLogger(GiftsCardService.class);

	@SuppressWarnings("deprecation")
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("接收到服务器礼包卡领取请求");
		}
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");

		Player player = null;
		String playerName = null;
		String tstamp = null;
		String sign = null;
		try {
			playerName = UrlBase64.decode(request.getParameter("playerName"));
			tstamp = request.getParameter("tstamp");
			sign = request.getParameter("sign");
			if (!MD5.digest(playerName + tstamp + HttpService.HttpCommunicationKey).equals(sign)) {
				logger.error("礼包卡领取异常@非法请求(密钥错误): identityName=" + playerName + " tstamp=" + tstamp + " sign=" + sign);
				response.getWriter().println("{ code:1,description:\"非法请求(密钥错误)\"}");
				return;
			}
			if (!TimeStampUtil.check(Long.parseLong(tstamp), TimeStampUtil.MiddleOffset)) {
				response.getWriter().println("{ code:3,description:\"非法请求(时间戳过期)\"}");
				return;
			}
			if (!StringUtils.isEmpty(playerName)) {
				PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
				player = playerManager.getPlayerByName(playerName);
			}
			if (player == null) {
				logger.error("礼包卡领取异常@无法找到玩家: playerName=" + playerName);
				response.getWriter().println("{ code:2,description:\"无法找到玩家\"}");
				return;
			}
			List<ItemPair> itemPairs = null;
			try {
				JSONArray JSONArray = (JSONArray) JSONValue.parse(request.getParameter("data"));
				itemPairs = new ArrayList<ItemPair>();
				for (Object object : JSONArray) {
					JSONObject jsonObject = (JSONObject) object;
					String itemRefId = (String) jsonObject.get("itemRefId");
					GameRefObject ref = GameRoot.getGameRefObjectManager().getManagedObject(itemRefId);
					if (ref == null) {
						response.getWriter().println("{ code:4,description:\"物品refId错误" + itemRefId + "\"}");
						return;
					}
					int count = Type.getInt(jsonObject.get("count"), 0);
					ItemPair itemPair = new ItemPair(itemRefId, count, true);
					itemPairs.add(itemPair);
				}
				if (itemPairs.size() > 0) {
					RuntimeResult runtimeResult = ItemFacade.addItem(player, itemPairs, ItemOptSource.GMSend);
					if (runtimeResult.getCode() != 1) {
						String content = new String("由于背包满，礼包卡奖励改为邮件发送。");
						String json = (new Gson()).toJson(itemPairs);
						MailMgr.sendMailById(player.getId(), content, Mail.gonggao, json, 0, 0, 0);
					}
				}
			} catch (Exception e) {
				logger.error("礼包卡领取异常: playerName=" + playerName + " " + DebugUtil.printStack(e));
				response.getWriter().println("{ code:4,description:\"" + URLEncoder.encode(e.toString()) + "\"}");
				return;
			}
			GetGiftsCardEvent event = new GetGiftsCardEvent();
			event.setItemPairs(itemPairs);
			GameRoot.sendMessage(player.getIdentity(), event);
			response.getWriter().println("{ code:0,description:\"获取礼品卡成功\"}");
			return;
		} catch (Throwable ex) {
			logger.error("礼包卡领取异常: playerName=" + playerName + " " + DebugUtil.printStack(ex));
			response.getWriter().println("{ code:4,description:\"未知错误\"}");
			return;
		}
	}

}
