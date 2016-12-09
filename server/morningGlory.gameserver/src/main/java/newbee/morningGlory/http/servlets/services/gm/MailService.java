package newbee.morningGlory.http.servlets.services.gm;

import groovy.json.JsonOutput;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import newbee.morningGlory.http.HttpService;
import newbee.morningGlory.http.servlets.services.AbstractService;
import newbee.morningGlory.http.util.MD5;
import newbee.morningGlory.http.util.TimeStampUtil;
import newbee.morningGlory.http.util.Type;

import org.apache.log4j.Logger;

import sophia.foundation.util.DebugUtil;
import sophia.game.GameRoot;
import sophia.game.utils.eventBuf.EventBufMgr;
import sophia.game.utils.eventBuf.Executable;
import sophia.game.utils.eventBuf.IDoer;
import sophia.game.utils.eventBuf.Pair;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.Mail.GmMail;
import sophia.mmorpg.Mail.GmMailMgr;
import sophia.mmorpg.Mail.Mail;
import sophia.mmorpg.Mail.MailMgr;
import sophia.mmorpg.Mail.persistence.GmMailDao;
import sophia.mmorpg.item.ref.ItemRef;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.player.itemBag.ItemPair;

import com.google.common.base.Strings;
import com.google.gson.Gson;

public class MailService extends AbstractService {
	private static final Logger logger = Logger.getLogger(MailService.class.getName());
	private boolean mailSendFlag = true; // 发送标记

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// http://localhost/game/services?action=mail&content=test&playerMiniLevel=0&playerMaxLevel=100&gold=10&coin=100&item=equip_40_6200,1,1;equip_40_6300,2,1&toPlayersType=1
		if (logger.isDebugEnabled()) {
			logger.debug(JsonOutput.toJson(request.getParameterMap()));
		}
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		
		final String mailType = request.getParameter("mailType");
		if (mailType == null || (!mailType.equals("2"))) {
			response.getWriter().print("failure");
			logger.error(String.format("mailType is error mailType=%s",mailType));
			return;
		}
		
		if(!mailSendFlag){
			response.getWriter().print("failure");
			logger.error(String.format("last mail send is not over mailSendFlag=%s",mailSendFlag));
			return;
		}

		try {
			mailSendFlag = false;
			final String title  = getDecodeString(request.getParameter("theme"));
			final String content = getDecodeString(request.getParameter("content"));
			long tstamp = Long.parseLong(request.getParameter("tstamp"));
			String sign = request.getParameter("sign");

			if (!MD5.digest(tstamp + mailType + HttpService.HttpCommunicationKey).equals(sign)) {
				// response.getWriter().println("{ code:3,description:\"非法请求(密钥错误)\"}" );
				response.getWriter().print("failure:"+MD5.digest(tstamp + mailType + HttpService.HttpCommunicationKey));
				mailSendFlag = true;
				logger.error(String.format("非法请求(密钥错误) digest=%s sign=%s",tstamp + mailType + HttpService.HttpCommunicationKey,sign));
				return;
			}
			
			if(title.length()>16){
				response.getWriter().print("failure");
				mailSendFlag = true;
				logger.error(String.format("邮件标题过长=%s",title.length()));
				return;
			}
			
			int playerMinLevel = Type.getInt(request.getParameter("playerMinLevel"), 0);
			int playerMaxLevel = Type.getInt(request.getParameter("playerMaxLevel"), 0);
			final int  gold= Type.getInt(request.getParameter("gold"), 0);
			final int  coin= Type.getInt(request.getParameter("coin"), 0);
			final int bindGold = Type.getInt(request.getParameter("bindGold"), 0);
			
			List<ItemPair> rewardItemList = doWithItem(request.getParameter("item"));
			if (rewardItemList!=null && !checkItem(rewardItemList)) {
				response.getWriter().print("failure");
				mailSendFlag = true;
				logger.error(String.format("itemArray无效！itemArray.size=%s",rewardItemList.size()));
				return;
			}
			
			final String itemJsonStr = (new Gson()).toJson(rewardItemList);
			byte toPlayersType = Type.getByte(request.getParameter("toPlayerType"), (byte) 0);
			Collection<Player> playerList = getPlayerList(toPlayersType, request.getParameter("toPlayer"));
			if (!checkArgumentInt(gold, coin, playerMinLevel, playerMaxLevel) || playerList == null) {
				response.getWriter().print("failure");
				mailSendFlag = true;
				logger.error( String.format("Argument is error  gold=%s coin=%s playerMinLevel=%s playerMaxLevel=%s or playerList is null",gold,coin,playerMinLevel, playerMaxLevel));
				return;
			}

			String effectBeginTimeStr = request.getParameter("effectBeginTime");
			String effectEndTimeStr = request.getParameter("effectEndTime");

			long effectBeginTime = 0;
			long effectEndTime   = 0;

			if(effectBeginTimeStr!=null && !effectBeginTimeStr.equals(""))
				effectBeginTime = getValidTime(effectBeginTimeStr);
			
			if(effectEndTimeStr!=null && !effectEndTimeStr.equals(""))
			    effectEndTime   = getValidTime(effectEndTimeStr);
			
			logger.info(String.format("mail argument is  title=%s content=%s gold=%s bindGold=%s coin=%s effectBeginTimeStr=%s effectEndTimeStr=%s",title,content,gold,bindGold,coin,effectBeginTimeStr, effectEndTimeStr));
				
			//记录数据库
			if(1==toPlayersType){ //发送整个服务器
				GmMail gMail = MailMgr.newGmMail(title,content,itemJsonStr, gold,bindGold, coin, playerMinLevel, playerMaxLevel, effectBeginTime, effectEndTime);
				GmMailDao.getInstance().insertMail(gMail);
				GmMailMgr.getInstance().getGmMaillist().add(gMail);
				final String gmMailId = gMail.getMailId();
				
				long currTime = System.currentTimeMillis();
				if (effectBeginTime!=0 && (currTime < effectBeginTime)) {
					response.getWriter().print("success");
					mailSendFlag = true;
					return;
				}
				
				Collection<Player> characters = new ArrayList<Player>();
				for (Player player : playerList) {
					if (checkPlayerLevel(playerMinLevel, playerMaxLevel, player.getLevel())) {
						if (logger.isDebugEnabled()) {
							logger.debug("playerId=" + player.getId() + ",content=" + content + ",itemJsonStr=" + itemJsonStr + ",gold=" + gold + ",coin=" + coin);
						}
						
						characters.add(player);
					}
				}
				
				
				Pair<Integer, TimeUnit> pair = EventBufMgr.getInstance().getOptimizationTotalUseTime(characters.size(), EventBufMgr.DefaultBatchSize);
				EventBufMgr.getInstance().addEvent(characters, null, new Executable() {
					@Override
					public void execute(IDoer doer) {
						Player playerCharacter = (Player) doer;

						MailMgr.sendMailById(playerCharacter.getId(),title,content, Mail.GMReplyCustomType, itemJsonStr,gold,bindGold, coin,gmMailId);

					}
				}, EventBufMgr.DefaultBatchSize, pair.getKey(), pair.getValue());
				
			}else if (0 == toPlayersType){	//发送个人			
				for (Player player : playerList) {
					if (logger.isDebugEnabled()) {
						logger.debug("playerId=" + player.getId() + ",content=" + content + ",itemJsonStr=" + itemJsonStr + ",gold=" + gold +",bindGold="+bindGold+",coin=" + coin);
					}
					MailMgr.sendMailById(player.getId(), title,content, Mail.GMReplyCustomType, itemJsonStr, gold, bindGold, coin,null);
				}
				
			}else{
				response.getWriter().print("failure");
				mailSendFlag = true;
				logger.error(String.format("错误的发送类型 toPlayersType=%s", toPlayersType));
				return;
			}
			
			response.getWriter().print("success");
		} catch (Exception e) {
			response.getWriter().print("failure");
			logger.error("未知错误 " + DebugUtil.printStack(e));
		}
		
		mailSendFlag = true;
		
		return;
	}

	// ==============================================================================================================================================
	private ArrayList<ItemPair> doWithItem(String item) {
		// equip_40_6200,1,1;equip_40_6300,2,1
		if (item != null && (!item.equals("null")) && (!Strings.isNullOrEmpty(item))) {

			ArrayList<ItemPair> items = new ArrayList<ItemPair>();

			String[] itemArray = item.split(";");
			for (int k = 0; k < itemArray.length; k++) {
				ItemPair iteminfo = new ItemPair();
				String[] itemValue = itemArray[k].split(",");
				if (itemValue.length != 3)
					continue;

				String itemRefId = itemValue[0];
				iteminfo.setItemRefId(itemRefId);
				int number = Integer.valueOf(itemValue[1]).intValue();
				iteminfo.setNumber(number);
				if (itemValue[2].equals("1")) {
					iteminfo.setBindStatus(true);
				} else {
					iteminfo.setBindStatus(false);
				}

				//物品检查
				
				items.add(iteminfo);
			}
			return items;
		}

		return null;
	}

	private String getDecodeString(String str) throws UnsupportedEncodingException {
		return URLDecoder.decode(new String(str.getBytes("ISO-8859-1"), "utf-8"), "utf-8");
	}

	private long getValidTime(String str) throws UnsupportedEncodingException {
		String strTime = getDecodeString(str);
		String p = "yyyy-MM-dd";
		if(strTime.length()==10){
			
		}else if(strTime.length()==19){
			p="yyyy-MM-dd HH:mm:ss";
		}else{
			logger.error(String.format("错误的时间格式 strTime=%s",strTime));
			
			return 0;
		}
		long validTime = 0;
		if(strTime!=null && !strTime.equals("") && !strTime.equals("null"))
			validTime = TimeStampUtil.parseDateTime(strTime,p).getTime();
		
		return validTime;
	}
	private Collection<Player> getPlayerList(byte toPlayersType, String toPlayerIds) {
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		if (1 == toPlayersType) {
			return playerManager.getOnlinePlayerList();
		} else if (0 == toPlayersType) {
			if (Strings.isNullOrEmpty(toPlayerIds)) {
				return null;
			}
			
			List<Player> playerList = new ArrayList<>();
			String[] toPlayerIdArr = toPlayerIds.split(",");
			for (String playerId : toPlayerIdArr) {
				Player player = playerManager.getPlayerByName(playerId);
				if (player != null) {
					playerList.add(player);
				} else{
					logger.error(String.format("playerId=%s  is not exist",playerId));
					return null;
				}
			}
			
			return playerList;
		}
		return null;
	}

	private boolean checkArgumentInt(int... args) {
		for (int arg : args) {
			if (arg < 0 || arg >= Integer.MAX_VALUE) {
				return false;
			}
		}
		return true;
	}

	public boolean checkItem(List<ItemPair> itemArray) {

		for (ItemPair item : itemArray) {	
			ItemRef itemRef = (ItemRef) GameRoot.getGameRefObjectManager().getManagedObject(item.getItemRefId());			
			if (itemRef == null) {
				logger.error("不存在refId=" + item.getItemRefId() + "的物品类型！");
				return false;
			}
			int number = item.getNumber();
			if (number > 1000 || number <= 0) {
				logger.error(String.format("number不能超过1000或者必须大于0,number=%s",number));
				return false;
			}
		}

		return true;
	}
	
	private boolean checkPlayerLevel(int playerMinLevel, int playerMaxLevel, int playerLevel) {
		
		if(playerMaxLevel<=0)
			return true;
		
		if (playerLevel >= playerMinLevel && playerLevel <= playerMaxLevel) {
			return true;
		}
		return false;
	}
}
